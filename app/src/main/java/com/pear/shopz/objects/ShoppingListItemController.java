package com.pear.shopz.objects;

import android.content.Context;
import android.util.Log;

import com.pear.shopz.database.ShoppingDataSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    public String[] getListArray()
    {
        ArrayList<String> itemNameList = new ArrayList<String>();
        String[] result = null;

        for (ShoppingListItem item: shoppingListItems)
        {
            itemNameList.add(item.getItemName());
        }

        if (itemNameList != null) result = (String[]) itemNameList.toArray(new String[itemNameList.size()]);
        return result;
    }

    public void addNetworkData(JSONObject json) throws JSONException {
        JSONObject data = json.getJSONObject("data");

        for (ShoppingListItem listItem: shoppingListItems) {
            JSONArray listArray = data.getJSONArray(listItem.getItemName());

            for(int i=0; i<listArray.length(); i++)
            {
                JSONObject json_data = listArray.getJSONObject(i);
                listItem.serverData.add(new Item(json_data.getString("name"), json_data.getString("aisle")));
            }
            Log.v("listDDD", listItem.serverData.toString());
        }

    }


}
