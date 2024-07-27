package io.github.lucasstarsz.fxdex.service;

import com.google.inject.Inject;
import io.github.lucasstarsz.fxdex.misc.StyleClasses;
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

import static io.github.lucasstarsz.fxdex.misc.ApiConversionTables.PokedexNameMap;

public class DefaultUIService implements UiService {

    private final JsonParserService jsonParserService;

    @Inject
    public DefaultUIService(JsonParserService jsonParserService) {
        this.jsonParserService = jsonParserService;
    }

    @Override
    public List<MenuItem> createPokedexItems(JSONObject allDexes, ListProperty<Label> currentDexUi,
                                             StringProperty currentDexName, DexService dexService) {
        return jsonParserService.parsePokedexItems(allDexes)
                .stream()
                .map((jsonDexItem) -> {
                    String apiPokedexName = jsonDexItem.getApiPokedexName();
                    String uiPokedexName = PokedexNameMap.get(apiPokedexName);

                    MenuItem dexItem = new MenuItem(uiPokedexName);
                    dexItem.setOnAction((event) -> dexService.loadPokedexList(
                            currentDexUi,
                            jsonDexItem,
                            currentDexName
                    ));
                    return dexItem;
                }).toList();
    }

    @Override
    public List<Region> createDexEntryUI(JSONObject dexEntryJSON, String currentDexEntry) {
        var dexEntryItem = jsonParserService.getDexEntryItem(dexEntryJSON);

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

        Label pokemonFlavorTexts = new Label(PokedexEntries);
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
