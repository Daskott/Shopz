package com.pear.shopz.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import com.pear.shopz.R;
import com.pear.shopz.objects.ShoppingListItem;
import com.pear.shopz.objects.ShoppingListItemController;

/**
 * Created by edmondcotterell on 2016-04-26.
 */
public class PagerContentFragment extends Fragment{
    public static final String ITEM_ID = "ITEM_ID";
    private final String LISTID = "LISTID";
    private final String INDEX = "INDEX";
    private final String LIST_SIZE = "LIST_SIZE";
//    public static final String ITEM_NAME = "ITEM_NAME";

    private ShoppingListItem item;
    private CheckBox itemCheckBox;
    private TextView itemCategoryTextView;
    private TextView itemAisle, quantity_price_view;
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
        itemCheckBox.setEllipsize(TextUtils.TruncateAt.END);
        itemCategoryTextView = (TextView)view.findViewById(R.id.category);
        itemAisle = (TextView)view.findViewById(R.id.aisle_number);
        quantity_price_view = (TextView)view.findViewById(R.id.quantity_price_view);

        itemCheckBox.setText(capitalize(item.getItemName()));
        itemCategoryTextView.setText(shoppingListItemController.getPossibleItemCategories().get(Integer.parseInt(item.getItemCategory())));
        itemAisle.setText(item.getItemAisle().trim());
        String dollarSign = getResources().getString(R.string.dollar_sign);
        String quantity = String.valueOf(item.getItemQuantity());
        String price = dollarSign+String.valueOf(item.getItemPrice());

        //display price & quantity
        if(item.getItemQuantity() <=1 && item.getItemPrice() >0.0)
            quantity_price_view.setText("("+quantity+") "+ (item.getItemPrice() != 0.0? price:""));
        else if(item.getItemQuantity() > 1 & item.getItemPrice() >0.0)
        {
            price = dollarSign+String.valueOf((Double)item.getItemPrice()*item.getItemQuantity());
            quantity_price_view.setText("(" + quantity + " x " +dollarSign+item.getItemPrice() + ")   " + (item.getItemPrice() != 0.0 ? price : ""));
        }
        else
            quantity_price_view.setVisibility(View.GONE);

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
