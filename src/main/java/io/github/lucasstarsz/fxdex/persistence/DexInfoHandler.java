package io.github.lucasstarsz.fxdex.persistence;

import io.github.lucasstarsz.fxdex.model.JsonDexEntryItem;
import io.github.lucasstarsz.fxdex.model.JsonDexItem;
import io.github.lucasstarsz.fxdex.model.JsonDexListItem;

import java.sql.SQLException;
import java.util.List;

public interface DexInfoHandler {
    void saveDexMenuList(List<JsonDexItem> jsonDexItems);

    void saveDexEntry(JsonDexEntryItem jsonDexEntry, String dexEntryLink);

    JsonDexEntryItem loadDexEntry(int nationalDexNumber) throws SQLException;

    void saveNatDexList(List<JsonDexListItem> dexList);

    void saveDexList(String apiDexName, List<JsonDexListItem> dexList);

    JsonDexItem loadDexItem(String apiDexName) throws SQLException;

    int loadPokemonNumber(String apiPokemonName);

    List<JsonDexListItem> loadDexList(String apiDexName);
}
