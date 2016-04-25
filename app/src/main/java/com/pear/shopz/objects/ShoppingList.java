package com.pear.shopz.objects;

/**
 * Created by edmondcotterell on 2016-04-21.
 */
public class ShoppingList {

    private int listID;
    private String listName;
    private String store;

    public ShoppingList(int listID, String listName, String store)
    {
        this.listID = listID;
        this.listName = listName;
        this.store = store;
    }

    public int getListID() {
        return listID;
    }

    public void setListID(int listID) {
        this.listID = listID;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }
}
