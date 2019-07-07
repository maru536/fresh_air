package team.perfect.fresh_air.Utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class JsonUtils {
    public static String getAsString(JsonElement src, String defaultValue) {
        String value = defaultValue;

        if (src != null) {
            try {
                value = src.getAsString();
            } catch (ClassCastException | IllegalStateException e) {

            }
        }

        return value;
    }

    public static float getAsFloat(JsonElement src, float defaultValue) {
        float value = defaultValue;

        if (src != null) {
            try {
                value = src.getAsFloat();
            } catch (ClassCastException | IllegalStateException e) {
                
            }
        }

        return value;
    }

    public static double getAsDouble(JsonElement src, double defaultValue) {
        double value = defaultValue;

        if (src != null) {
            try {
                value = src.getAsFloat();
            } catch (ClassCastException | IllegalStateException e) {
                
            }
        }

        return value;
    }

    public static int getAsInt(JsonElement src, int defaultValue) {
        int value = defaultValue;

        if (src != null) {
            try {
                value = src.getAsInt();
            } catch (ClassCastException | IllegalStateException e) {
                
            }
        }

        return value;
    }

    public static long getAsLong(JsonElement src, long defaultValue) {
        long value = defaultValue;

        if (src != null) {
            try {
                value = src.getAsLong();
            } catch (ClassCastException | IllegalStateException e) {
                
            }
        }

        return value;
    }

    public static JsonObject getAsJsonObject(JsonElement src) {
        JsonObject value = new JsonObject();

        if (src != null) {
            try {
                value = src.getAsJsonObject();
            } catch (IllegalStateException e) {
                
            }
        }

        return value;
    }
}