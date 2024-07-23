package io.github.lucasstarsz.fxdex.vmodel;

import java.io.IOException;
import java.net.URISyntaxException;

import com.google.inject.Inject;

import io.github.lucasstarsz.fxdex.service.DexService;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.layout.VBox;

public class DexViewModel {

    @FXML
    private VBox dexContainer;

    @FXML
    private Menu dexMenu;

    private ObservableList<Label> currentDex;
    private ListProperty dexProperty;

    private final DexService dexService;

    @Inject
    public DexViewModel(DexService dexService) {
        this.dexService = dexService;
    }

    @FXML
    public void initialize() throws IOException, URISyntaxException, InterruptedException {
        currentDex = FXCollections.observableArrayList();
        dexProperty = new SimpleListProperty(currentDex);
        dexProperty.addListener((change, o, n) -> {
            dexContainer.getChildren().setAll(currentDex);
        });

        dexService.loadPokedexesForMenu(currentDex, dexMenu);
        dexService.loadDefaultPokedex(currentDex, dexMenu);
    }
}
