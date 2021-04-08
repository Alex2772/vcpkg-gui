package ru.alex2772.vcpkggui.core;

import ru.alex2772.vcpkggui.model.VcpkgPackage;
import ru.alex2772.vcpkggui.util.OSUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class VcpkgHelper {

    public static String call(String... args) throws IOException, InterruptedException {
        List<String> finalArgs = new LinkedList<>();
        finalArgs.add(OSUtil.isWindows() ? new File(Config.getConfig().mVcpkgLocation).getAbsolutePath() + "\\vcpkg.exe" : "./vcpkg");
        finalArgs.addAll(Arrays.asList(args));
        Process proc = new ProcessBuilder(finalArgs)
                .directory(new File(Config.getConfig().mVcpkgLocation))
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .redirectError(ProcessBuilder.Redirect.PIPE)
                .start();

        proc.waitFor(30, TimeUnit.SECONDS);
        return new String(proc.getInputStream().readAllBytes());
    }

    public static String getVersion() throws IOException, InterruptedException {
        String[] words = call("version").split("[ \n]");

        try {
            for (int i = 0; i < words.length; ++i) {
                if (words[i].equals("version")) {
                    return words[i + 1];
                }
            }
        } catch (Throwable e) {}

        return "unknown";
    }

    public static List<VcpkgPackage> getInstalledPackages() {
        return new ArrayList<>();
    }
}
