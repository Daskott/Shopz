package com.pear.shopz.objects;

import android.content.Context;

import com.pear.shopz.database.ShoppingDataSource;

import java.util.ArrayList;
import java.util.Arrays;

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

    public int addShoppingList(ShoppingList newList)
    {
        return dataSource.createList(newList);
    }

    public void updateShoppingList(ShoppingList list)
    {
        dataSource.updateList(list);
    }

    public void deleteShoppingList(int listID)
    {
        dataSource.deleteList(listID);
    }

    public void deleteShoppingLists(ArrayList<ShoppingList> lists)
    {
        for(ShoppingList list : lists)
            dataSource.deleteList(list.getListID());
    }

    public ArrayList<String>  getStoreOptions()
    {
        ArrayList<String> storeOptions = new ArrayList<String>(Arrays.asList("General Store","Superstore(CA)"));

        return storeOptions;
    }

}
