package io.github.lucasstarsz.fxdex.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
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

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;

        JsonDexItem dexItem = (JsonDexItem) other;

        return new EqualsBuilder()
                .append(apiPokedexName, dexItem.apiPokedexName)
                .append(apiPokedexUrl, dexItem.apiPokedexUrl)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(apiPokedexName)
                .append(apiPokedexUrl)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("apiPokedexName", apiPokedexName)
                .append("apiPokedexUrl", apiPokedexUrl)
                .toString();
    }
}
