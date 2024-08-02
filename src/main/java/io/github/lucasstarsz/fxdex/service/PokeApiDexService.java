/* Copyright 2024 Andrew Dey

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. */

package io.github.lucasstarsz.fxdex.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import io.github.lucasstarsz.fxdex.misc.ApiConversionTables;
import io.github.lucasstarsz.fxdex.misc.ApiLinks;
import io.github.lucasstarsz.fxdex.model.JsonDexEntryItem;
import io.github.lucasstarsz.fxdex.model.JsonDexItem;
import io.github.lucasstarsz.fxdex.model.JsonDexListItem;
import io.github.lucasstarsz.fxdex.persistence.DexInfoHandler;
import javafx.scene.control.MenuItem;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.inject.Inject;

import javafx.beans.property.ListProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.Region;

import static io.github.lucasstarsz.fxdex.misc.ApiConversionTables.DexNameMap;
import static io.github.lucasstarsz.fxdex.service.UiService.NoDexesAvailable;

public class PokeApiDexService implements DexService {

    public static final String DefaultDexUrl = ApiLinks.DexUrl
            + ApiConversionTables.DexNameToIdMap.get(ApiConversionTables.National);

    private final HttpService httpService;
    private final UiService uiService;
    private final JsonParserService jsonParserService;
    private final DexInfoHandler dexInfoHandler;

    @Inject
    public PokeApiDexService(HttpService httpService, UiService uiService, JsonParserService jsonParserService, DexInfoHandler dexInfoHandler) {
        this.httpService = httpService;
        this.uiService = uiService;
        this.jsonParserService = jsonParserService;
        this.dexInfoHandler = dexInfoHandler;
    }

    @Override
    public void loadDexesForMenu(ListProperty<Label> currentDexUi, MenuButton dexMenu, StringProperty currentDexName)
            throws IOException, InterruptedException, URISyntaxException {

        var dexesResponse = httpService.get(DexRequestOptions.defaultOptions());
        if (dexesResponse.statusCode() != 200) {
            dexMenu.getItems().add(NoDexesAvailable);
            throw new IOException(dexesResponse.statusCode() + ": " + dexesResponse.body());
        }

        List<MenuItem> menuItems = uiService.createDexItems(
                new JSONObject(dexesResponse.body()),
                currentDexUi,
                currentDexName,
                this
        );

        dexMenu.getItems().setAll(menuItems);
    }

    @Override
    public void loadDexList(ListProperty<Label> currentDexUi, JsonDexItem dexItem, StringProperty currentDexName) {
        List<Label> dexListItems = new ArrayList<>();
        List<JsonDexListItem> dexEntries = new ArrayList<>();

        try {
            try {
                dexEntries = dexInfoHandler.loadDexList(dexItem.getApiDexName());
                JsonDexItem dexInfo = dexInfoHandler.loadDexItem(dexItem.getApiDexName());
                if (dexEntries.isEmpty()) {
                    throw new RuntimeException("No dex entries found");
                }

                int pokemonDigitCount = countDigits(dexEntries.size());

                for (var dexEntry : dexEntries) {
                    Label dexItemUi = uiService.createDexListItem(pokemonDigitCount, dexEntry);
                    dexListItems.add(dexItemUi);
                }

                currentDexName.set("Pok\u00e9dex: " + dexInfo.getUiName());

            } catch (Exception e) {
                System.err.println("Unable to load from database: " + e.getMessage());
                System.err.println("Loading from PokeApi instead...");

                var requestOptions = new DexRequestOptions(DexRequestType.DexList, DefaultDexUrl);
                var dexResponse = httpService.get(requestOptions);

                if (dexResponse.statusCode() != 200) {
                    throw new IOException(
                            dexResponse.statusCode() + ": " + dexResponse.body() + " on GET " + DefaultDexUrl
                    );
                }

                JSONObject dexInfo = new JSONObject(dexResponse.body());
                JSONArray dexEntriesJSON = dexInfo.getJSONArray("pokemon_entries");

                int pokemonDigitCount = countDigits(dexEntriesJSON.length());

                for (var dexItemJSON : dexEntriesJSON) {
                    var dexEntryFromList = jsonParserService.parseDexItemIntoPokemon((JSONObject) dexItemJSON);
                    dexEntries.add(dexEntryFromList);

                    Label dexItemUi = uiService.createDexListItem(pokemonDigitCount, dexEntryFromList);
                    dexListItems.add(dexItemUi);
                }

                currentDexName.set("Pok\u00e9dex: " + DexNameMap.get(dexInfo.getString("name").toLowerCase()));
            } finally {
                System.out.println("saving " + dexListItems.size() + " pokemon");
                currentDexUi.setAll(dexListItems);
                dexInfoHandler.saveDexList(dexItem.getApiDexName(), dexEntries);
            }
        } catch (IOException | InterruptedException | URISyntaxException ex) {
            Alert errorAlert = UiService.createErrorAlert("Unable to open Pok\u00e9dex", ex);
            errorAlert.showAndWait();
        }
    }

    private int countDigits(int n) {
        return String.valueOf(n).length();
    }

    @Override
    public void loadDefaultDex(ListProperty<Label> currentDexUi, StringProperty currentDexName) {
        List<Label> dexListItems = new ArrayList<>();
        List<JsonDexListItem> dexEntries = new ArrayList<>();

        try {
            try {
                dexEntries = dexInfoHandler.loadDexList(ApiConversionTables.National);
                JsonDexItem dexInfo = dexInfoHandler.loadDexItem(ApiConversionTables.National);
                if (dexEntries.isEmpty()) {
                    throw new RuntimeException("No dex entries found");
                }

                int pokemonDigitCount = countDigits(dexEntries.size());

                for (var dexEntry : dexEntries) {
                    Label dexItemUi = uiService.createDexListItem(pokemonDigitCount, dexEntry);
                    dexListItems.add(dexItemUi);
                }

                currentDexName.set("Pok\u00e9dex: " + dexInfo.getUiName());

            } catch (Exception e) {
                System.err.println("Unable to load from database: " + e.getMessage());
                System.err.println("Loading from PokeApi instead...");

                var requestOptions = new DexRequestOptions(DexRequestType.DexList, DefaultDexUrl);
                var dexResponse = httpService.get(requestOptions);

                if (dexResponse.statusCode() != 200) {
                    throw new IOException(
                            dexResponse.statusCode() + ": " + dexResponse.body() + " on GET " + DefaultDexUrl
                    );
                }

                JSONObject dexInfo = new JSONObject(dexResponse.body());
                JSONArray dexEntriesJSON = dexInfo.getJSONArray("pokemon_entries");

                int pokemonDigitCount = countDigits(dexEntriesJSON.length());

                for (var dexItemJSON : dexEntriesJSON) {
                    var dexEntryFromList = jsonParserService.parseDexItemIntoPokemon((JSONObject) dexItemJSON);
                    dexEntries.add(dexEntryFromList);

                    Label dexItemUi = uiService.createDexListItem(pokemonDigitCount, dexEntryFromList);
                    dexListItems.add(dexItemUi);
                }

                currentDexName.set("Pok\u00e9dex: " + DexNameMap.get(dexInfo.getString("name").toLowerCase()));
            } finally {
                currentDexUi.setAll(dexListItems);
                dexInfoHandler.saveNatDexList(dexEntries);
                dexInfoHandler.saveDexList(ApiConversionTables.National, dexEntries);
            }
        } catch (IOException | InterruptedException | URISyntaxException ex) {
            Alert errorAlert = UiService.createErrorAlert("Unable to open Pok\u00e9dex", ex);
            errorAlert.showAndWait();
        }
    }

    @Override
    public void loadDexEntry(ListProperty<Region> dexEntriesList, String currentDexEntryName, int nationalDexNumber)
            throws IOException, InterruptedException, URISyntaxException {
        JsonDexEntryItem dexEntryItem;
        var requestOptions = new DexRequestOptions(DexRequestType.DexEntry, currentDexEntryName);

        try {
            dexEntryItem = dexInfoHandler.loadDexEntry(nationalDexNumber);
            System.out.println(dexEntryItem);
        } catch (Exception e) {
            System.err.println("Unable to load from database: " + e.getMessage());
            System.err.println("Loading from PokeApi instead...");

            var dexResponse = httpService.get(requestOptions);
            JSONObject dexEntry = new JSONObject(dexResponse.body());
            dexEntryItem = jsonParserService.getDexEntryItem(dexEntry);
        }

        var dexEntryUI = uiService.createDexEntryUI(dexEntryItem, currentDexEntryName);
        dexEntriesList.setAll(dexEntryUI);

        dexInfoHandler.saveDexEntry(dexEntryItem, requestOptions.linkProperty().getValue().toString());
    }
}
