import FileEngine.Web.Plugin.PluginMain;

public class TestMain {

    public static void main(String[] args) {
        PluginMain pluginMain = new PluginMain();
        pluginMain.loadPlugin();
        pluginMain.textChanged("test");
    }
}
