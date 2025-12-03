package service;

import model.InventoryStats;
import model.LowStockItem;
import repository.InventoryRepository;

import java.util.Collections;
import java.util.List;

public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryService() {
        this(new InventoryRepository());
    }

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public List<LowStockItem> getLowStockItems() {
        try {
            return inventoryRepository.fetchLowStockItems(10);
        } catch (RuntimeException ex) {
            return Collections.emptyList();
        }
    }

    public InventoryStats getInventoryStats() {
        try {
            int totalInStore = inventoryRepository.fetchTotalInStoreQuantity();
            int totalTemporary = inventoryRepository.fetchStoreIssuedQuantityByStatus("Temporary");
            int totalPermanent = inventoryRepository.fetchStoreIssuedQuantityByStatus("Permanent");
            int totalStore = Math.addExact(totalInStore, totalTemporary);
            return new InventoryStats(totalStore, totalInStore, totalTemporary, totalPermanent);
        } catch (RuntimeException ex) {
            return new InventoryStats(0, 0, 0, 0);
        }
    }
}

