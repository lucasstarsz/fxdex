package io.github.lucasstarsz.fxdex.service;

import java.io.IOException;
import java.net.URISyntaxException;

import org.json.JSONObject;

import javafx.beans.property.ListProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.Region;

public interface DexService {

    void loadPokedexesForMenu(ListProperty<Label> currentDex, MenuButton dexMenu, StringProperty currentDexDisplayedProperty)
            throws URISyntaxException, IOException, InterruptedException;

    void parseJSONIntoPokedex(ListProperty<Label> currentDex, JSONObject entry);

    void loadDefaultPokedex(ListProperty<Label> currentDex, StringProperty currentDexDisplayedProperty)
            throws IOException, InterruptedException, URISyntaxException;

    public void loadDexEntry(ListProperty<Region> dexEntriesProperty, String currentDexEntry)
            throws IOException, InterruptedException, URISyntaxException;
}
