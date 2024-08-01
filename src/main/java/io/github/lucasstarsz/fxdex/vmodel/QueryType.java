package io.github.lucasstarsz.fxdex.vmodel;

public enum QueryType {
    SaveDexToName("dexPokemon", "nationalDexNumber", "nationalDexNumber", "apiDexName"),
    SaveNameToDex("pokemonNamesToDex", "apiDexName", "apiDexName", "nationalDexNumber"),
    SaveDexItems("dexes", "apiName", "apiName", "uiName", "apiUrl");

    final String databaseName;
    final String primaryKey;
    final String[] data;

    QueryType(String databaseName, String key, String... data) {
        this.databaseName = databaseName;
        this.primaryKey = key;
        this.data = data;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public String[] getData() {
        return data;
    }
}
