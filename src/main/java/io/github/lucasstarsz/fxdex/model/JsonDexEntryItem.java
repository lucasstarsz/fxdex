package io.github.lucasstarsz.fxdex.model;

import io.github.lucasstarsz.fxdex.service.JsonParserService;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
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

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;

        JsonDexEntryItem dexEntryItem = (JsonDexEntryItem) other;

        return new EqualsBuilder()
                .append(genus, dexEntryItem.genus)
                .append(generation, dexEntryItem.generation)
                .append(eggGroups, dexEntryItem.eggGroups)
                .append(flavorTexts, dexEntryItem.flavorTexts)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(genus)
                .append(generation)
                .append(eggGroups)
                .append(flavorTexts)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("genus", genus)
                .append("generation", generation)
                .append("eggGroups", eggGroups)
                .append("flavorTexts", flavorTexts)
                .toString();
    }
}
