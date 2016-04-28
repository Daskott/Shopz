package com.pear.shopz.ui;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.pear.shopz.R;
import com.pear.shopz.adapters.ShoppingItemAdapter;
import com.pear.shopz.fragments.PagerContentFragment;
import com.pear.shopz.fragments.ViewPagerFragment;
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

    private  int listId = -1;
    private String listName = "";
    private boolean isShopping = false;
    private String title;
    private final String LISTID = "LISTID";
    private final String ITEM_ID = "ITEM_ID";
    private final String ITEM_NAME = "ITEM_NAME";
    private final String LISTNAME = "LISTNAME";
    public static final String ITEM_IDS = "ITEM_IDS";


    public static final String PAGER_FRAGMENT = "PAGER_FRAGMENT";


    private final OkHttpClient client = new OkHttpClient();

    private Toolbar toolbar;
    private FrameLayout frameLayout;
    private CollapsingToolbarLayout collapseBar;
    private AppBarLayout appBar;
    FloatingActionButton playFab,stopFab;

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
        }

        fragmentManager = getFragmentManager();

        //init layouts in toolbar
        appBar =  ((AppBarLayout) findViewById(R.id.app_bar_layout));
        collapseBar = ((CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout));
        frameLayout = (FrameLayout)findViewById(R.id.fragment_placeholder);

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

        //test network
        try {
            run();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Toast.makeText(this, reslt[0], Toast.LENGTH_LONG).show();

        CardView addButton = (CardView) findViewById(R.id.add_item_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShopinItemsActivity.this, AddItemActivity.class);
                intent.putExtra(LISTID,listId);
                intent.putExtra(LISTNAME,listName);
                startActivity(intent);
                finish();
            }
        });



        playFab = (FloatingActionButton) findViewById(R.id.play_fab);
        playFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //if list is not empty enable shopping mode
                if(!(lists.size() <= 0))
                {
                    startShopping();
                    playFab.setVisibility(View.GONE);
                    stopFab.setVisibility(View.VISIBLE);
                    appBar.setExpanded(true);

                    //Snack bar to indicate editing is disabled
                    final Snackbar snackBar = Snackbar.make(view, "Editing Disabled", Snackbar.LENGTH_LONG);
                    snackBar.setAction("Dismiss", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            snackBar.dismiss();
                            stopFab.setTranslationY(0);
                        }
                    });
                    snackBar.show();
                }
                else
                {
                    //Snack bar to indicate no items in list
                    final Snackbar snackBar = Snackbar.make(view, "\""+title.trim()+"\" is empty", Snackbar.LENGTH_LONG);
                    snackBar.setAction("Dismiss", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            snackBar.dismiss();
                            stopFab.setTranslationY(0);
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
                stopShopping();
                stopFab.setVisibility(View.GONE);
                playFab.setVisibility(View.VISIBLE);

                //Snack bar to indicate editing is enabled
                final Snackbar snackBar = Snackbar.make(view, "Editing Enabled", Snackbar.LENGTH_LONG);
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

        addViewPagerFragment();
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
            viewPagerFragment = new ViewPagerFragment();
            Bundle bundle = new Bundle();
            bundle.putIntegerArrayList(ITEM_IDS,getItemIDs(lists));
            bundle.putInt(LISTID,listId);
            viewPagerFragment.setArguments(bundle);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.fragment_placeholder, viewPagerFragment, PAGER_FRAGMENT);
            fragmentTransaction.commit();
        }
    }

    public void startShopping()
    {
        isShopping = true;

        if(!title.equals("")){
            collapseBar.setTitle("");
            frameLayout.setVisibility(View.VISIBLE);
            title ="";
            toolbar.setNavigationIcon(null);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                getWindow().setStatusBarColor(getResources().getColor(R.color.dark_grey));
        }
    }

    public void stopShopping()
    {
        isShopping = false;

        if(title.equals(""))
        {
            title = capitalize(listName);
            collapseBar.setTitle(title);
            frameLayout.setVisibility(View.GONE);
            toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.arrow_left));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
    }


    //get ID's of all items
    public ArrayList<Integer> getItemIDs(ArrayList<ShoppingListItem> list)
    {
        ArrayList<Integer> result = new ArrayList<Integer>();

        for(ShoppingListItem item: list)
            result.add(item.getItemID());

        return result;
    }

    private void run() throws Exception{
        String url = "http://ec2-52-39-22-233.us-west-2.compute.amazonaws.com/api/";
        final MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");

        String[] list = shoppingListItemController.getListArray();//{"Milk", "Baby", "Fish"};

        if(list.length != 0)
        {
            JSONObject json = new JSONObject();

            json.put("list", new JSONArray(Arrays.asList(list)));

            RequestBody body = RequestBody.create(JSON, String.valueOf(json));
            Log.v("JSON", String.valueOf(json));
            Request request = new Request.Builder()
                    .url(url + "items")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);

                    //res[0] = response.body().toString();
                    try {
                        print(response.body().string());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            });
        }
    }

    private void print(String data) throws JSONException {
        JSONObject jsonData = new JSONObject(data);
        Log.v("NetworkDATA", jsonData.getString("data"));
        shoppingListItemController.addNetworkData(jsonData);
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

        CollapsingToolbarLayout collapseBar = ((CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout));

        //control collapsebar behaviour when shopping
        if(collapseBar.getHeight() + verticalOffset < 2 * ViewCompat.getMinimumHeight(collapseBar) && isShopping)
        {
            String title = capitalize(listName);
            collapseBar.setTitle(title);
            frameLayout.setVisibility(View.GONE);
            toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.arrow_left));
        }else if(collapseBar.getHeight() + verticalOffset > 2 * ViewCompat.getMinimumHeight(collapseBar) && isShopping)
        {
            collapseBar.setTitle("");
            frameLayout.setVisibility(View.VISIBLE);
            toolbar.setNavigationIcon(null);
        }

    }

    @Override
    public void onComplete() {

        //called when checkbox in fragment is clicked -PageContentFragment.java
        updateList();
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
                    listAdapter.removeItems(listAdapter.getSelectedItems());
                    mode.finish();
                    return true;
                case R.id.edit_menu_item:
                    Intent intent = new Intent(ShopinItemsActivity.this,AddItemActivity.class);
                    intent.putExtra(LISTID, lists.get(listAdapter.getSelectedItems().get(0)).getListID());
                    intent.putExtra(ITEM_ID, lists.get(listAdapter.getSelectedItems().get(0)).getItemID());
                    intent.putExtra(ITEM_NAME, lists.get(listAdapter.getSelectedItems().get(0)).getItemName());
                    startActivity(intent);
                    finish();
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            listAdapter.clearSelection();
            actionMode = null;
            toolbar.setVisibility(View.VISIBLE);
            //return to "old" color of status bar
            //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            //  getWindow().setStatusBarColor(statusBarColor);
        }
    }

}
