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
    public static final String SUPPORTED_STORES_VERSION_NUMBER = "SUPPORTED_STORES_VERSION_NUMBER";

    public static ArrayList<String> getStoreOptions(Context context)
    {
        // Restore preferences
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);

        Gson gson = new Gson();
        String json = settings.getString(STORE_OPTIONS, null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        ArrayList<String> storeOptions = gson.fromJson(json, type);

        //init store options
        if(storeOptions == null) {
            storeOptions = new ArrayList<String>(Arrays.asList("General Store", "Superstore(CA)"));
            saveArrayListToSharedPreferenceFile(context, storeOptions);
        }

        return storeOptions;
    }

    public static ArrayList<String> getListOFSupportedStores()
    {
        return new ArrayList<>(Arrays.asList("superstore(ca)"));
    }

    public static int getSupportedStoreVersionNumber(Context context)
    {
        // Restore preferences
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        int versionNumber = settings.getInt(SUPPORTED_STORES_VERSION_NUMBER,0);

        //init version number in pref. file
        if(versionNumber == 0)
            setSupportedStoreVersionNumber(context,versionNumber);

        return versionNumber;
    }

    public static void setSupportedStoreVersionNumber(Context context, int versionNumber)
    {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(SUPPORTED_STORES_VERSION_NUMBER,versionNumber);

        // Commit the edits!
        editor.commit();
    }

    public static void saveArrayListToSharedPreferenceFile(Context context, ArrayList<String> arrayList)
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
