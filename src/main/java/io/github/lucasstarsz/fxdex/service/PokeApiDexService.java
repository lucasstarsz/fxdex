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
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;

public class PokeApiDexService implements DexService {

    public static final String IntroducedIn = "Introduced in: ";
    public static final String Habitat = "Lives in: ";
    public static final String EggGroup = "Egg groups: ";

    private final HttpService httpService;

    @Inject
    public PokeApiDexService(HttpService httpService) {
        this.httpService = httpService;
    }

    @Override
    public void loadPokedexesForMenu(ObservableList<Label> currentDex, Menu dexMenu) throws URISyntaxException, IOException, InterruptedException {
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
    public void parseJSONIntoPokedex(ObservableList<Label> currentDex, JSONObject entry) {
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
    public void loadDefaultPokedex(ObservableList<Label> currentDex, Menu dexMenu) throws IOException, InterruptedException, URISyntaxException {
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
    public void loadDexEntry(VBox pokemonInfoContainer, String currentDexEntry) throws IOException, InterruptedException, URISyntaxException {
        HttpRequest dexEntryRequest = httpService.buildDexEntryRequest(currentDexEntry);
        var response = httpService.getString(dexEntryRequest);

        if (response != null) {
            JSONObject dexEntry = new JSONObject(response.body());

            // pokemon genus
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
            String habitat = Habitat + dexEntry.getJSONObject("habitat").getString("name");

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
            Label pokemonGenus = new Label(genus);
            Label introduced = new Label(generationIntroducedIn);
            Label pokemonHabitat = new Label(habitat);
            Label pokemonEggGroups = new Label("Egg groups: " + String.join(", ", eggGroups));
            Label pokemonFlavorTexts = new Label("Pokedex Entries:");

            VBox flavorTextsContainer = new VBox();
            List<Label> flavorTextList = flavorTexts.entrySet().stream()
                    .map((entry) -> entry.getKey() + ": " + entry.getValue().replaceAll("\n", " ").replaceAll("\u000c", " "))
                    .map((text) -> new Label(text))
                    .toList();

            flavorTextList.forEach((l) -> l.setWrapText(true));
            flavorTextsContainer.getChildren().addAll(flavorTextList);

            pokemonInfoContainer.getChildren().addAll(
                    pokemonName,
                    pokemonGenus,
                    introduced,
                    pokemonHabitat,
                    pokemonEggGroups,
                    pokemonFlavorTexts,
                    flavorTextsContainer
            );
        }
    }
}
