package com.pear.shopz.objects;

import android.content.Context;

import com.pear.shopz.database.ShoppingDataSource;

import java.util.ArrayList;

/**
 * Created by edmondcotterell on 2016-04-21.
 */
public class ShoppingListController {

    private ArrayList<ShoppingList> shoppingLists;
    private ShoppingDataSource dataSource;
    private Context context;

    public ShoppingListController(Context context)
    {
        dataSource = new ShoppingDataSource(context);
        shoppingLists = dataSource.readLists();
        this.context = context;
    }

    public ArrayList<ShoppingList> getShoppingLists() {
        return shoppingLists;
    }

    public void setShoppingLists(ArrayList<ShoppingList> shoppingLists) {
        this.shoppingLists = shoppingLists;
    }

    public void addShoppingList(String listName, String store)
    {
        ShoppingList newList = new ShoppingList(-1, listName,store);

        //update list
        shoppingLists.add(newList);

        //update db
        dataSource.createList(newList);
    }


}
