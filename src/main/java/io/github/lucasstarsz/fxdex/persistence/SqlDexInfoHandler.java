package io.github.lucasstarsz.fxdex.persistence;

import io.github.lucasstarsz.fxdex.misc.ApiConversionTables;
import io.github.lucasstarsz.fxdex.misc.FileLinks;
import io.github.lucasstarsz.fxdex.model.JsonDexEntryItem;
import io.github.lucasstarsz.fxdex.model.JsonDexItem;
import io.github.lucasstarsz.fxdex.model.JsonDexListItem;
import io.github.lucasstarsz.fxdex.service.UiService;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SqlDexInfoHandler implements DexInfoHandler {

    private static final String SaveDexPokemon = """
            INSERT INTO dexPokemon(nationalDexNumber, apiDexName) VALUES (?, ?)
            ON CONFLICT(nationalDexNumber) DO NOTHING
            """;

    private static final String SaveDexPokemonName = """
            INSERT INTO pokemonNamesToDex(apiDexName, nationalDexNumber) VALUES (?, ?)
            ON CONFLICT(apiDexName) DO NOTHING
            """;

    private static final String SaveDexItems = """
            INSERT INTO dexes(apiName, uiName, apiUrl) VALUES (?, ?, ?)
            ON CONFLICT(apiName) DO NOTHING
            """;

    private static final String SaveDexEntry = """
            INSERT INTO dexEntries(nationalDexNumber, name, speciesUrl, genus, generation) VALUES (?, ?, ?, ?, ?)
            ON CONFLICT(nationalDexNumber) DO NOTHING;
            """;

    private static final String SaveDexEntryEggGroups = """
            INSERT INTO eggGroups(nationalDexNumber, eggGroupsString) VALUES (?, ?)
            ON CONFLICT(nationalDexNumber) DO NOTHING;
            """;

    private static final String SaveDexEntryFlavorText = """
            INSERT INTO flavorTexts(nationalDexNumber, region, regionText) VALUES (?, ?, ?)
            ON CONFLICT(nationalDexNumber, region) DO NOTHING;
            """;

    private static final String LoadDexEntry = """
            SELECT * FROM dexEntries WHERE dexEntries.nationalDexNumber = (?)
            """;

    private static final String LoadDexEntryEggGroup = """
            SELECT * FROM eggGroups WHERE eggGroups.nationalDexNumber = (?)
            """;

    private static final String LoadDexEntryFlavorTexts = """
            SELECT * FROM flavorTexts WHERE (flavorTexts.nationalDexNumber, flavorTexts.region) = (?, ?)
            """;

    private static final String LoadDexPokemonByName = """
            SELECT * FROM pokemonNamesToDex WHERE pokemonNamesToDex.apiDexName=(?)
            """;

    @Override
    public void saveDexPokemon(JsonDexListItem dexEntryFromList) {
        try (var dbConnection = DriverManager.getConnection(FileLinks.JDBCConnectionUrl);
             var dexEntryStatement = dbConnection.prepareStatement(SaveDexPokemon);
             var pokemonNameStatement = dbConnection.prepareStatement(SaveDexPokemonName)) {

            dexEntryStatement.setInt(1, dexEntryFromList.getDexNumber());
            dexEntryStatement.setString(2, dexEntryFromList.getApiPokemonName());

            dexEntryStatement.executeUpdate();

            pokemonNameStatement.setString(1, dexEntryFromList.getApiPokemonName());
            pokemonNameStatement.setInt(2, dexEntryFromList.getDexNumber());

            pokemonNameStatement.executeUpdate();
        } catch (SQLException e) {
            UiService.createErrorAlert("Unable to save pok\u00e9mon", e).showAndWait();
        }
    }

    @Override
    public int loadPokemonNumber(String apiPokemonName) {
        try (var dbConnection = DriverManager.getConnection(FileLinks.JDBCConnectionUrl);
             var dexEntryStatement = dbConnection.prepareStatement(LoadDexPokemonByName)) {
            System.out.println("search for " + apiPokemonName);
            dexEntryStatement.setString(1, apiPokemonName);
            var result = dexEntryStatement.executeQuery();

            return result.getInt("nationalDexNumber");
        } catch (SQLException e) {
            UiService.createErrorAlert("Unable to load pok\u00e9mon", e).showAndWait();
        }

        return 0;
    }

    @Override
    public void saveDexItems(List<JsonDexItem> jsonDexItems) {
        try (var dbConnection = DriverManager.getConnection(FileLinks.JDBCConnectionUrl);
             var statementObj = dbConnection.prepareStatement(SaveDexItems)) {

            for (JsonDexItem jsonDexItem : jsonDexItems) {
                statementObj.setString(1, jsonDexItem.getApiDexName());
                statementObj.setString(2, ApiConversionTables.DexNameMap.get(jsonDexItem.getApiDexName().toLowerCase()));
                statementObj.setString(3, jsonDexItem.getApiDexUrl());
                statementObj.executeUpdate();
            }
        } catch (SQLException e) {
            UiService.createErrorAlert("Unable to save pok\u00e9dex list", e).showAndWait();
        }
    }

    @Override
    public void saveDexEntry(JsonDexEntryItem jsonDexEntry, String dexEntryLink) {
        try (var dbConnection = DriverManager.getConnection(FileLinks.JDBCConnectionUrl);
             var dexEntryStatement = dbConnection.prepareStatement(SaveDexEntry);
             var dexEntryEGStatement = dbConnection.prepareStatement(SaveDexEntryEggGroups);
             var dexEntryFTStatement = dbConnection.prepareStatement(SaveDexEntryFlavorText)) {

            dexEntryStatement.setInt(1, jsonDexEntry.getNationalDexNumber());
            dexEntryStatement.setString(2, jsonDexEntry.getName());
            dexEntryStatement.setString(3, dexEntryLink);
            dexEntryStatement.setString(4, jsonDexEntry.getGenus());
            dexEntryStatement.setString(5, jsonDexEntry.getGeneration());
            dexEntryStatement.executeUpdate();

            dexEntryEGStatement.setInt(1, jsonDexEntry.getNationalDexNumber());
            dexEntryEGStatement.setString(2, String.join(",", jsonDexEntry.getEggGroups()));

            dexEntryEGStatement.executeUpdate();

            for (var flavorText : jsonDexEntry.getFlavorTexts().entrySet()) {
                dexEntryFTStatement.setInt(1, jsonDexEntry.getNationalDexNumber());
                dexEntryFTStatement.setString(2, flavorText.getKey());
                dexEntryFTStatement.setString(3, flavorText.getValue());

                dexEntryFTStatement.executeUpdate();
            }
        } catch (SQLException e) {
            UiService.createErrorAlert("Unable to save pok\u00e9dex entries", e).showAndWait();
        }
    }

    @Override
    public JsonDexEntryItem loadDexEntry(int nationalDexNumber) throws SQLException {
        try (var dbConnection = DriverManager.getConnection(FileLinks.JDBCConnectionUrl);
             var dexEntryStatement = dbConnection.prepareStatement(LoadDexEntry);
             var dexEntryEGStatement = dbConnection.prepareStatement(LoadDexEntryEggGroup);
             var dexEntryFTStatement = dbConnection.prepareStatement(LoadDexEntryFlavorTexts)) {

            dexEntryStatement.setInt(1, nationalDexNumber);
            var dexEntryResult = dexEntryStatement.executeQuery();

            dexEntryEGStatement.setInt(1, nationalDexNumber);
            var eggGroupResult = dexEntryEGStatement.executeQuery();

            Map<String, String> flavorTexts = new LinkedHashMap<>();
            for (String dexName : ApiConversionTables.GameVersions) {
                dexEntryFTStatement.setInt(1, nationalDexNumber);
                dexEntryFTStatement.setString(2, dexName);

                var flavorTextResult = dexEntryFTStatement.executeQuery();
                if (flavorTextResult.getString("region") == null) {
                    continue;
                }

                flavorTexts.put(flavorTextResult.getString("region"), flavorTextResult.getString("regionText"));
            }

            return new JsonDexEntryItem(dexEntryResult, eggGroupResult, flavorTexts);
        }
    }
}
