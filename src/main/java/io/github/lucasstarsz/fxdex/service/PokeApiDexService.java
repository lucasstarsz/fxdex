package io.github.lucasstarsz.fxdex.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.inject.Inject;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

public class PokeApiDexService implements DexService {

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

        Label pokemonLabel = new Label(pokedexNumber + ": " + pokemon.getString("name"));
        pokemonLabel.onMousePressedProperty().set((event) -> System.out.println("Send to pokedex " + pokedexNumber));

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
}
