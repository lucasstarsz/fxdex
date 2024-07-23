package io.github.lucasstarsz.fxdex.service;

import java.io.IOException;
import java.net.URISyntaxException;

import org.json.JSONObject;

import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.layout.VBox;

public interface DexService {

    void loadPokedexesForMenu(ObservableList<Label> currentDex, Menu dexMenu) throws URISyntaxException, IOException, InterruptedException;

    void parseJSONIntoPokedex(ObservableList<Label> currentDex, JSONObject entry);

    void loadDefaultPokedex(ObservableList<Label> currentDex, Menu dexMenu) throws IOException, InterruptedException, URISyntaxException;

    public void loadDexEntry(VBox pokemonInfoContainer, String currentDexEntry) throws IOException, InterruptedException, URISyntaxException;
}
