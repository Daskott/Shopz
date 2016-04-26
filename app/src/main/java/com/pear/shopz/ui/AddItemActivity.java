package com.pear.shopz.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.pear.shopz.R;
import com.pear.shopz.objects.ShoppingListItem;
import com.pear.shopz.objects.ShoppingListItemController;

public class AddItemActivity extends AppCompatActivity {

    private Button saveButton;
    private TextView nameTextView;

    private int listId = -1;
    private int itemId = -1;
    private String listName ="";
    private String itemName = "";
    private final String DEFAULT_STORE = "Default";
    private final String LISTID = "LISTID";
    private final String LISTNAME = "LISTNAME";
    private final String ITEM_ID = "ITEM_ID";
    private final String ITEM_NAME = "ITEM_NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        Bundle extras = getIntent().getExtras();
        if(extras != null)
        {
            listId = extras.getInt(LISTID);
            listName = extras.getString(LISTNAME);
            itemName = extras.getString(ITEM_NAME,"");
            itemId = extras.getInt(ITEM_ID, -1);
        }


        init();
    }

    private void init()
    {
        saveButton = (Button)findViewById(R.id.save_button_item);
        nameTextView = (TextView)findViewById(R.id.item_name);

        //check if its an edit/add
        if(itemId != -1)
            nameTextView.setText(itemName);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShoppingListItemController shoppingListController = new ShoppingListItemController(AddItemActivity.this,listId);

                if(attemptSave()) {

                    //check if its an edit/add
                    if(itemId == -1) {
                        shoppingListController.addShoppingListItem
                                (new ShoppingListItem(
                                        listId,
                                        -1,
                                        nameTextView.getText().toString(),
                                        0,
                                        "",
                                        "",
                                        0

                                ));

                    }
                    else
                    {
                        shoppingListController.updateShoppingListItem(
                                (new ShoppingListItem(
                                        listId,
                                        itemId,
                                        nameTextView.getText().toString(),
                                        0,
                                        "",
                                        "",
                                        0

                                )));

                    }
                    bacKToList();

                }
            }
        });
    }

    public void bacKToList()
    {
        Intent intent = new Intent(AddItemActivity.this, ShopinItemsActivity.class);
        intent.putExtra(LISTID,listId);
        intent.putExtra(LISTNAME,listName);
        startActivity(intent);
        finish();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        bacKToList();
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
