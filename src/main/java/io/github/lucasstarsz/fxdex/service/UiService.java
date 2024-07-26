package io.github.lucasstarsz.fxdex.service;

import io.github.lucasstarsz.fxdex.App;
import io.github.lucasstarsz.fxdex.misc.StyleClasses;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import org.apache.commons.text.WordUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static io.github.lucasstarsz.fxdex.misc.ApiConversionTables.PokedexNameMap;
import static io.github.lucasstarsz.fxdex.misc.ApiConversionTables.RomanNumeralMap;

public interface UiService {

    String IntroducedIn = "Introduced In: Generation ";
    String PokedexEntries = "Pokedex Entries:";
    String EggGroups = "Egg Groups:";
    Insets InfoInsets = new Insets(0, 0, 5, 0);
    MenuItem NoDexesAvailable = new MenuItem("No Pokedexes loaded.");

    default List<MenuItem> createPokedexItems(JSONObject allDexes) {
        int dexCount = allDexes.getInt("count");
        List<MenuItem> menuItems = new ArrayList<>(dexCount);

        for (int i = 0; i < dexCount; i++) {
            String apiPokedexName = allDexes.getJSONArray("results").getJSONObject(i).getString("name");
            String uiPokedexName = PokedexNameMap.get(apiPokedexName.toLowerCase());

            MenuItem dexItem = new MenuItem(uiPokedexName);
            menuItems.add(dexItem);
        }
        
        return menuItems;
    }

    default Alert createErrorAlert(String customErrorMessage, Exception e) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.initModality(Modality.APPLICATION_MODAL);
        errorAlert.setResizable(true);
        errorAlert.setHeaderText(customErrorMessage);
        errorAlert.setContentText(e.getLocalizedMessage());

        return errorAlert;
    }

    default Label createPokedexListItem(int pokemonDigitCount, int dexNumber, String apiPokemonName) {
        String pokemonName = WordUtils.capitalize(apiPokemonName);

        // account for Porygon-Z, Tapu-Koko, Tapu-Lele, Tapu-Bulu, & Tapu-Fini
        pokemonName = pokemonName.replaceAll("-z", "-Z");
        pokemonName = pokemonName.replaceAll("-koko", "-Koko");
        pokemonName = pokemonName.replaceAll("-lele", "-Lele");
        pokemonName = pokemonName.replaceAll("-bulu", "-Bulu");
        pokemonName = pokemonName.replaceAll("-fini", "-Fini");

        int dexNumberDigitCount = countDigits(dexNumber);
        String dexNumberString = "0".repeat(pokemonDigitCount - dexNumberDigitCount) + dexNumber;

        Label pokemonLabel = new Label(dexNumberString + ": " + pokemonName);
        pokemonLabel.onMousePressedProperty().set((event) -> {
            App.PokedexEntry.set(apiPokemonName);
            App.CurrentScene.set("pokedexEntry.fxml");
        });

        return pokemonLabel;
    }

    default int countDigits(int n) {
        return String.valueOf(n).length();
    }

    default List<Region> createDexEntryUI(JSONObject dexEntryJSON, String currentDexEntry) {
        String genus = "Genus not found";
        JSONArray genuses = dexEntryJSON.getJSONArray("genera");
        for (int i = 0; i < genuses.length(); i++) {
            JSONObject genusCandidate = genuses.getJSONObject(i);
            if (genusCandidate.getJSONObject("language").getString("name").equals("en")) {
                genus = genusCandidate.getString("genus");
                break;
            }
        }

        String introducedInString = dexEntryJSON.getJSONObject("generation").getString("name");
        introducedInString = introducedInString.replaceAll("generation-", "");
        String generationIntroducedIn = IntroducedIn + RomanNumeralMap.get(introducedInString.toLowerCase());

        List<String> eggGroups = new ArrayList<>();
        JSONArray eggGroupJSON = dexEntryJSON.getJSONArray("egg_groups");
        for (int i = 0; i < eggGroupJSON.length(); i++) {
            JSONObject eggGroup = eggGroupJSON.getJSONObject(i);

            String eggGroupString = eggGroup.getString("name");

            // account for Human-Like egg group
            eggGroupString = eggGroupString.replaceAll("shape", " like");
            eggGroupString = WordUtils.capitalize(eggGroupString);
            eggGroupString = eggGroupString.replaceAll(" ", "-");


            eggGroups.add(eggGroupString);
        }

        Map<String, String> flavorTexts = new LinkedHashMap<>();
        JSONArray flavorTextsJSON = dexEntryJSON.getJSONArray("flavor_text_entries");

        for (int i = 0; i < flavorTextsJSON.length(); i++) {
            JSONObject flavorTextCandidate = flavorTextsJSON.getJSONObject(i);

            if (flavorTextCandidate.getJSONObject("language").getString("name").equals("en")) {
                String flavorText = flavorTextCandidate.getString("flavor_text");
                String textVersion = flavorTextCandidate.getJSONObject("version").getString("name");
                flavorTexts.put(textVersion, flavorText);
            }
        }

        Label pokemonName = new Label(currentDexEntry);
        pokemonName.setId(StyleClasses.PokemonName);
        pokemonName.setText(pokemonName.getText().toUpperCase());

        Label pokemonGenus = new Label(genus);
        pokemonGenus.setId(StyleClasses.Subtitle);

        Label introduced = new Label(generationIntroducedIn);
        introduced.setId(StyleClasses.Subtitle);

        HBox eggGroupContainer = new HBox(5);
        Label pokemonEggGroups = new Label(EggGroups);
        pokemonEggGroups.setMinWidth(75);
        eggGroupContainer.getChildren().add(pokemonEggGroups);

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

        VBox flavorTextsContainer = new VBox();
        List<HBox> flavorTextList = flavorTexts.entrySet().stream()
                .map((entry) -> {
                    HBox container = new HBox(5);

                    String gameNameString = entry.getKey() + ":";
                    gameNameString = gameNameString.replaceAll("-", " ");
                    gameNameString = WordUtils.capitalize(gameNameString);

                    Label gameName = new Label(gameNameString);
                    gameName.setMinWidth(100);
                    gameName.setWrapText(false);
                    gameName.setAlignment(Pos.CENTER_RIGHT);

                    String flavorTextString = entry.getValue();
                    flavorTextString = flavorTextString.replaceAll("([\n\f])", " ");
                    flavorTextString = flavorTextString.replaceAll("- ", "-");

                    Label flavorText = new Label(flavorTextString);
                    flavorText.setWrapText(true);

                    container.getChildren().addAll(gameName, flavorText);
                    container.setMinWidth(container.getWidth());

                    return container;
                }).toList();

        flavorTextList.forEach((l) -> {
            l.setId(StyleClasses.Subtext);
            VBox.setMargin(l, InfoInsets);
        });

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
