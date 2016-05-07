package com.pear.shopz.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

    //private ArrayList<Item> serverData = null;

    private final String SERVERDATA = "SERVERDATA";

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_list);
        Bundle extras = getIntent().getExtras();

        if(extras!=null) {
            listId = extras.getInt(LISTID);
            listName = extras.getString(LISTNAME);
            //serverData = extras.getParcelableArrayList(SERVERDATA);
        }

        init();
    }

    private void init()
    {
        final ShoppingListController listController = new ShoppingListController(this);
        final ArrayList<String> currStoreOptions = Settings.getStoreOptions(this);//spinner/dropdown list
        ShoppingList currList = null;//new ShoppingList(listController.get(listId))***get shopping list with id
        saveButton = (TextView)findViewById(R.id.save_button_ls);
        nameTextView = (TextView)findViewById(R.id.list_name);
        storeSpinner = (Spinner) findViewById(R.id.store_spinner);
        addStoreButton = (ImageView)findViewById(R.id.add_store_icon);

        intent = new Intent(AddListActivity.this, MainActivity.class);

        //initial store options
        ArrayAdapter<String> storeAdapter = CreateSpinnerAdapter(currStoreOptions);
        storeSpinner.setAdapter(storeAdapter);

        //check if its an edit
        if(listId != -1) {
            nameTextView.setText(listName);
            saveButton.setText("UPDATE");
            currList = listController.getShoppingList(listId);
            storeSpinner.setSelection(Integer.parseInt(currList.getStore())); //set spinner
        }

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
                            ArrayAdapter<String> storeAdapter = CreateSpinnerAdapter(currStoreOptions);
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

                if(attemptSave()) {

                    ShoppingList list = new ShoppingList(listId, nameTextView.getText().toString(), String.valueOf(storeSpinner.getSelectedItemPosition()));

                    //create new shopping list
                    if(list.getListID() == -1)
                    {
                        list.setListID(shoppingListController.addShoppingList(list)); //add list to db & get id
                        intent = new Intent(AddListActivity.this, ShopinItemsActivity.class);
                        intent.putExtra(LISTNAME,nameTextView.getText().toString());
                        intent.putExtra(LISTID, list.getListID());
                        //intent.putParcelableArrayListExtra(SERVERDATA, serverData);
                    }
                    //update existing shopping list
                    else
                    {
                        shoppingListController.updateShoppingList(list);
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

    public  ArrayAdapter<String> CreateSpinnerAdapter(final ArrayList<String> list)
    {
        return new ArrayAdapter<String>(AddListActivity.this,R.layout.spinner_row, R.id.spinner_row_text,list)
        {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                TextView textView = (TextView)view.findViewById(R.id.spinner_row_text);
                //ImageView icon = (ImageView) spinnerLayout.findViewById(R.id.spinner_row_icon);
                String storeName = list.get(position);


                /*
                * set store text to blue, if its supported
                * */
                if(Settings.getListOFSupportedStores().contains(storeName.toLowerCase()))
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        textView.setTextColor(getResources().getColor(R.color.blue, null));
                    else
                        textView.setTextColor(getResources().getColor(R.color.blue));
                }

                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);

                TextView textView = (TextView)view.findViewById(R.id.spinner_row_text);
                //ImageView icon = (ImageView) spinnerLayout.findViewById(R.id.spinner_row_icon);
                String storeName = list.get(position);


                /*
                * set store text to blue, if its supported, else black
                * */
                if(Settings.getListOFSupportedStores().contains(storeName.toLowerCase()))
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        textView.setTextColor(getResources().getColor(R.color.blue, null));
                    else
                        textView.setTextColor(getResources().getColor(R.color.blue));
                }
                else
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        textView.setTextColor(getResources().getColor(R.color.grey_icon, null));
                    else
                        textView.setTextColor(getResources().getColor(R.color.grey_icon));
                }

                return view;
            }
        };
    }

    public String capitalize(String word)
    {
        if(word.length() == 1)
            return word.toUpperCase();

        return word.substring(0,1).toUpperCase()+""+word.substring(1).toLowerCase();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //go back to main activity
        startActivity(intent);
        finish();
    }
}
