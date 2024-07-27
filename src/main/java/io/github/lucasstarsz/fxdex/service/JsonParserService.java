package io.github.lucasstarsz.fxdex.service;

import io.github.lucasstarsz.fxdex.model.JsonDexEntryItem;
import io.github.lucasstarsz.fxdex.model.JsonDexListItem;
import io.github.lucasstarsz.fxdex.model.JsonDexItem;
import org.apache.commons.text.WordUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static io.github.lucasstarsz.fxdex.misc.ApiConversionTables.RomanNumeralMap;

public interface JsonParserService {

    String IntroducedIn = "Introduced In: Generation ";
    String GenusNotFound = "Genus not found";

    default List<JsonDexItem> parsePokedexItems(JSONObject dexesJSON) {
        int dexCount = dexesJSON.getInt("count");
        List<JsonDexItem> dexItems = new ArrayList<>(dexCount);

        for (int i = 0; i < dexCount; i++) {
            JSONObject pokedex = dexesJSON.getJSONArray("results").getJSONObject(i);
            JsonDexItem dexItem = new JsonDexItem(pokedex);
            dexItems.add(dexItem);
        }

        return dexItems;
    }

    default JsonDexListItem parseDexItemIntoPokemon(JSONObject entry) {
        return new JsonDexListItem(entry);
    }

    default JsonDexEntryItem getDexEntryItem(JSONObject dexEntryJSON) {
        return new JsonDexEntryItem(dexEntryJSON, this);
    }

    default String getPokemonGenus(JSONObject dexEntryJSON) {
        String genus = GenusNotFound;
        JSONArray geneses = dexEntryJSON.getJSONArray("genera");

        for (int i = 0; i < geneses.length(); i++) {
            JSONObject genusCandidate = geneses.getJSONObject(i);
            if (genusCandidate.getJSONObject("language").getString("name").equals("en")) {
                genus = genusCandidate.getString("genus");
                break;
            }
        }

        return genus;
    }

    default List<String> getEggGroups(JSONObject dexEntryJSON) {
        List<String> eggGroups = new ArrayList<>();
        JSONArray eggGroupJSON = dexEntryJSON.getJSONArray("egg_groups");

        for (int i = 0; i < eggGroupJSON.length(); i++) {
            JSONObject eggGroup = eggGroupJSON.getJSONObject(i);
            String eggGroupString = eggGroup.getString("name");

            // account for Human-Like egg group
            eggGroupString = eggGroupString.replaceAll("shape", " like");
            eggGroupString = WordUtils.capitalize(eggGroupString);
            eggGroupString = eggGroupString.replaceAll(" ", "-");

            eggGroups.add(eggGroupString);
        }

        return eggGroups;
    }

    default String getGenerationIntroducedIn(JSONObject dexEntryJSON) {
        String introducedInString = dexEntryJSON.getJSONObject("generation").getString("name");
        introducedInString = introducedInString.replaceAll("generation-", "");

        return IntroducedIn + RomanNumeralMap.get(introducedInString.toLowerCase());
    }

    default Map<String, String> getFlavorTexts(JSONObject dexEntryJSON) {
        Map<String, String> flavorTexts = new LinkedHashMap<>();
        JSONArray flavorTextsJSON = dexEntryJSON.getJSONArray("flavor_text_entries");

        for (int i = 0; i < flavorTextsJSON.length(); i++) {
            JSONObject flavorTextCandidate = flavorTextsJSON.getJSONObject(i);

            if (flavorTextCandidate.getJSONObject("language").getString("name").equals("en")) {
                String flavorText = flavorTextCandidate.getString("flavor_text");
                String textVersion = flavorTextCandidate.getJSONObject("version").getString("name");
                flavorTexts.put(textVersion, flavorText);
            }
        }

        return flavorTexts;
    }
}
