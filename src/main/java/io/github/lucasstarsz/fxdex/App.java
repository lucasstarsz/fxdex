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

package io.github.lucasstarsz.fxdex;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

import com.google.inject.Guice;
import com.google.inject.Injector;

import atlantafx.base.theme.PrimerDark;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class App extends Application {

    private static final VBox DefaultParent = new VBox();

    public static final StringProperty PokedexEntry = new SimpleStringProperty();
    public static final StringProperty CurrentScene = new SimpleStringProperty();
    public static final Property<ExecutorService> DexThreadHandler = new SimpleObjectProperty<>();

    private Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());
        CurrentScene.addListener((c, o, n) -> {
            try {
                switchSceneInfo(n);
            } catch (IOException ex) {
                ex.printStackTrace();
                Platform.exit();
            }
        });

        this.primaryStage = stage;

        Scene scene = new Scene(DefaultParent, 640, 480);
        stage.setScene(scene);

        CurrentScene.set("main.fxml");
        stage.show();
    }

    @Override
    public void stop() {
        ExecutorService dexThreadHandler = DexThreadHandler.getValue();
        if (dexThreadHandler != null) {
            dexThreadHandler.shutdown();
        }

        System.exit(0);
    }

    private void switchSceneInfo(String fxml) throws IOException {
        FXMLLoader mainFXML = new FXMLLoader(getClass().getResource(fxml));

        Injector injector = Guice.createInjector(new DexModule());
        mainFXML.setControllerFactory(injector::getInstance);

        Scene scene = primaryStage.getScene();
        Parent rootNode = mainFXML.load();
        scene.setRoot(rootNode);

        reloadStylesheets();
    }

    private void reloadStylesheets() {
        Scene scene = primaryStage.getScene();

        scene.getStylesheets().clear();
        scene.getStylesheets().add(App.class.getResource("style.css").toExternalForm());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
