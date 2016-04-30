package com.pear.shopz.ui;

import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.pear.shopz.R;
import com.pear.shopz.objects.Item;
import com.pear.shopz.objects.ShoppingListItem;
import com.pear.shopz.objects.ShoppingListItemController;

import java.util.ArrayList;

public class EditItemActivity extends AppCompatActivity {

    private AutoCompleteTextView nameTextView, quantityTextView, priceTextView;
    private Spinner categorySpinner;
    FloatingActionButton saveItemFab;

    private int listId = -1;
    private int itemId = -1;
    private String listName ="";
    private String itemName = "";
    private final String DEFAULT_STORE = "Default";
    private final String LISTID = "LISTID";
    private final String LISTNAME = "LISTNAME";
    private final String ITEM_ID = "ITEM_ID";
    private final String ITEM_NAME = "ITEM_NAME";

    private ArrayList<Item> serverData = null;

    private final String SERVERDATA = "SERVERDATA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        Toolbar toolbar = (Toolbar)findViewById(R.id.app_bar_edit);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Bundle extras = getIntent().getExtras();
        if(extras != null)
        {
            listId = extras.getInt(LISTID);
            listName = extras.getString(LISTNAME);
            itemName = extras.getString(ITEM_NAME,"");
            itemId = extras.getInt(ITEM_ID, -1);
            serverData = extras.getParcelableArrayList(SERVERDATA);
        }


        init();
    }

    private void init()
    {
        ShoppingListItemController itemController = new ShoppingListItemController(this,listId);
        final ShoppingListItem item = itemController.getShoppingListItem(itemId);

        String[] timeItems = new String[]{"Other","Meat","Pharmacy","Bakery"}; //spinner array
        saveItemFab = (FloatingActionButton) findViewById(R.id.save_item_fab);
        nameTextView = (AutoCompleteTextView) findViewById(R.id.item_name);
        quantityTextView = (AutoCompleteTextView) findViewById(R.id.quantity_view_edit);
        priceTextView = (AutoCompleteTextView) findViewById(R.id.price_view_edit);
        categorySpinner = (Spinner) findViewById(R.id.category_spinner);


        ArrayAdapter<String>categoryAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, timeItems);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        String[] serverItems = getArray();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,serverItems);
        nameTextView.setAdapter(adapter);
        nameTextView.setThreshold(1);
        nameTextView.setHintTextColor(getResources().getColor(R.color.white));

        //Toast.makeText(this, serverItems[0], Toast.LENGTH_LONG).show();

        //init edit view content
        nameTextView.setText(item.getItemName());
        priceTextView.setText(item.getItemPrice()+"");
        categorySpinner.setAdapter(categoryAdapter);
        categorySpinner.setSelection(item.getItemCategory().trim().equals("")?0:Integer.parseInt(item.getItemCategory()));


        saveItemFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShoppingListItemController shoppingListController = new ShoppingListItemController(EditItemActivity.this,listId);

                if(attemptSave())
                {
                    //update item & save
                    item.setItemName(nameTextView.getText().toString());
                    item.setItemPrice(Double.parseDouble(priceTextView.getText().toString()));
                    item.setItemCategory(String.valueOf(categorySpinner.getSelectedItemPosition()));
                    //TODO add quantity to db

                    shoppingListController.updateShoppingListItem((item));
                    finish();
                }

            }
        });
    }

    private String[] getArray() {

        if (serverData == null || serverData.size() == 0) return new String[0];

        String[] itemNames = new String[serverData.size()];

        for (int i = 0; i < serverData.size(); i++)
        {
            itemNames[i] = serverData.get(i).getName();
        }

        return itemNames;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private boolean attemptSave()
    {
        boolean attempt = true;
        if (nameTextView.getText().toString().isEmpty()) {
            nameTextView.setError(getString(R.string.error_field_required));
            attempt = false;
        }

        return attempt;
    }
}
