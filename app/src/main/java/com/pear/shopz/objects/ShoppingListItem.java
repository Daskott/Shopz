package com.pear.shopz.objects;

import java.util.ArrayList;
import java.util.Arrays;

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
    private int itemBought;
    private int itemQuantity;


    //public ArrayList<Item> serverData;


    public ShoppingListItem(int listID, int itemID, String itemName, double itemPrice, String itemCategory, String itemAisle, int itemBought, int itemQuantity) {
        this.listID = listID;
        this.itemID = itemID;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.itemCategory = itemCategory;
        this.itemAisle = itemAisle;
        this.itemBought = itemBought;
        this.itemQuantity = itemQuantity;
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

     /*
      * Right now, we store the index of where the category name is located in
      * @see in ShoppingListItemController.getPossibleItemCategories()
      * N/B: Actual category name is not stored in db, just index
      *
      */
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

    public int getItemBought() {
        return itemBought;
    }

    public void setItemBought(int itemBought) {
        this.itemBought = itemBought;
    }

    public int getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(int itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

//    public ArrayList<Item> getServerData() {
//        return serverData;
//    }
//
//    public void setServerData(ArrayList<Item> serverData) {
//        this.serverData = serverData;
//    }
}
