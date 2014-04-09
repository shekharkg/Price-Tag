package com.pricetag.app;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.TextView;

import com.etsy.android.grid.StaggeredGridView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;


public class MainActivity extends ActionBarActivity implements AbsListView.OnItemClickListener, AbsListView.OnScrollListener {

    private static final String TAG = "MainActivity";
    String productTitle, productImage, productUrl;
    StaggeredGridView myGridView;
    GridAdapter myAdapter;
    LayoutInflater getLayoutFooter;
    View footer;
    TextView txtFooterTitle;
    String postUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myGridView = (StaggeredGridView) findViewById(R.id.grid_view);
        getLayoutFooter = getLayoutInflater();
        footer = getLayoutFooter.inflate(R.layout.list_item_header_footer, null);
        txtFooterTitle =  (TextView) footer.findViewById(R.id.txt_title);
        txtFooterTitle.setText("THE FOOTER!");
        myGridView.addFooterView(footer);
        myAdapter = new GridAdapter(this, R.id.txt_line);
        myGridView.setAdapter(myAdapter);
        myGridView.setOnScrollListener(this);
        myGridView.setOnItemClickListener(this);
        postUrl = getResources().getString(R.string.base_url);
        new JsoupHttpAsyncTask().execute(postUrl);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(position != myAdapter.getCount()) {
            String[] shownToDrawer = new String[myAdapter.getCount()];
            int i;
            for(i=0;i< myAdapter.getCount();i++){
                ProductData productTitle = myAdapter.getItem(i);
                shownToDrawer[i] = productTitle.getTitle();
            }
            ProductData productUrl = myAdapter.getItem(position);
            Intent setIntentProdId = new Intent(this, DrawerActivity.class);
            setIntentProdId.putExtra("shownToDrawer",shownToDrawer);
            setIntentProdId.putExtra("baseUrl", productUrl.getUrl());
            setIntentProdId.putExtra("selectPosition", position);
            startActivity(setIntentProdId);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    class JsoupHttpAsyncTask extends AsyncTask<String, Void, Document> {

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
                doc = Jsoup.connect(urls[0]).userAgent("Mozilla").get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return doc;
        }

        @Override
        protected void onPostExecute(Document doc) {
            ConnectivityManager connec = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connec != null && (connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED) ||(connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED)){
                //You are connected, do something online.
                Elements title_url = doc.select("a[href*=m.pricedekho]");
                for(int i=0; i<title_url.size()-4; i++){
                    productTitle = title_url.get(i).text();
                    productUrl = title_url.get(i).attr("abs:href");
                    productImage = null;
                    myAdapter.add(new ProductData(productTitle, productImage, productUrl));
                }
                myAdapter.notifyDataSetChanged();
                txtFooterTitle.setText("");
                }else if (connec.getNetworkInfo(0).getState() == NetworkInfo.State.DISCONNECTED ||  connec.getNetworkInfo(1).getState() == NetworkInfo.State.DISCONNECTED ) {
                //Not connected.
                txtFooterTitle.setText("Connect to Internet...");
            }



        }

    }
}
