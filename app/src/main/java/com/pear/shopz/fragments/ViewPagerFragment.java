package com.pear.shopz.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pear.shopz.R;

import java.util.ArrayList;

/**
 * Created by edmondcotterell on 2016-04-26.
 */
public class ViewPagerFragment extends Fragment {

    public static final String ITEM_IDS = "ITEM_IDS";
    public static final String ITEM_ID = "ITEM_ID";
    private final String LISTID = "LISTID";
    private final String INDEX = "INDEX";
    private final String LIST_SIZE = "LIST_SIZE";

    private ViewPager viewPager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.pager_fragment,container,false);
        viewPager = (ViewPager)view.findViewById(R.id.view_pager);


        //get arguments
        final ArrayList<Integer> itemIDs = getArguments().getIntegerArrayList(ITEM_IDS);
        int listID = getArguments().getInt(LISTID);

        setUpViewPager(itemIDs, listID);

        return view;
    }

    public void setUpViewPager(ArrayList<Integer> itemIDs, int listID)
    {
        final ArrayList<PagerContentFragment> pagerContentFragments = createItemFragments(itemIDs,listID);
        final int listSize = itemIDs.size();

        viewPager.setAdapter(new FragmentPagerAdapter(getFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return pagerContentFragments.get(position);
            }

            @Override
            public int getCount() {
                return listSize;
            }

            @Override
            public int getItemPosition(Object object) {
                if (object instanceof PagerContentFragment) {
                    ((PagerContentFragment)object).updateView();
                }
                return super.getItemPosition(object);
            }

            //if only one page, fill screen, else show part of next page
            @Override public float getPageWidth(int position) { return listSize <=1? 1.0f:(0.85f); }
        });

        viewPager.setOffscreenPageLimit(6);

    }


    public ArrayList<PagerContentFragment> createItemFragments(ArrayList<Integer> itemIDs, int listID)
    {
        ArrayList<PagerContentFragment> result = new ArrayList<PagerContentFragment>();
        int size = itemIDs.size();
        //create fragments
        for (int i = 0; i< size; i++)
        {
            //set arguments to pass to fragment
            Bundle bundle = new Bundle();
            bundle.putInt(ITEM_ID,itemIDs.get(i));
            bundle.putInt(LISTID,listID);
            bundle.putInt(INDEX,i);
            bundle.putInt(LIST_SIZE,size);

            result.add(new PagerContentFragment());
            result.get(i).setArguments(bundle);
        }

        return result;
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
}
