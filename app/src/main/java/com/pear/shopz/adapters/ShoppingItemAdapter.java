package com.pear.shopz.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.pear.shopz.R;
import com.pear.shopz.objects.ShoppingListItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by edmondcotterell on 2016-04-21.
 */

public class ShoppingItemAdapter  extends SelectableAdapter<ShoppingItemAdapter.ViewHolder>  {
    private ArrayList<ShoppingListItem> mDataset;
    private final Context mContext;
    public SparseBooleanArray selectedItems;
    private ViewHolder.ClickListener clickListener;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        // each data item is just a string in this case
        private CardView cardItem;
        private CheckBox itemCheckBox;
        private ViewHolder.ClickListener listener;

        public ViewHolder(CardView v,ClickListener listener) {
            super(v);
            this.listener = listener;
            cardItem = (CardView)v.findViewById(R.id.item_card);
            itemCheckBox = (CheckBox) v.findViewById(R.id.item_checkBox);

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
    public ShoppingItemAdapter(ArrayList<ShoppingListItem> myDataset, Context mContext, ViewHolder.ClickListener clickListener) {
        mDataset = myDataset;
        this.mContext = mContext;
        this.clickListener = clickListener;
        selectedItems = new SparseBooleanArray();
    }

    // Create new views (invoked by the cardView manager)
    @Override
    public ShoppingItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_shopin_item, parent, false);
        // set the view's size, margins, paddings and cardView parameters

        ViewHolder vh = new ViewHolder((CardView) v, clickListener);
        return vh;
    }

    // Replace the contents of a view (invoked by the cardView manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        //set checkboxes text
        holder.itemCheckBox.setText(mDataset.get(position).getItemName());

        //*** IF YOU CHECK AN ITEM SET THAT ATTRIBUTE TO TRUE, IF TRUE SET CARD TO GREY & MOVE TO BUTTOM ***

        holder.itemCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleCheckBox(holder.itemCheckBox, holder.cardItem, position);
            }
        });

        if(isSelected(position))
            holder.cardItem.setBackgroundColor(mContext.getResources().getColor(R.color.select));
        else
            holder.cardItem.setBackgroundColor(mContext.getResources().getColor(R.color.unselect));

    }

    public void toggleCheckBox(CheckBox checkBox, CardView cardView, int position)
    {
        List positions = new ArrayList<>(1);
        positions.add(position);
        ShoppingListItem data = mDataset.get(position);

        if(checkBox.isChecked()) {
            checkBox.setChecked(false);
            moveItemToBottom(position, mDataset.size()-1, data);
            cardView.setBackgroundColor(mContext.getResources().getColor(R.color.light_grey));
        }
        else
            cardView.setBackgroundColor(mContext.getResources().getColor(R.color.unselect));


    }

    // Return the size of your dataset (invoked by the cardView manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void removeItem(int position) {
        mDataset.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mDataset.size());
    }

    public void addItem(ShoppingListItem item) {
        mDataset.add(item);
        notifyItemInserted(mDataset.size()-1);
        notifyItemRangeChanged(mDataset.size()-1, mDataset.size());
    }

    public void moveItemToBottom(int fromPosition, int toPosition, ShoppingListItem data) {
        mDataset.remove(fromPosition);
        mDataset.add(data);
        notifyItemMoved(fromPosition,toPosition);
        notifyItemRangeChanged(fromPosition, mDataset.size());
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
}