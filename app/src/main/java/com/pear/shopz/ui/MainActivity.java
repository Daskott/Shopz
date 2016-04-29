package com.pear.shopz.ui;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


import com.pear.shopz.R;
import com.pear.shopz.adapters.ShoppingListAdapter;
import com.pear.shopz.objects.Item;
import com.pear.shopz.objects.ShoppingList;
import com.pear.shopz.objects.ShoppingListController;
import com.pear.shopz.objects.ShoppingListItem;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity implements ShoppingListAdapter.ViewHolder.ClickListener{

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

    private ArrayList<Item> serverData = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddListActivity.class);
                startActivity(intent);
                //finish();
            }
        });

        //test network
        try {
            run();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //setUpList(null);
    }

    private void run() throws Exception{
        String url = "http://ec2-52-39-22-233.us-west-2.compute.amazonaws.com/api/";
        final MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");

        if (isNetworkAvailable() && serverData == null)
        {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url + "items")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    alertUserAboutFailure();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        alertUserAboutFailure();
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
            Toast.makeText(this, "Network unavailable", Toast.LENGTH_LONG).show();
        }
        setUpList(null);

        //String[] list = shoppingListItemController.getListArray();//{"Milk", "Baby", "Fish"};

//        if(list.length != 0)
//        {
//            JSONObject json = new JSONObject();
//
//            json.put("list", new JSONArray(Arrays.asList(list)));
//
//            //RequestBody body = RequestBody.create(JSON, String.valueOf(json));
//            Log.v("JSON", String.valueOf(json));
//            Request request = new Request.Builder()
//                    .url(url + "items")
//                    .build();
//
//            client.newCall(request).enqueue(new Callback() {
//                @Override
//                public void onFailure(Call call, IOException e) {
//                    e.printStackTrace();
//                }
//
//                @Override
//                public void onResponse(Call call, Response response) throws IOException {
//                    if (!response.isSuccessful())
//                        throw new IOException("Unexpected code " + response);
//
//                    //res[0] = response.body().toString();
//                    try {
//                        print(response.body().string());
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//
//                }
//            });
//        }
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
       // Log.v("NetworkDATA", jsonData.getString("data"));
        addNetworkData(jsonData);
        //shoppingListItemController.addNetworkData(jsonData);
    }

    public void addNetworkData(JSONObject json) throws JSONException {
        JSONArray listArray = json.getJSONArray("data");

        serverData = new ArrayList<Item>();
        for(int i=0; i<listArray.length(); i++)
        {
            JSONObject json_data = listArray.getJSONObject(i);
            serverData.add(new Item(json_data.getString("name"), json_data.getString("aisle")));
        }

    }

    public void setUpList(ArrayList<ShoppingList> currLists)
    {
        shoppingListController = new ShoppingListController(this);
        lists = currLists == null? shoppingListController.getShoppingLists() : currLists;
        shopinListView = (RecyclerView) findViewById(R.id.shopin_list_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the cardView size of the RecyclerView
        shopinListView.setHasFixedSize(true);

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
        if (id == R.id.action_settings) {
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
            intent.putParcelableArrayListExtra(SERVERDATA, serverData);
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

            //disable edit if multiple items selected
            if(count > 1)
                findViewById(R.id.edit_menu_item).setVisibility(View.GONE);
            else
                findViewById(R.id.edit_menu_item).setVisibility(View.VISIBLE);

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
                    intent.putParcelableArrayListExtra(SERVERDATA, serverData);
                    startActivity(intent);
                    finish();
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

}

