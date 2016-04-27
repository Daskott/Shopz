package com.pear.shopz.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.item_fragment,container,false);

        //get arguments
        int index = getArguments().getInt(INDEX);
        int listSize = getArguments().getInt(LIST_SIZE);
        int listID = getArguments().getInt(LISTID);
        int itemID = getArguments().getInt(ITEM_ID);
        ShoppingListItemController shoppingListItemController = new ShoppingListItemController(getActivity(),listID);
        ShoppingListItem item = shoppingListItemController.getShoppingListItem(itemID);

        ArrayList<Item> data = item.serverData;
        //if 1st item, don't show back button
        //if last item, don't show next button

        //init display text
        CheckBox itemCheckBox = (CheckBox) view.findViewById(R.id.grocery_name);
        itemCheckBox.setText(item.getItemName());



        if(item.getItemBought() == 0)
            itemCheckBox.setChecked(false);
        else
            itemCheckBox.setChecked(true);

        itemCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //// TODO: 2016-04-27 set item to bought or not
            }
        });
        //name.setText("ListID: "+getArguments().getInt(LISTID)+" ItemID: "+getArguments().getInt(ITEM_ID));

        return view;
    }


}
