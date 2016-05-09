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
            saveArrayListToSharedPreferenceFile(context, storeOptions, STORE_OPTIONS);
        }

        return storeOptions;
    }

    public static ArrayList<String> getListOFSupportedStores()
    {
        return new ArrayList<>(Arrays.asList("superstore(ca)"));
    }

    public static float getSupportedStoreVersionNumber(Context context)
    {
        // Restore preferences
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        float versionNumber = settings.getFloat(SUPPORTED_STORES_VERSION_NUMBER,0);

        //init version number in pref. file
        if(versionNumber == 0)
            setSupportedStoreVersionNumber(context,versionNumber);

        return versionNumber;
    }

    public static void setSupportedStoreVersionNumber(Context context, float versionNumber)
    {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putFloat(SUPPORTED_STORES_VERSION_NUMBER, versionNumber);

        // Commit the edits!
        editor.commit();
    }

    public static void saveArrayListToSharedPreferenceFile(Context context, ArrayList<String> arrayList, String TAG)
    {
        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        Gson gson = new Gson();
        String json = gson.toJson(arrayList);
        editor.putString(TAG, json);

        // Commit the edits!
        editor.commit();
    }
}
