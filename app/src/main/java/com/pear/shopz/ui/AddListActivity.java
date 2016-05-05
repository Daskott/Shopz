package com.pear.shopz.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.pear.shopz.R;
import com.pear.shopz.objects.Item;
import com.pear.shopz.objects.Settings;
import com.pear.shopz.objects.ShoppingList;
import com.pear.shopz.objects.ShoppingListController;

import java.util.ArrayList;


public class AddListActivity extends AppCompatActivity {

    private TextView saveButton;
    private TextView nameTextView;
    private ImageView addStoreButton;
    private Spinner storeSpinner;

    private int listId = -1;
    private String listName = "";
    private final String DEFAULT_STORE = "Default";

    private final String LISTID = "LISTID";
    private final String LISTNAME = "LISTNAME";

    private ArrayList<Item> serverData = null;

    private final String SERVERDATA = "SERVERDATA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_list);
        Bundle extras = getIntent().getExtras();

        if(extras!=null) {
            listId = extras.getInt(LISTID);
            listName = extras.getString(LISTNAME);
            serverData = extras.getParcelableArrayList(SERVERDATA);
        }

        init();
    }

    private void init()
    {
        ShoppingListController listController = new ShoppingListController(this);
        final ArrayList<String> currStoreOptions = Settings.getStoreOptions(this);//spinner/dropdown list
        //ShoppingList currList = new ShoppingList(listController.get(listId))***get shopping list with id
        saveButton = (TextView)findViewById(R.id.save_button_ls);
        nameTextView = (TextView)findViewById(R.id.list_name);
        storeSpinner = (Spinner) findViewById(R.id.store_spinner);
        addStoreButton = (ImageView)findViewById(R.id.add_store_icon);

        //check if its an edit/add
        if(listId != -1)
            nameTextView.setText(listName);

        //initial store options
        ArrayAdapter<String> storeAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, currStoreOptions);
        storeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        storeSpinner.setAdapter(storeAdapter);
        //storeSpinner.setSelection(Integer.parseInt(currList.getStore().trim())); //index of store on dropdown is what we store

        addStoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Add Store dialog
                View viewInflated = getLayoutInflater().inflate(R.layout.input_frame, null);
                final TextView storeNameInput = (TextView)viewInflated.findViewById(R.id.input);
                AlertDialog.Builder builder = new AlertDialog.Builder(AddListActivity.this);
                builder.setTitle("Add Store").setView(viewInflated)
                        .setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //add new store to store options
                        if(attemptSave(storeNameInput)) {

                            //update & save list
                            currStoreOptions.add(capitalize(storeNameInput.getText().toString()));
                            Settings.saveSettings(AddListActivity.this,currStoreOptions);

                            //create new spinner adapter & update ui
                            ArrayAdapter<String> storeAdapter = new ArrayAdapter<String>(AddListActivity.this,android.R.layout.simple_spinner_item, currStoreOptions);
                            storeAdapter.notifyDataSetChanged();
                            storeSpinner.setAdapter(storeAdapter);
                            storeSpinner.setSelection(currStoreOptions.size()-1);
                        }
                    }
                }).setNegativeButton("CANCEL", null);
                AlertDialog dialog = builder.create();

                if (viewInflated.getParent() != null)
                    ((ViewGroup) viewInflated.getParent()).removeView(viewInflated); // <- fix

                dialog.show();
              }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShoppingListController shoppingListController = new ShoppingListController(AddListActivity.this);
                Intent intent;

                if(attemptSave()) {

                    ShoppingList list = new ShoppingList(listId, nameTextView.getText().toString(), String.valueOf(storeSpinner.getSelectedItemPosition()));

                    //create new shopping list
                    if(list.getListID() == -1)
                    {
                        list.setListID(shoppingListController.addShoppingList(list)); //add list to db & get id
                        intent = new Intent(AddListActivity.this, ShopinItemsActivity.class);
                        intent.putExtra(LISTNAME,nameTextView.getText().toString());
                        intent.putExtra(LISTID, list.getListID());
                        intent.putParcelableArrayListExtra(SERVERDATA, serverData);
                    }
                    //update existing shopping list
                    else
                    {
                        shoppingListController.updateShoppingList(list);
                        intent = new Intent(AddListActivity.this, MainActivity.class);
                    }
                    startActivity(intent);
                    finish();
                }
            }
        });
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

    private boolean attemptSave(TextView nameTextView)
    {
        boolean attempt = true;
        if (nameTextView.getText().toString().isEmpty()) {
            nameTextView.setError(getString(R.string.error_field_required));
            attempt = false;
        }

        return attempt;
    }

    public String capitalize(String word)
    {
        if(word.length() == 1)
            return word.toUpperCase();

        return word.substring(0,1).toUpperCase()+""+word.substring(1).toLowerCase();
    }

}
