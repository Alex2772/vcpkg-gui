package ru.alex2772.vcpkggui.model;

import com.google.gson.annotations.SerializedName;

/**
 * Data class representing a vcpkg.json file
 */
public class VcpkgJson {
    public String name;

    @SerializedName("version-string")
    public String versionString;
}
