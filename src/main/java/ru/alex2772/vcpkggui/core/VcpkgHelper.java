package ru.alex2772.vcpkggui.core;

import com.google.gson.GsonBuilder;
import ru.alex2772.vcpkggui.VcpkgGui;
import ru.alex2772.vcpkggui.model.VcpkgJson;
import ru.alex2772.vcpkggui.model.VcpkgPackage;
import ru.alex2772.vcpkggui.ui.ProgressDialog;
import ru.alex2772.vcpkggui.util.LazyList;
import ru.alex2772.vcpkggui.util.OSUtil;
import ru.alex2772.vcpkggui.util.ProcessUtil;
import ru.alex2772.vcpkggui.util.VcpkgConfigParser;

import javax.swing.*;
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
        finalArgs.add(getVcpkgExecutable());
        finalArgs.addAll(Arrays.asList(args));
        Process proc = new ProcessBuilder(finalArgs)
                .directory(new File(Config.getConfig().vcpkgLocation))
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .redirectError(ProcessBuilder.Redirect.PIPE)
                .start();

        proc.waitFor(30, TimeUnit.SECONDS);
        return new String(proc.getInputStream().readAllBytes());
    }

    private static String getVcpkgExecutable() {
        return OSUtil.isWindows() ? new File(Config.getConfig().vcpkgLocation).getAbsolutePath() + "\\vcpkg.exe" : "./vcpkg";
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

    public static class VcpkgInstallRecord {
        public String name;
        public String version;

        public VcpkgInstallRecord(String name, String version) {
            this.name = name;
            this.version = version;
        }
    }

    public static List<VcpkgInstallRecord> getInstalledPackagesVcpkg() {
        try {
            /*
            vcpkg list outputs installed packages in the following format:

            package1:platform       version1     description1
            package2:platform       version2     description2
            package3:platform       version3     description3

             */
            List<VcpkgInstallRecord> s = new ArrayList<>();
            String[] lines = call("list").split("\n");
            for (String line : lines) {
                // separate package and platform
                int colonIndex = line.indexOf(':');

                // find version
                int blankIndex = line.indexOf(' ');
                int versionIndex = blankIndex + 1;
                for (; line.charAt(versionIndex) == ' '; ++versionIndex);

                s.add(new VcpkgInstallRecord(line.substring(0, colonIndex), line.substring(versionIndex, line.indexOf(' ', versionIndex))));
            }
            return s;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
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
                return VcpkgPackage.get(mFiles[index].getName());
            }

            @Override
            public void seekToElement(int index) {
                // ignore
            }
        });
    }

    public static void install(VcpkgPackage vcpkgPackage) {

    }
    public static void uninstall(VcpkgPackage vcpkgPackage) {
        new ProgressDialog("Uninstalling " + vcpkgPackage.getName(), new ProgressDialog.Callback() {
            @Override
            public void doInBackground(ProgressDialog pd) throws Exception {
                Process proc = new ProcessBuilder(getVcpkgExecutable(), "remove", vcpkgPackage.getName())
                        .directory(new File(Config.getConfig().vcpkgLocation))
                        .redirectOutput(ProcessBuilder.Redirect.PIPE)
                        .redirectError(ProcessBuilder.Redirect.PIPE)
                        .start();

                ProcessUtil.outputToProgressDialog(pd, proc);
            }

            @Override
            public void onSuccess() {
                JOptionPane.showMessageDialog(VcpkgGui.getMainWindow(),
                                "Package: " + vcpkgPackage.getName(),
                        "Package uninstalled successfully",
                        JOptionPane.INFORMATION_MESSAGE);
                VcpkgGui.invalidateListInstalledPackages();
            }
        });
    }
}
