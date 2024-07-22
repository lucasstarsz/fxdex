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
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class DexViewModel {

    @FXML
    private VBox dexContainer;

    @FXML
    public void initialize() throws IOException, URISyntaxException, InterruptedException {
        HttpClient client = HttpClient.newBuilder()
                .executor(DexThreadHandler)
                .build();
        System.out.println("test!");

        String dexUrl = "https://pokeapi.co/api/v2/pokedex/";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(dexUrl))
                .GET()
                .build();

        var r = HttpRequest.newBuilder(request, (n, v) -> true)
                .uri(new URI(dexUrl + 2 + "/"))
                .build();

        var response = client.send(r, HttpResponse.BodyHandlers.ofString());
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
