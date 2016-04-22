package com.pear.shopz.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;


import com.pear.shopz.R;
import com.pear.shopz.adapters.ShoppingListAdapter;
import com.pear.shopz.objects.ShoppingList;
import com.pear.shopz.objects.ShoppingListController;


import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements ShoppingListAdapter.ViewHolder.ClickListener{

    private RecyclerView shopinListView;
    private ShoppingListAdapter listAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ShoppingListController shoppingListController;
    private ArrayList<ShoppingList> lists;
    private ActionMode actionMode;
    private GestureDetectorCompat  gestureDetector;
    private Toolbar toolbar;
    private ActionModeCallback actionModeCallback = new ActionModeCallback();

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
            }
        });

        setActionListeners();
    }

    public void setActionListeners() {
    }

    public void setUpLists() {
        shoppingListController = new ShoppingListController(this);
        lists = shoppingListController.getShoppingLists();
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
        shopinListView.setItemAnimator(new DefaultItemAnimator());

    }

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
    protected void onResume() {
        super.onResume();

        setUpLists();
    }


    @Override
    public void onItemClicked(int position) {
        if (actionMode != null) {
            toggleSelection(position);
        }
        else
        {
            Intent intent = new Intent(MainActivity.this, ShopinItemsActivity.class);
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
                    // TODO: actually remove items
                    listAdapter.removeItems(listAdapter.getSelectedItems());
                    mode.finish();
                    return true;
                case R.id.edit_menu_item:
                    //TODO: Actually edit item
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

