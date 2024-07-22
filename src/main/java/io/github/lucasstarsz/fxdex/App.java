package io.github.lucasstarsz.fxdex;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    public static final ExecutorService DexThreadHandler = Executors.newFixedThreadPool(4);

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader mainFXML = new FXMLLoader(getClass().getResource("main.fxml"));
        Scene scene = new Scene(mainFXML.load(), 640, 480);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        DexThreadHandler.shutdown();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
