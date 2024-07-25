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

import io.github.lucasstarsz.fxdex.service.DexService;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.VBox;

public class DexViewModel {

    @FXML
    private VBox dexContainer;

    @FXML
    private MenuButton dexMenu;

    @FXML
    private Label currentDexDisplayed;

    private final StringProperty currentDexDisplayedProperty;
    private final ListProperty<Label> dexProperty;
    private final DexService dexService;

    @Inject
    public DexViewModel(DexService dexService) {
        this.dexService = dexService;
        currentDexDisplayedProperty = new SimpleStringProperty();
        dexProperty = new SimpleListProperty<>(FXCollections.observableArrayList());
    }

    @FXML
    public void initialize() throws IOException, URISyntaxException, InterruptedException {
        dexProperty.addListener((c, o, n) -> dexContainer.getChildren().setAll(dexProperty.get()));

        currentDexDisplayedProperty.addListener((c, o, n) -> currentDexDisplayed.setText(n));

        dexService.loadPokedexesForMenu(dexProperty, dexMenu, currentDexDisplayedProperty);
        dexService.loadDefaultPokedex(dexProperty, currentDexDisplayedProperty);
    }
}
