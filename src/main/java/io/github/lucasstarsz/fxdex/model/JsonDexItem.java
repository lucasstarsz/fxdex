package io.github.lucasstarsz.fxdex.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.json.JSONObject;

public class JsonDexItem {

    private final String apiDexName;
    private final String apiDexUrl;
    
    public JsonDexItem(JSONObject dex) {
        this.apiDexName = dex.getString("name");
        this.apiDexUrl = dex.getString("url");
    }

    public String getApiDexName() {
        return apiDexName;
    }

    public String getApiDexUrl() {
        return apiDexUrl;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;

        JsonDexItem dexItem = (JsonDexItem) other;

        return new EqualsBuilder()
                .append(apiDexName, dexItem.apiDexName)
                .append(apiDexUrl, dexItem.apiDexUrl)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(apiDexName)
                .append(apiDexUrl)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("apiDexName", apiDexName)
                .append("apiDexUrl", apiDexUrl)
                .toString();
    }
}
