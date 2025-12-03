package main;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import util.ResourceLoader;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            java.net.URL resource = ResourceLoader.getResource("/view/login.fxml");
            if (resource == null) {
                throw new IOException("Cannot find resource: /view/login.fxml");
            }
            Parent root = FXMLLoader.load(resource);
            Scene scene = new Scene(root);
            // Load CSS programmatically
            java.net.URL cssResource = ResourceLoader.getResource("/css/global.css");
            if (cssResource != null) {
                scene.getStylesheets().add(cssResource.toExternalForm());
            }
            primaryStage.setTitle("Store Inventory Management System");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load login.fxml: " + e.getMessage(), e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
