package com.socialreader.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@SuppressWarnings("ConstantConditions")
public class JavaFXApplication extends Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(JavaFXApplication.class);

    public static void main(String[] args) throws Exception{
        try {
            launch(args);
        } catch (Exception x) {
            LOGGER.error(x.getMessage(), x);
            throw x;
        }
    }


    @Override
    public void start(Stage stage) throws IOException {
        Thread.setDefaultUncaughtExceptionHandler((t, x) -> LOGGER.error(x.getMessage(), x));

        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("fxml/main.fxml"));
        Scene scene = new Scene(root, 1000, 750);

        stage.setTitle("Social Reader");
        stage.setScene(scene);
        stage.show();
    }

}