package ru.alex2772.vcpkggui.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Type;

/**
 * Data class representing a vcpkg.json file
 */
public class VcpkgJson {
    public String name;
    public String homepage;

    @JsonAdapter(DescriptionAdapter.class)
    public String description;

    @SerializedName("version-string")
    public String versionString;


    /**
     * Normally description goes as single string literal but sometimes it goes as a string array. This adapter
     * tries read as string, if it doesn't work - it tries to read as a string array.
     */
    public static class DescriptionAdapter implements JsonDeserializer<String> {
        @Override
        public String deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                return json.getAsString();
            } catch (Exception ignored) {
                // try to read as an array.
                String s = "";
                for (JsonElement e : json.getAsJsonArray()) {
                    s += "<p>";
                    s += e.getAsString();
                    s += "</p><br />";
                }
                return s;
            }
        }
    }
}
