package io.github.lucasstarsz.fxdex.model;

import io.github.lucasstarsz.fxdex.service.JsonParserService;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public class JsonDexEntryItem {

    private final String genus;
    private final String generation;
    private final List<String> eggGroups;
    private final Map<String, String> flavorTexts;

    public JsonDexEntryItem(JSONObject dexEntry, JsonParserService jsonParserService) {
        genus = jsonParserService.getPokemonGenus(dexEntry);
        generation = jsonParserService.getGenerationIntroducedIn(dexEntry);
        eggGroups = jsonParserService.getEggGroups(dexEntry);
        flavorTexts = jsonParserService.getFlavorTexts(dexEntry);
    }

    public String getGenus() {
        return genus;
    }

    public String getGeneration() {
        return generation;
    }

    public List<String> getEggGroups() {
        return eggGroups;
    }

    public Map<String, String> getFlavorTexts() {
        return flavorTexts;
    }
}
