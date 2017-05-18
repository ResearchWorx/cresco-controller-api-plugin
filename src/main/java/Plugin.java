import com.google.auto.service.AutoService;
import com.researchworx.cresco.library.plugin.core.CPlugin;

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
    }

    @Override
    public void cleanUp() {
        /*
         *  Insert your shutdown / clean up code here
         */
    }
}
