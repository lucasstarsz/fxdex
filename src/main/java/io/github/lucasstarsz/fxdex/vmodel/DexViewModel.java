package io.github.lucasstarsz.fxdex.vmodel;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static io.github.lucasstarsz.fxdex.App.DexThreadHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
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

        for (int i = 2; i <= 2; i++) {
            var r = HttpRequest.newBuilder(request, (n, v) -> true)
                    .uri(new URI(dexUrl + i + "/"))
                    .build();

            var response = client.send(r, HttpResponse.BodyHandlers.ofString());

            if (response != null) {
                System.out.println(response.body());
                System.out.println(dexContainer.getChildren());
                dexContainer.getChildren().add(new Label(response.body()));
                System.out.println(dexContainer.getChildren());
            }
        }
    }
}
