package ru.alex2772.vcpkggui.model;

import com.google.gson.GsonBuilder;
import ru.alex2772.vcpkggui.VcpkgGui;
import ru.alex2772.vcpkggui.core.Config;
import ru.alex2772.vcpkggui.core.VcpkgHelper;
import ru.alex2772.vcpkggui.util.VcpkgConfigParser;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * Represents a vcpkg package
 */
public class VcpkgPackage {
    private String mName;
    private String mVersion = "";
    private String mPlatform = "";
    private String mHomepage = "";
    private String mDescription = "";
    private boolean mIsInstalled = false;

    private static Map<String, VcpkgPackage> ourInstanceMap = new HashMap<>();

    /**
     * List of installed packages. Can be null if invalidated
     */
    private static List<VcpkgPackage> ourInstalledPackages = null;

    private VcpkgPackage(String name) {
        mName = name;
    }

    /**
     * Retrieves a vcpkg package with its info. Does work like a singleton. The two VcpkgPackage object pointing to the
     * same package name cannot exist.
     * @param packageFolderName package folder name in ports/ folder
     * @return VcpkgPackage object
     */
    public static VcpkgPackage get(String packageFolderName) {
        if (ourInstalledPackages == null) {
            // we should initiate and populate this list in order to determine which packages are installed
            ourInstalledPackages = new ArrayList<>();
            for (VcpkgHelper.VcpkgInstallRecord packageName : VcpkgHelper.getInstalledPackagesVcpkg()) {
                VcpkgPackage p = get(packageName.name);
                p.mIsInstalled = true;
                p.mVersion = packageName.version;
                ourInstalledPackages.add(p);
            }
        }
        {
            VcpkgPackage p = ourInstanceMap.get(packageFolderName);
            if (p != null)
                return p;
        }
        // new package
        final VcpkgPackage p = new VcpkgPackage(packageFolderName);
        ourInstanceMap.put(packageFolderName, p);

        // package info located in either CONTROL file or vcpkg.json file.
        File controlFile = new File(Config.getConfig().vcpkgLocation + "/ports/" + packageFolderName + "/CONTROL");
        File vcpkgJsonFile = new File(Config.getConfig().vcpkgLocation + "/ports/" + packageFolderName + "/vcpkg.json");
        if (controlFile.isFile()) {
            try {
                // parse CONTROL
                VcpkgConfigParser.parse(new FileReader(controlFile), (key, value) -> {
                    switch (key) {
                        case "Source" -> p.mName = value;
                        case "Version" -> p.mVersion = value;
                        case "Supports" -> p.mPlatform = value;
                        case "Homepage" -> p.mHomepage = value;
                        case "Description" -> p.mDescription = value;
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
                p.mName = json.name;
                p.mVersion = json.versionString;
                p.mHomepage = json.homepage;
                p.mDescription = json.description;
            } catch (Exception e) {
                VcpkgGui.getLogger().log(Level.WARNING, "Could not fetch package info of " + packageFolderName, e);
            }
        }

        return p;
    }

    public static void invalidateListInstalledPackages() {
        ourInstalledPackages = null;
    }


    public String getName() {
        return mName;
    }

    public String getVersion() {
        return mVersion;
    }

    public String getPlatform() {
        return mPlatform;
    }
    public String getHomepage() {
        return mHomepage;
    }

    public String getDescription() {
        return mDescription;
    }

    public boolean isInstalled() {
        return mIsInstalled;
    }
}
