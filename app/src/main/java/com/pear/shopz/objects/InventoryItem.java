package com.pear.shopz.objects;

/**
 * Created by johnlarmie on 2016-05-06.
 */
public class InventoryItem {
    private int inventoryID;
    private String name;
    private String category;

    public InventoryItem(int inventoryID, String name, String category) {
        this.inventoryID = inventoryID;
        this.name = name;
        this.category = category;
    }

    public int getInventoryID() {
        return inventoryID;
    }

    public void setInventoryID(int inventoryID) {
        this.inventoryID = inventoryID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
