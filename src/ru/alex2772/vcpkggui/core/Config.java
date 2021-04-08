package ru.alex2772.vcpkggui.core;

import ru.alex2772.vcpkggui.VcpkgGui;

import java.io.*;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

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
            s.close();
        } catch (Exception e){
            VcpkgGui.getLogger().log(Level.INFO, "Could not open config", e);
        }
    }

    public void save() {
        try {
            BufferedWriter fos = new BufferedWriter(new FileWriter("vcpkg-gui-config.txt"));
            fos.write(mVcpkgLocation); fos.newLine();
            fos.close();
        } catch (IOException e) {
            VcpkgGui.getLogger().log(Level.INFO, "Could not write config", e);
        }
    }
}
