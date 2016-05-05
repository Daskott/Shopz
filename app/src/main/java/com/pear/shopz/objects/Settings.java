package com.pear.shopz.objects;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by cottee2 on 5/5/2016.
 */
public class Settings {

    public static final String PREFS_NAME = "Pear_Shopz";
    public static final String STORE_OPTIONS = "STORE_OPTIONS";

    public static ArrayList<String> getStoreOptions(Context context)
    {
        // Restore preferences
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);

        Gson gson = new Gson();
        String json = settings.getString(STORE_OPTIONS, null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        ArrayList<String> storeOptions = gson.fromJson(json, type);

        if(storeOptions == null) {
            //init store options
            storeOptions = new ArrayList<String>(Arrays.asList("General Store", "Superstore(CA)"));
            saveSettings(context, storeOptions);
        }

        return storeOptions;
    }

    public static void saveSettings(Context context, ArrayList<String> arrayList)
    {
        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        Gson gson = new Gson();
        String json = gson.toJson(arrayList);
        editor.putString(STORE_OPTIONS, json);

        // Commit the edits!
        editor.commit();
    }
}
