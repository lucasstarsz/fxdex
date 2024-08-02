package io.github.lucasstarsz.fxdex.service;

import com.google.inject.Inject;
import io.github.lucasstarsz.fxdex.App;
import io.github.lucasstarsz.fxdex.misc.StyleClasses;
import io.github.lucasstarsz.fxdex.model.JsonDexEntryItem;
import io.github.lucasstarsz.fxdex.model.JsonDexItem;
import io.github.lucasstarsz.fxdex.model.JsonDexListItem;
import io.github.lucasstarsz.fxdex.persistence.DexInfoHandler;
import javafx.beans.property.ListProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.apache.commons.text.WordUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static io.github.lucasstarsz.fxdex.misc.ApiConversionTables.DexNameMap;

public class DefaultUIService implements UiService {

    private final JsonParserService jsonParserService;
    private final DexInfoHandler dexInfoHandler;

    @Inject
    public DefaultUIService(JsonParserService jsonParserService, DexInfoHandler dexInfoHandler) {
        this.jsonParserService = jsonParserService;
        this.dexInfoHandler = dexInfoHandler;
    }

    @Override
    public List<MenuItem> createDexItems(JSONObject dexListJSON, ListProperty<Label> currentDexUi,
                                         StringProperty currentDexName, DexService dexService) {
        var jsonDexItems = jsonParserService.parseDexItems(dexListJSON);
        return createDexItems(jsonDexItems, currentDexUi, currentDexName, dexService);
    }

    @Override
    public List<MenuItem> createDexItems(List<JsonDexItem> jsonDexItems, ListProperty<Label> currentDexUi,
                                         StringProperty currentDexName, DexService dexService) {
        dexInfoHandler.saveDexMenuList(jsonDexItems);

        List<MenuItem> itemUiList = new ArrayList<>();
        for (JsonDexItem item : jsonDexItems) {
            String apiMonName = item.getApiDexName();
            String uiDexName = Objects.requireNonNullElse(item.getUiName(), DexNameMap.get(apiMonName));

            MenuItem dexItem = new MenuItem(uiDexName);
            dexItem.setOnAction((event) -> dexService.loadDexList(
                    currentDexUi,
                    item,
                    currentDexName
            ));

            itemUiList.add(dexItem);
        }
        return itemUiList;
    }

    @Override
    public Label createDexListItem(int pokemonDigitCount, JsonDexListItem dexEntryFromList) {
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
            App.CurrentDexEntry.set(dexEntryFromList.getApiPokemonName());
            App.CurrentDexNumber.set(dexInfoHandler.loadPokemonNumber(dexEntryFromList.getApiPokemonName()));
            App.CurrentScene.set("pokedexEntry.fxml");
        });

        return pokemonLabel;
    }

    @Override
    public List<Region> createDexEntryUI(JsonDexEntryItem dexEntryItem, String currentDexEntry) {
        Label pokemonName = new Label(currentDexEntry);
        pokemonName.setId(StyleClasses.PokemonName);
        pokemonName.setText(pokemonName.getText().toUpperCase());

        Label pokemonGenus = new Label(dexEntryItem.getGenus());
        pokemonGenus.setId(StyleClasses.Subtitle);

        Label introduced = new Label(dexEntryItem.getGeneration());
        introduced.setId(StyleClasses.Subtitle);

        HBox eggGroupContainer = new HBox(5);
        Label pokemonEggGroups = new Label(EggGroups);
        pokemonEggGroups.setMinWidth(75);
        eggGroupContainer.getChildren().add(pokemonEggGroups);

        var eggGroups = dexEntryItem.getEggGroups();
        for (int i = 0; i < eggGroups.size(); i++) {
            String eggGroupString = eggGroups.get(i);
            if (i < eggGroups.size() - 1) {
                eggGroupString += ",";
            }

            Label eggGroupLabel = new Label(eggGroupString);
            eggGroupLabel.setWrapText(false);
            eggGroupContainer.getChildren().add(eggGroupLabel);
        }

        Label pokemonFlavorTexts = new Label(DexEntries);
        pokemonFlavorTexts.setId(StyleClasses.Subtitle);

        var flavorTexts = dexEntryItem.getFlavorTexts();
        VBox flavorTextsContainer = new VBox();
        List<HBox> flavorTextList = new ArrayList<>();

        for (var flavorTextEntry : flavorTexts.entrySet()) {
            HBox container = new HBox(5);

            String gameNameString = flavorTextEntry.getKey() + ":";
            gameNameString = gameNameString.replaceAll("-", " ");
            gameNameString = WordUtils.capitalize(gameNameString);

            Label gameName = new Label(gameNameString);
            gameName.setMinWidth(100);
            gameName.setWrapText(false);
            gameName.setAlignment(Pos.CENTER_RIGHT);

            String flavorTextString = flavorTextEntry.getValue();
            flavorTextString = flavorTextString.replaceAll("([\n\f])", " ");
            flavorTextString = flavorTextString.replaceAll("- ", "-");

            Label flavorText = new Label(flavorTextString);
            flavorText.setWrapText(true);

            container.getChildren().addAll(gameName, flavorText);
            container.setMinWidth(container.getWidth());
            container.setId(StyleClasses.Subtext);
            VBox.setMargin(container, InfoInsets);

            flavorTextList.add(container);
        }

        flavorTextsContainer.getChildren().addAll(flavorTextList);

        return List.of(
                pokemonName,
                pokemonGenus,
                introduced,
                eggGroupContainer,
                pokemonFlavorTexts,
                flavorTextsContainer
        );
    }
}
