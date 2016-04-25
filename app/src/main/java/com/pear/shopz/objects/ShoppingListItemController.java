package com.pear.shopz.objects;

import android.content.Context;

import com.pear.shopz.database.ShoppingDataSource;

import java.util.ArrayList;

/**
 * Created by edmondcotterell on 2016-04-21.
 */
public class ShoppingListItemController {

    private ArrayList<ShoppingListItem> shoppingListItems;
    private ShoppingDataSource dataSource;
    private Context context;

    public ShoppingListItemController(Context context, int listID)
    {
        dataSource = new ShoppingDataSource(context);
        shoppingListItems = dataSource.readListItems(listID);
        this.context = context;
    }

    public ArrayList<ShoppingListItem> getShoppingListItems() {
        return shoppingListItems;
    }

    public void setShoppingListItems(ArrayList<ShoppingListItem> shoppingListItems) {
        this.shoppingListItems = shoppingListItems;
    }

    public void addShoppingListItem(ShoppingListItem newItem)
    {
        dataSource.createItem(newItem);
    }

    public void updateShoppingListItem(ShoppingListItem item)
    {
        dataSource.updateListItem(item);
    }

    public void deleteShoppingListItem(int itemID)
    {
        dataSource.deleteListItem(itemID);
    }

    public void deleteShoppingListItems(ArrayList<ShoppingListItem> Items)
    {
        for(ShoppingListItem item : Items)
            dataSource.deleteListItem(item.getItemID());
    }


}
