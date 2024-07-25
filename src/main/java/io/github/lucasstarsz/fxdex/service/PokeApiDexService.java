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
import java.net.http.HttpRequest;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.text.WordUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.inject.Inject;

import io.github.lucasstarsz.fxdex.App;
import io.github.lucasstarsz.fxdex.misc.StyleClasses;
import javafx.beans.property.ListProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;

import static io.github.lucasstarsz.fxdex.misc.ApiConversionTables.PokedexNameMap;
import static io.github.lucasstarsz.fxdex.misc.ApiConversionTables.RomanNumeralMap;

public class PokeApiDexService implements DexService {

    public static final String IntroducedIn = "Introduced In: Generation ";
    public static final String PokedexEntries = "Pokedex Entries:";
    public static final String EggGroups = "Egg Groups:";

    private static final MenuItem NoDexesAvailable = new MenuItem("No Pokedexes loaded.");
    private static final Insets InfoInsets = new Insets(0, 0, 5, 0);

    private final HttpService httpService;

    @Inject
    public PokeApiDexService(HttpService httpService) {
        this.httpService = httpService;
    }

    @Override
    public void loadPokedexesForMenu(ListProperty<Label> currentDex, MenuButton dexMenu,
                                     StringProperty currentDexDisplayed)
            throws IOException, InterruptedException {
        HttpRequest dexesRequest = httpService.getDefaultDexRequest();
        var dexesResponse = httpService.getString(dexesRequest);

        if (dexesResponse == null) {
            dexMenu.getItems().add(NoDexesAvailable);
            return;
        }

        JSONObject allDexes = new JSONObject(dexesResponse.body());
        int dexCount = allDexes.getInt("count");

        for (int i = 0; i < dexCount; i++) {
            String pokedexName = allDexes.getJSONArray("results").getJSONObject(i).getString("name");
            pokedexName = PokedexNameMap.get(pokedexName.toLowerCase());
            MenuItem dexItem = new MenuItem(pokedexName);

            // PokeApi's pokedex list starts at index 1
            AtomicInteger dexIndex = new AtomicInteger(i + 1);

            // at index nine of the PokeAPI JSON, the dex number increases its offset by 1
            // https://pokeapi.co/api/v2/pokedex/?offset=0&limit=32
            if (i >= 9) {
                dexIndex.set(dexIndex.get() + 1);
            }

            dexItem.onActionProperty()
                    .set((event) -> this.loadPokedexList(currentDex, dexIndex.get(), currentDexDisplayed));

            dexMenu.getItems().add(dexItem);
        }
    }

    private void loadPokedexList(ListProperty<Label> currentDex, int dexIndex, StringProperty currentDexDisplayed) {
        try {
            var dexRequest = httpService.buildDexRequest(dexIndex);
            var dexResponse = httpService.getString(dexRequest);

            if (dexResponse.statusCode() != 200) {
                throw new IOException(dexResponse.statusCode() + ": " + dexResponse.body());
            }

            if (dexResponse != null) {
                JSONObject dexInfo = new JSONObject(dexResponse.body());
                JSONArray dexEntries = dexInfo.getJSONArray("pokemon_entries");

                currentDex.clear();
                int pokemonDigitCount = countDigits(dexEntries.length());
                dexEntries.forEach((entry) -> parseJSONIntoPokedex(pokemonDigitCount, currentDex, (JSONObject) entry));
                currentDexDisplayed.set("Pokedex: " + PokedexNameMap.get(dexInfo.getString("name").toLowerCase()));
            }
        } catch (IOException | InterruptedException | URISyntaxException ex) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.initModality(Modality.APPLICATION_MODAL);
            errorAlert.setResizable(true);
            errorAlert.setHeaderText("Unable to open Pokedex");
            errorAlert.setContentText(ex.getLocalizedMessage());
            errorAlert.showAndWait();
        }
    }

    private void parseJSONIntoPokedex(int pokemonDigitCount, ListProperty<Label> currentDex, JSONObject entry) {
        int dexNumber = entry.getInt("entry_number");
        JSONObject pokemon = entry.getJSONObject("pokemon_species");

        String apiPokemonName = pokemon.getString("name");
        String pokemonName = WordUtils.capitalize(apiPokemonName);

        // account for Porygon-Z, Tapu-Koko, Tapu-Lele, Tapu-Bulu, & Tapu-Fini
        pokemonName = pokemonName.replaceAll("-z", "-Z");
        pokemonName = pokemonName.replaceAll("-koko", "-Koko");
        pokemonName = pokemonName.replaceAll("-lele", "-Lele");
        pokemonName = pokemonName.replaceAll("-bulu", "-Bulu");
        pokemonName = pokemonName.replaceAll("-fini", "-Fini");

        int dexNumberDigitCount = countDigits(dexNumber);
        String dexNumberString = "0".repeat(pokemonDigitCount - dexNumberDigitCount) + dexNumber;

        Label pokemonLabel = new Label(dexNumberString + ": " + pokemonName);
        pokemonLabel.onMousePressedProperty().set((event) -> {
            App.PokedexEntry.set(apiPokemonName);
            App.CurrentScene.set("pokedexEntry.fxml");
        });

        currentDex.add(pokemonLabel);
    }

    private int countDigits(int n) {
        return String.valueOf(n).length();
    }

    @Override
    public void loadDefaultPokedex(ListProperty<Label> currentDex, StringProperty currentDexDisplayed)
            throws IOException, InterruptedException, URISyntaxException {
        var defaultDexRequest = httpService.buildDexRequest(2);
        var dexResponse = httpService.getString(defaultDexRequest);

        if (dexResponse != null) {
            JSONObject dexInfo = new JSONObject(dexResponse.body());
            JSONArray dexEntries = dexInfo.getJSONArray("pokemon_entries");

            currentDex.clear();
            int pokemonDigitCount = countDigits(dexEntries.length());
            dexEntries.forEach((entry) -> parseJSONIntoPokedex(pokemonDigitCount, currentDex, (JSONObject) entry));
            currentDexDisplayed.set("Pokedex: " + PokedexNameMap.get(dexInfo.getString("name").toLowerCase()));
        }
    }

    @Override
    public void loadDexEntry(ListProperty<Region> dexEntriesProperty, String currentDexEntry)
            throws IOException, InterruptedException, URISyntaxException {
        HttpRequest dexEntryRequest = httpService.buildDexEntryRequest(currentDexEntry);
        var dexResponse = httpService.getString(dexEntryRequest);

        if (dexResponse != null) {
            JSONObject dexEntry = new JSONObject(dexResponse.body());

            String genus = "Genus not found";
            JSONArray genuses = dexEntry.getJSONArray("genera");
            for (int i = 0; i < genuses.length(); i++) {
                JSONObject genusCandidate = genuses.getJSONObject(i);
                if (genusCandidate.getJSONObject("language").getString("name").equals("en")) {
                    genus = genusCandidate.getString("genus");
                    break;
                }
            }

            String introducedInString = dexEntry.getJSONObject("generation").getString("name");
            introducedInString = introducedInString.replaceAll("generation-", "");
            String generationIntroducedIn = IntroducedIn + RomanNumeralMap.get(introducedInString.toLowerCase());

            List<String> eggGroups = new ArrayList<>();
            JSONArray eggGroupJSON = dexEntry.getJSONArray("egg_groups");
            for (int i = 0; i < eggGroupJSON.length(); i++) {
                JSONObject eggGroup = eggGroupJSON.getJSONObject(i);

                String eggGroupString = eggGroup.getString("name");

                // account for Human-Like egg group
                eggGroupString = eggGroupString.replaceAll("shape", " like");
                eggGroupString = WordUtils.capitalize(eggGroupString);
                eggGroupString = eggGroupString.replaceAll(" ", "-");


                eggGroups.add(eggGroupString);
            }

            Map<String, String> flavorTexts = new LinkedHashMap<>();
            JSONArray flavorTextsJSON = dexEntry.getJSONArray("flavor_text_entries");

            for (int i = 0; i < flavorTextsJSON.length(); i++) {
                JSONObject flavorTextCandidate = flavorTextsJSON.getJSONObject(i);

                if (flavorTextCandidate.getJSONObject("language").getString("name").equals("en")) {
                    String flavorText = flavorTextCandidate.getString("flavor_text");
                    String textVersion = flavorTextCandidate.getJSONObject("version").getString("name");
                    flavorTexts.put(textVersion, flavorText);
                }
            }

            Label pokemonName = new Label(currentDexEntry);
            pokemonName.setId(StyleClasses.PokemonName);
            pokemonName.setText(pokemonName.getText().toUpperCase());

            Label pokemonGenus = new Label(genus);
            pokemonGenus.setId(StyleClasses.Subtitle);

            Label introduced = new Label(generationIntroducedIn);
            introduced.setId(StyleClasses.Subtitle);

            HBox eggGroupContainer = new HBox(5);
            Label pokemonEggGroups = new Label(EggGroups);
            pokemonEggGroups.setMinWidth(75);
            eggGroupContainer.getChildren().add(pokemonEggGroups);

            for (int i = 0; i < eggGroups.size(); i++) {
                String eggGroupString = eggGroups.get(i);
                if (i < eggGroups.size() - 1) {
                    eggGroupString += ",";
                }

                Label eggGroupLabel = new Label(eggGroupString);
                eggGroupLabel.setWrapText(false);
                eggGroupContainer.getChildren().add(eggGroupLabel);
            }

            Label pokemonFlavorTexts = new Label(PokedexEntries);
            pokemonFlavorTexts.setId(StyleClasses.Subtitle);

            VBox flavorTextsContainer = new VBox();
            List<HBox> flavorTextList = flavorTexts.entrySet().stream()
                    .map((entry) -> {
                        HBox container = new HBox(5);

                        String gameNameString = entry.getKey() + ":";
                        gameNameString = gameNameString.replaceAll("-", " ");
                        gameNameString = WordUtils.capitalize(gameNameString);

                        Label gameName = new Label(gameNameString);
                        gameName.setMinWidth(100);
                        gameName.setWrapText(false);
                        gameName.setAlignment(Pos.CENTER_RIGHT);

                        String flavorTextString = entry.getValue();
                        flavorTextString = flavorTextString.replaceAll("([\n\f])", " ");
                        flavorTextString = flavorTextString.replaceAll("- ", "-");

                        Label flavorText = new Label(flavorTextString);
                        flavorText.setWrapText(true);

                        container.getChildren().addAll(gameName, flavorText);
                        container.setMinWidth(container.getWidth());

                        return container;
                    }).toList();

            flavorTextList.forEach((l) -> {
                l.setId(StyleClasses.Subtext);
                VBox.setMargin(l, InfoInsets);
            });

            flavorTextsContainer.getChildren().addAll(flavorTextList);

            dexEntriesProperty.setAll(
                    pokemonName,
                    pokemonGenus,
                    introduced,
                    eggGroupContainer,
                    pokemonFlavorTexts,
                    flavorTextsContainer);
        }
    }
}
