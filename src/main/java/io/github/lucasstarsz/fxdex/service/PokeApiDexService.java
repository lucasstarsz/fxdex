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

import io.github.lucasstarsz.fxdex.misc.ApiConversionTables;
import io.github.lucasstarsz.fxdex.misc.ApiLinks;
import io.github.lucasstarsz.fxdex.model.JsonDexItem;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.inject.Inject;

import javafx.beans.property.ListProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.Region;

import static io.github.lucasstarsz.fxdex.misc.ApiConversionTables.PokedexNameMap;
import static io.github.lucasstarsz.fxdex.service.UiService.NoDexesAvailable;

public class PokeApiDexService implements DexService {

    public static final String DefaultPokedexUrl = ApiLinks.DexUrl
            + ApiConversionTables.PokedexNameToIdMap.get(ApiConversionTables.Kanto);

    private final HttpService httpService;
    private final UiService uiService;
    private final JsonParserService jsonParserService;

    @Inject
    public PokeApiDexService(HttpService httpService, UiService uiService, JsonParserService jsonParserService) {
        this.httpService = httpService;
        this.uiService = uiService;
        this.jsonParserService = jsonParserService;
    }

    @Override
    public void loadPokedexesForMenu(ListProperty<Label> currentDexUi, MenuButton dexMenu, StringProperty currentDexName)
            throws IOException, InterruptedException, URISyntaxException {
        var dexesResponse = httpService.get(DexRequestOptions.defaultOptions());
        if (dexesResponse.statusCode() != 200) {
            dexMenu.getItems().add(NoDexesAvailable);
            throw new IOException(dexesResponse.statusCode() + ": " + dexesResponse.body());
        }

        var menuItems = uiService.createPokedexItems(
                new JSONObject(dexesResponse.body()),
                currentDexUi,
                currentDexName,
                this
        );

        dexMenu.getItems().setAll(menuItems);
    }

    @Override
    public void loadPokedexList(ListProperty<Label> currentDexUi, JsonDexItem dexItem, StringProperty currentDexName) {
        try {
            DexRequestOptions dexRequestOptions = new DexRequestOptions(DexRequestType.DexList, dexItem.getApiPokedexUrl());
            var dexResponse = httpService.get(dexRequestOptions);

            if (dexResponse.statusCode() != 200) {
                throw new IOException(
                        dexResponse.statusCode() + ": " + dexResponse.body()
                                + " on GET " + dexRequestOptions.linkProperty()
                );
            }

            JSONObject dexInfo = new JSONObject(dexResponse.body());
            JSONArray dexEntries = dexInfo.getJSONArray("pokemon_entries");

            int pokemonDigitCount = countDigits(dexEntries.length());
            currentDexUi.clear();
            dexEntries.forEach((dexEntryJSON) -> {
                var dexEntryFromList = jsonParserService.parseDexItemIntoPokemon((JSONObject) dexEntryJSON);
                Label pokemonLabel = uiService.createPokedexListItem(pokemonDigitCount, dexEntryFromList);
                currentDexUi.add(pokemonLabel);
            });

            currentDexName.set("Pokedex: " + PokedexNameMap.get(dexItem.getApiPokedexName()));
        } catch (IOException | InterruptedException | URISyntaxException ex) {
            Alert errorAlert = uiService.createErrorAlert("Unable to open Pokedex", ex);
            errorAlert.showAndWait();
        }
    }

    private int countDigits(int n) {
        return String.valueOf(n).length();
    }

    @Override
    public void loadDefaultPokedex(ListProperty<Label> currentDexUi, StringProperty currentDexName)
            throws IOException, InterruptedException, URISyntaxException {
        var requestOptions = new DexRequestOptions(DexRequestType.DexList, DefaultPokedexUrl);
        var dexResponse = httpService.get(requestOptions);

        if (dexResponse != null) {
            JSONObject dexInfo = new JSONObject(dexResponse.body());
            JSONArray dexEntries = dexInfo.getJSONArray("pokemon_entries");

            currentDexUi.clear();
            int pokemonDigitCount = countDigits(dexEntries.length());
            dexEntries.forEach((dexEntryJSON) -> {
                var dexEntryFromList = jsonParserService.parseDexItemIntoPokemon((JSONObject) dexEntryJSON);
                Label pokemonLabel = uiService.createPokedexListItem(pokemonDigitCount, dexEntryFromList);
                currentDexUi.add(pokemonLabel);
            });

            currentDexName.set("Pokedex: " + PokedexNameMap.get(dexInfo.getString("name").toLowerCase()));
        }
    }

    @Override
    public void loadDexEntry(ListProperty<Region> dexEntriesProperty, String currentDexEntry)
            throws IOException, InterruptedException, URISyntaxException {
        var requestOptions = new DexRequestOptions(DexRequestType.DexEntry, currentDexEntry);
        var dexResponse = httpService.get(requestOptions);

        JSONObject dexEntry = new JSONObject(dexResponse.body());

        var dexEntryUI = uiService.createDexEntryUI(dexEntry, currentDexEntry);

        dexEntriesProperty.setAll(dexEntryUI);
    }
}
