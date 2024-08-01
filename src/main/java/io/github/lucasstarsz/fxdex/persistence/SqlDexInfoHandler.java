package io.github.lucasstarsz.fxdex.persistence;

import io.github.lucasstarsz.fxdex.misc.ApiConversionTables;
import io.github.lucasstarsz.fxdex.misc.FileLinks;
import io.github.lucasstarsz.fxdex.model.JsonDexEntryItem;
import io.github.lucasstarsz.fxdex.model.JsonDexItem;
import io.github.lucasstarsz.fxdex.model.JsonDexListItem;
import io.github.lucasstarsz.fxdex.service.UiService;
import io.github.lucasstarsz.fxdex.vmodel.QueryType;
import org.apache.commons.lang3.StringUtils;

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
    public void saveDexPokemon(List<JsonDexListItem> dexEntries) {
        String dexToNameQuery = SqlDexInfoHandler.createInsertQuery(QueryType.SaveDexToName, dexEntries.size());
        String nameToDexQuery = SqlDexInfoHandler.createInsertQuery(QueryType.SaveNameToDex, dexEntries.size());

        System.out.println(dexToNameQuery);
        try (var dbConnection = DriverManager.getConnection(FileLinks.JDBCConnectionUrl);
             var dexToNameStatement = dbConnection.prepareStatement(dexToNameQuery);
             var nameToDexStatement = dbConnection.prepareStatement(nameToDexQuery)) {

            int dexToNameCount = QueryType.SaveDexToName.getData().length;

            for (int i = 1; i <= dexEntries.size() * dexToNameCount; i += dexToNameCount) {
                int itemIndex = ((i + dexToNameCount - 1) / dexToNameCount) - 1;
                JsonDexListItem dexEntryFromList = dexEntries.get(itemIndex);
                dexToNameStatement.setInt(i, dexEntryFromList.getDexNumber());
                dexToNameStatement.setString(i + 1, dexEntryFromList.getApiPokemonName());
            }

            dexToNameStatement.executeUpdate();

            int nameToDexCount = QueryType.SaveDexToName.getData().length;

            System.out.println(nameToDexQuery);
            for (int i = 1; i <= dexEntries.size() * nameToDexCount; i += nameToDexCount) {
                int itemIndex = ((i + dexToNameCount - 1) / dexToNameCount) - 1;
                JsonDexListItem dexEntryFromList = dexEntries.get(itemIndex);
                nameToDexStatement.setString(i, dexEntryFromList.getApiPokemonName());
                nameToDexStatement.setInt(i + 1, dexEntryFromList.getDexNumber());
            }

            nameToDexStatement.executeUpdate();
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
        String saveDexItems = SqlDexInfoHandler.createInsertQuery(QueryType.SaveDexItems, jsonDexItems.size());
        try (var dbConnection = DriverManager.getConnection(FileLinks.JDBCConnectionUrl);
             var saveDexItemsStatement = dbConnection.prepareStatement(saveDexItems)) {

            int saveDexItemsCount = QueryType.SaveDexItems.getData().length;

            System.out.println(saveDexItems);
            for (int i = 1; i <= jsonDexItems.size() * saveDexItemsCount; i += saveDexItemsCount) {
                int itemIndex = ((i + saveDexItemsCount - 1) / saveDexItemsCount) - 1;
                JsonDexItem jsonDexItem = jsonDexItems.get(itemIndex);
                saveDexItemsStatement.setString(i, jsonDexItem.getApiDexName());
                saveDexItemsStatement.setString(i + 1, ApiConversionTables.DexNameMap.get(jsonDexItem.getApiDexName().toLowerCase()));
                saveDexItemsStatement.setString(i + 2, jsonDexItem.getApiDexUrl());
            }

            saveDexItemsStatement.executeUpdate();
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

    public static String createInsertQuery(QueryType queryType, int valueCount) {
        String value = " (?" + ", ?".repeat(queryType.getData().length - 1) + "),";
        return "INSERT INTO " + queryType.getDatabaseName()
                + "(" + String.join(", ", queryType.getData()) + ") VALUES"
                + StringUtils.substringBeforeLast(value.repeat(valueCount), ",")
                + "\nON CONFLICT(" + queryType.getPrimaryKey() + ") DO NOTHING";
    }
}
