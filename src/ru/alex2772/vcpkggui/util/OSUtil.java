package ru.alex2772.vcpkggui.util;

import java.io.File;
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
}
