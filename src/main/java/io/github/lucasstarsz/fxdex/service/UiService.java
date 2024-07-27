package io.github.lucasstarsz.fxdex.service;

import io.github.lucasstarsz.fxdex.App;
import io.github.lucasstarsz.fxdex.model.JsonDexListItem;
import javafx.beans.property.ListProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import org.apache.commons.text.WordUtils;
import org.json.JSONObject;

import java.util.List;

public interface UiService {

    String PokedexEntries = "Pokedex Entries:";
    String EggGroups = "Egg Groups:";
    Insets InfoInsets = new Insets(0, 0, 5, 0);
    MenuItem NoDexesAvailable = new MenuItem("No Pokedexes loaded.");

    List<MenuItem> createPokedexItems(JSONObject allDexes, ListProperty<Label> currentDexUi,
                                      StringProperty currentDexName, DexService dexService);

    default Alert createErrorAlert(String customErrorMessage, Exception e) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.initModality(Modality.APPLICATION_MODAL);
        errorAlert.setResizable(true);
        errorAlert.setHeaderText(customErrorMessage);
        errorAlert.setContentText(e.getLocalizedMessage());

        return errorAlert;
    }

    default Label createPokedexListItem(int pokemonDigitCount, JsonDexListItem dexEntryFromList) {
        String pokemonName = WordUtils.capitalize(dexEntryFromList.getApiPokemonName());

        // account for Porygon-Z, Tapu-Koko, Tapu-Lele, Tapu-Bulu, & Tapu-Fini
        pokemonName = pokemonName.replaceAll("-z", "-Z");
        pokemonName = pokemonName.replaceAll("-koko", "-Koko");
        pokemonName = pokemonName.replaceAll("-lele", "-Lele");
        pokemonName = pokemonName.replaceAll("-bulu", "-Bulu");
        pokemonName = pokemonName.replaceAll("-fini", "-Fini");

        int dexNumberDigitCount = countDigits(dexEntryFromList.getDexNumber());
        String dexNumberString = "0".repeat(pokemonDigitCount - dexNumberDigitCount) + dexEntryFromList.getDexNumber();

        Label pokemonLabel = new Label(dexNumberString + ": " + pokemonName);
        pokemonLabel.onMousePressedProperty().set((event) -> {
            App.PokedexEntry.set(dexEntryFromList.getApiPokemonName());
            App.CurrentScene.set("pokedexEntry.fxml");
        });

        return pokemonLabel;
    }

    default int countDigits(int n) {
        return String.valueOf(n).length();
    }

    List<Region> createDexEntryUI(JSONObject dexEntryJSON, String currentDexEntry);
}
