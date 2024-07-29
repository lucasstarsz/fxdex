package io.github.lucasstarsz.fxdex.model;

import io.github.lucasstarsz.fxdex.service.JsonParserService;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class JsonDexEntryItem {

    private final String name;
    private final int nationalDexNumber;
    private final String genus;
    private final String generation;
    private final List<String> eggGroups;
    private final Map<String, String> flavorTexts;

    public JsonDexEntryItem(JSONObject dexEntry, JsonParserService jsonParserService) {
        genus = jsonParserService.getPokemonGenus(dexEntry);
        generation = jsonParserService.getGenerationIntroducedIn(dexEntry);
        eggGroups = jsonParserService.getEggGroups(dexEntry);
        flavorTexts = jsonParserService.getFlavorTexts(dexEntry);
        name = jsonParserService.getPokemonNameFromDexEntry(dexEntry);
        nationalDexNumber = jsonParserService.getNationalDexNumber(dexEntry);
    }

    public JsonDexEntryItem(ResultSet mainDexEntry, ResultSet eggGroups, Map<String, String> flavorTexts) throws SQLException {
        nationalDexNumber = mainDexEntry.getInt("nationalDexNumber");
        name = Objects.requireNonNull(mainDexEntry.getString("name"));
        genus = Objects.requireNonNull(mainDexEntry.getString("genus"));
        generation = Objects.requireNonNull(mainDexEntry.getString("generation"));
        this.eggGroups = Arrays.stream(eggGroups.getString("eggGroupsString").split(",")).toList();
        this.flavorTexts = flavorTexts;
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

    public String getName() {
        return name;
    }

    public int getNationalDexNumber() {
        return nationalDexNumber;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;

        JsonDexEntryItem dexEntryItem = (JsonDexEntryItem) other;

        return new EqualsBuilder()
                .append(name, dexEntryItem.name)
                .append(nationalDexNumber, dexEntryItem.nationalDexNumber)
                .append(genus, dexEntryItem.genus)
                .append(generation, dexEntryItem.generation)
                .append(eggGroups, dexEntryItem.eggGroups)
                .append(flavorTexts, dexEntryItem.flavorTexts)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(name)
                .append(nationalDexNumber)
                .append(genus)
                .append(generation)
                .append(eggGroups)
                .append(flavorTexts)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("name", name)
                .append("nationalDexNumber", nationalDexNumber)
                .append("genus", genus)
                .append("generation", generation)
                .append("eggGroups", eggGroups)
                .append("flavorTexts", flavorTexts)
                .toString();
    }
}
