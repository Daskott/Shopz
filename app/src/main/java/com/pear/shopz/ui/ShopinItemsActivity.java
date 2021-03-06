package com.pear.shopz.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.ActionMode;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.pear.shopz.R;
import com.pear.shopz.adapters.ShoppingItemAdapter;
import com.pear.shopz.fragments.PagerContentFragment;
import com.pear.shopz.fragments.ViewPagerFragment;
import com.pear.shopz.objects.InventoryItem;
import com.pear.shopz.objects.InventoryItemController;
import com.pear.shopz.objects.ShoppingList;
import com.pear.shopz.objects.ShoppingListController;
import com.pear.shopz.objects.ShoppingListItem;
import com.pear.shopz.objects.ShoppingListItemController;
import com.pear.shopz.objects.Util;

import java.util.ArrayList;
import java.util.List;

public class ShopinItemsActivity extends AppCompatActivity
        implements ShoppingItemAdapter.ViewHolder.ClickListener,
        AppBarLayout.OnOffsetChangedListener,PagerContentFragment.OnCompleteListener, ShoppingItemAdapter.OnCompleteListener{

    private RecyclerView shopinListView;
    private ShoppingItemAdapter listAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<ShoppingListItem> lists;
    private ActionModeCallback actionModeCallback = new ActionModeCallback();
    private ActionMode actionMode;
    private ShoppingListItemController shoppingListItemController;

   // private ArrayList<Item> serverData = null;
    private ArrayList<InventoryItem> inventoryItems = null;
    private InventoryItemController inventoryItemController;

    private  int listId = -1;
    private String listName = "";
    private boolean isShopping = false;
    private String title;
    private final String LISTID = "LISTID";
    private final String ITEM_ID = "ITEM_ID";
    private final String ITEM_NAME = "ITEM_NAME";
    private final String LISTNAME = "LISTNAME";
    public static final String ITEM_IDS = "ITEM_IDS";

    private final String SERVERDATA = "SERVERDATA";


    public static final String PAGER_FRAGMENT = "PAGER_FRAGMENT";




    private AutoCompleteTextView add_item_view;
    private TextView totalPriceUncheckedView;
    private Toolbar toolbar;
    private RelativeLayout viewFragmentLayout;
    private CollapsingToolbarLayout collapseBar;
    private AppBarLayout appBar;
    private ImageView addItemButton;
    FloatingActionButton playFab,stopFab;

    FragmentManager fragmentManager;
    ViewPagerFragment currViewPagerFragment;

    private ShoppingList currShoppingList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopin_items);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        //ad view
        AdView mAdView = (AdView) findViewById(R.id.adView_shopping_items);
        AdRequest adRequest = new AdRequest.Builder().build(); //deployment


        //for testing
//        AdRequest adRequest = new AdRequest.Builder()
//                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
//                .addTestDevice("83C91F7145125BB8C343C40C7EE10194")  // An example device ID
//                .addTestDevice("EED7C07AB7B885F7DAAD60C3A0788296") //second test device
//                .build();

        mAdView.loadAd(adRequest);


        Bundle extras = getIntent().getExtras();

        if(extras != null)
        {
            listId = extras.getInt(LISTID);
            listName = extras.getString(LISTNAME);
            //serverData = extras.getParcelableArrayList(SERVERDATA);
        }

        inventoryItemController = new InventoryItemController(ShopinItemsActivity.this);
        inventoryItems = inventoryItemController.getInventory();

        fragmentManager = getFragmentManager();

        shoppingListItemController = new ShoppingListItemController(ShopinItemsActivity.this,listId); //for items info
        ShoppingListController shoppingListController = new ShoppingListController(ShopinItemsActivity.this); //for list info
        currShoppingList = shoppingListController.getShoppingList(listId);

        //init total unchecked price
        totalPriceUncheckedView = (TextView)findViewById(R.id.total_price_unchecked);
        updateTotalPriceUncheckedView();

        //init layouts in toolbar
        appBar =  ((AppBarLayout) findViewById(R.id.app_bar_layout));
        collapseBar = ((CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout));
        viewFragmentLayout = (RelativeLayout)findViewById(R.id.fragment_placeholder);

        //initialize title bar
        title = Util.capitalize(listName);
        collapseBar.setTitle(title);
        toolbar = (Toolbar) findViewById(R.id.app_bar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToMain();
            }
        });

        appBar.addOnOffsetChangedListener(this);

        //list view
        shopinListView = (RecyclerView) findViewById(R.id.shopin_list_view);
        setUpLists(null);

        currViewPagerFragment = new ViewPagerFragment();
        addViewPagerFragment();

        initLayoutHeight();
        //Toast.makeText(this, reslt[0], Toast.LENGTH_LONG).show();

        //Add item text box
        final AutoCompleteTextView add_item_view = (AutoCompleteTextView) findViewById(R.id.add_item_edit_view);
        final CardView addItemCard = (CardView)findViewById(R.id.add_item_card);
        String[] serverItems = Util.getAdapterArray(inventoryItems);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,serverItems);
        add_item_view.setAdapter(adapter);
        add_item_view.setThreshold(1);
        add_item_view.clearFocus();
        add_item_view.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //toggle visibility of save fab
                if(add_item_view.getText().toString().trim().length() > 0)
                {
                    if(addItemButton.getVisibility() == View.INVISIBLE)
                    {
                        addItemButton.setScaleX(0);
                        addItemButton.setScaleY(0);
                        addItemButton.animate()
                                .scaleX(1)
                                .scaleY(1)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        addItemButton.setVisibility(View.VISIBLE);
                                    }
                                }).start();
                    }

                }
                else
                {
                    addItemButton.animate()
                            .scaleX(0)
                            .scaleY(0)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    addItemButton.setVisibility(View.INVISIBLE);
                                }
                            }).start();

                }
            }
        });

        addItemButton = (ImageView)findViewById(R.id.add_item_button);
        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String itemName = add_item_view.getText().toString();
                String category = "Other";

                //if not in superstore and category is number, save as other
                InventoryItem inventoryItem = inventoryItemController.getByItemName(itemName);

                if (inventoryItem != null)
                {
                    itemName = inventoryItem.getName();

                    //"1" is mapped as the index for superstore [gotten based on list item index]
                    //logic is currently like this because of support for one store
                    if(currShoppingList.getStore().equals("1"))
                    {
                        if(inventoryItem.getCategory().matches(".*[0-9]+.*"))
                        {
                            category = "Aisle: " + inventoryItem.getCategory();
                        }
                        else {
                            category = inventoryItem.getCategory();
                        }
                    }
                    else
                    {
                        if(inventoryItem.getCategory().matches(".*[0-9]+.*"))
                        {
                            category = "other";
                        }
                        else
                        {
                            category = inventoryItem.getCategory();
                        }

                    }
                }
                //save new item to db
                shoppingListItemController.addShoppingListItem
                        (new ShoppingListItem(
                                listId,
                                1,
                                itemName,
                                0,
                                category,
                                "",
                                0,
                                1

                        ));

                //clear text and update list view
                add_item_view.setText("");
                updateList();

                //refresh viewpager
                refreshViewFragment();

            }
        });

        playFab = (FloatingActionButton) findViewById(R.id.play_fab);
        playFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //if list is not empty enable shopping mode
                if(lists.size() > 0)
                {
                    startShopping(view);

                    //disable adding textbox
                    addItemCard.setVisibility(View.GONE);

                    //clear selected items if any
                    clearItemSelection();

                }
                else
                {
                    //Snack bar to indicate no items in list
                    final Snackbar snackBar = Snackbar.make(view, "\""+title.trim()+"\" is empty", Snackbar.LENGTH_LONG);
                    snackBar.setAction("Dismiss", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            snackBar.dismiss();
                            playFab.setTranslationY(0);
                        }
                    });
                    snackBar.show();
                }


           }
        });

        stopFab = (FloatingActionButton) findViewById(R.id.stop_fab);
        stopFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopShopping(view);

                //enable adding textbox
                addItemCard.setVisibility(View.VISIBLE);
            }
        });

    }
    //set layout height to 88% to accomodate admob
    public void initLayoutHeight()
    {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int screenHeight = displaymetrics.heightPixels;


        android.support.design.widget.CoordinatorLayout mainLayout = (android.support.design.widget.CoordinatorLayout) findViewById(R.id.main_shopping_items_layout);
        mainLayout.getLayoutParams().height = (int) (0.85 * screenHeight);
    }

    public void addViewPagerFragment()
    {
        ViewPagerFragment currFragment = (ViewPagerFragment)getFragmentManager().findFragmentByTag(PAGER_FRAGMENT);
        if(currFragment == null)
        {
            Bundle bundle = new Bundle();
            bundle.putIntegerArrayList(ITEM_IDS,getItemIDs(lists));
            bundle.putInt(LISTID,listId);
            currViewPagerFragment.setArguments(bundle);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.fragment_placeholder, currViewPagerFragment, PAGER_FRAGMENT);
            fragmentTransaction.commit();
        }

    }

    public  void refreshViewFragment()
    {
        Bundle bundle = new Bundle();
        bundle.putIntegerArrayList(ITEM_IDS,getItemIDs(lists));
        bundle.putInt(LISTID,listId);

        //destroy old viewpager fragment
        currViewPagerFragment.onDestroy();
        currViewPagerFragment = null;

        //replace it
        ViewPagerFragment newViewPagerFragment = new ViewPagerFragment();
        newViewPagerFragment.setArguments(bundle);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_placeholder, newViewPagerFragment);
        fragmentTransaction.commit();

        //re-init
        currViewPagerFragment = newViewPagerFragment;
    }



    public void startShopping(View notifyView)
    {
        isShopping = true;
        viewFragmentLayout.getLayoutParams().height = (int) getResources().getDimension(R.dimen.view_pager_height);
        viewFragmentLayout.setVisibility(View.VISIBLE);

        //toggle fab icons
        playFab.setVisibility(View.GONE);
        stopFab.setVisibility(View.VISIBLE);

        appBar.setExpanded(false);
        refreshViewFragment();
    }

    public void stopShopping(View notifyView) {
        isShopping = false;
        viewFragmentLayout.getLayoutParams().height = (int) getResources().getDimension(R.dimen.view_pager_close_height);
        viewFragmentLayout.setVisibility(View.INVISIBLE);

        //toggle fab icons
        stopFab.setVisibility(View.GONE);
        playFab.setVisibility(View.VISIBLE);

        appBar.setExpanded(true);

    }


    //get ID's of all items
    public ArrayList<Integer> getItemIDs(ArrayList<ShoppingListItem> list)
    {
        ArrayList<Integer> result = new ArrayList<Integer>();

        for(ShoppingListItem item: list)
            result.add(item.getItemID());

        return result;
    }


    public void setUpLists( ArrayList<ShoppingListItem> currList)
    {

        shoppingListItemController = new ShoppingListItemController(this,listId);
        lists = currList == null? shoppingListItemController.getShoppingListItems() : currList;

        // use this setting to improve performance if you know that changes
        // in content do not change the cardView size of the RecyclerView
        shopinListView.setHasFixedSize(true);

        // use a linear cardView manager
        layoutManager = new LinearLayoutManager(this);
        shopinListView.setLayoutManager(layoutManager);

        // specify an adapter
        listAdapter = new ShoppingItemAdapter(lists, this, this);
        shopinListView.setAdapter(listAdapter);

        shopinListView.setItemAnimator(new DefaultItemAnimator());
    }

    public void updateList()
    {
        shoppingListItemController = new ShoppingListItemController(this,listId);
        lists = shoppingListItemController.getShoppingListItems();
        listAdapter.setDataSet(lists);
    }

    public void updateTotalPriceUncheckedView()
    {
        String total = getResources().getString(R.string.dollar_sign)+ Util.roundToDecimals(currShoppingList.getTotalPriceUnchecked(this));

        /*
        * update total price unchecked
        *
        * if total price is $0, do not display anything
        * */
        if(currShoppingList.getTotalPriceUnchecked(this) <= 0)
        {
            //animate & make invisible if visible
            if(totalPriceUncheckedView.getVisibility() == View.VISIBLE)
            {
                totalPriceUncheckedView
                        .animate()
                        .scaleX(0)
                        .scaleY(0)
                        .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        totalPriceUncheckedView.setVisibility(View.GONE);
                    }
                }).start();
            }

            totalPriceUncheckedView.setText(total);
        }
        else
        {
            //animate & make visible if invisible
            if(totalPriceUncheckedView.getVisibility() == View.GONE)
            {
                totalPriceUncheckedView.setVisibility(View.VISIBLE);
                totalPriceUncheckedView.setScaleX(0);
                totalPriceUncheckedView.setScaleY(0);
                totalPriceUncheckedView.animate()
                        .scaleX(1)
                        .scaleY(1)
                        .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        totalPriceUncheckedView.setVisibility(View.VISIBLE);
                    }
                }).start();
            }

            totalPriceUncheckedView.setText(total);
        }
    }

    public void backToMain()
    {
        Intent intent = new Intent(ShopinItemsActivity.this,MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        backToMain();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_settings) {
          //  return true;
        //}

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClicked(int position) {
        if (actionMode != null) {
            toggleSelection(position);
        }
        else{
            //update adapter & db indicating whether item is bought or not
            listAdapter.toggleItemBought(position);
            ShoppingListItemController itemController= new ShoppingListItemController(ShopinItemsActivity.this,listId);
            itemController.updateShoppingListItem(listAdapter.getmDataset().get(position));

            //update slide fragment when item clicked
            if(isShopping)
            {
                if(currViewPagerFragment == null)
                    currViewPagerFragment = (ViewPagerFragment) fragmentManager.findFragmentByTag(PAGER_FRAGMENT);

                currViewPagerFragment.getViewPager().getAdapter().notifyDataSetChanged();
                currViewPagerFragment.setCurrentPage(position);
            }

            //update total price unchecked view
            updateTotalPriceUncheckedView();

        }
    }

    @Override
    public boolean onItemLongClicked(int position) {
        int count = listAdapter.getSelectedItemCount();


        if (actionMode == null ) {
            actionMode = startActionMode(actionModeCallback);
            toolbar.setVisibility(View.INVISIBLE);
        }

        toggleSelection(position);

        return true;

    }

    private void toggleSelection(int position) {
        listAdapter.toggleSelection(position);
        int count = listAdapter.getSelectedItemCount();

        if (count == 0) {
            actionMode.finish();
        } else {

            //disable edit if multiple items selected
            if(count > 1)
                findViewById(R.id.edit_menu_item).setVisibility(View.GONE);
            else
                findViewById(R.id.edit_menu_item).setVisibility(View.VISIBLE);

            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        updateList();
        updateTotalPriceUncheckedView();

        //refresh viewpager
        refreshViewFragment();
    }

    private ArrayList<ShoppingListItem> getSelectedItems()
    {
        ArrayList<ShoppingListItem> result = new ArrayList<ShoppingListItem>();
        List<Integer> selection = listAdapter.getSelectedItems();
        for(Integer i: selection)
            result.add(lists.get(i));

        return result;
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

//        CollapsingToolbarLayout collapseBar = ((CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout));

//        //control collapsebar behaviour when shopping
//        if(collapseBar.getHeight() + verticalOffset < 2 * ViewCompat.getMinimumHeight(collapseBar) && isShopping)
//        {
//            String title = capitalize(listName);
//            collapseBar.setTitle(title);
//            viewFragmentLayout.setVisibility(View.GONE);
//            toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.arrow_left));
//        }else if(collapseBar.getHeight() + verticalOffset > 2 * ViewCompat.getMinimumHeight(collapseBar) && isShopping)
//        {
//            collapseBar.setTitle("");
//            viewFragmentLayout.setVisibility(View.VISIBLE);
//            toolbar.setNavigationIcon(null);
//        }

    }

    /*
    * called when checkbox in
    * fragment is clicked -PageContentFragment.java
    *
    * */
    @Override
    public void onComplete() {
        updateList();

        //update total price unchecked view
        updateTotalPriceUncheckedView();
    }


    public void clearItemSelection()
    {
        listAdapter.clearSelection();
        if(actionMode != null)
            actionMode.finish();

        actionMode = null;
        toolbar.setVisibility(View.VISIBLE);
    }

    //update view with placeholder if list is empty
    public void updateLayoutIfListEmpty(int dataSetSize)
    {
        RelativeLayout emptyLayout = (RelativeLayout) findViewById(R.id.empty_layout);
        if(dataSetSize == 0 && emptyLayout.getVisibility() ==View.GONE)
            emptyLayout.setVisibility(View.VISIBLE);
        else if(dataSetSize > 0 && emptyLayout.getVisibility() ==View.VISIBLE)
            emptyLayout.setVisibility(View.GONE);
    }

    /*
   *
   * called each time shopping list adapter
   * updates the dataset count, so if data set is empty,
   * view is updated with placeholder
   *
   * */
    @Override
    public void onComplete(int dataSetSize) {

        updateLayoutIfListEmpty(dataSetSize);
    }

    private class ActionModeCallback implements ActionMode.Callback {
        @SuppressWarnings("unused")
        private final String TAG = ActionModeCallback.class.getSimpleName();
        private int statusBarColor;

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate (R.menu.menu_shopping_items, menu);

            //hold current color of status bar(post-lollipop)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                statusBarColor = getWindow().getStatusBarColor();
                //set your gray color
                getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
            }
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.delete_menu_item:
                    //delete from db, list & refresh viewpager
                    shoppingListItemController.deleteShoppingListItems(getSelectedItems());
                    updateList();
                    mode.finish();
                    refreshViewFragment();
                    return true;
                case R.id.edit_menu_item:
                    Intent intent = new Intent(ShopinItemsActivity.this,EditItemActivity.class);
                    intent.putExtra(LISTID, lists.get(listAdapter.getSelectedItems().get(0)).getListID());
                    intent.putExtra(ITEM_ID, lists.get(listAdapter.getSelectedItems().get(0)).getItemID());
                    intent.putExtra(ITEM_NAME, lists.get(listAdapter.getSelectedItems().get(0)).getItemName());
                    //intent.putParcelableArrayListExtra(SERVERDATA, serverData);
                    startActivity(intent);
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            clearItemSelection();
            //return to "old" color of status bar
            //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            //  getWindow().setStatusBarColor(statusBarColor);
        }
    }

}
