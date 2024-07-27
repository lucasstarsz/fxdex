package io.github.lucasstarsz.fxdex.model;

import org.json.JSONObject;

public class JsonDexListItem {
    private final int dexNumber;
    private final String apiPokemonName;

    public JsonDexListItem(JSONObject dexEntryFromInfoList) {
        this.dexNumber = dexEntryFromInfoList.getInt("entry_number");
        JSONObject pokemonSpecies = dexEntryFromInfoList.getJSONObject("pokemon_species");
        this.apiPokemonName = pokemonSpecies.getString("name");
    }

    public int getDexNumber() {
        return dexNumber;
    }

    public String getApiPokemonName() {
        return apiPokemonName;
    }
}
