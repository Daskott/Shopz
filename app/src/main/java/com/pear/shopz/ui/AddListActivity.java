package com.pear.shopz.ui;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.pear.shopz.R;
import com.pear.shopz.objects.Item;
import com.pear.shopz.objects.ShoppingList;
import com.pear.shopz.objects.ShoppingListController;

import java.util.ArrayList;

public class AddListActivity extends AppCompatActivity {

    private Button saveButton;
    private TextView nameTextView;

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
        saveButton = (Button)findViewById(R.id.save_button_ls);
        nameTextView = (TextView)findViewById(R.id.list_name);

        //check if its an edit/add
        if(listId != -1)
            nameTextView.setText(listName);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShoppingListController shoppingListController = new ShoppingListController(AddListActivity.this);
                Intent intent;

                if(attemptSave()) {

                    //check if its an edit/add
                    if(listId == -1)
                    {
                        listId = shoppingListController.addShoppingList(new ShoppingList(-1, nameTextView.getText().toString(), DEFAULT_STORE));
                        intent = new Intent(AddListActivity.this, ShopinItemsActivity.class);
                        intent.putExtra(LISTNAME,nameTextView.getText().toString());
                        intent.putExtra(LISTID, listId);
                        intent.putParcelableArrayListExtra(SERVERDATA, serverData);
                    }
                    else
                    {
                        shoppingListController.updateShoppingList(new ShoppingList(listId, nameTextView.getText().toString(), DEFAULT_STORE));
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

}
