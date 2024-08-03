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
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

import com.google.inject.Guice;
import com.google.inject.Injector;

import atlantafx.base.theme.PrimerDark;
import io.github.lucasstarsz.fxdex.misc.DatabaseSetup;
import io.github.lucasstarsz.fxdex.misc.DexModule;
import io.github.lucasstarsz.fxdex.misc.DexViewModelCache;
import io.github.lucasstarsz.fxdex.misc.FileLinks;
import io.github.lucasstarsz.fxdex.service.UiService;
import javafx.application.Application;
import javafx.beans.property.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class App extends Application {

    private static final VBox DefaultParent = new VBox();

    public static final StringProperty CurrentDexEntry = new SimpleStringProperty();
    public static final StringProperty CurrentScene = new SimpleStringProperty();
    public static final IntegerProperty CurrentDexNumber = new SimpleIntegerProperty();
    public static final Property<ExecutorService> DexThreadHandler = new SimpleObjectProperty<>();
    private static final DexViewModelCache dexVMCache = new DexViewModelCache();

    private Stage primaryStage;

    @Override
    public void start(Stage stage) {
        try {

            try (var dbConnection = DriverManager.getConnection(FileLinks.JDBCConnectionUrl);
                 var statementObj = dbConnection.createStatement()) {
                var meta = dbConnection.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("Setting up...");

                for (String sqlStatement : DatabaseSetup.getStartupStatements()) {
                    System.out.println(sqlStatement);
                    statementObj.execute(sqlStatement);
                }
            } catch (SQLException e) {
                Alert errorAlert = UiService.createErrorAlert("Unable to open Pokédex Database", e);
                errorAlert.showAndWait();
            }

            Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());
            CurrentScene.addListener((c, o, n) -> {
                try {
                    switchSceneInfo(n);
                } catch (Exception ex) {
                    Alert errorAlert = UiService.createErrorAlert("Unable to open Pokédex", ex);
                    ex.printStackTrace();
                    errorAlert.initOwner(stage);
                    errorAlert.showAndWait();
                }
            });

            this.primaryStage = stage;

            Scene scene = new Scene(DefaultParent, 640, 480);
            stage.setScene(scene);

            CurrentScene.set("main.fxml");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        FXMLLoader mainFXML = new FXMLLoader(App.class.getResource(fxml));

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
        scene.getStylesheets().add(Objects.requireNonNull(App.class.getResource("style.css")).toExternalForm());
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static DexViewModelCache getDexVMCache() {
        return dexVMCache;
    }
}
