package com.pear.shopz.objects;

/**
 * Created by johnlarmie on 2016-04-24.
 */
public class ShoppingListItem {
    private int listID;
    private int itemID;
    private String itemName;
    private double itemPrice;
    private String itemCategory;
    private String itemAisle;
    private String itemBought;

    public ShoppingListItem(int listID, int itemID, String itemName, double itemPrice, String itemCategory, String itemAisle, String itemBought) {
        this.listID = listID;
        this.itemID = itemID;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.itemCategory = itemCategory;
        this.itemAisle = itemAisle;
        this.itemBought = itemBought;
    }

    public int getListID() {
        return listID;
    }

    public void setListID(int listID) {
        this.listID = listID;
    }

    public int getItemID() {
        return itemID;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public double getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(double itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getItemCategory() {
        return itemCategory;
    }

    public void setItemCategory(String itemCategory) {
        this.itemCategory = itemCategory;
    }

    public String getItemAisle() {
        return itemAisle;
    }

    public void setItemAisle(String itemAisle) {
        this.itemAisle = itemAisle;
    }

    public String getItemBought() {
        return itemBought;
    }

    public void setItemBought(String itemBought) {
        this.itemBought = itemBought;
    }
}
