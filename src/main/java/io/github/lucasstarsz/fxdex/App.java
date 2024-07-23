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
        CurrentScene.addListener((c, o, n) -> {
            try {
                switchSceneInfo(n);
            } catch (IOException ex) {
                ex.printStackTrace();
                Platform.exit();
            }
        });

        Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());

        this.primaryStage = stage;

        Scene scene = new Scene(DefaultParent, 640, 480);
        stage.setScene(scene);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

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
        final Injector injector = Guice.createInjector(new DexModule());
        FXMLLoader mainFXML = new FXMLLoader(getClass().getResource(fxml));
        mainFXML.setControllerFactory(injector::getInstance);

        primaryStage.getScene().setRoot(mainFXML.load());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
