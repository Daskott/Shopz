package com.pear.shopz.fragments;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pear.shopz.R;
import com.pear.shopz.objects.ShoppingListItem;
import com.pear.shopz.objects.ShoppingListItemController;

import java.text.DecimalFormat;

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
    private TextView itemCategoryTextView;
    private TextView itemAisle, quantity_price_view;
    private LinearLayout topPanelLayout;
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
        topPanelLayout = (LinearLayout)view.findViewById(R.id.topPanelLayout);

        itemCheckBox.setText(capitalize(item.getItemName()));
        itemCategoryTextView.setText(capitalize(shoppingListItemController.getPossibleItemCategories().get(Integer.parseInt(item.getItemCategory()))));
        itemAisle.setText(item.getItemAisle().trim());
        String dollarSign = getResources().getString(R.string.dollar_sign);
        String quantity = String.valueOf(item.getItemQuantity());
        String price = dollarSign+String.valueOf(roundToDecimals(item.getItemPrice()));

        //set top panel layout to white, if no category was picked [20 - index of "Other"]
        if(Integer.parseInt(item.getItemCategory()) == 20) {
            topPanelLayout.setBackgroundColor(getResources().getColor(R.color.white));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                itemCategoryTextView.setTextColor(getResources().getColor(R.color.dark_grey,getResources().newTheme()));
            else
                itemCategoryTextView.setTextColor(getResources().getColor(R.color.dark_grey));
        }
        //display price & quantity
        if(item.getItemQuantity() <=1 && item.getItemPrice() >0.0)
            quantity_price_view.setText("("+quantity+")  "+ (item.getItemPrice() != 0.0? price:""));
        else if(item.getItemQuantity() > 1 & item.getItemPrice() >0.0)
        {
            price = dollarSign+String.valueOf(roundToDecimals((double) item.getItemPrice()*item.getItemQuantity()));
            quantity_price_view.setText("(" + quantity + " x " +dollarSign+roundToDecimals(item.getItemPrice()) + ")  " + (item.getItemPrice() != 0.0 ? price : ""));
        }
        else
            quantity_price_view.setVisibility(View.GONE);

        //set checkbox to true if item is bought
        initItemCheckBox(item);

        itemCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleItemBought(item);

                //if listener null re-init
                if(mListener == null)
                    mListener = (OnCompleteListener)getActivity();

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
            itemCheckBox.setPaintFlags(itemCheckBox.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                itemCheckBox.setTextColor(getActivity().getResources().getColor(R.color.dark_grey,null));
            else
                itemCheckBox.setTextColor(getActivity().getResources().getColor(R.color.dark_grey));

            item.setItemBought(1);
            shoppingListItemController.updateShoppingListItem(item);
        }
        else
        {
            itemCheckBox.setChecked(false);
            itemCheckBox.setPaintFlags(itemCheckBox.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                itemCheckBox.setTextColor(getActivity().getResources().getColor(R.color.black,null));
            else
                itemCheckBox.setTextColor(getActivity().getResources().getColor(R.color.black));


            item.setItemBought(0);
            shoppingListItemController.updateShoppingListItem(item);
        }
    }

    public void initItemCheckBox(ShoppingListItem item)
    {
        if(item.getItemBought() == 0)
        {
            itemCheckBox.setChecked(false);
            itemCheckBox.setPaintFlags(itemCheckBox.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                itemCheckBox.setTextColor(getActivity().getResources().getColor(R.color.black,null));
            else
                itemCheckBox.setTextColor(getActivity().getResources().getColor(R.color.black));
        }
        else
        {
            itemCheckBox.setChecked(true);
            itemCheckBox.setPaintFlags(itemCheckBox.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                itemCheckBox.setTextColor(getActivity().getResources().getColor(R.color.dark_grey,null));
            else
                itemCheckBox.setTextColor(getActivity().getResources().getColor(R.color.dark_grey));
        }
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

    public String roundToDecimals(double number)
    {
        DecimalFormat df = new DecimalFormat("#.00");
        return df.format(number);

    }
}
