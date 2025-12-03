package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.Role;
import model.User;
import util.ResourceLoader;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class BaseController {
    @FXML
    private Button logoutButton;
    @FXML
    private Button usersButton;
    @FXML
    private AnchorPane centerPane;
    @FXML
    private ImageView logoImage;


    private User currentUser;
    private final Map<String, String> urls;

    public BaseController() {
        urls = new HashMap<>();
        urls.put("Dashboard", "/view/dashboard.fxml");
        urls.put("Add Item", "/view/categories.fxml");
        urls.put("Assign Item", "/view/products.fxml");
        urls.put("Return Item", "/view/customers.fxml");
        urls.put("Store Inventory", "/view/products.fxml");
        urls.put("User Inventory", "/view/users.fxml");
        urls.put("Auction Inventory", "/view/orders.fxml");
        urls.put("Report", "/view/orders.fxml");
        // Keep old mappings for backward compatibility
        urls.put("Categories", "/view/categories.fxml");
        urls.put("Products", "/view/products.fxml");
        urls.put("Customers", "/view/customers.fxml");
        urls.put("Suppliers", "/view/suppliers.fxml");
        urls.put("Orders", "/view/orders.fxml");
        urls.put("Purchases", "/view/purchases.fxml");
        urls.put("Users", "/view/users.fxml");
    }

    @FXML
    private void initialize() {
        loadLogoImage();
        setupButtonVisibility();
    }

    private void setupButtonVisibility() {
        if (currentUser != null && usersButton != null && currentUser.getRole() != Role.ADMIN) {
            usersButton.setVisible(false);
            usersButton.setManaged(false);
        }
    }

    private void loadLogoImage() {
        if (logoImage == null) {
            return;
        }
        URL logoUrl = ResourceLoader.getResource("/logo.png");
        if (logoUrl != null) {
            logoImage.setImage(new Image(logoUrl.toExternalForm()));
        } else {
            DisplayAlert.showError("Error", "Invalid resource: logo.png not found on the classpath");
        }
    }

    @FXML
    private void btnNavigators(ActionEvent event) {
        Button btn = (Button) event.getSource();
        String btnText = btn.getText();
        String url = urls.get(btnText);

        if (url == null) {
            DisplayAlert.showError("Error", "No page found for: " + btnText);
            return;
        }

        try {
            ctrlRightPane(url);
        } catch (IOException e) {
            DisplayAlert.showError("Error", "Error loading FXML: " + url);
        }
    }

    @FXML
    private void ctrlRightPane(String url) throws IOException {
        try {
            centerPane.getChildren().clear();
            URL resource = ResourceLoader.getResource(url);
            if (resource == null) {
                DisplayAlert.showError("Error", "Cannot find resource: " + url);
                return;
            }
            FXMLLoader loader = new FXMLLoader(resource);
            AnchorPane newCenterPane = loader.load();
            newCenterPane.setPrefHeight(centerPane.getHeight());
            newCenterPane.setPrefWidth(centerPane.getWidth());
            centerPane.getChildren().add(newCenterPane);
            
            // Load CSS for the loaded view
            loadCSSForView(url, newCenterPane);
        } catch (IOException e) {
            DisplayAlert.showError("Error", "Could not load FXML: " + url);
        }
    }
    
    private void loadCSSForView(String fxmlUrl, javafx.scene.Node node) {
        javafx.scene.Scene scene = node.getScene();
        if (scene == null) {
            // If node doesn't have a scene yet, get it from the parent
            javafx.scene.Node parent = node.getParent();
            while (parent != null && scene == null) {
                scene = parent.getScene();
                parent = parent.getParent();
            }
        }
        if (scene == null) {
            return; // Can't load CSS without a scene
        }
        
        // Determine which CSS files to load based on the FXML file
        if (fxmlUrl.contains("dashboard.fxml")) {
            URL css = ResourceLoader.getResource("/css/dashboard.css");
            if (css != null && !scene.getStylesheets().contains(css.toExternalForm())) {
                scene.getStylesheets().add(css.toExternalForm());
            }
        } else if (fxmlUrl.contains("users.fxml")) {
            URL css = ResourceLoader.getResource("/css/global.css");
            if (css != null && !scene.getStylesheets().contains(css.toExternalForm())) {
                scene.getStylesheets().add(css.toExternalForm());
            }
        }
        // Ensure global.css is always loaded
        URL globalCss = ResourceLoader.getResource("/css/global.css");
        if (globalCss != null && !scene.getStylesheets().contains(globalCss.toExternalForm())) {
            scene.getStylesheets().add(globalCss.toExternalForm());
        }
    }

    @FXML
    private void logout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
            Scene scene = new Scene(root);
            Stage currentStage = (Stage) logoutButton.getScene().getWindow();
            Stage newStage = new Stage();
            newStage.setScene(scene);
            currentStage.close();
            newStage.show();
        } catch (IOException e) {
            DisplayAlert.showError("Error", "Could not load login view");
        }
    }
}