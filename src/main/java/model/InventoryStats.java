package model;

public class InventoryStats {
    private final int totalStoreItems;
    private final int totalInStoreItems;
    private final int totalTemporaryAssigned;
    private final int totalPermanentAssigned;

    public InventoryStats(int totalStoreItems,
                          int totalInStoreItems,
                          int totalTemporaryAssigned,
                          int totalPermanentAssigned) {
        this.totalStoreItems = totalStoreItems;
        this.totalInStoreItems = totalInStoreItems;
        this.totalTemporaryAssigned = totalTemporaryAssigned;
        this.totalPermanentAssigned = totalPermanentAssigned;
    }

    public int getTotalStoreItems() {
        return totalStoreItems;
    }

    public int getTotalInStoreItems() {
        return totalInStoreItems;
    }

    public int getTotalTemporaryAssigned() {
        return totalTemporaryAssigned;
    }

    public int getTotalPermanentAssigned() {
        return totalPermanentAssigned;
    }
}

