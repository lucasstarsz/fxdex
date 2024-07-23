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
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class DexEntryViewModel {

    @FXML
    public VBox pokemonInfoContainer;

    private final DexService dexService;

    private final StringProperty currentDexEntryNameProperty;

    private ObservableList<Node> currentDexEntries;
    private ListProperty<ObservableList<Node>> dexEntriesProperty;

    @Inject
    public DexEntryViewModel(DexService dexService) {
        this.dexService = dexService;
        this.currentDexEntryNameProperty = new SimpleStringProperty();
        currentDexEntryNameProperty.bind(App.PokedexEntry);

        this.currentDexEntries = FXCollections.observableArrayList();
        this.dexEntriesProperty = new SimpleListProperty<>();
    }

    @FXML
    public void initialize() throws IOException, InterruptedException, URISyntaxException {
        dexEntriesProperty.addListener((c, o, n) -> {
            for (Node node : currentDexEntries) {
                VBox.setVgrow(node, Priority.ALWAYS);
                HBox.setHgrow(node, Priority.ALWAYS);
            }
            pokemonInfoContainer.getChildren().setAll(currentDexEntries);
        });
        dexService.loadDexEntry(pokemonInfoContainer, currentDexEntryNameProperty.get());
    }

    @FXML
    public void backToPokedex(ActionEvent actionEvent) {
        App.CurrentScene.set("main.fxml");
    }
}
