package com.pear.shopz.objects;

import android.content.Context;

import com.pear.shopz.database.ShoppingDataSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by johnlarmie on 2016-05-06.
 */
public class InventoryItemController {

    private ArrayList<InventoryItem> inventoryList;
    private ShoppingDataSource dataSource;
    private Context context;

    public InventoryItemController(Context context) {

        this.dataSource = new ShoppingDataSource(context);
        this.inventoryList = this.dataSource.readInventory();
        this.context = context;
    }

    public ArrayList<InventoryItem> getInventory() {
        inventoryList = this.dataSource.readInventory();
        return inventoryList;
    }

    public void addToDB(JSONObject json) throws JSONException {
        JSONArray listArray = json.getJSONArray("data");

        for(int i=0; i<listArray.length(); i++)
        {
            JSONObject json_data = listArray.getJSONObject(i);
            dataSource.createInventoryItem(new InventoryItem(-1, json_data.getString("name"), json_data.getString("aisle")));
        }

    }

    public void purgeDB()
    {
        dataSource.purgeInventory();
        getInventory();
    }

    public InventoryItem getByItemName(String itemName)
    {
        return dataSource.getInventoryItem(itemName);
    }
}
