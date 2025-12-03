package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import model.InventoryStats;
import model.LowStockItem;
import service.InventoryService;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class DashboardController {
    @FXML
    private Button storeButton;
    @FXML
    private Button userButton;
    @FXML
    private Button auctionButton;

    @FXML
    private TableView<LowStockItem> lowStockTable;
    @FXML
    private TableColumn<LowStockItem, String> itemNameColumn;
    @FXML
    private TableColumn<LowStockItem, Integer> quantityColumn;

    @FXML
    private Label totalStoreItemsLabel;
    @FXML
    private Label totalInStoreLabel;
    @FXML
    private Label totalTemporaryAssignedLabel;
    @FXML
    private Label totalPermanentAssignedLabel;

    private InventoryService inventoryService;

    public void initialize() {
        inventoryService = new InventoryService();
        configureLowStockTable();
        refreshDashboard();
        setActiveFilter(storeButton);
    }

    private void configureLowStockTable() {
        lowStockTable.setColumnResizePolicy(resolveConstrainedPolicy());
        lowStockTable.setPlaceholder(new Label("All items are sufficiently stocked."));
        itemNameColumn.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        quantityColumn.setStyle("-fx-alignment: CENTER_RIGHT;");
    }

    private void refreshDashboard() {
        List<LowStockItem> lowStockItems = inventoryService.getLowStockItems();
        ObservableList<LowStockItem> tableItems = FXCollections.observableArrayList(lowStockItems);
        lowStockTable.setItems(tableItems);

        InventoryStats stats = inventoryService.getInventoryStats();
        totalStoreItemsLabel.setText(String.valueOf(stats.getTotalStoreItems()));
        totalInStoreLabel.setText(String.valueOf(stats.getTotalInStoreItems()));
        totalTemporaryAssignedLabel.setText(String.valueOf(stats.getTotalTemporaryAssigned()));
        totalPermanentAssignedLabel.setText(String.valueOf(stats.getTotalPermanentAssigned()));
    }

    @FXML
    private void handleFilterSelection(ActionEvent event) {
        Button selected = (Button) event.getSource();
        setActiveFilter(selected);
        // Placeholder for future data filtering logic.
    }

    private void setActiveFilter(Button activeButton) {
        List<Button> filters = Arrays.asList(storeButton, userButton, auctionButton);
        filters.forEach(button -> button.getStyleClass().remove("filter-button-active"));
        if (!activeButton.getStyleClass().contains("filter-button-active")) {
            activeButton.getStyleClass().add("filter-button-active");
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private Callback<TableView.ResizeFeatures, Boolean> resolveConstrainedPolicy() {
        try {
            Field field = TableView.class.getField("CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN");
            Object value = field.get(null);
            if (value instanceof Callback) {
                Callback<TableView.ResizeFeatures, Boolean> policy =
                        (Callback<TableView.ResizeFeatures, Boolean>) value;
                return policy;
            }
        } catch (NoSuchFieldException | IllegalAccessException ignored) {
            // fallback to legacy policy below
        }
        Callback<TableView.ResizeFeatures, Boolean> legacyPolicy = TableView.CONSTRAINED_RESIZE_POLICY;
        return legacyPolicy;
    }
}
