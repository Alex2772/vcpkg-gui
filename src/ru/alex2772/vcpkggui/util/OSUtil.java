package ru.alex2772.vcpkggui.util;

public class OSUtil {
    public static boolean isWindows() {
        return System.getProperty("os.name").contains("win");
    }
}
