package pe.edu.upc.wallpapeer.utils;

import com.google.gson.Gson;

public class JsonConverter {
    private static final Gson gson = new Gson();

    public static Gson getGson() {
        return gson;
    }
}
