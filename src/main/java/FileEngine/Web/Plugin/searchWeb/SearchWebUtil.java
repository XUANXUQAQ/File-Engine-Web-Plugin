package FileEngine.Web.Plugin.searchWeb;

import FileEngine.Web.Plugin.settings.SettingsFrame;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

public class SearchWebUtil {

    public static void searchWeb(String text) {
        try {
            text = URLEncoder.encode(text, "UTF-8");
            String url = SettingsFrame.getInstance().getUrl().replaceAll("%s", text);
            Desktop desktop;
            if (Desktop.isDesktopSupported()) {
                desktop = Desktop.getDesktop();
                desktop.browse(new URI(url));
            }
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
    }
}
