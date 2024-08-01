package io.github.lucasstarsz.fxdex.persistence;

import io.github.lucasstarsz.fxdex.model.JsonDexEntryItem;
import io.github.lucasstarsz.fxdex.model.JsonDexItem;
import io.github.lucasstarsz.fxdex.model.JsonDexListItem;

import java.sql.SQLException;
import java.util.List;

public interface DexInfoHandler {
    void saveDexItems(List<JsonDexItem> jsonDexItems);

    void saveDexEntry(JsonDexEntryItem jsonDexEntry, String dexEntryLink);

    JsonDexEntryItem loadDexEntry(int nationalDexNumber) throws SQLException;

    void saveDexPokemon(List<JsonDexListItem> dexEntries);

    int loadPokemonNumber(String apiPokemonName);
}
