package globalhttp;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.researchworx.cresco.library.messaging.MsgEvent;
import com.researchworx.cresco.library.plugin.core.CPlugin;
import com.researchworx.cresco.library.utilities.CLogger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/API")
public class webREST {

	//http://192.168.1.116:9999/API?type=exec&region=test&agent=controller2&plugin=plugin/0&paramkey=cmd&paramvalue=show_version
	
	//add plugin
	//http://192.168.1.116:9999/API?type=config&region=test&agent=controller2&paramkey=configtype&paramvalue=pluginadd&paramkey=plugin&paramvalue=plugin/1
	
	//remove plugin
	//http://192.168.1.116:9999/API?type=config&region=test&agent=controller2&paramkey=configtype&paramvalue=pluginremove&paramkey=plugin&paramvalue=plugin/1
	
	
	//shutdown agent
	//http://192.168.1.116:9999/API?type=config&region=test&agent=controller2&paramkey=configtype&paramvalue=componentstate&paramkey=msg&paramvalue=disable

    private static CPlugin mainPlugin;
    private static CLogger logger;

    //private static GlobalCommandExec gce;

	public static void connectPlugin(CPlugin plugin) {
		mainPlugin = plugin;
		logger = new CLogger(webREST.class, plugin.getMsgOutQueue(), plugin.getRegion(), plugin.getAgent(), plugin.getPluginID(), CLogger.Level.Info);
	//	gce = new GlobalCommandExec(plugin);
	}

/*
	public webREST() {

        this.mainPlugin = HTTPServerEngine.plugin;
        this.logger = HTTPServerEngine.logger;

    }
*/

    @POST
    @Path("/addgpipeline")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response crunchifyREST11(InputStream incomingData) {

        StringBuilder crunchifyBuilder = new StringBuilder();
        String returnString = null;
        try {

            BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
            String line = null;
            while ((line = in.readLine()) != null) {
                crunchifyBuilder.append(line);
            }

            MsgEvent me = meFromJson(crunchifyBuilder.toString());
            //MsgEvent ce = gce.cmdExec(me);
			me.setParam("src_region", mainPlugin.getRegion());
			me.setParam("src_agent", mainPlugin.getAgent());
			me.setParam("src_plugin", mainPlugin.getPluginID());
			me.setParam("dst_region", mainPlugin.getRegion());

			MsgEvent ce = mainPlugin.sendRPC(me);

            if(ce != null)
            {
                Gson gson = new Gson();
                returnString = gson.toJson(ce);
            }
            else
            {
                returnString = "ok";
            }
            //return Response.ok(returnString, MediaType.TEXT_HTML_TYPE).build();

        } catch (Exception e) {
            logger.error("Error Parsing: - ");
        }
        logger.debug("Data Received: " + crunchifyBuilder.toString());

        // return HTTP response 200 in case of success
        //return Response.status(200).entity("woot2").build();
        return Response.ok(returnString, MediaType.APPLICATION_JSON_TYPE).build();
    }

    @GET
	public Response getRoot(

			@QueryParam("type") String type,
			@QueryParam("region") String region,
			@QueryParam("agent") String agent,
			@QueryParam("plugin") String plugin,
			@QueryParam("paramkey") final List<String> paramskey,
			@QueryParam("paramvalue") final List<String> paramsvalue)

	{
	    try
	    {

            MsgEvent me = null;
	    	try
	    	{
	    		if((paramskey.size() != paramsvalue.size()))
	    		{
	    			logger.info("getRoot : Params key:value size does not match");
		    		return Response.status(Response.Status.BAD_REQUEST).entity("Params key:value size does not match").build();
	    		}
	    		if(paramskey.isEmpty())
	    		{
	    			logger.info("getRoot : No params values provided in the request");
		    		return Response.status(Response.Status.BAD_REQUEST).entity("No params values provided in the request").build();
	    		}
		   	    Map<String,String> params = new HashMap<String,String>();
		   	    for(String param : paramskey)
		   	    {
		   	    	params.put(param, paramsvalue.get(paramskey.indexOf(param)));
		   	    }
	    		me = new MsgEvent(MsgEvent.Type.valueOf(type.toUpperCase()),region,agent,plugin,params);
	    	}
	    	catch(Exception ex)
	    	{
	    		logger.info("getRoot : Problem building message:" + ex.toString());
	    		return Response.status(Response.Status.BAD_REQUEST).entity("bad request").build();
	    	}
	    	
	        //getmsg
	    	//controllercmd
	    	
	    	//if(me.getParam("cmd") != null) {
                logger.debug("Controller : webREST : Incoming");
                logger.debug("MsgType=" + me.getMsgType().toString());
                logger.debug("Region=" + me.getMsgRegion() + " Agent=" + me.getMsgAgent() + " plugin=" + me.getMsgPlugin());
                logger.debug("params=" + me.getParams().toString());
                //CODY
            //}
	    	
	    	try
	    	{
	    		//logger.info("Incoming message: " + me.getParamsString());
	    		//MsgEvent ce = ControllerEngine.commandExec.cmdExec(me);


				//MsgEvent ce = gce.cmdExec(me);
				me.setParam("src_region", mainPlugin.getRegion());
				me.setParam("src_agent", mainPlugin.getAgent());
				me.setParam("src_plugin", mainPlugin.getPluginID());
				me.setParam("dst_region", mainPlugin.getRegion());

				MsgEvent ce = mainPlugin.sendRPC(me);


				String returnString = null;
	    		if(ce != null)
	    		{
	    			Gson gson = new Gson();
					returnString = gson.toJson(ce);
	    		}
	    		else
	    		{
	    			returnString = "ok";
	    		}
	    		return Response.ok(returnString, MediaType.TEXT_HTML_TYPE).build();
			}
	    	catch(Exception ex)
	    	{
	    		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Internal Server Error").build();
		    		
	    	}
	    }
		catch(Exception ex)
		{
			logger.info("getRoot:" + ex.toString());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Internal Server Error").build();
		}

	}
	
	private MsgEvent meFromJson(String jsonMe)
	{
		Gson gson = new GsonBuilder().create();
        MsgEvent me = gson.fromJson(jsonMe, MsgEvent.class);
        //logger.info(p);
        return me;
	}
	
	
/*	
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String getClichedMessage5() throws IOException {
	    
		//InputStream in = com.codybum.graphserv.Jsonserver.class.getClass().getResourceAsStream("/FPS_liveflow.txt");
		String fileContents = null;
		try{
			fileContents = getFile("index.html");
		}
		catch(Exception ex)
		{
			fileContents = "Core Error:" + ex.toString();
		}
		//return payload;
		
		InputStream in = getClass().getResourceAsStream("/index.html");
		BufferedReader input = new BufferedReader(new InputStreamReader(in));
		String message = org.apache.commons.io.IOUtils.toString(input);
		
		return message;
	    //return("BooyAAA"); 
	}
		
	@GET
	@Path("/js/{subResources:.*}")
	@Produces("text/javascript")
	
	public String getjs(@PathParam("subResources") String subResources) throws FileNotFoundException, UnsupportedEncodingException
	{
	  //return subResources;
	  return getFile("js/" + subResources);
	}
	
	
	@GET
	@Path("/css/{subResources:.*}")
	@Produces("text/css")
	public String getcss(@PathParam("subResources") String subResources) throws FileNotFoundException, UnsupportedEncodingException
	{
	  //return subResources;
	  return getFile("css/" + subResources);
	}
	

	@GET
	@Path("/gdb")
	@Produces(MediaType.TEXT_HTML)
	public String getClichedMessage3() throws FileNotFoundException, UnsupportedEncodingException {
	    
		//InputStream in = com.codybum.graphserv.Jsonserver.class.getClass().getResourceAsStream("/FPS_liveflow.txt");
		
		
		String fileContents = null;
		try{
			fileContents = String.valueOf(PluginEngine.gdb.getNodeId("test", "controller2", null));
		}
		catch(Exception ex)
		{
			fileContents = "Core Error:" + ex.toString();
		}
		//return payload;
		return fileContents;
	    //return("BooyAAA"); 
	}
	
String getFile(String filename) throws FileNotFoundException, UnsupportedEncodingException
{
	String path = httpServerEngine.class.getProtectionDomain().getCodeSource().getLocation().getPath();
	if(path.startsWith("file:"))
	{
		path = path.substring(5);
	}
	path = URLDecoder.decode(path, "UTF-8");
	//BufferedImage img = ImageIO.read(new File((new File(path).getParentFile().getPath()) +  File.separator + "folder" + File.separator + "yourfile.jpg"));
	
	InputStream in = new FileInputStream((new File(path).getParentFile().getPath()) + File.separator + filename);
	BufferedReader input = new BufferedReader(new InputStreamReader(in));
	 
	String sCurrentLine;
	
	StringBuilder liveflowscript = new StringBuilder();
	
	try {
		while ((sCurrentLine = input.readLine()) != null) {
			liveflowscript.append(sCurrentLine + "\n");
		}
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	return liveflowscript.toString();
	
}
 */
}

