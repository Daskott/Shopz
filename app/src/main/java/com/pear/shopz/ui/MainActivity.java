package com.pear.shopz.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.ArraySet;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.pear.shopz.R;
import com.pear.shopz.adapters.ShoppingListAdapter;
import com.pear.shopz.database.ShoppingDataSource;
import com.pear.shopz.objects.ShoppingList;
import com.pear.shopz.objects.ShoppingListController;
import com.pear.shopz.ui.AddListActivity;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private RecyclerView shopinListView;
    private RecyclerView.Adapter listAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ShoppingListController shoppingListController;
    private ArrayList<ShoppingList>lists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                  //      .setAction("Action", null).show();
                Intent intent = new Intent(MainActivity.this, AddListActivity.class);
                startActivity(intent);
            }
        });

        setUpLists();
    }

    public void setUpLists()
    {
        shoppingListController = new ShoppingListController(this);
        lists = shoppingListController.getShoppingLists(); //new ArrayList<String>(Arrays.asList("Superstore", "Walmart", "Soebeys"));
        shopinListView = (RecyclerView) findViewById(R.id.shopin_list_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        shopinListView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        shopinListView.setLayoutManager(layoutManager);

        // specify an adapter
        listAdapter = new ShoppingListAdapter(lists, this);
        shopinListView.setAdapter(listAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
}
