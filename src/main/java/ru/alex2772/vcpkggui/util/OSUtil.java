package ru.alex2772.vcpkggui.util;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

public class OSUtil {
    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("win");
    }

    /**
     * @return On Windows - C:\Program Files\Git\bin\git.exe (if file present), otherwise - git
     */
    public static String getGitExecutable() {
        if (isWindows()) {
            String p = "C:\\Program Files\\Git\\bin\\git.exe";
            if (new File(p).isFile()) {
                return p;
            }
        }
        return "git";
    }


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
