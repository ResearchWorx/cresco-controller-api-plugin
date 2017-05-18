import com.google.auto.service.AutoService;
import com.researchworx.cresco.library.plugin.core.CPlugin;
import globalhttp.HTTPServerEngine;

@AutoService(CPlugin.class)
public class Plugin extends CPlugin {
    @Override
    public void setExecutor() {
        setExec(new Executor(this));
    }

    public void start() {
        /*
         *  Insert your startup code here
         */
        try {
            //Start Global Controller Services
            logger.info("Starting Global HTTPInternal Service");
            HTTPServerEngine httpEngineInternal = new HTTPServerEngine(this);
            Thread httpServerThreadExternal = new Thread(httpEngineInternal);
            httpServerThreadExternal.start();
        }
        catch (Exception ex) {
            logger.error("startHTTP() " + ex.getMessage());
        }
    }


    @Override
    public void cleanUp() {
        /*
         *  Insert your shutdown / clean up code here
         */
    }
}
