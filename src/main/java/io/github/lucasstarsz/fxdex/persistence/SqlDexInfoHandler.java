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
import java.util.*;

public class SqlDexInfoHandler implements DexInfoHandler {

    private static final String SaveDexPokemon = """
            INSERT INTO natDexToName(nationalDexNumber, apiMonName) VALUES (?, ?)
            ON CONFLICT(nationalDexNumber) DO NOTHING
            """;

    private static final String SaveDexPokemonName = """
            INSERT INTO nameToNatDex(apiMonName, nationalDexNumber) VALUES (?, ?)
            ON CONFLICT(apiMonName) DO NOTHING
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
            SELECT * FROM nameToNatDex WHERE nameToNatDex.apiMonName=(?)
            """;

    private static final String LoadDexList = """
            SELECT * FROM pokemonByDex WHERE pokemonByDex.apiDexName=(?) ORDER BY pokemonByDex.dexNumber ASC
            """;

    private static final String LoadDexItem = """
            SELECT * FROM dexes WHERE dexes.apiDexName=(?)
            """;

    @Override
    public void saveDexList(String apiDexName, List<JsonDexListItem> dexList) {
        String monByDexQuery = SqlDexInfoHandler.createInsertQuery(QueryType.SaveDexList, dexList.size());

        try (var dbConnection = DriverManager.getConnection(FileLinks.JDBCConnectionUrl);
             var monByDexStatement = dbConnection.prepareStatement(monByDexQuery)) {

            int monByDexCount = QueryType.SaveDexList.getData().length;
            for (int i = 1; i <= dexList.size() * monByDexCount; i += monByDexCount) {
                int itemIndex = ((i + monByDexCount - 1) / monByDexCount) - 1;
                JsonDexListItem dexEntryFromList = dexList.get(itemIndex);
                monByDexStatement.setString(i, apiDexName);
                monByDexStatement.setString(i + 1, dexEntryFromList.getApiPokemonName());
                monByDexStatement.setInt(i + 2, dexEntryFromList.getDexNumber());
            }

            System.out.println("mons by dex list:\n" + monByDexStatement.toString());
            monByDexStatement.executeUpdate();
        } catch (SQLException e) {
            UiService.createErrorAlert("Unable to save pok\u00e9mon", e).showAndWait();
        }
    }

    @Override
    public void saveNatDexList(List<JsonDexListItem> dexList) {
        String dexToNameQuery = SqlDexInfoHandler.createInsertQuery(QueryType.SaveDexToName, dexList.size());
        String nameToDexQuery = SqlDexInfoHandler.createInsertQuery(QueryType.SaveNameToDex, dexList.size());

        try (var dbConnection = DriverManager.getConnection(FileLinks.JDBCConnectionUrl);
             var dexToNameStatement = dbConnection.prepareStatement(dexToNameQuery);
             var nameToDexStatement = dbConnection.prepareStatement(nameToDexQuery)) {

            int dexToNameCount = QueryType.SaveDexToName.getData().length;
            for (int i = 1; i <= dexList.size() * dexToNameCount; i += dexToNameCount) {
                int itemIndex = ((i + dexToNameCount - 1) / dexToNameCount) - 1;
                JsonDexListItem dexEntryFromList = dexList.get(itemIndex);
                dexToNameStatement.setInt(i, dexEntryFromList.getDexNumber());
                dexToNameStatement.setString(i + 1, dexEntryFromList.getApiPokemonName());
            }

            System.out.println("dex to name list:\n" + dexToNameStatement.toString());
            dexToNameStatement.executeUpdate();

            int nameToDexCount = QueryType.SaveDexToName.getData().length;
            for (int i = 1; i <= dexList.size() * nameToDexCount; i += nameToDexCount) {
                int itemIndex = ((i + nameToDexCount - 1) / nameToDexCount) - 1;
                JsonDexListItem dexEntryFromList = dexList.get(itemIndex);
                nameToDexStatement.setString(i, dexEntryFromList.getApiPokemonName());
                nameToDexStatement.setInt(i + 1, dexEntryFromList.getDexNumber());
            }

            System.out.println("name to dex list:\n" + nameToDexStatement.toString());
            nameToDexStatement.executeUpdate();
        } catch (SQLException e) {
            UiService.createErrorAlert("Unable to save pok\u00e9mon", e).showAndWait();
        }
    }

    @Override
    public List<JsonDexListItem> loadDexList(String apiDexName) {
        try (var dbConnection = DriverManager.getConnection(FileLinks.JDBCConnectionUrl);
             var loadDexListStatement = dbConnection.prepareStatement(LoadDexList)) {
            System.out.println("search for " + apiDexName);
            loadDexListStatement.setString(1, apiDexName);
            var result = loadDexListStatement.executeQuery();

            List<JsonDexListItem> dexItems = new ArrayList<>();
            System.out.println("finding...");
            while (result.next()) {
                System.out.println("found 1");
                dexItems.add(new JsonDexListItem(result));
            }

            return dexItems;
        } catch (SQLException e) {
            UiService.createErrorAlert("Unable to load pok\u00e9mon", e).showAndWait();
        }

        return List.of();
    }

    @Override
    public JsonDexItem loadDexItem(String apiDexName) throws SQLException {
        try (var dbConnection = DriverManager.getConnection(FileLinks.JDBCConnectionUrl);
             var loadDexNameStatement = dbConnection.prepareStatement(LoadDexItem)) {
            System.out.println("search for " + apiDexName);
            loadDexNameStatement.setString(1, apiDexName);
            var result = loadDexNameStatement.executeQuery();

            return new JsonDexItem(result);
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
    public void saveDexMenuList(List<JsonDexItem> jsonDexItems) {
        String saveDexItems = SqlDexInfoHandler.createInsertQuery(QueryType.SaveDexItems, jsonDexItems.size());
        try (var dbConnection = DriverManager.getConnection(FileLinks.JDBCConnectionUrl);
             var saveDexItemsStatement = dbConnection.prepareStatement(saveDexItems)) {

            int saveDexItemsCount = QueryType.SaveDexItems.getData().length;

            System.out.println(saveDexItems);
            for (int i = 1; i <= jsonDexItems.size() * saveDexItemsCount; i += saveDexItemsCount) {
                int itemIndex = ((i + saveDexItemsCount - 1) / saveDexItemsCount) - 1;
                JsonDexItem jsonDexItem = jsonDexItems.get(itemIndex);
                saveDexItemsStatement.setString(i, jsonDexItem.getApiDexName());
                saveDexItemsStatement.setString(i + 1, Objects.requireNonNullElse(jsonDexItem.getUiName(), ApiConversionTables.DexNameMap.get(jsonDexItem.getApiDexName().toLowerCase())));
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
