package model;

public class LowStockItem {
    private final String itemName;
    private final int quantity;

    public LowStockItem(String itemName, int quantity) {
        this.itemName = itemName;
        this.quantity = quantity;
    }

    public String getItemName() {
        return itemName;
    }

    public int getQuantity() {
        return quantity;
    }
}

