package io.github.lucasstarsz.fxdex.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.inject.Inject;

import io.github.lucasstarsz.fxdex.App;
import io.github.lucasstarsz.fxdex.StyleClass;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class PokeApiDexService implements DexService {

    public static final String IntroducedIn = "Introduced in: ";
    public static final String Habitat = "Lives in: ";
    public static final String EggGroup = "Egg groups: ";
    private static final Insets InfoInsets = new Insets(0, 0, 5, 0);

    private final HttpService httpService;

    @Inject
    public PokeApiDexService(HttpService httpService) {
        this.httpService = httpService;
    }

    @Override
    public void loadPokedexesForMenu(ListProperty<Label> currentDex, Menu dexMenu)
            throws URISyntaxException, IOException, InterruptedException {
        HttpRequest dexesRequest = httpService.getDefaultDexRequest();

        var dexesResponse = httpService.getString(dexesRequest);
        if (dexesResponse == null) {
            MenuItem noDexesAvailable = new MenuItem("No Pokedexes loaded.");
            dexMenu.getItems().add(noDexesAvailable);

            return;
        }

        JSONObject allDexes = new JSONObject(dexesResponse.body());
        int dexCount = allDexes.getInt("count");

        for (int i = 1; i <= dexCount; i++) {
            var dexRequest = httpService.buildDexRequest(i);

            String pokedexName = allDexes.getJSONArray("results").getJSONObject(i - 1).getString("name");
            MenuItem dexItem = new MenuItem(pokedexName);

            dexItem.onActionProperty().set((event) -> {
                try {
                    var dexResponse = httpService.getString(dexRequest);
                    if (dexResponse != null) {
                        JSONObject dexes = new JSONObject(dexResponse.body());
                        JSONArray dexEntries = dexes.getJSONArray("pokemon_entries");

                        currentDex.clear();
                        dexEntries.forEach((entry) -> parseJSONIntoPokedex(currentDex, (JSONObject) entry));
                    }
                } catch (IOException | InterruptedException ex) {
                    Platform.exit();
                }
            });

            dexMenu.getItems().add(dexItem);
        }
    }

    @Override
    public void parseJSONIntoPokedex(ListProperty<Label> currentDex, JSONObject entry) {
        int pokedexNumber = entry.getInt("entry_number");
        JSONObject pokemon = entry.getJSONObject("pokemon_species");
        String pokemonName = pokemon.getString("name");

        Label pokemonLabel = new Label(pokedexNumber + ": " + pokemonName);
        pokemonLabel.onMousePressedProperty().set((event) -> {
            App.PokedexEntry.set(pokemonName);
            App.CurrentScene.set("pokedexEntry.fxml");
        });

        currentDex.add(pokemonLabel);
    }

    @Override
    public void loadDefaultPokedex(ListProperty<Label> currentDex, Menu dexMenu)
            throws IOException, InterruptedException, URISyntaxException {
        var defaultDex = httpService.buildDexRequest(2);
        var response = httpService.getString(defaultDex);

        if (response != null) {
            JSONObject dexes = new JSONObject(response.body());
            JSONArray dexEntries = dexes.getJSONArray("pokemon_entries");
            currentDex.clear();
            dexEntries.forEach((entry) -> parseJSONIntoPokedex(currentDex, (JSONObject) entry));
        }
    }

    @Override
    public void loadDexEntry(ListProperty<Region> dexEntriesProperty, String currentDexEntry)
            throws IOException, InterruptedException, URISyntaxException {
        HttpRequest dexEntryRequest = httpService.buildDexEntryRequest(currentDexEntry);
        var response = httpService.getString(dexEntryRequest);

        if (response != null) {
            JSONObject dexEntry = new JSONObject(response.body());

            String genus = "Genus not found";
            JSONArray genuses = dexEntry.getJSONArray("genera");
            for (int i = 0; i < genuses.length(); i++) {
                JSONObject genusCandidate = genuses.getJSONObject(i);
                if (genusCandidate.getJSONObject("language").getString("name").equals("en")) {
                    genus = genusCandidate.getString("genus");
                    break;
                }
            }

            String generationIntroducedIn = IntroducedIn + dexEntry.getJSONObject("generation").getString("name");

            List<String> eggGroups = new ArrayList<>();
            JSONArray eggGroupJSON = dexEntry.getJSONArray("egg_groups");
            for (int i = 0; i < eggGroupJSON.length(); i++) {
                JSONObject eggGroup = eggGroupJSON.getJSONObject(i);
                eggGroups.add(eggGroup.getString("name"));
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

            Label pokemonEggGroups = new Label("Egg groups: " + String.join(", ", eggGroups));

            Label pokemonFlavorTexts = new Label("Pokedex Entries:");
            pokemonFlavorTexts.setId(StyleClass.Subtitle);

            VBox flavorTextsContainer = new VBox();
            List<HBox> flavorTextList = flavorTexts.entrySet().stream()
                    .map((entry) -> {
                        HBox container = new HBox(5);
                        Label gameName = new Label(entry.getKey() + ": ");
                        Label flavorText = new Label(entry.getValue().replaceAll("(\n|\u000c)", " "));

                        gameName.setMinWidth(100);
                        gameName.setWrapText(false);
                        gameName.setAlignment(Pos.CENTER_RIGHT);
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
                    pokemonEggGroups,
                    pokemonFlavorTexts,
                    flavorTextsContainer);
        }
    }
}
