package com.pear.shopz.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.pear.shopz.objects.ShoppingList;

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

    //*****DO ANOTHER QUERY TO GET TOTAL (BOUGHT_ITEMS/ TOTAL_ITEMS)


    private int getIntFromColumnName(Cursor cursor, String columnName)
    {
        int columnIndex = cursor.getColumnIndex(columnName);
        return cursor.getInt(columnIndex);
    }

    private String getStringFromColumnName(Cursor cursor, String columnName)
    {
        int columnIndex = cursor.getColumnIndex(columnName);
        return cursor.getString(columnIndex);
    }

    //add new shopping list to database
    public void createList(ShoppingList shoppingList){

        SQLiteDatabase database = open();
        database.beginTransaction();

        //implementation
        ContentValues shoppingListValues = new ContentValues();
        shoppingListValues.put(shoppingSQLiteHelper.COLUMN_ITEM_NAME, shoppingList.getListName());
        shoppingListValues.put(shoppingSQLiteHelper.COLUMN_SLIST_STORE,shoppingList.getStore());
        database.insert(shoppingSQLiteHelper.SHOPPING_LIST_TABLE,null,shoppingListValues);

        database.setTransactionSuccessful();
        database.endTransaction();
        close(database);
    }
}
