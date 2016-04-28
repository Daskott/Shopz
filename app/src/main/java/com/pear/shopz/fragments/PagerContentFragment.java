package com.pear.shopz.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import com.pear.shopz.R;
import com.pear.shopz.objects.Item;
import com.pear.shopz.objects.ShoppingListItem;
import com.pear.shopz.objects.ShoppingListItemController;

import java.util.ArrayList;

/**
 * Created by edmondcotterell on 2016-04-26.
 */
public class PagerContentFragment extends Fragment{
    public static final String ITEM_ID = "ITEM_ID";
    private final String LISTID = "LISTID";
    private final String INDEX = "INDEX";
    private final String LIST_SIZE = "LIST_SIZE";

    private ShoppingListItem item;
    private CheckBox itemCheckBox;
    private ShoppingListItemController shoppingListItemController;

    private int itemID;
    private OnCompleteListener mListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.item_fragment,container,false);

        //get arguments
        int index = getArguments().getInt(INDEX);
        int listSize = getArguments().getInt(LIST_SIZE);
        int listID = getArguments().getInt(LISTID);
        itemID = getArguments().getInt(ITEM_ID);
        shoppingListItemController = new ShoppingListItemController(getActivity(),listID);
        item = shoppingListItemController.getShoppingListItem(itemID);

        //ArrayList<Item> data = item.serverData;


        //init display text
        itemCheckBox = (CheckBox) view.findViewById(R.id.grocery_name);
        itemCheckBox.setText(capitalize(item.getItemName()));


        //set checkbox to true if item is bought
        initItemCheckBox(item);

        itemCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleItemBought(item);
                mListener.onComplete();
            }
        });

        return view;
    }

    public void toggleItemBought(ShoppingListItem item)
    {
        if(item.getItemBought() == 0)
        {
            itemCheckBox.setChecked(true);
            item.setItemBought(1);
            shoppingListItemController.updateShoppingListItem(item);
        }
        else
        {
            itemCheckBox.setChecked(false);
            item.setItemBought(0);
            shoppingListItemController.updateShoppingListItem(item);
        }
    }

    public void initItemCheckBox(ShoppingListItem item)
    {
        if(item.getItemBought() == 0)
            itemCheckBox.setChecked(false);
        else
            itemCheckBox.setChecked(true);
    }

    //update view from parent fragment
    public void updateView() {

        item = shoppingListItemController.getShoppingListItem(itemID);
        initItemCheckBox(item);
    }

    public static interface OnCompleteListener {
        public abstract void onComplete();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            this.mListener = (OnCompleteListener)context;
        }
        catch (final ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnCompleteListener");
        }
    }

    public String capitalize(String word)
    {
        if(word.length() == 1)
            return word.toUpperCase();

        return word.substring(0,1).toUpperCase()+""+word.substring(1).toLowerCase();
    }
}
