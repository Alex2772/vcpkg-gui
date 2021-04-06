package ru.alex2772.vcpkggui.core;

import java.io.*;
import java.util.Scanner;

public class Config {
    private static Config ourConfig = new Config();

    public String mVcpkgLocation = "";

    public static Config getConfig() {
        return ourConfig;
    }

    public Config() {
        try {
            Scanner s = new Scanner(new FileInputStream("vcpkg-gui-config.txt"));
            mVcpkgLocation = s.nextLine();
            // add other settings here...
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            BufferedWriter fos = new BufferedWriter(new FileWriter("vcpkg-gui-config.txt"));
            fos.write(mVcpkgLocation); fos.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
