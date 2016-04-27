package com.pear.shopz.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.pear.shopz.R;

import java.util.ArrayList;

/**
 * Created by edmondcotterell on 2016-04-26.
 */
public class ViewPagerFragment extends Fragment{

    public static final String ITEM_IDS = "ITEM_IDS";
    public static final String ITEM_ID = "ITEM_ID";
    private final String LISTID = "LISTID";
    private final String INDEX = "INDEX";
    private final String LIST_SIZE = "LIST_SIZE";

    private ImageView back_button, next_button;
    private ViewPager viewPager;

    public static int listSize;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.pager_fragment,container,false);

        //get arguments
        final ArrayList<Integer> itemIDs = getArguments().getIntegerArrayList(ITEM_IDS);
        int listID = getArguments().getInt(LISTID);
        listSize = itemIDs.size();

       final ArrayList<PagerContentFragment> pagerContentFragments = createItemFragments(itemIDs,listID);

        viewPager = (ViewPager)view.findViewById(R.id.view_pager);
        viewPager.setAdapter(new FragmentPagerAdapter(getFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return pagerContentFragments.get(position);
            }

            @Override
            public int getCount() {
                return listSize;
            }
        });

        setActionListener(view);

        return view;
    }

    public void setActionListener(View view)
    {

        next_button = (ImageView) view.findViewById(R.id.next_item);
        back_button = (ImageView) view.findViewById(R.id.prev_item);

        toggleNavigationArrows();

        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(viewPager.getCurrentItem()+1);
                getActivity().invalidateOptionsMenu();
                toggleNavigationArrows();
            }
        });

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(viewPager.getCurrentItem()-1);
                getActivity().invalidateOptionsMenu();
                toggleNavigationArrows();
            }
        });
    }

    public void toggleNavigationArrows()
    {
        if(viewPager.getCurrentItem() == 0)
        {
            back_button.setVisibility(View.INVISIBLE);
            next_button.setVisibility(View.VISIBLE);
        }
        else if(viewPager.getCurrentItem() == listSize-1)
        {
            next_button.setVisibility(View.INVISIBLE);
            back_button.setVisibility(View.VISIBLE);
        }
        else
        {
            back_button.setVisibility(View.VISIBLE);
            next_button.setVisibility(View.VISIBLE);
        }
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

}
