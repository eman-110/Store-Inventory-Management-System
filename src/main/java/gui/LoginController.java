package gui;

import handler.LoginHandler;
import handler.LoginResult;
import handler.UserExistsHandler;
import handler.ValidPasswordHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import repository.UserRepository;
import service.UserService;
import util.ResourceLoader;

import java.io.IOException;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private final UserService userService;

    public LoginController() {
        userService = new UserService(new UserRepository());
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        LoginHandler handler = new UserExistsHandler(userService);
        handler.setNext(new ValidPasswordHandler());

        try {
            LoginResult result = handler.handle(username, password);
            if (result.success()) {
                java.net.URL baseResource = ResourceLoader.getResource("/view/base.fxml");
                if (baseResource == null) {
                    errorLabel.setText("Cannot find base.fxml");
                    return;
                }
                FXMLLoader loader = new FXMLLoader(baseResource);
                Parent root = loader.load();

                Stage stage = (Stage) usernameField.getScene().getWindow();
                Scene scene = new Scene(root);
                // Load CSS files
                java.net.URL globalCss = ResourceLoader.getResource("/css/global.css");
                java.net.URL baseCss = ResourceLoader.getResource("/css/base.css");
                if (globalCss != null) {
                    scene.getStylesheets().add(globalCss.toExternalForm());
                }
                if (baseCss != null) {
                    scene.getStylesheets().add(baseCss.toExternalForm());
                }
                stage.setScene(scene);
                stage.centerOnScreen();
                stage.setMaximized(true);
                stage.show();
            } else {
                errorLabel.setText(result.message());
            }
        } catch (IOException e) {
            errorLabel.setText("Error starting the app: " + e.getMessage());
        } catch (RuntimeException e) {
            String errorMsg = e.getMessage();
            if (errorMsg != null && errorMsg.contains("MySQL JDBC Driver not found")) {
                errorLabel.setText("Database driver not found. Please add MySQL JDBC driver to classpath.");
            } else if (errorMsg != null && errorMsg.contains("Failed to connect")) {
                errorLabel.setText("Cannot connect to database. Please check your database connection.");
            } else {
                errorLabel.setText("Error: " + (errorMsg != null ? errorMsg : e.getClass().getSimpleName()));
            }
        }
    }
}