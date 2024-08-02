package io.github.lucasstarsz.fxdex.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;

public class JsonDexItem {

    private final String apiMonName;
    private final String apiDexUrl;
    private final String uiName;

    public JsonDexItem(JSONObject dex) {
        this.apiMonName = dex.getString("name");
        this.apiDexUrl = dex.getString("url");
        this.uiName = null;
    }

    public JsonDexItem(ResultSet dexSql) throws SQLException {
        this.apiMonName = dexSql.getString("apiDexName");
        this.uiName = dexSql.getString("uiName");
        this.apiDexUrl = dexSql.getString("apiUrl");
    }

    public String getApiDexName() {
        return apiMonName;
    }

    public String getApiDexUrl() {
        return apiDexUrl;
    }

    public String getUiName() {
        return uiName;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;

        JsonDexItem dexItem = (JsonDexItem) other;

        return new EqualsBuilder()
                .append(apiMonName, dexItem.apiMonName)
                .append(apiDexUrl, dexItem.apiDexUrl)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(apiMonName)
                .append(apiDexUrl)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("apiMonName", apiMonName)
                .append("uiName", uiName)
                .append("apiDexUrl", apiDexUrl)
                .toString();
    }
}
