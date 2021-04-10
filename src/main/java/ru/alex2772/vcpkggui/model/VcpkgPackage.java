package ru.alex2772.vcpkggui.model;

/**
 * Data class which represents a package
 */
public class VcpkgPackage {
    private String mName;
    private String mVersion;
    private String mPlatform;

    public VcpkgPackage(String name, String version, String platform) {
        mName = name;
        mVersion = version;
        mPlatform = platform;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getVersion() {
        return mVersion;
    }

    public void setVersion(String version) {
        mVersion = version;
    }

    public String getPlatform() {
        return mPlatform;
    }

    public void setPlatform(String platform) {
        mPlatform = platform;
    }
}
