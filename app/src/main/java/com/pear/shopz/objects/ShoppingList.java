package com.pear.shopz.objects;

import android.content.Context;

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

    public int getTotalBoughtItems(Context context) {

        ShoppingListItemController itemController =  new ShoppingListItemController(context,listID);
        int totalBought = 0;
        for(ShoppingListItem item: itemController.getShoppingListItems())
        {
            if(item.getItemBought() == 1)
                totalBought++;
        }

        return totalBought;
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
