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

package io.github.lucasstarsz.fxdex.service;

import java.io.IOException;
import java.net.URISyntaxException;

import org.json.JSONObject;

import javafx.beans.property.ListProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.Region;

public interface DexService {

    void loadPokedexesForMenu(ListProperty<Label> currentDex, MenuButton dexMenu, StringProperty currentDexDisplayedProperty)
            throws URISyntaxException, IOException, InterruptedException;

    void parseJSONIntoPokedex(int pokemonDigitCount, ListProperty<Label> currentDex, JSONObject entry);

    void loadDefaultPokedex(ListProperty<Label> currentDex, StringProperty currentDexDisplayedProperty)
            throws IOException, InterruptedException, URISyntaxException;

    public void loadDexEntry(ListProperty<Region> dexEntriesProperty, String currentDexEntry)
            throws IOException, InterruptedException, URISyntaxException;
}
