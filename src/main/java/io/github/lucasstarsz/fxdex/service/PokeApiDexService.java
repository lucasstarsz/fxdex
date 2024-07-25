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
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.text.WordUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.inject.Inject;

import io.github.lucasstarsz.fxdex.App;
import io.github.lucasstarsz.fxdex.StyleClass;
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

public class PokeApiDexService implements DexService {

    public static final String IntroducedIn = "Introduced In: Generation ";
    public static final String PokedexEntries = "Pokedex Entries:";
    public static final String EggGroups = "Egg Groups:";

    private static final MenuItem NoDexesAvailable = new MenuItem("No Pokedexes loaded.");
    private static final Insets InfoInsets = new Insets(0, 0, 5, 0);

    private static final Map<String, Integer> RomanNumeralMap = Map.of(
            "i", 1,
            "ii", 2,
            "iii", 3,
            "iv", 4,
            "v", 5,
            "vi", 6,
            "vii", 7,
            "viii", 8,
            "ix", 9,
            "x", 10
    );

    private static final Map<String, String> PokedexNameMap = Map.ofEntries(
            Map.entry("national", "National Dex"),
            Map.entry("kanto", "Kanto (Red/Blue/Yellow/Green)"),
            Map.entry("original-johto", "Johto (Gold/Silver/Crystal)"),
            Map.entry("hoenn", "Hoenn (Ruby/Sapphire/Emerald)"),
            Map.entry("original-sinnoh", "Sinnoh (Diamond/Pearl)"),
            Map.entry("extended-sinnoh", "Sinnoh (Platinum)"),
            Map.entry("updated-johto", "Johto (HeartGold/SoulSilver/Crystal)"),
            Map.entry("original-unova", "Unova (Black/White)"),
            Map.entry("updated-unova", "Unova (Black2/White2)"),
            Map.entry("conquest-gallery", "Conquest (Pokemon Conquest)"),
            Map.entry("kalos-central", "Central Kalos (X/Y)"),
            Map.entry("kalos-coastal", "Coastal Kalos (X/Y)"),
            Map.entry("kalos-mountain", "Mountain Kalos (X/Y)"),
            Map.entry("updated-hoenn", "Hoenn (Omega Ruby/Alpha Sapphire)"),
            Map.entry("original-alola", "Alola (Sun/Moon)"),
            Map.entry("original-melemele", "Alola-Melemele (Sun/Moon)"),
            Map.entry("original-akala", "Alola-Akala (Sun/Moon)"),
            Map.entry("original-ulaula", "Alola-Ulaula (Sun/Moon)"),
            Map.entry("original-poni", "Alola-Poni (Sun/Moon)"),
            Map.entry("updated-alola", "Alola (Ultra Sun/Ultra Moon)"),
            Map.entry("updated-melemele", "Alola-Melemele (Ultra Sun/Ultra Moon)"),
            Map.entry("updated-akala", "Alola-Akala (Ultra Sun/Ultra Moon)"),
            Map.entry("updated-ulaula", "Alola-Ulaula (Ultra Sun/Ultra Moon)"),
            Map.entry("updated-poni", "Alola-Poni (Ultra Sun/Ultra Moon)"),
            Map.entry("letsgo-kanto", "Let's Go - Kanto (Let's go Pikachu/Eevee)"),
            Map.entry("galar", "Galar (Sword/Shield)"),
            Map.entry("isle-of-armor", "Galar - Isle of Armor (Sword/Shield DLC)"),
            Map.entry("crown-tundra", "Galar - Crown Tundra (Sword/Shield DLC)"),
            Map.entry("hisui", "Hisui (Legends: Arceus)"),
            Map.entry("paldea", "Paldea (Scarlet/Violet)"),
            Map.entry("kitakami", "Paldea - Kitakami (Scarlet/Violet DLC)"),
            Map.entry("blueberry", "Paldea - Blueberry (Scarlet/Violet DLC)")
    );

    private final HttpService httpService;

    @Inject
    public PokeApiDexService(HttpService httpService) {
        this.httpService = httpService;
    }

    @Override
    public void loadPokedexesForMenu(ListProperty<Label> currentDex, MenuButton dexMenu,
                                     StringProperty currentDexDisplayedProperty)
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
            AtomicInteger pokedexIndex = new AtomicInteger(i + 1);

            // at index nine of the PokeAPI JSON, the dex number increases its offset by 1
            // https://pokeapi.co/api/v2/pokedex/?offset=0&limit=32
            if (i >= 9) {
                pokedexIndex.set(pokedexIndex.get() + 1);
            }

            dexItem.onActionProperty()
                    .set((event) -> this.loadPokedexList(currentDex, pokedexIndex.get(), currentDexDisplayedProperty));

            dexMenu.getItems().add(dexItem);
        }
    }

    private void loadPokedexList(ListProperty<Label> currentDex, int pokedexIndex,
                                 StringProperty currentDexDisplayedProperty) {
        try {
            var dexRequest = httpService.buildDexRequest(pokedexIndex);
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
                currentDexDisplayedProperty.set("Pokedex: " + PokedexNameMap.get(dexInfo.getString("name").toLowerCase()));
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

    @Override
    public void parseJSONIntoPokedex(int pokemonDigitCount, ListProperty<Label> currentDex, JSONObject entry) {
        int pokedexNumber = entry.getInt("entry_number");
        JSONObject pokemon = entry.getJSONObject("pokemon_species");

        AtomicReference<String> pokemonName = new AtomicReference<>(WordUtils.capitalize(pokemon.getString("name")));

        // account for Porygon-Z, Tapu-Koko, Tapu-Lele, Tapu-Bulu, & Tapu-Fini
        pokemonName.set(pokemonName.get().replaceAll("-z", "-Z"));
        pokemonName.set(pokemonName.get().replaceAll("-koko", "-Koko"));
        pokemonName.set(pokemonName.get().replaceAll("-lele", "-Lele"));
        pokemonName.set(pokemonName.get().replaceAll("-bulu", "-Bulu"));
        pokemonName.set(pokemonName.get().replaceAll("-fini", "-Fini"));

        int pokedexNumberDigitCount = countDigits(pokedexNumber);
        String pokedexNumberString = "0".repeat(pokemonDigitCount - pokedexNumberDigitCount) + pokedexNumber;

        Label pokemonLabel = new Label(pokedexNumberString + ": " + pokemonName);
        pokemonLabel.onMousePressedProperty().set((event) -> {
            App.PokedexEntry.set(pokemonName.get());
            App.CurrentScene.set("pokedexEntry.fxml");
        });

        currentDex.add(pokemonLabel);
    }

    private int countDigits(int n) {
        return String.valueOf(n).length();
    }

    @Override
    public void loadDefaultPokedex(ListProperty<Label> currentDex, StringProperty currentDexDisplayedProperty)
            throws IOException, InterruptedException, URISyntaxException {
        var defaultDexRequest = httpService.buildDexRequest(2);
        var dexResponse = httpService.getString(defaultDexRequest);

        if (dexResponse != null) {
            JSONObject dexInfo = new JSONObject(dexResponse.body());
            JSONArray dexEntries = dexInfo.getJSONArray("pokemon_entries");

            currentDex.clear();
            int pokemonDigitCount = countDigits(dexEntries.length());
            dexEntries.forEach((entry) -> parseJSONIntoPokedex(pokemonDigitCount, currentDex, (JSONObject) entry));
            currentDexDisplayedProperty.set("Pokedex: " + PokedexNameMap.get(dexInfo.getString("name").toLowerCase()));
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
            pokemonName.setId(StyleClass.PokemonName);
            pokemonName.setText(pokemonName.getText().toUpperCase());

            Label pokemonGenus = new Label(genus);
            pokemonGenus.setId(StyleClass.Subtitle);

            Label introduced = new Label(generationIntroducedIn);
            introduced.setId(StyleClass.Subtitle);

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
            pokemonFlavorTexts.setId(StyleClass.Subtitle);

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
                l.setId(StyleClass.Subtext);
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
