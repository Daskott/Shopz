package com.pear.shopz.objects;

import android.content.Context;

import com.pear.shopz.R;

import java.text.DecimalFormat;

/**
 * Created by edmondcotterell on 2016-04-21.
 */
public class ShoppingList {

    private int listID;
    private String listName;
    private String store;

    public ShoppingList(int listID, String listName, String store)
    {
        this.listID = listID;
        this.listName = listName;
        this.store = store;
    }

    public int getTotalBoughtItems(Context context) {

        ShoppingListItemController itemController =  new ShoppingListItemController(context,listID);
        int totalBought = 0;
        for(ShoppingListItem item: itemController.getShoppingListItems())
        {
            if(item.getItemBought() == 1)
                totalBought++;
        }

        return totalBought;
    }

    public String getTotalPriceUnchecked(Context context)
    {
        ShoppingListItemController itemController =  new ShoppingListItemController(context,listID);
        String result ="";
        double uncheckedPrice = 0.00;

        for(ShoppingListItem item: itemController.getShoppingListItems())
        {
            if(item.getItemBought() == 0)
                uncheckedPrice+=(item.getItemPrice()*item.getItemQuantity());
        }

        result = context.getResources().getString(R.string.dollar_sign)+uncheckedPrice;
        return result;
    }

    public int getListID() {
        return listID;
    }

    public void setListID(int listID) {
        this.listID = listID;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    /*
    * Right now, we store the index of where the store name is located in
    * @see in Settings.getStoreOptions(Context context)
    * N/B: Actual store name is not stored in db, just index
    *
    * */
    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public String getFormattedShoppingListString(Context context)
    {
        ShoppingListItemController itemController =  new ShoppingListItemController(context,listID);
        String formattedList = "";
        String result ="";
        double uncheckedPrice = 0.00;

        formattedList+="Shopping List\n";
        formattedList+="_______________\n\n";

        for(ShoppingListItem item: itemController.getShoppingListItems())
        {
            String price = "";
            String totalPrice = "- -";

            if(item.getItemPrice() > 0)
            {
                price = context.getResources().getString(R.string.dollar_sign) + String.valueOf(item.getItemPrice());
                totalPrice = context.getResources().getString(R.string.dollar_sign) + roundToDecimals(item.getItemPrice()*item.getItemQuantity());

            }

            //formattedList+="\t"+item.getItemName()+"\t"+price+"\n\n";
            formattedList+=String.format("\t%s(x%s) %s    =   \t%3s\n\n", item.getItemName(), String.valueOf(item.getItemQuantity()), price, totalPrice);

        }

        formattedList+="\n*** SHOPEZZY APP ***";

        return formattedList;
    }

    public int getListSize(Context context)
    {
        return new ShoppingListItemController(context,listID).getShoppingListItems().size();
    }

    public String roundToDecimals(double number)
    {
        DecimalFormat df = new DecimalFormat("#.00");
        return df.format(number);

    }
}
