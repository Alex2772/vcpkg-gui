package ru.alex2772.vcpkggui.core;

import com.google.gson.GsonBuilder;
import ru.alex2772.vcpkggui.VcpkgGui;

import java.io.*;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Config {
    public static class ConfigImpl {
        String vcpkgLocation = "vcpkg";
    }

    private static Config ourConfig = new Config();
    private ConfigImpl mConfigImpl;

    public static ConfigImpl getConfig() {
        return ourConfig.mConfigImpl;
    }

    public Config() {
        try {
            mConfigImpl = new GsonBuilder().create().fromJson(new FileReader("vcpkg-gui-config.json"), ConfigImpl.class);
        } catch (FileNotFoundException e) {
            VcpkgGui.getLogger().log(Level.INFO, "Config file does not exists.");
        } catch (Exception e){
            VcpkgGui.getLogger().log(Level.INFO, "Could not open config", e);
        }
        if (mConfigImpl == null) {
            mConfigImpl = new ConfigImpl();
            saveImpl();
        }
    }

    public static void save() {
        ourConfig.saveImpl();
    }

    private void saveImpl() {
        try {
            BufferedWriter fos = new BufferedWriter(new FileWriter("vcpkg-gui-config.json"));
            fos.write(new GsonBuilder().create().toJson(mConfigImpl));
            fos.close();
        } catch (IOException e) {
            VcpkgGui.getLogger().log(Level.INFO, "Could not write config", e);
        }
    }
}
