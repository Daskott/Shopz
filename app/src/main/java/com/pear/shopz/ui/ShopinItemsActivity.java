package com.pear.shopz.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.pear.shopz.R;
import com.pear.shopz.adapters.ShoppingItemAdapter;
import com.pear.shopz.fragments.PagerContentFragment;
import com.pear.shopz.fragments.ViewPagerFragment;
import com.pear.shopz.objects.Item;
import com.pear.shopz.objects.ShoppingListItem;
import com.pear.shopz.objects.ShoppingListItemController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ShopinItemsActivity extends AppCompatActivity  implements ShoppingItemAdapter.ViewHolder.ClickListener, AppBarLayout.OnOffsetChangedListener,PagerContentFragment.OnCompleteListener{

    private RecyclerView shopinListView;
    private ShoppingItemAdapter listAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<ShoppingListItem> lists;
    private ActionModeCallback actionModeCallback = new ActionModeCallback();
    private ActionMode actionMode;
    private ShoppingListItemController shoppingListItemController;
    private ShoppingListItemController shoppingListController;

    private ArrayList<Item> serverData = null;

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
    private Toolbar toolbar;
    private RelativeLayout viewFragmentLayout;
    private CollapsingToolbarLayout collapseBar;
    private AppBarLayout appBar;
    FloatingActionButton playFab,stopFab,saveItemFab;

    FragmentManager fragmentManager;
    ViewPagerFragment viewPagerFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopin_items);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        Bundle extras = getIntent().getExtras();

        if(extras != null)
        {
            listId = extras.getInt(LISTID);
            listName = extras.getString(LISTNAME);
            serverData = extras.getParcelableArrayList(SERVERDATA);
        }

        fragmentManager = getFragmentManager();

        shoppingListController = new ShoppingListItemController(ShopinItemsActivity.this,listId);

        //init layouts in toolbar
        appBar =  ((AppBarLayout) findViewById(R.id.app_bar_layout));
        collapseBar = ((CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout));
        viewFragmentLayout = (RelativeLayout)findViewById(R.id.fragment_placeholder);

        //initialize title bar
        title = capitalize(listName);
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

        viewPagerFragment = new ViewPagerFragment();
        addViewPagerFragment();



        //Toast.makeText(this, reslt[0], Toast.LENGTH_LONG).show();

        //Add item text box
        final AutoCompleteTextView add_item_view = (AutoCompleteTextView) findViewById(R.id.add_item_edit_view);
        final CardView addItemCard = (CardView)findViewById(R.id.add_item_card);
        String[] serverItems = getArray();

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
                if(add_item_view.getText().toString().length() > 0)
                {
                    playFab.setVisibility(View.GONE);
                    saveItemFab.setVisibility(View.VISIBLE);
                }
                else
                {
                    saveItemFab.setVisibility(View.GONE);
                    playFab.setVisibility(View.VISIBLE);
                }
            }
        });

        saveItemFab = (FloatingActionButton) findViewById(R.id.save_item_fab);
        saveItemFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //save new item to db
                shoppingListController.addShoppingListItem
                        (new ShoppingListItem(
                                listId,
                                1,
                                add_item_view.getText().toString(),
                                0,
                                "0",
                                "",
                                0,
                                1

                        ));

                //clear text and update list view
                add_item_view.setText("");
                updateList();

                //Snack bar to indicate data saved
                final Snackbar snackBar = Snackbar.make(v, "New item added to list", Snackbar.LENGTH_LONG);
                snackBar.setAction("Dismiss", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        snackBar.dismiss();
                        playFab.setTranslationY(0);
                    }
                });
                snackBar.show();
            }
        });

        playFab = (FloatingActionButton) findViewById(R.id.play_fab);
        playFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //addViewPagerFragment();
                //refresh viewpager for shopping mode - ViewPagerFragment
                viewPagerFragment.setUpViewPager(getItemIDs(shoppingListItemController.getShoppingListItems()), listId);

                //if list is not empty enable shopping mode
                if(!(lists.size() <= 0))
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

    private String[] getArray() {

        if (serverData == null || serverData.size() == 0) return new String[0];

        String[] itemNames = new String[serverData.size()];

        for (int i = 0; i < serverData.size(); i++)
        {
            itemNames[i] = serverData.get(i).getName();
        }

        return itemNames;
    }

    public String capitalize(String word)
    {
        if(word.length() == 1)
            return word.toUpperCase();

        return word.substring(0,1).toUpperCase()+""+word.substring(1).toLowerCase();
    }

    public void addViewPagerFragment()
    {
        ViewPagerFragment currFragment = (ViewPagerFragment)getFragmentManager().findFragmentByTag(PAGER_FRAGMENT);
        if(currFragment == null)
        {
            Bundle bundle = new Bundle();
            bundle.putIntegerArrayList(ITEM_IDS,getItemIDs(lists));
            bundle.putInt(LISTID,listId);
            viewPagerFragment.setArguments(bundle);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.fragment_placeholder, viewPagerFragment, PAGER_FRAGMENT);
            fragmentTransaction.commit();
        }

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

        //Snack bar to indicate editing is disabled
        final Snackbar snackBar = Snackbar.make(notifyView, "Shopping Mode Enabled", Snackbar.LENGTH_LONG);
        snackBar.setAction("Dismiss", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackBar.dismiss();
                stopFab.setTranslationY(0);
            }
        });
        snackBar.show();
    }

    public void stopShopping(View notifyView) {
        isShopping = false;
        viewFragmentLayout.getLayoutParams().height = (int) getResources().getDimension(R.dimen.view_pager_close_height);
        viewFragmentLayout.setVisibility(View.INVISIBLE);

        //toggle fab icons
        stopFab.setVisibility(View.GONE);
        playFab.setVisibility(View.VISIBLE);

        appBar.setExpanded(true);

        //Snack bar to indicate editing is enabled
        final Snackbar snackBar = Snackbar.make(notifyView, "Shopping Mode Disabled", Snackbar.LENGTH_LONG);
        snackBar.setAction("Dismiss", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackBar.dismiss();
                playFab.setTranslationY(0);
            }
        });
        snackBar.show();

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
        if (id == R.id.action_settings) {
            return true;
        }

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
                if(viewPagerFragment == null)
                    viewPagerFragment = (ViewPagerFragment) fragmentManager.findFragmentByTag(PAGER_FRAGMENT);

                viewPagerFragment.getViewPager().getAdapter().notifyDataSetChanged();
                viewPagerFragment.setCurrentPage(position);
            }

        }

        //shopinListView.smoothScrollToPosition(position);
        //LinearLayoutManager lp = (LinearLayoutManager)shopinListView.getLayoutManager();
        //lp.scrollToPositionWithOffset(position, 0);;
    }

    @Override
    public boolean onItemLongClicked(int position) {
        int count = listAdapter.getSelectedItemCount();

        if(!isShopping)
        {
            if (actionMode == null ) {
                actionMode = startActionMode(actionModeCallback);
                toolbar.setVisibility(View.INVISIBLE);
            }

            toggleSelection(position);

            return true;
        }

        return false;
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

    @Override
    public void onComplete() {
        //called when checkbox in
        // fragment is clicked -PageContentFragment.java
        updateList();
    }

    public void clearItemSelection()
    {
        listAdapter.clearSelection();
        if(actionMode != null)
            actionMode.finish();

        actionMode = null;
        toolbar.setVisibility(View.VISIBLE);
    }

    private class ActionModeCallback implements ActionMode.Callback {
        @SuppressWarnings("unused")
        private final String TAG = ActionModeCallback.class.getSimpleName();
        private int statusBarColor;

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate (R.menu.menu_shopping_lists, menu);

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
                    //delete from db & list
                    shoppingListItemController.deleteShoppingListItems(getSelectedItems());
                    updateList();
                    mode.finish();
                    return true;
                case R.id.edit_menu_item:
                    Intent intent = new Intent(ShopinItemsActivity.this,EditItemActivity.class);
                    intent.putExtra(LISTID, lists.get(listAdapter.getSelectedItems().get(0)).getListID());
                    intent.putExtra(ITEM_ID, lists.get(listAdapter.getSelectedItems().get(0)).getItemID());
                    intent.putExtra(ITEM_NAME, lists.get(listAdapter.getSelectedItems().get(0)).getItemName());
                    intent.putParcelableArrayListExtra(SERVERDATA, serverData);
                    startActivity(intent);
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
