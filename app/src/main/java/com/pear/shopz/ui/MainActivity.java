package com.pear.shopz.ui;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ActionMode;
import android.view.Display;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.pear.shopz.R;
import com.pear.shopz.adapters.ShoppingListAdapter;
import com.pear.shopz.objects.InventoryItemController;
import com.pear.shopz.objects.Settings;
import com.pear.shopz.objects.ShoppingList;
import com.pear.shopz.objects.ShoppingListController;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


public class MainActivity extends AppCompatActivity implements ShoppingListAdapter.ViewHolder.ClickListener, ShoppingListAdapter.OnCompleteListener{

    private RecyclerView shopinListView;
    private ShoppingListAdapter listAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ShoppingListController shoppingListController;
    private ArrayList<ShoppingList> lists;
    private ArrayList<ShoppingList> tempLists;
    private ActionMode actionMode;
    private GestureDetectorCompat  gestureDetector;
    private Toolbar toolbar;
    private ActionModeCallback actionModeCallback = new ActionModeCallback();

    private final String LISTID = "LISTID";
    private final String LISTNAME = "LISTNAME";
    private final String SERVERDATA = "SERVERDATA";

    //private ArrayList<Item> serverData = null;

    private FloatingActionButton addFab;

    //stale endpoint, leave for reference
    //private final String url = "http://ec2-52-39-22-233.us-west-2.compute.amazonaws.com/api/";

    //have to replace these with a more appropriate implementation
    private final String itemsJsonUrl = "https://api.myjson.com/bins/14ayyx";
    private final String versionJson = "https://api.myjson.com/bins/1cck4h";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //ad view
        AdView mAdView = (AdView) findViewById(R.id.adView_home);
        AdRequest adRequest = new AdRequest.Builder().build(); //deployment


//        //for testing
//        AdRequest adRequest = new AdRequest.Builder()
//                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
//                .addTestDevice("83C91F7145125BB8C343C40C7EE10194")  // An example device ID
//                .addTestDevice("EED7C07AB7B885F7DAAD60C3A0788296") //second test device
//                .build();

        mAdView.loadAd(adRequest);

        //background color of activity
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.main_background, null));
        else
            getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.main_background));

        //status bar color for only post Lollipop devices
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark, null));
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        addFab = (FloatingActionButton) findViewById(R.id.fab);
        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddListActivity.class);
                startActivity(intent);
                //finish();
            }
        });


        //check for version update, then update if new version on response
        try {
            versionUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        setUpList(null);
    }

    private void run() throws Exception{

//        final MediaType JSON
//                = MediaType.parse("application/json; charset=utf-8");

        if (isNetworkAvailable())
        {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(itemsJsonUrl)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    //alertUserAboutFailure();
                    Log.e("NETWORK", "network failure");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        //alertUserAboutFailure();
                        Log.e("NETWORK", "network failure");
                        throw new IOException("Unexpected code " + response);
                    }

                    //res[0] = response.body().toString();
                    try {
                        process(response.body().string());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            });
        }
        else
        {
            Log.e("NETWORK", "network unavailable");
            //Toast.makeText(this, "Network unavailable", Toast.LENGTH_LONG).show();
        }

    }

    private void versionUpdate()
    {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(versionJson)
                .build();

        final boolean[] result = {false};

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                //alertUserAboutFailure();
                Log.e("NETWORK", "network failure");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e("NETWORK", "network failure");
                    //alertUserAboutFailure();
                    throw new IOException("Unexpected code " + response);
                }

                //res[0] = response.body().toString();
                try {
                    JSONObject jsonData = new JSONObject(response.body().string());

                    if ((float)jsonData.getDouble("version") > Settings.getSupportedStoreVersionNumber(MainActivity.this))
                    {
                        run();

                        //save new version number
                        Settings.setSupportedStoreVersionNumber(MainActivity.this, (float)jsonData.getDouble("version"));
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });

    }

    private void alertUserAboutFailure() {
        Toast.makeText(this, "Network Failure", Toast.LENGTH_LONG).show();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;

        if(networkInfo != null && networkInfo.isConnected())
        {
            isAvailable = true;
        }

        return isAvailable;

    }

    private void process(String data) throws JSONException {
        JSONObject jsonData = new JSONObject(data);
        InventoryItemController inventoryItemController = new InventoryItemController(this);

        //purge db before fresh insert
        inventoryItemController.purgeDB();

        inventoryItemController.addToDB(jsonData);

    }


    public void setUpList(ArrayList<ShoppingList> currLists)
    {
        shoppingListController = new ShoppingListController(this);
        lists = currLists == null? shoppingListController.getShoppingLists() : currLists;
        shopinListView = (RecyclerView) findViewById(R.id.shopin_list_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the cardView size of the RecyclerView
        //capture the size of the devices screen
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int screenHeight = displaymetrics.heightPixels;

        shopinListView.setHasFixedSize(true);



        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        params.height = (int) (0.7 * screenHeight);
        shopinListView.setLayoutParams(params);

        // use a linear cardView manager
        layoutManager = new LinearLayoutManager(this);
        shopinListView.setLayoutManager(layoutManager);

        // specify an adapter
        listAdapter = new ShoppingListAdapter(lists, this, this);
        shopinListView.setAdapter(listAdapter);

        //GridLayout
        //shopinListView.setLayoutManager(new GridLayoutManager(this, 2));
        //listAdapter = new ShoppingListAdapter(lists, this, this);
        //shopinListView.setAdapter(listAdapter);

        shopinListView.setItemAnimator(new DefaultItemAnimator());
    }

//    //check if List is up to date & update it
//    public void upDateList()
//    {
//
//        Bundle extras = getIntent().getExtras();
//
//        if(extras!=null)
//        {
//            ArrayList<ShoppingList> currList = new ShoppingListController(this).getShoppingLists();
//
//            if (this.actionMode != null)
//                this.actionMode.finish();
//
//            setUpList(currList);
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //if list has changed, update it
        //upDateList();
        //setUpList(null);
    }


    @Override
    public void onItemClicked(int position) {
        if (actionMode != null) {
            toggleSelection(position);
        }
        else
        {
            Intent intent = new Intent(MainActivity.this, ShopinItemsActivity.class);
            intent.putExtra(LISTID, lists.get(position).getListID());
            intent.putExtra(LISTNAME, lists.get(position).getListName());
            //intent.putParcelableArrayListExtra(SERVERDATA, serverData);
            startActivity(intent);
        }
    }

    @Override
    public boolean onItemLongClicked(int position) {
        int count = listAdapter.getSelectedItemCount();

        if (actionMode == null) {
            actionMode = startActionMode(actionModeCallback);
        }

        toggleSelection(position);
        return true;
    }

    private void toggleSelection(int position) {
        listAdapter.toggleSelection(position);
        int count = listAdapter.getSelectedItemCount();

        if (count == 0) {
            actionMode.finish();
        } else {

            //disable edit & share icon if multiple items selected
            if(count > 1)
            {
                findViewById(R.id.edit_menu_item).setVisibility(View.GONE);
                findViewById(R.id.share_menu_item).setVisibility(View.GONE);
            }
            else
            {
                findViewById(R.id.edit_menu_item).setVisibility(View.VISIBLE);
                findViewById(R.id.share_menu_item).setVisibility(View.VISIBLE);
            }

            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
    }

    private ArrayList<ShoppingList> getSelectedLists()
    {
        ArrayList<ShoppingList> result = new ArrayList<ShoppingList>();
        List<Integer> selection = listAdapter.getSelectedItems();
        for(Integer i: selection)
            result.add(lists.get(i));

        return result;
    }

    //used to update main activity view when is empty
    @Override
    public void onComplete(int dataSetSize) {
        updateLayoutIfListEmpty(dataSetSize);
    }

    private class ActionModeCallback implements ActionMode.Callback {
        @SuppressWarnings("unused")
        private final String TAG = ActionModeCallback.class.getSimpleName();
        private int statusBarColor;

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate (R.menu.menu_shopping_lists, menu);

            //hold current color of status bar(post-lollipop)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                statusBarColor = getWindow().getStatusBarColor();
                //set your gray color
                getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
            }
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.delete_menu_item:
                    //delete from db & list
                    shoppingListController.deleteShoppingLists(getSelectedLists());
                    listAdapter.removeItems(listAdapter.getSelectedItems());
                    mode.finish();
                    return true;
                case R.id.edit_menu_item:
                    Intent intent = new Intent(MainActivity.this,AddListActivity.class);
                    intent.putExtra(LISTID, lists.get(listAdapter.getSelectedItems().get(0)).getListID());
                    intent.putExtra(LISTNAME, lists.get(listAdapter.getSelectedItems().get(0)).getListName());
                    //intent.putParcelableArrayListExtra(SERVERDATA, serverData);
                    startActivity(intent);
                    finish();
                    return true;
                case R.id.share_menu_item:
                    //if list is not empty share it, else show snackbar
                    if(lists.get(listAdapter.getSelectedItems().get(0)).getListSize(MainActivity.this) > 0)
                    {
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, lists.get(listAdapter.getSelectedItems().get(0)).getFormattedShoppingListString(MainActivity.this));
                        sendIntent.setType("text/plain");
                        startActivity(Intent.createChooser(sendIntent, "Share Grocery List..."));
                    }else
                    {
                        //Snack bar to indicate list is empty
                        RelativeLayout rootView = (RelativeLayout) findViewById(R.id.main_layout);
                        final Snackbar snackBar = Snackbar.make(rootView, "Your List is Empty :)", Snackbar.LENGTH_LONG);
                        snackBar.setAction("Dismiss", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                snackBar.dismiss();
                                addFab.setTranslationY(0);
                            }
                        });
                        snackBar.show();
                    }
                    return true;
                default:
                    return false;

            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            listAdapter.clearSelection();
            actionMode = null;

            //return to "old" color of status bar
            //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
              //  getWindow().setStatusBarColor(statusBarColor);
        }
    }

    //update view with placeholder if list is empty
    public void updateLayoutIfListEmpty(int dataSetSize)
    {
        RelativeLayout emptyLayout = (RelativeLayout) findViewById(R.id.empty_layout);
        if(dataSetSize == 0 && emptyLayout.getVisibility() ==View.GONE)
            emptyLayout.setVisibility(View.VISIBLE);
        else if(dataSetSize > 0 && emptyLayout.getVisibility() ==View.VISIBLE)
            emptyLayout.setVisibility(View.GONE);
    }

}

