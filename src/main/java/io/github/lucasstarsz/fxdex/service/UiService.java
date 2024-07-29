package io.github.lucasstarsz.fxdex.service;

import io.github.lucasstarsz.fxdex.model.JsonDexEntryItem;
import io.github.lucasstarsz.fxdex.model.JsonDexListItem;
import javafx.beans.property.ListProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import org.json.JSONObject;

import java.util.List;

public interface UiService {

    String DexEntries = "Pok\u00e9dex Entries:";
    String EggGroups = "Egg Groups:";
    Insets InfoInsets = new Insets(0, 0, 5, 0);
    MenuItem NoDexesAvailable = new MenuItem("No Pokédex is loaded.");

    List<MenuItem> createDexItems(JSONObject dexListJSON, ListProperty<Label> currentDexUi,
                                  StringProperty currentDexName, DexService dexService);

    Label createDexListItem(int pokemonDigitCount, JsonDexListItem dexEntryFromList);

    default int countDigits(int n) {
        return String.valueOf(n).length();
    }

    List<Region> createDexEntryUI(JsonDexEntryItem dexEntryJSON, String currentDexEntry);

    static Alert createErrorAlert(String customErrorMessage, Exception e) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.initModality(Modality.APPLICATION_MODAL);
        errorAlert.setResizable(true);
        errorAlert.setHeaderText(customErrorMessage);
        errorAlert.setContentText(e.getLocalizedMessage());

        return errorAlert;
    }
}
