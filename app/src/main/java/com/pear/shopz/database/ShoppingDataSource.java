package com.pear.shopz.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import com.pear.shopz.objects.InventoryItem;
import com.pear.shopz.objects.ShoppingList;
import com.pear.shopz.objects.ShoppingListItem;

import java.util.ArrayList;

/**
 * Created by edmondcotterell on 2016-04-21.
 */
public class ShoppingDataSource {
    private Context context;
    private ShoppingSQLiteHelper shoppingSQLiteHelper;

    public ShoppingDataSource(Context context)
    {
        this.context = context;
        shoppingSQLiteHelper = new ShoppingSQLiteHelper(context);
    }

    private SQLiteDatabase open()
    {
        return shoppingSQLiteHelper.getWritableDatabase();
    }

    private void close(SQLiteDatabase database)
    {
        database.close();
    }

    //get all shopping lists from database
    public ArrayList<ShoppingList> readLists()
    {
        SQLiteDatabase database = open();
        Cursor cursor  = database.query(
                ShoppingSQLiteHelper.SHOPPING_LIST_TABLE,
                new String[] {BaseColumns._ID,ShoppingSQLiteHelper.COLUMN_SLIST_NAME,ShoppingSQLiteHelper.COLUMN_SLIST_STORE},
                null, //selection
                null, //selection args
                null, //group by
                null, //having
                null //order
        );

        ArrayList<ShoppingList> shoppingLists = new ArrayList<ShoppingList>();

        if(cursor.moveToFirst())
        {
            do
            {
                //get each record
                ShoppingList shoppingList = new ShoppingList(
                        getIntFromColumnName(cursor, BaseColumns._ID),
                        getStringFromColumnName(cursor, ShoppingSQLiteHelper.COLUMN_SLIST_NAME),
                        getStringFromColumnName(cursor, shoppingSQLiteHelper.COLUMN_SLIST_STORE));

                //add record to list
                shoppingLists.add(shoppingList);
            }while(cursor.moveToNext());
        }

        cursor.close();
        close(database);
        return shoppingLists;
    }

    //get all shopping items from a list in database
    public ArrayList<ShoppingListItem> readListItems(int listID)
    {
        SQLiteDatabase database = open();
        Cursor cursor = database.rawQuery(
                "SELECT * FROM " + shoppingSQLiteHelper.GROCERY_ITEMS_TABLE +
                        " WHERE " + shoppingSQLiteHelper.COLUMN_FOREIGN_KEY_SLIST_ID + " = " + listID, null);



        ArrayList<ShoppingListItem> shoppingListItems = new ArrayList<ShoppingListItem>();

        if(cursor.moveToFirst())
        {
            do
            {
                //get each record
                //(int listID, int itemID String itemName, double itemPrice, String itemCategory, String itemAisle, String itemBought)
                ShoppingListItem shoppingListItem = new ShoppingListItem(
                        getIntFromColumnName(cursor, shoppingSQLiteHelper.COLUMN_FOREIGN_KEY_SLIST_ID),
                        getIntFromColumnName(cursor, BaseColumns._ID),
                        getStringFromColumnName(cursor, ShoppingSQLiteHelper.COLUMN_ITEM_NAME),
                        getDoubleFromColumnName(cursor, ShoppingSQLiteHelper.COLUMN_ITEM_PRICE),
                        getStringFromColumnName(cursor, ShoppingSQLiteHelper.COLUMN_ITEM_CATEGORY),
                        getStringFromColumnName(cursor, ShoppingSQLiteHelper.COLUMN_ITEM_AISLE),
                        getIntFromColumnName(cursor, shoppingSQLiteHelper.COLUMN_ITEM_BOUGHT),
                        getIntFromColumnName(cursor, shoppingSQLiteHelper.COLUMN_ITEM_QUANTITY)
                );

                //add record to list
                shoppingListItems.add(shoppingListItem);
            }while(cursor.moveToNext());
        }

        cursor.close();
        close(database);
        Log.v("itemlistsize", shoppingListItems.size() + " " + listID);
        return shoppingListItems;
    }

    public ShoppingListItem getItem(int itemID)
    {
        ShoppingListItem shoppingListItem = null;
        SQLiteDatabase database = open();
        Cursor cursor = database.rawQuery(
                "SELECT * FROM " + shoppingSQLiteHelper.GROCERY_ITEMS_TABLE +
                        " WHERE " + BaseColumns._ID + " = " + itemID, null);



        //ArrayList<ShoppingListItem> shoppingListItems = new ArrayList<ShoppingListItem>();

        if(cursor.moveToFirst())
        {
            do
            {
                //get each record
                //(int listID, int itemID String itemName, double itemPrice, String itemCategory, String itemAisle, String itemBought)
                shoppingListItem = new ShoppingListItem(
                        getIntFromColumnName(cursor, shoppingSQLiteHelper.COLUMN_FOREIGN_KEY_SLIST_ID),
                        getIntFromColumnName(cursor, BaseColumns._ID),
                        getStringFromColumnName(cursor, ShoppingSQLiteHelper.COLUMN_ITEM_NAME),
                        getDoubleFromColumnName(cursor, ShoppingSQLiteHelper.COLUMN_ITEM_PRICE),
                        getStringFromColumnName(cursor, ShoppingSQLiteHelper.COLUMN_ITEM_CATEGORY),
                        getStringFromColumnName(cursor, ShoppingSQLiteHelper.COLUMN_ITEM_AISLE),
                        getIntFromColumnName(cursor, shoppingSQLiteHelper.COLUMN_ITEM_BOUGHT),
                        getIntFromColumnName(cursor, shoppingSQLiteHelper.COLUMN_ITEM_QUANTITY)
                );

            }while(cursor.moveToNext());
        }

        cursor.close();
        close(database);
        return shoppingListItem;
    }

    public ShoppingList getList(int listID)
    {
        ShoppingList shoppingList = null;
        SQLiteDatabase database = open();
        Cursor cursor = database.rawQuery(
                "SELECT * FROM " + shoppingSQLiteHelper.SHOPPING_LIST_TABLE +
                        " WHERE " + BaseColumns._ID + " = " + listID, null);



        //ArrayList<ShoppingListItem> shoppingListItems = new ArrayList<ShoppingListItem>();

        if(cursor.moveToFirst())
        {
            do
            {
                //get each record
                shoppingList = new ShoppingList(
                        getIntFromColumnName(cursor, BaseColumns._ID),
                        getStringFromColumnName(cursor, ShoppingSQLiteHelper.COLUMN_SLIST_NAME),
                        getStringFromColumnName(cursor, shoppingSQLiteHelper.COLUMN_SLIST_STORE)
                );

            }while(cursor.moveToNext());
        }

        cursor.close();
        close(database);
        return shoppingList;
    }

    //*****DO ANOTHER QUERY TO GET TOTAL (BOUGHT_ITEMS/ TOTAL_ITEMS)


    private int getIntFromColumnName(Cursor cursor, String columnName)
    {
        int columnIndex = cursor.getColumnIndex(columnName);
        return cursor.getInt(columnIndex);
    }

    private double getDoubleFromColumnName(Cursor cursor, String columnName)
    {
        int columnIndex = cursor.getColumnIndex(columnName);
        return cursor.getDouble(columnIndex);
    }

    private String getStringFromColumnName(Cursor cursor, String columnName)
    {
        int columnIndex = cursor.getColumnIndex(columnName);
        return cursor.getString(columnIndex);
    }

    //add new shopping list to database
    public int createList(ShoppingList shoppingList){

        SQLiteDatabase database = open();
        database.beginTransaction();

        //implementation
        int id = -1;
        ContentValues shoppingListValues = new ContentValues();
        shoppingListValues.put(shoppingSQLiteHelper.COLUMN_SLIST_NAME, shoppingList.getListName());
        shoppingListValues.put(shoppingSQLiteHelper.COLUMN_SLIST_STORE,shoppingList.getStore());
        id = (int)database.insert(shoppingSQLiteHelper.SHOPPING_LIST_TABLE,null,shoppingListValues);

        database.setTransactionSuccessful();
        database.endTransaction();
        close(database);

        return id;
    }

    //add new items to a shopping list to database
    public void createItem(ShoppingListItem shoppingListItem){

        SQLiteDatabase database = open();
        database.beginTransaction();

        //implementation
        int id = -1;
        ContentValues shoppingListItemValues = new ContentValues();
        shoppingListItemValues.put(shoppingSQLiteHelper.COLUMN_ITEM_NAME, shoppingListItem.getItemName());
        shoppingListItemValues.put(shoppingSQLiteHelper.COLUMN_ITEM_PRICE,shoppingListItem.getItemPrice());
        shoppingListItemValues.put(shoppingSQLiteHelper.COLUMN_ITEM_AISLE,shoppingListItem.getItemAisle());
        shoppingListItemValues.put(shoppingSQLiteHelper.COLUMN_ITEM_CATEGORY,shoppingListItem.getItemCategory());
        shoppingListItemValues.put(shoppingSQLiteHelper.COLUMN_ITEM_BOUGHT,shoppingListItem.getItemBought());
        shoppingListItemValues.put(shoppingSQLiteHelper.COLUMN_ITEM_QUANTITY,shoppingListItem.getItemQuantity());
        shoppingListItemValues.put(shoppingSQLiteHelper.COLUMN_FOREIGN_KEY_SLIST_ID,shoppingListItem.getListID());

        database.insert(shoppingSQLiteHelper.GROCERY_ITEMS_TABLE,null,shoppingListItemValues);

        Log.v("Add-to-List-Source", shoppingListItem.getListID() + "");
        database.setTransactionSuccessful();
        database.endTransaction();
        close(database);
    }

    //update shopping list
    public void updateList(ShoppingList shoppingList)
    {
        SQLiteDatabase database = open();
        database.beginTransaction();

        //implementation
        ContentValues updateShoppingListValues = new ContentValues();
        updateShoppingListValues.put(shoppingSQLiteHelper.COLUMN_SLIST_NAME, shoppingList.getListName());
        updateShoppingListValues.put(shoppingSQLiteHelper.COLUMN_SLIST_STORE,shoppingList.getStore());
        database.update(shoppingSQLiteHelper.SHOPPING_LIST_TABLE,
                updateShoppingListValues,
                String.format("%s=%d", BaseColumns._ID, shoppingList.getListID()), null);

        database.setTransactionSuccessful();
        database.endTransaction();
        close(database);
    }

    //update shopping list item
    public void updateListItem(ShoppingListItem shoppingListItem)
    {
        SQLiteDatabase database = open();
        database.beginTransaction();

        //implementation
        ContentValues updateShoppingListItemValues = new ContentValues();
        updateShoppingListItemValues.put(shoppingSQLiteHelper.COLUMN_ITEM_NAME, shoppingListItem.getItemName());
        updateShoppingListItemValues.put(shoppingSQLiteHelper.COLUMN_ITEM_PRICE,shoppingListItem.getItemPrice());
        updateShoppingListItemValues.put(shoppingSQLiteHelper.COLUMN_ITEM_AISLE,shoppingListItem.getItemAisle());
        updateShoppingListItemValues.put(shoppingSQLiteHelper.COLUMN_ITEM_CATEGORY,shoppingListItem.getItemCategory());
        updateShoppingListItemValues.put(shoppingSQLiteHelper.COLUMN_ITEM_BOUGHT,shoppingListItem.getItemBought());
        updateShoppingListItemValues.put(shoppingSQLiteHelper.COLUMN_ITEM_QUANTITY,shoppingListItem.getItemQuantity());
        updateShoppingListItemValues.put(shoppingSQLiteHelper.COLUMN_FOREIGN_KEY_SLIST_ID,shoppingListItem.getListID());
        database.update(shoppingSQLiteHelper.GROCERY_ITEMS_TABLE,
                updateShoppingListItemValues,
                String.format("%s=%d", BaseColumns._ID, shoppingListItem.getItemID()), null);

        database.setTransactionSuccessful();
        database.endTransaction();
        close(database);
    }

    //delete shopping list
    public void deleteList(int listID)
    {
        SQLiteDatabase database = open();
        database.beginTransaction();

        //implementation
        database.delete(shoppingSQLiteHelper.GROCERY_ITEMS_TABLE,
                String.format("%s=%s", shoppingSQLiteHelper.COLUMN_FOREIGN_KEY_SLIST_ID , String.valueOf(listID)), null);

        database.delete(shoppingSQLiteHelper.SHOPPING_LIST_TABLE,
                String.format("%s=%s", BaseColumns._ID , String.valueOf(listID)), null);

        database.setTransactionSuccessful();
        database.endTransaction();
        close(database);
    }

    //delete shopping list item
    public void deleteListItem(int itemID)
    {
        SQLiteDatabase database = open();
        database.beginTransaction();

        //implementation
        database.delete(shoppingSQLiteHelper.GROCERY_ITEMS_TABLE,
                String.format("%s=%s", BaseColumns._ID , String.valueOf(itemID)), null);


        database.setTransactionSuccessful();
        database.endTransaction();
        close(database);
    }

    public ArrayList<InventoryItem> readInventory() {

        SQLiteDatabase database = open();
        Cursor cursor = database.rawQuery(
                "SELECT * FROM " + shoppingSQLiteHelper.INVENTORY_ITEMS_TABLE, null);



        ArrayList<InventoryItem> inventoryItems = new ArrayList<InventoryItem>();

        if(cursor.moveToFirst())
        {
            do
            {
                InventoryItem inventoryItem = new InventoryItem(
                        getIntFromColumnName(cursor, BaseColumns._ID),
                        getStringFromColumnName(cursor, ShoppingSQLiteHelper.COLUMN_ITEM_NAME),
                        getStringFromColumnName(cursor, ShoppingSQLiteHelper.COLUMN_ITEM_CATEGORY)
                );

                //add record to list
                inventoryItems.add(inventoryItem);
            }while(cursor.moveToNext());
        }

        cursor.close();
        close(database);
        return inventoryItems;
    }

    public void createInventoryItem(InventoryItem item){

        SQLiteDatabase database = open();
        database.beginTransaction();

        //implementation
        ContentValues inventoryItemValues = new ContentValues();
        inventoryItemValues.put(shoppingSQLiteHelper.COLUMN_ITEM_NAME, item.getName());
        inventoryItemValues.put(shoppingSQLiteHelper.COLUMN_ITEM_CATEGORY,item.getCategory());

        database.insert(shoppingSQLiteHelper.INVENTORY_ITEMS_TABLE,null,inventoryItemValues);

        database.setTransactionSuccessful();
        database.endTransaction();
        close(database);
    }

    public void purgeInventory()
    {
        SQLiteDatabase database = open();
        database.beginTransaction();

        //implementation
        database.delete(shoppingSQLiteHelper.INVENTORY_ITEMS_TABLE, null , null);

        database.setTransactionSuccessful();
        database.endTransaction();
        close(database);
    }

    public InventoryItem getInventoryItem(String itemName)
    {
        InventoryItem item = null;
        SQLiteDatabase database = open();
        Cursor cursor = database.rawQuery(
                "SELECT * FROM " + shoppingSQLiteHelper.INVENTORY_ITEMS_TABLE +
                        " WHERE " + shoppingSQLiteHelper.COLUMN_ITEM_NAME + " = '" + itemName + "'", null);

        if(cursor.moveToFirst())
        {
            do
            {
                item = new InventoryItem(
                        getIntFromColumnName(cursor, BaseColumns._ID),
                        getStringFromColumnName(cursor, ShoppingSQLiteHelper.COLUMN_SLIST_NAME),
                        getStringFromColumnName(cursor, shoppingSQLiteHelper.COLUMN_ITEM_CATEGORY)
                );

            }while(cursor.moveToNext());
        }

        cursor.close();
        close(database);
        return item;
    }


}
