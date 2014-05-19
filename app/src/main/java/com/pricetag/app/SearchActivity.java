package com.pricetag.app;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.SearchView;
import android.widget.TextView;

import com.etsy.android.grid.StaggeredGridView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by SKG on 10-Apr-14.
 */
public class SearchActivity extends ActionBarActivity implements AbsListView.OnScrollListener, AbsListView.OnItemClickListener, SearchView.OnQueryTextListener {

    private static final String TAG = "Searchctivity";
    private static StaggeredGridView myGridView;
    private static GridAdapterPriceList myAdapter;
    private static LayoutInflater layoutInflater;
    private boolean myHasRequestedMore;
    private static View footer;
    private static TextView txtFooterTitle;
    private static SearchView mSearchView;
    String productTitle, productImage, productPrice;
    private int page;
    private String searchValue, baseUrl,searchUrl,baseUrlWdOutPage;

    @Override
    protected void onCreate(Bundle savedInstanceStateCategory) {
        super.onCreate(savedInstanceStateCategory);
        setContentView(R.layout.activity_search);
        searchUrl = getResources().getString(R.string.search_url);
        try {
            searchUrl = URLDecoder.decode(searchUrl,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        page = 1;

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

    }

    private void selectItem(int position) {
        baseUrlWdOutPage = searchUrl.replace("searchQuery",searchValue);
        baseUrl = baseUrlWdOutPage.replace("pageNumber",""+page);
        Fragment fragment = new PlanetFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setIconified(false);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        searchValue = query.replace(" ","+");
        selectItem(0);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return true;
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
        page += 1;
        baseUrl = baseUrlWdOutPage.replace("pageNumber",""+page);
        new HttpAsyncTask().execute(baseUrl);
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        if(position != myAdapter.getCount()){
            ProductData productID= myAdapter.getItem(position);
            Intent intent = new Intent(this, ProductDetailsActivity.class);
            intent.putExtra("productID", productID.getImage());
            startActivity(intent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return(true);
        }
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

                Elements title_img = doc.select("[height=130]");
                Elements price = doc.select("[class=price]");
                try {
                    for (int i = 0; i < price.size(); i++) {
                        productTitle = title_img.get(i).attr("abs:alt").split("search/")[1];
                        productTitle = productTitle.replace(" Price", "");
                        productPrice = price.get(i).text();
                        productPrice = productPrice.replace("Starts at", "");
                        productImage = title_img.get(i).attr("abs:src");
                        if (productImage.contains("pd.jpg") == true) {
                            productImage = title_img.get(i).attr("abs:data-original");
                        }
                        myAdapter.add(new ProductData(productTitle, productImage, productPrice));
                    }
                }catch(Exception e){
                }
                myAdapter.notifyDataSetChanged();
                myHasRequestedMore = false;
                txtFooterTitle.setText("");
                if(myAdapter.getCount()==0){
                    txtFooterTitle.setText("No result found");
                }
            }else if (connec.getNetworkInfo(0).getState() == NetworkInfo.State.DISCONNECTED ||  connec.getNetworkInfo(1).getState() == NetworkInfo.State.DISCONNECTED ) {
                //Not connected.
                txtFooterTitle.setText("Connect to Internet...");
            }else{
                txtFooterTitle.setText("Connection Problem...");
            }
        }
    }
    public class PlanetFragment extends Fragment {
        public static final String ARG_PLANET_NUMBER = "planet_number";

        public PlanetFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.activity_main, container, false);
            myGridView = (StaggeredGridView) rootView.findViewById(R.id.grid_view);
            layoutInflater = getLayoutInflater();
            footer = layoutInflater.inflate(R.layout.list_item_header_footer, null);
            txtFooterTitle =  (TextView) footer.findViewById(R.id.txt_title);
            txtFooterTitle.setText("THE FOOTER!");
            myGridView.addFooterView(footer);
            myAdapter = new GridAdapterPriceList(SearchActivity.this, R.id.txt_line);
            myGridView.setAdapter(myAdapter);
            myGridView.setOnScrollListener(SearchActivity.this);
            myGridView.setOnItemClickListener(SearchActivity.this);
            new HttpAsyncTask().execute(baseUrl);
            return rootView;
        }
    }
}
