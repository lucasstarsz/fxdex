package io.github.lucasstarsz.fxdex.model;

import org.json.JSONObject;

public class JsonDexItem {

    private final String apiPokedexName;
    private final String apiPokedexUrl;
    
    public JsonDexItem(JSONObject dex) {
        this.apiPokedexName = dex.getString("name");
        this.apiPokedexUrl = dex.getString("url");
    }

    public String getApiPokedexName() {
        return apiPokedexName;
    }

    public String getApiPokedexUrl() {
        return apiPokedexUrl;
    }
}
