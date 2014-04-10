package com.pricetag.app;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.etsy.android.grid.StaggeredGridView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Created by SKG on 10-Apr-14.
 */
public class PriceListActivity extends ActionBarActivity implements AbsListView.OnScrollListener, AbsListView.OnItemClickListener, ActionBar.OnNavigationListener {

    private static final String TAG = "MainActivity";
    private static StaggeredGridView myGridView;
    private static GridAdapterPriceList myAdapter;
    private static LayoutInflater layoutInflater;
    private boolean myHasRequestedMore;
    private static View footer;
    private static TextView txtFooterTitle;
    private static SearchView mSearchView;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private String[] myDrawerTitles, myDrawerUrls, mySpinnerTitles, mySpinnerUrls;
    static int drawerValue;
    private String baseUrl;
    private int positionValue;
    String productTitle, productImage, productPrice;
    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
    private int start, minus;

    @Override
    protected void onCreate(Bundle savedInstanceStateCategory) {
        super.onCreate(savedInstanceStateCategory);
        setContentView(R.layout.drawer_layout);
        start = 1;
        minus = 0;

        Intent getIntentFromMainActivity = getIntent();
        if (getIntentFromMainActivity != null) {
            baseUrl = getIntentFromMainActivity.getStringExtra("baseUrl");
            myDrawerTitles = getIntentFromMainActivity.getStringArrayExtra("shownToDrawer");
            myDrawerUrls = getIntentFromMainActivity.getStringArrayExtra("shownToDrawerUrl");
            mySpinnerTitles = getIntentFromMainActivity.getStringArrayExtra("shownToSpinner");
            mySpinnerUrls = getIntentFromMainActivity.getStringArrayExtra("shownToSpinnerUrl");
            positionValue = getIntentFromMainActivity.getIntExtra("selectPosition",0);
        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set up the action bar to show a dropdown list.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        // Set up the dropdown list navigation in the action bar.
        actionBar.setListNavigationCallbacks(
                // Specify a SpinnerAdapter to populate the dropdown list.
                new ArrayAdapter<String>(
                        actionBar.getThemedContext(),android.R.layout.simple_list_item_1,
                        android.R.id.text1, mySpinnerTitles), this);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, myDrawerTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        // getActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        selectItem(positionValue);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Serialize the current dropdown position.
        outState.putInt(STATE_SELECTED_NAVIGATION_ITEM,
                getSupportActionBar().getSelectedNavigationIndex());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        return super.onPrepareOptionsMenu(menu);
    }



    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent setIntentProdId = new Intent(PriceListActivity.this, DrawerActivity.class);
            setIntentProdId.putExtra("shownToDrawer",myDrawerTitles);
            setIntentProdId.putExtra("shownToDrawerUrl",myDrawerUrls);
            setIntentProdId.putExtra("baseUrl", myDrawerUrls[position]);
            setIntentProdId.putExtra("selectPosition", position);
            finish();
            startActivity(setIntentProdId);
        }
    }
    @Override
    public boolean onNavigationItemSelected(int i, long l) {
        //selectItem(i);
        return true;
    }
    private void selectItem(int position) {
        minus = 0;
        // update the main content by replacing fragments
        baseUrl = mySpinnerUrls[position];
        Fragment fragment = new PlanetFragment();
        Bundle args = new Bundle();
        args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
        fragment.setArguments(args);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, false);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void onScrollStateChanged(final AbsListView view, final int scrollState) {
        Log.d(TAG, "onScrollStateChanged:" + scrollState);
    }

    @Override
    public void onScroll(final AbsListView view, final int firstVisibleItem, final int visibleItemCount, final int totalItemCount) {
        if (!myHasRequestedMore) {
            int lastInScreen = firstVisibleItem + visibleItemCount;
            if ((lastInScreen >= totalItemCount)){
                Log.d(TAG, "onScroll lastInScreen - so load more");
                myHasRequestedMore = true;
                onLoadMoreItems();
            }
        }
    }
    private void onLoadMoreItems() {
        start += 1;
        int replace;
        if(start==2){
            baseUrl = baseUrl+"?page="+start+"&resultonly=true";
        }else{
            replace = start-1;
            baseUrl = baseUrl.replace("?page="+replace,"?page="+start);
        }
        new HttpAsyncTask().execute(baseUrl);
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.action_search:
                Intent intent = new Intent(this,SearchActivity.class);
                startActivity(intent);
                return(true);
        }
        // Handle action buttons
        return super.onOptionsItemSelected(item);

    }

    class HttpAsyncTask extends AsyncTask<String, Void, Document> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            txtFooterTitle.setText("Loading...");
            super.onPreExecute();
        }

        @Override
        protected Document doInBackground(String... urls) {
            Document doc = null;
            try {
                doc = Jsoup.connect(baseUrl).userAgent("Mozilla").get();
            } catch (IOException e) {
                e.printStackTrace();
                return doc;
            }
            return doc;
        }

        @Override
        protected void onPostExecute(Document doc) {
            ConnectivityManager connec = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);


            if (doc != null && connec != null && (connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED) ||(doc != null && (connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED))) {
                //You are connected, do something online.
                Elements title_img = doc.select("[height=115]");
                Elements price = doc.select("[class=price]");
                for(int i=minus; i<title_img.size(); i++){
                    productTitle = title_img.get(i).attr("abs:alt").split("pricedekho.com/")[1];
                    productTitle = productTitle.replace(" Price","");
                    productPrice = price.get(i).text();
                    productPrice = productPrice.replace("Starts at","");
                    productImage = title_img.get(i).attr("abs:src");
                    if(productImage.contains("pd.jpg") == true){
                        productImage = title_img.get(i).attr("abs:data-original");
                    }
                    myAdapter.add(new ProductData(productTitle, productImage, productPrice));
                }

                myAdapter.notifyDataSetChanged();
                myHasRequestedMore = false;
                txtFooterTitle.setText("");
                minus = 5;
            }else if (connec.getNetworkInfo(0).getState() == NetworkInfo.State.DISCONNECTED ||  connec.getNetworkInfo(1).getState() == NetworkInfo.State.DISCONNECTED ) {
                //Not connected.
                txtFooterTitle.setText("Connect to Internet...");
            }else{
                txtFooterTitle.setText("Connection Problem...");
            }
        }
    }

    /**
     * Fragment that appears in the "content_frame", shows a planet
     */
    public class PlanetFragment extends Fragment {
        public static final String ARG_PLANET_NUMBER = "planet_number";

        public PlanetFragment() {
            // Empty constructor required for fragment subclasses
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.activity_main, container, false);
            drawerValue = getArguments().getInt(ARG_PLANET_NUMBER);
            myGridView = (StaggeredGridView) rootView.findViewById(R.id.grid_view);
            layoutInflater = getLayoutInflater();
            footer = layoutInflater.inflate(R.layout.list_item_header_footer, null);
            txtFooterTitle =  (TextView) footer.findViewById(R.id.txt_title);
            txtFooterTitle.setText("THE FOOTER!");
            myGridView.addFooterView(footer);
            myAdapter = new GridAdapterPriceList(PriceListActivity.this, R.id.txt_line);
            myGridView.setAdapter(myAdapter);
            myGridView.setOnScrollListener(PriceListActivity.this);
            myGridView.setOnItemClickListener(PriceListActivity.this);
            new HttpAsyncTask().execute(baseUrl);
            return rootView;
        }
    }
}
