package ru.alex2772.vcpkggui.util;

import java.io.File;

public class FileUtil {
    public static boolean deleteDirectory(File directoryToBeDeleted) {
        File[] contents = directoryToBeDeleted.listFiles();
        if (contents != null) {
            for (File file : contents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }
}
