package com.pear.shopz.ui;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.pear.shopz.R;
import com.pear.shopz.objects.ShoppingListController;

public class AddListActivity extends AppCompatActivity {

    private Button saveButton;
    private TextView nameTextView;

    private final String DEFAULT_STORE = "Default";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_list);

        init();
    }

    private void init()
    {
        saveButton = (Button)findViewById(R.id.save_button_ls);
        nameTextView = (TextView)findViewById(R.id.list_name);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShoppingListController shoppingListController = new ShoppingListController(AddListActivity.this);

                if(attemptSave()) {
                    shoppingListController.addShoppingList(nameTextView.getText().toString(), DEFAULT_STORE);
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
