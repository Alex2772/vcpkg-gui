package ru.alex2772.vcpkggui;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class Util {

    public static void openUrl(String url) {

        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (IOException | URISyntaxException ioException) {
            ioException.printStackTrace();
        } catch (UnsupportedOperationException e) {
            if (System.getProperty("os.name").toLowerCase().indexOf("ux") >= 0) {
                // use xdg-open to open url on linux
                try {
                    Runtime.getRuntime().exec("xdg-open " + url);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }
    }
}
