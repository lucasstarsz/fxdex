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
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class DexEntryViewModel {

    @FXML
    public VBox pokemonInfoContainer;

    private final DexService dexService;
    private final StringProperty currentDexEntryNameProperty;
    private final ListProperty<Region> dexEntriesProperty;

    @Inject
    public DexEntryViewModel(DexService dexService) {
        this.dexService = dexService;

        currentDexEntryNameProperty = new SimpleStringProperty();
        currentDexEntryNameProperty.bind(App.PokedexEntry);

        dexEntriesProperty = new SimpleListProperty<>(FXCollections.observableArrayList());
    }

    @FXML
    public void initialize() throws IOException, InterruptedException, URISyntaxException {
        dexEntriesProperty.addListener((c, o, n) -> {
            for (Region node : dexEntriesProperty.get()) {
                node.prefWidthProperty().bind(pokemonInfoContainer.widthProperty());
            }

            pokemonInfoContainer.getChildren().setAll(dexEntriesProperty.get());
        });

        dexService.loadDexEntry(dexEntriesProperty, currentDexEntryNameProperty.get());
    }

    @FXML
    public void backToPokedex(ActionEvent actionEvent) {
        App.CurrentScene.set("main.fxml");
    }
}
