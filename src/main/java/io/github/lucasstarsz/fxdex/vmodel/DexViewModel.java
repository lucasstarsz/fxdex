package io.github.lucasstarsz.fxdex.vmodel;

import java.io.IOException;
import java.net.URISyntaxException;

import com.google.inject.Inject;

import io.github.lucasstarsz.fxdex.service.DexService;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
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

    private final ListProperty<Label> dexProperty;
    private final DexService dexService;

    @Inject
    public DexViewModel(DexService dexService) {
        this.dexService = dexService;
        dexProperty = new SimpleListProperty<>(FXCollections.observableArrayList());
    }

    @FXML
    public void initialize() throws IOException, URISyntaxException, InterruptedException {
        dexProperty.addListener((c, o, n) -> dexContainer.getChildren().setAll(dexProperty.get()));

        dexService.loadPokedexesForMenu(dexProperty, dexMenu);
        dexService.loadDefaultPokedex(dexProperty);
    }
}
