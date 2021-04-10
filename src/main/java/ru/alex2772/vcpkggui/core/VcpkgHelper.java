package ru.alex2772.vcpkggui.core;

import com.google.gson.GsonBuilder;
import ru.alex2772.vcpkggui.VcpkgGui;
import ru.alex2772.vcpkggui.model.VcpkgJson;
import ru.alex2772.vcpkggui.model.VcpkgPackage;
import ru.alex2772.vcpkggui.util.LazyList;
import ru.alex2772.vcpkggui.util.OSUtil;
import ru.alex2772.vcpkggui.util.VcpkgConfigParser;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class VcpkgHelper {

    public static String call(String... args) throws IOException, InterruptedException {
        List<String> finalArgs = new LinkedList<>();
        finalArgs.add(OSUtil.isWindows() ? new File(Config.getConfig().vcpkgLocation).getAbsolutePath() + "\\vcpkg.exe" : "./vcpkg");
        finalArgs.addAll(Arrays.asList(args));
        Process proc = new ProcessBuilder(finalArgs)
                .directory(new File(Config.getConfig().vcpkgLocation))
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

    public static VcpkgPackage fetchPortInfo(String packageFolderName) {
        VcpkgPackage p = new VcpkgPackage(packageFolderName);

        // package info located in either CONTROL file or vcpkg.json file.
        File controlFile = new File(Config.getConfig().vcpkgLocation + "/ports/" + packageFolderName + "/CONTROL");
        File vcpkgJsonFile = new File(Config.getConfig().vcpkgLocation + "/ports/" + packageFolderName + "/vcpkg.json");
        if (controlFile.isFile()) {
            try {
                // parse CONTROL
                VcpkgConfigParser.parse(new FileReader(controlFile), (key, value) -> {
                    switch (key) {
                        case "Source" -> p.setName(value);
                        case "Version" -> p.setVersion(value);
                        case "Supports" -> p.setPlatform(value);
                        case "Homepage" -> p.setHomepage(value);
                        case "Description" -> p.setDescription(value);
                    }
                });
            } catch (Exception e) {
                VcpkgGui.getLogger().log(Level.WARNING, "Could not fetch package info of " + packageFolderName, e);
            }
        } else if (vcpkgJsonFile.isFile()) {
            try {
                // parse vcpkg.json
                VcpkgJson json = new GsonBuilder()
                        .create().fromJson(new FileReader(vcpkgJsonFile), VcpkgJson.class);
                p.setName(json.name);
                p.setVersion(json.versionString);
                p.setHomepage(json.homepage);
                p.setDescription(json.description);
            } catch (Exception e) {
                VcpkgGui.getLogger().log(Level.WARNING, "Could not fetch package info of " + packageFolderName, e);
            }
        }

        return p;
    }

    public static List<VcpkgPackage> getInstalledPackages() {
        return new ArrayList<>();
    }

    public static List<VcpkgPackage> getAvailablePackages() {
        return LazyList.create(new LazyList.IStreamObjectProvider<VcpkgPackage>() {

            File[] mFiles = new File(Config.getConfig().vcpkgLocation + "/ports").listFiles();

            @Override
            public int estimateListSize() {
                return mFiles.length;
            }

            @Override
            public VcpkgPackage readElement(int index) {
                return fetchPortInfo(mFiles[index].getName());
            }

            @Override
            public void seekToElement(int index) {
                // ignore
            }
        });
    }
}
