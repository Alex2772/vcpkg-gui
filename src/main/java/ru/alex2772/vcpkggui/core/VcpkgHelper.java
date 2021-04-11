package ru.alex2772.vcpkggui.core;

import com.google.gson.GsonBuilder;
import ru.alex2772.vcpkggui.VcpkgGui;
import ru.alex2772.vcpkggui.model.VcpkgJson;
import ru.alex2772.vcpkggui.model.VcpkgPackage;
import ru.alex2772.vcpkggui.ui.ProgressDialog;
import ru.alex2772.vcpkggui.ui.ReportDialog;
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

        String output = new String(proc.getInputStream().readAllBytes());
        proc.waitFor(30, TimeUnit.SECONDS);
        return output;
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
        } catch (Throwable ignored) {}

        return "unknown";
    }

    public static class VcpkgInstallRecord {
        public String name;
        public String version;
        public String platform;

        public VcpkgInstallRecord(String name, String version, String platform) {
            this.name = name;
            this.version = version;
            this.platform = platform;
        }
    }

    public static List<VcpkgInstallRecord> getInstalledPackagesVcpkg() {
        try {
            /*
            vcpkg list outputs installed packages in the following format:

            package1:platform1      version1#port_version1     description1
            package2:platform2      version2#port_version2     description2
            package3:platform3      version3#port_version3     description3

             */
            List<VcpkgInstallRecord> s = new ArrayList<>();
            String output = call("list");
            if (output.startsWith("No packages are")) {
                // whoops, no packages
                return s;
            }
            String[] lines = output.split("\n");
            for (String line : lines) {
                // separate package and platform
                int colonIndex = line.indexOf(':');

                // find version
                int blankIndex = line.indexOf(' ');
                int versionIndex = blankIndex + 1;
                for (; line.charAt(versionIndex) == ' '; ++versionIndex);

                String versionString = line.substring(versionIndex, line.indexOf(' ', versionIndex));
                // version string always contains '#' symbol otherwise it's not a version string
                s.add(new VcpkgInstallRecord(line.substring(0, colonIndex),
                                             versionString.contains("#") ? versionString : "",
                                             line.substring(colonIndex + 1, line.indexOf(' '))
                        ));
            }
            return s;
        } catch (Exception e) {
            VcpkgGui.getLogger().log(Level.WARNING, "Could not get list of installed packages", e);
        }

        return new ArrayList<>();
    }


    public static void install(VcpkgPackage vcpkgPackage) {
        new ProgressDialog("Installing " + vcpkgPackage.getName(), new ProgressDialog.Callback() {
            private String output;
            private int exitCode;

            @Override
            public void doInBackground(ProgressDialog pd) throws Exception {
                Process proc = new ProcessBuilder(getVcpkgExecutable(), "install", vcpkgPackage.getName())
                        .directory(new File(Config.getConfig().vcpkgLocation))
                        .redirectOutput(ProcessBuilder.Redirect.PIPE)
                        .redirectError(ProcessBuilder.Redirect.PIPE)
                        .start();

                output = ProcessUtil.outputToProgressDialog(pd, proc);
                exitCode = proc.waitFor();
            }

            @Override
            public void onSuccess() {
                if (exitCode == 0) {
                    ReportDialog.show(output,
                            "Installed package: " + vcpkgPackage.getName(),
                            "Package is installed");
                } else {
                    ReportDialog.show(output,
                            "Failed to install the package: " + vcpkgPackage.getName(),
                            "Package is not installed");
                }
                VcpkgGui.invalidateListInstalledPackages();
            }

            @Override
            public void onDone() {
                VcpkgGui.getMainWindow().setEnabled(true);
            }
        });
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
                                "Uninstalled package: " + vcpkgPackage.getName(),
                        "Package uninstalled successfully",
                        JOptionPane.INFORMATION_MESSAGE);
                VcpkgGui.invalidateListInstalledPackages();
            }

            @Override
            public void onDone() {
                VcpkgGui.getMainWindow().setEnabled(true);
            }
        });
    }
}
