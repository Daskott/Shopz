package com.pear.shopz.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pear.shopz.R;
import com.pear.shopz.objects.ShoppingListItem;
import com.pear.shopz.objects.ShoppingListItemController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by edmondcotterell on 2016-04-26.
 */
public class ViewPagerFragment extends Fragment {

    public static final String ITEM_IDS = "ITEM_IDS";
    public static final String ITEM_ID = "ITEM_ID";
    public static final String ITEM_NAME = "ITEM_NAME";
    private final String LISTID = "LISTID";
    private final String INDEX = "INDEX";
    private final String LIST_SIZE = "LIST_SIZE";

    private ViewPager viewPager;
    private View view;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.pager_fragment,container,false);
        this.view = view;

        //get arguments
        ArrayList<Integer> itemIDs = getArguments().getIntegerArrayList(ITEM_IDS);
        int listID = getArguments().getInt(LISTID);

        setUpViewPager(itemIDs, listID);

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public void setUpViewPager(ArrayList<Integer> itemIDs, int listID)
    {
        ArrayList<PagerContentFragment> pagerContentFragments = createItemFragments(itemIDs,listID);

        viewPager = (ViewPager)view.getRootView().findViewById(R.id.view_pager);
        viewPager.setAdapter(new PagerAdapter(getFragmentManager(),itemIDs,pagerContentFragments, listID));
        viewPager.setOffscreenPageLimit(6);

    }


    public ArrayList<PagerContentFragment> createItemFragments(ArrayList<Integer> itemIDs, int listID)
    {
        int size = itemIDs.size();
        ArrayList<PagerContentFragment> pagerContentFragments = new ArrayList<>();

        //create fragments
        for (int i = 0; i< size; i++)
        {
            //set arguments to pass to fragment
            Bundle bundle = new Bundle();
            bundle.putInt(ITEM_ID,itemIDs.get(i));
            bundle.putInt(LISTID,listID);
            bundle.putInt(INDEX,i);
            bundle.putInt(LIST_SIZE,size);

            PagerContentFragment pagerContentFragment = new PagerContentFragment();
            pagerContentFragment.setArguments(bundle);
            pagerContentFragments.add(pagerContentFragment);
        }

        return pagerContentFragments;
    }

    public ViewPager getViewPager()
    {
        return viewPager;
    }

    public void setCurrentPage(int position)
    {
        if(position != viewPager.getCurrentItem())
            viewPager.setCurrentItem(position);
    }

    //get ID's of all items
    public ArrayList<Integer> getItemIDs(ArrayList<ShoppingListItem> list)
    {
        ArrayList<Integer> result = new ArrayList<Integer>();

        for(ShoppingListItem item: list)
            result.add(item.getItemID());

        return result;
    }

    public static class PagerAdapter extends FragmentStatePagerAdapter {

        ArrayList<PagerContentFragment> pagerContentFragments;
        ArrayList<Integer> itemIDs;
        int listID;

        public PagerAdapter(FragmentManager fm, ArrayList<Integer> itemIDs, ArrayList<PagerContentFragment> pagerContentFragments,int listID) {
            super(fm);
            this.itemIDs = itemIDs;
            this.pagerContentFragments = pagerContentFragments;
            this.listID = listID;
        }

        @Override
        public int getCount() {
            return itemIDs.size();
        }

        @Override
        public Fragment getItem(int position) {
            return pagerContentFragments.get(position);
        }

        @Override
        public int getItemPosition(Object object) {

            //if item found
            if (pagerContentFragments.contains((PagerContentFragment)object)) {
                ((PagerContentFragment) object).updateView();
                return super.getItemPosition(object);
            }
            else
                return POSITION_NONE;
        }

        //if only one page, fill screen, else show part of next page
        @Override public float getPageWidth(int position) { return itemIDs.size() <=1? 1.0f:(0.85f); }

        public void removePage(int position)
        {
            pagerContentFragments.remove(position);
            notifyDataSetChanged();
        }
    }


}
