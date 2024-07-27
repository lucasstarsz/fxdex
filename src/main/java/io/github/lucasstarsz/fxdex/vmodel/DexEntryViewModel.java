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

package io.github.lucasstarsz.fxdex.vmodel;

import java.io.IOException;
import java.net.URISyntaxException;

import com.google.inject.Inject;

import io.github.lucasstarsz.fxdex.App;
import io.github.lucasstarsz.fxdex.service.DexService;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class DexEntryViewModel {

    @FXML
    public VBox pokemonInfoContainer;

    private final DexService dexService;
    private final StringProperty currentDexEntryName;
    private final ListProperty<Region> dexEntriesList;

    @Inject
    public DexEntryViewModel(DexService dexService) {
        this.dexService = dexService;

        currentDexEntryName = new SimpleStringProperty();
        currentDexEntryName.bind(App.CurrentDexEntry);

        dexEntriesList = new SimpleListProperty<>(FXCollections.observableArrayList());
    }

    @FXML
    public void initialize() throws IOException, InterruptedException, URISyntaxException {
        dexEntriesList.addListener((c, o, n) -> prepDexEntriesOnChange());
        dexService.loadDexEntry(dexEntriesList, currentDexEntryName.get());
    }

    private void prepDexEntriesOnChange() {
        for (Region node : dexEntriesList.get()) {
            node.prefWidthProperty().bind(pokemonInfoContainer.widthProperty());
        }

        pokemonInfoContainer.getChildren().setAll(dexEntriesList.get());
    }

    @FXML
    public void backToDexView() {
        App.CurrentScene.set("main.fxml");
    }
}
