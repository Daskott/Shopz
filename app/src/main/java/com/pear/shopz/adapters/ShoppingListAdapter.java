package com.pear.shopz.adapters;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pear.shopz.R;
import com.pear.shopz.objects.Settings;
import com.pear.shopz.objects.ShoppingList;
import com.pear.shopz.objects.ShoppingListItemController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by edmondcotterell on 2016-04-21.
 */

public class ShoppingListAdapter extends SelectableAdapter<ShoppingListAdapter.ViewHolder> {
    private ArrayList<ShoppingList> mDataset;
    private final Context mContext;
    public SparseBooleanArray selectedItems;
    private ViewHolder.ClickListener clickListener;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnLongClickListener {

        // each data item is just a string in this case
        private CardView cardView;
        private TextView storeNameView;
        private View storeSupportIcon;
        private RelativeLayout cardLayout;
        private ViewHolder.ClickListener listener;

        public ViewHolder(CardView v, ClickListener listener) {
            super(v);
            cardView = v;
            storeNameView = (TextView) v.findViewById(R.id.store_name_view);
            storeSupportIcon = v.findViewById(R.id.store_support_icon);
            cardLayout = (RelativeLayout)v.findViewById(R.id.card_layout_sl);
            this.listener = listener;

            v.setOnClickListener(this);
            v.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                listener.onItemClicked(getPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {

            if (listener != null) {
                return listener.onItemLongClicked(getPosition());
            }
            return false;
        }

        public interface ClickListener {
            public void onItemClicked(int position);
            public boolean onItemLongClicked(int position);
        }
    }


    // Provide a suitable constructor (depends on the kind of dataset)
    public ShoppingListAdapter(ArrayList<ShoppingList> myDataset, Context mContext, ViewHolder.ClickListener clickListener) {
        mDataset = myDataset;
        this.mContext = mContext;
        selectedItems = new SparseBooleanArray();
        this.clickListener = clickListener;
    }

    // Create new views (invoked by the cardView manager)
    @Override
    public ShoppingListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_shopin_card, parent, false);
        ViewHolder vh = new ViewHolder((CardView) v, clickListener);
        return vh;
    }

    // Replace the contents of a view (invoked by the cardView manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        ShoppingList listItem = mDataset.get(position);

        //set list name
        TextView listName = (TextView) holder.cardView.findViewById(R.id.list_name);
        listName.setText(capitalize(listItem.getListName()));

        //set progress
        ShoppingListItemController listItemController = new ShoppingListItemController(mContext,listItem.getListID());
        int total = listItemController.getSize();
        int totalItemsBought = listItem.getTotalBoughtItems(mContext);

        //init progress
        TextView listProgress = (TextView) holder.cardView.findViewById(R.id.list_progress_view);
        listProgress.setText(totalItemsBought+"/"+total);
        double progressPercentage = total == 0? 0:((double)totalItemsBought/total)*100;

        /*set color indicator for progress
        */
        //100%
        if(progressPercentage == 100)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                listProgress.setBackground(mContext.getResources().getDrawable(R.drawable.green_rect,mContext.getResources().newTheme()));
            else
                listProgress.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.green_rect));
        }
        //50%
        else if(progressPercentage >= 50.0)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                listProgress.setBackground(mContext.getResources().getDrawable(R.drawable.yellow_rect,mContext.getResources().newTheme()));
            else
                listProgress.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.yellow_rect));
        }
        //0-49.9%
        else
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                listProgress.setBackground(mContext.getResources().getDrawable(R.drawable.red_rect,mContext.getResources().newTheme()));
            else
                listProgress.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.red_rect));
        }

        //init store name
        int storeIndex = Integer.parseInt(listItem.getStore());
        String storeName = Settings.getStoreOptions(mContext).get(storeIndex);
        holder.storeNameView.setText(storeName);

        /*
        * set store icon to blue, if its supported
        * */
        if(Settings.getListOFSupportedStores().contains(storeName.toLowerCase()))
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                holder.storeSupportIcon.setBackground(mContext.getResources().getDrawable(R.drawable.blue_rect,mContext.getResources().newTheme()));
            else
                holder.storeSupportIcon.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.blue_rect));
        }
        else
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                holder.storeSupportIcon.setBackground(mContext.getResources().getDrawable(R.drawable.myrect,mContext.getResources().newTheme()));
            else
                holder.storeSupportIcon.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.myrect));
        }

        // Highlight the item if it's selected
        if(isSelected(position))
            holder.cardLayout.setBackgroundColor(mContext.getResources().getColor(R.color.select));
        else
            holder.cardLayout.setBackgroundColor(mContext.getResources().getColor(R.color.unselect));

    }

    // Return the size of your dataset (invoked by the cardView manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void removeItem(int position) {
        mDataset.remove(position);
        notifyItemRemoved(position);
    }

    public void removeItems(List<Integer> positions) {
        // Reverse-sort the list
        Collections.sort(positions, new Comparator<Integer>() {
            @Override
            public int compare(Integer lhs, Integer rhs) {
                return rhs - lhs;
            }
        });

        // Split the list in ranges
        while (!positions.isEmpty()) {
            if (positions.size() == 1) {
                removeItem(positions.get(0));
                positions.remove(0);
            } else {
                int count = 1;
                while (positions.size() > count && positions.get(count).equals(positions.get(count - 1) - 1)) {
                    ++count;
                }

                if (count == 1) {
                    removeItem(positions.get(0));
                } else {
                    removeRange(positions.get(count - 1), count);
                }

                for (int i = 0; i < count; ++i) {
                    positions.remove(0);
                }
            }
        }
    }

    private void removeRange(int positionStart, int itemCount) {
        for (int i = 0; i < itemCount; ++i) {
            mDataset.remove(positionStart);
        }
        notifyItemRangeRemoved(positionStart, itemCount);
    }

    public String capitalize(String word)
    {
        if(word.length() == 1)
            return word.toUpperCase();

        return word.substring(0,1).toUpperCase()+""+word.substring(1).toLowerCase();
    }
}