package com.pear.shopz.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.pear.shopz.R;
import com.pear.shopz.adapters.ShoppingItemAdapter;

public class ShopinItemsActivity extends AppCompatActivity {

    private RecyclerView shopinListView;
    private RecyclerView.Adapter listAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private String[] lists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopin_items);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setUpLists();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public void setUpLists()
    {
        lists = new String[]{"Yams", "Eggs","Plantain","Donut","Meat","Milk"};
        shopinListView = (RecyclerView) findViewById(R.id.shopin_list_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        shopinListView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        shopinListView.setLayoutManager(layoutManager);

        // specify an adapter
        listAdapter = new ShoppingItemAdapter(lists, this);
        shopinListView.setAdapter(listAdapter);
//        startActivity();
    }
}
