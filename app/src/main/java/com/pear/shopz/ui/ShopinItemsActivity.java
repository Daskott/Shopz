package com.pear.shopz.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.pear.shopz.R;
import com.pear.shopz.adapters.ShoppingItemAdapter;
import com.pear.shopz.objects.ShoppingListItem;
import com.pear.shopz.objects.ShoppingListItemController;

import java.util.ArrayList;
import java.util.List;

public class ShopinItemsActivity extends AppCompatActivity  implements ShoppingItemAdapter.ViewHolder.ClickListener{

    private RecyclerView shopinListView;
    private ShoppingItemAdapter listAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<ShoppingListItem> lists;
    private ActionModeCallback actionModeCallback = new ActionModeCallback();
    private ActionMode actionMode;
    private ShoppingListItemController shoppingListItemController;

    private  int listId = -1;
    private String listName = "";

    private final String LISTID = "LISTID";
    private final String ITEM_ID = "ITEM_ID";
    private final String ITEM_NAME = "ITEM_NAME";
    private final String LISTNAME = "LISTNAME";

    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopin_items);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        Bundle extras = getIntent().getExtras();

        if(extras != null)
        {
            listId = extras.getInt(LISTID);
            listName = extras.getString(LISTNAME);
        }

        //set title bar
        String title = listName.substring(0,1).toUpperCase()+""+listName.substring(1).toLowerCase();
        ((CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout)).setTitle(title);
        toolbar = (Toolbar) findViewById(R.id.app_bar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToMain();
            }
        });

        //list view
        shopinListView = (RecyclerView) findViewById(R.id.shopin_list_view);
        setUpLists(null);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShopinItemsActivity.this, AddItemActivity.class);
                intent.putExtra(LISTID,listId);
                intent.putExtra(LISTNAME,listName);
                startActivity(intent);
                finish();
            }
        });

    }

    public void setUpLists( ArrayList<ShoppingListItem> currList)
    {

        shoppingListItemController = new ShoppingListItemController(this,listId);
        lists = currList == null? shoppingListItemController.getShoppingListItems() : currList;

        // use this setting to improve performance if you know that changes
        // in content do not change the cardView size of the RecyclerView
        shopinListView.setHasFixedSize(true);

        // use a linear cardView manager
        layoutManager = new LinearLayoutManager(this);
        shopinListView.setLayoutManager(layoutManager);

        // specify an adapter
        listAdapter = new ShoppingItemAdapter(lists, this, this);
        shopinListView.setAdapter(listAdapter);

        shopinListView.setItemAnimator(new DefaultItemAnimator());
    }

    public void backToMain()
    {
        Intent intent = new Intent(ShopinItemsActivity.this,MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        backToMain();
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
    public void onItemClicked(int position) {
        if (actionMode != null) {
            toggleSelection(position);
        }
        else{
            //update adapter & db indicating whether item is bought or not
            listAdapter.toggleItemBought(position);
            ShoppingListItemController itemController= new ShoppingListItemController(ShopinItemsActivity.this,listId);
            itemController.updateShoppingListItem(listAdapter.getmDataset().get(position));
        }
    }

    @Override
    public boolean onItemLongClicked(int position) {
        int count = listAdapter.getSelectedItemCount();

        if (actionMode == null) {
            actionMode = startActionMode(actionModeCallback);
            toolbar.setVisibility(View.INVISIBLE);
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


    //check if List is up to date & update it
    public void upDateList()
    {
        ArrayList<ShoppingListItem> currList = new ShoppingListItemController(this,listId).getShoppingListItems();

        if(shopinListView != null && currList.size() != lists.size())
        {
            if (this.actionMode != null)
                this.actionMode.finish();

            listAdapter.addItem(currList.get(currList.size()-1));
            //setUpLists(currList);
            //listAdapter.notifyItemInserted(lists.size()-1);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //upDateList();
    }

    private ArrayList<ShoppingListItem> getSelectedItems()
    {
        ArrayList<ShoppingListItem> result = new ArrayList<ShoppingListItem>();
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
                    shoppingListItemController.deleteShoppingListItems(getSelectedItems());
                    listAdapter.removeItems(listAdapter.getSelectedItems());
                    mode.finish();
                    return true;
                case R.id.edit_menu_item:
                    Intent intent = new Intent(ShopinItemsActivity.this,AddItemActivity.class);
                    intent.putExtra(LISTID, lists.get(listAdapter.getSelectedItems().get(0)).getListID());
                    intent.putExtra(ITEM_ID, lists.get(listAdapter.getSelectedItems().get(0)).getItemID());
                    intent.putExtra(ITEM_NAME, lists.get(listAdapter.getSelectedItems().get(0)).getItemName());
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
            toolbar.setVisibility(View.VISIBLE);
            //return to "old" color of status bar
            //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            //  getWindow().setStatusBarColor(statusBarColor);
        }
    }
}
