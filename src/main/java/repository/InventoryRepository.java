package repository;

import database.ConnectionFactory;
import model.LowStockItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InventoryRepository {

    private static final int DEFAULT_LOW_STOCK_THRESHOLD = 5;
    private final ConnectionFactory connectionFactory;

    public InventoryRepository() {
        this.connectionFactory = ConnectionFactory.getInstance();
    }

    private Connection getConnection() throws SQLException {
        return connectionFactory.getConnection();
    }

    public List<LowStockItem> fetchLowStockItems(int limit) {
        String query = "SELECT i.ItemName, i.RemainingQty "
                + "FROM InStore i "
                + "LEFT JOIN LowStock ls ON i.ItemName = ls.InStoreName "
                + "WHERE (ls.LowStockLimit IS NOT NULL AND i.RemainingQty <= ls.LowStockLimit) "
                + "OR (ls.LowStockLimit IS NULL AND i.RemainingQty <= ?) "
                + "ORDER BY i.RemainingQty ASC "
                + "LIMIT ?";

        List<LowStockItem> items = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, DEFAULT_LOW_STOCK_THRESHOLD);
            preparedStatement.setInt(2, limit);

            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                items.add(new LowStockItem(
                        rs.getString("ItemName"),
                        rs.getInt("RemainingQty")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load low stock items", e);
        }
        return items;
    }

    public int fetchTotalInStoreQuantity() {
        String query = "SELECT IFNULL(SUM(RemainingQty), 0) AS total FROM InStore";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load in-store quantity", e);
        }
        return 0;
    }

    public int fetchStoreIssuedQuantityByStatus(String status) {
        String query = "SELECT IFNULL(SUM(si.Quantity), 0) AS total "
                + "FROM StoreIssued si "
                + "JOIN IssueType it ON si.IssueTypeID = it.ID "
                + "WHERE LOWER(it.TypeName) = LOWER(?)";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, status);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load store issued quantity", e);
        }
        return 0;
    }
}

