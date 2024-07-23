package io.github.lucasstarsz.fxdex;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

import com.google.inject.Guice;
import com.google.inject.Injector;

import javafx.application.Application;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    public static final Property<ExecutorService> DexThreadHandler = new SimpleObjectProperty<>();

    @Override
    public void start(Stage stage) throws IOException {
        final Injector injector = Guice.createInjector(new DexModule());
        FXMLLoader mainFXML = new FXMLLoader(getClass().getResource("main.fxml"));
        mainFXML.setControllerFactory(injector::getInstance);

        Scene scene = new Scene(mainFXML.load(), 640, 480);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        ExecutorService dexThreadHandler = DexThreadHandler.getValue();
        if (dexThreadHandler != null) {
            dexThreadHandler.shutdown();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
