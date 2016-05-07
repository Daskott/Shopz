package com.pear.shopz.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by edmondcotterell on 2016-04-21.
 */
public class ShoppingSQLiteHelper extends SQLiteOpenHelper{

    private static final String DB_NAME = "shopin_list.db";
    private static final int DB_VERSION = 1;

    //init Shopping list table
    public static final String SHOPPING_LIST_TABLE = "SHOPPING_LISTS";
    public static final String COLUMN_SLIST_NAME = "NAME";
    public static final String COLUMN_SLIST_STORE = "STORE";

    private static String CREATE_SHOPPING_LIST =
            "CREATE TABLE "+SHOPPING_LIST_TABLE+ "("+
                    BaseColumns._ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                    COLUMN_SLIST_NAME+" TEXT,"+
                    COLUMN_SLIST_STORE+" TEXT)";

    //init Grocery item table
    public static final String GROCERY_ITEMS_TABLE = "GROCERY_ITEMS";
    public static final String COLUMN_ITEM_NAME = "NAME";
    public static final String COLUMN_ITEM_PRICE = "PRICE";
    public static final String COLUMN_ITEM_CATEGORY = "CATEGORY";
    public static final String COLUMN_ITEM_AISLE = "AISLE";
    public static final String COLUMN_ITEM_BOUGHT = "BOUGHT";
    public static final String COLUMN_ITEM_QUANTITY = "QUANTITY";
    public static final String COLUMN_FOREIGN_KEY_SLIST_ID = "SHOPPING_LIST_ID";

    private static String CREATE_GROCERY_ITEMS =
            "CREATE TABLE "+GROCERY_ITEMS_TABLE+ "("+
                    BaseColumns._ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                    COLUMN_ITEM_NAME+" TEXT,"+
                    COLUMN_ITEM_PRICE+" REAL, "+
                    COLUMN_ITEM_CATEGORY+" TEXT, "+
                    COLUMN_ITEM_AISLE+" TEXT, "+
                    COLUMN_ITEM_BOUGHT+" INTEGER, "+
                    COLUMN_ITEM_QUANTITY+" INTEGER, "+
                    COLUMN_FOREIGN_KEY_SLIST_ID +" INTEGER, "+
                    "FOREIGN KEY("+ COLUMN_FOREIGN_KEY_SLIST_ID +") REFERENCES  SHOPPING_LISTS(_ID))";

    //init Grocery item table
    public static final String INVENTORY_ITEMS_TABLE = "INVENTORY_ITEMS";

    private static String CREATE_INVENTORY_ITEMS =
            "CREATE TABLE "+INVENTORY_ITEMS_TABLE+ "("+
                    BaseColumns._ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                    COLUMN_ITEM_NAME+" TEXT,"+
                    COLUMN_ITEM_CATEGORY+" TEXT)";

    public ShoppingSQLiteHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SHOPPING_LIST);
        db.execSQL(CREATE_GROCERY_ITEMS);
        db.execSQL(CREATE_INVENTORY_ITEMS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
