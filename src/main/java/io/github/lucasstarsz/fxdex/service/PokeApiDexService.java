package io.github.lucasstarsz.fxdex.service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.inject.Inject;

import static io.github.lucasstarsz.fxdex.ApiLinks.DexUrl;
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
        HttpClient client = httpService.getClient();
        HttpRequest dexesRequest = httpService.getDefaultDexRequest();

        var dexesResponse = client.send(dexesRequest, HttpResponse.BodyHandlers.ofString());

        if (dexesResponse == null) {
            MenuItem noDexesAvailable = new MenuItem("No Pokedexes loaded.");
            dexMenu.getItems().add(noDexesAvailable);

            return;
        }

        JSONObject allDexes = new JSONObject(dexesResponse.body());
        int dexCount = allDexes.getInt("count");

        for (int i = 1; i <= dexCount; i++) {
            var dexRequest = httpService.buildDexRequest(i);

            MenuItem dexItem = new MenuItem(allDexes.getJSONArray("results").getJSONObject(i - 1).getString("name"));
            dexItem.onActionProperty().set((event) -> {
                try {
                    var dexResponse = client.send(dexRequest, HttpResponse.BodyHandlers.ofString());
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

        Label pokemonButton = new Label(pokedexNumber + ": " + pokemon.getString("name"));
        pokemonButton.onMousePressedProperty().set((event) -> System.out.println("Send to pokedex " + pokedexNumber));

        currentDex.add(pokemonButton);
    }

    @Override
    public void loadDefaultPokedex(ObservableList<Label> currentDex, Menu dexMenu) throws IOException, InterruptedException, URISyntaxException {
        HttpClient client = httpService.getClient();
        HttpRequest dexesRequest = httpService.getDefaultDexRequest();

        var defaultDex = HttpRequest.newBuilder(dexesRequest, (n, v) -> true)
                .uri(new URI(DexUrl + 2 + "/"))
                .build();

        var response = client.send(defaultDex, HttpResponse.BodyHandlers.ofString());
        if (response != null) {
            JSONObject dexes = new JSONObject(response.body());
            JSONArray dexEntries = dexes.getJSONArray("pokemon_entries");
            currentDex.clear();
            dexEntries.forEach((entry) -> parseJSONIntoPokedex(currentDex, (JSONObject) entry));
        }
    }
}
