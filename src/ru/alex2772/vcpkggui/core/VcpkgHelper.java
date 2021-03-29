package ru.alex2772.vcpkggui.core;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class VcpkgHelper {

    public static String call(String... args) throws IOException, InterruptedException {
        List<String> finalArgs = new LinkedList<>();
        finalArgs.add("vcpkg");
        finalArgs.addAll(Arrays.asList(args));
        Process proc = new ProcessBuilder(finalArgs)
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .redirectError(ProcessBuilder.Redirect.PIPE)
                .start();

        proc.waitFor(30, TimeUnit.SECONDS);
        return new String(proc.getInputStream().readAllBytes());
    }

    public static String getVersion() throws IOException, InterruptedException {
        return call("--version");
    }
}
