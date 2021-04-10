package ru.alex2772.vcpkggui.model;

/**
 * Data class which represents a package
 */
public class VcpkgPackage {
    private String mName;
    private String mVersion = "";
    private String mPlatform = "";
    private String mHomepage = "";
    private String mDescription = "";

    public VcpkgPackage(String name) {
        mName = name;
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

    public String getHomepage() {
        return mHomepage;
    }

    public void setHomepage(String homepage) {
        mHomepage = homepage;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getDescription() {
        return mDescription;
    }
}
