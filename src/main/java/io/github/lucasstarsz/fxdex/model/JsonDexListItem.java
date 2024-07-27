package io.github.lucasstarsz.fxdex.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
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

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;

        JsonDexListItem dexListItem = (JsonDexListItem) other;

        return new EqualsBuilder()
                .append(dexNumber, dexListItem.dexNumber)
                .append(apiPokemonName, dexListItem.apiPokemonName)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(dexNumber)
                .append(apiPokemonName)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("dexNumber", dexNumber)
                .append("apiPokemonName", apiPokemonName)
                .toString();
    }
}
