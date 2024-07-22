package io.github.lucasstarsz.fxdex.vmodel;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import static io.github.lucasstarsz.fxdex.App.DexThreadHandler;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;

public class DexViewModel {

    @FXML
    private VBox dexContainer;

    @FXML
    private Menu dexMenu;

    @FXML
    public void initialize() throws IOException, URISyntaxException, InterruptedException {
        HttpClient client = HttpClient.newBuilder()
                .executor(DexThreadHandler)
                .build();
        System.out.println("test!");

        String dexUrl = "https://pokeapi.co/api/v2/pokedex/";
        String allDexesUrl = "https://pokeapi.co/api/v2/pokedex/?offset=0&limit=32";

        HttpRequest dexesRequest = HttpRequest.newBuilder()
                .uri(new URI(allDexesUrl))
                .GET()
                .build();

        var dexesResponse = client.send(dexesRequest, HttpResponse.BodyHandlers.ofString());
        if (dexesResponse != null) {
            JSONObject allDexes = new JSONObject(dexesResponse.body());

            int dexCount = allDexes.getInt("count");

            for (int i = 1; i <= dexCount; i++) {
                var dexRequest = HttpRequest.newBuilder(dexesRequest, (n, v) -> true)
                        .uri(new URI(dexUrl + i + "/"))
                        .build();
                MenuItem dexItem = new MenuItem(allDexes.getJSONArray("results").getJSONObject(i - 1).getString("name"));

                dexItem.onActionProperty().set((event) -> {
                    try {
                        var dexResponse = client.send(dexRequest, HttpResponse.BodyHandlers.ofString());
                        if (dexResponse != null) {
                            System.out.println(dexResponse.body());

                            JSONObject dexes = new JSONObject(dexResponse.body());
                            JSONArray dexEntries = dexes.getJSONArray("pokemon_entries");
                            dexContainer.getChildren().clear();
                            dexEntries.forEach((entry) -> {
                                System.out.println(entry.toString());
                                int pokedexNumber = ((JSONObject) entry).getInt("entry_number");
                                JSONObject pokemon = ((JSONObject) entry).getJSONObject("pokemon_species");
                                Label pokemonButton = new Label(pokedexNumber + ": " + pokemon.getString("name"));
                                pokemonButton.onMousePressedProperty().set((e) -> System.out.println("Send to pokedex " + pokedexNumber));

                                dexContainer.getChildren().add(pokemonButton);
                            });
                        }
                    } catch (IOException | InterruptedException ex) {
                        ex.printStackTrace();
                        Platform.exit();
                    }
                });

                dexMenu.getItems().add(dexItem);
            }
        }

        for (int i = 0; i < 10; i++) {

        }

        var defaultDex = HttpRequest.newBuilder(dexesRequest, (n, v) -> true)
                .uri(new URI(dexUrl + 2 + "/"))
                .build();

        var response = client.send(defaultDex, HttpResponse.BodyHandlers.ofString());
        if (response != null) {
            System.out.println(response.body());

            JSONObject dexes = new JSONObject(response.body());

            JSONArray dexEntries = dexes.getJSONArray("pokemon_entries");
            dexEntries.forEach((entry) -> {
                System.out.println(entry.toString());
                int pokedexNumber = ((JSONObject) entry).getInt("entry_number");
                JSONObject pokemon = ((JSONObject) entry).getJSONObject("pokemon_species");
                Button pokemonButton = new Button(pokedexNumber + ": " + pokemon.getString("name"));
                pokemonButton.onMousePressedProperty().set((event) -> System.out.println("Send to pokedex " + pokedexNumber));

                dexContainer.getChildren().add(pokemonButton);
            });
        }
    }
}
