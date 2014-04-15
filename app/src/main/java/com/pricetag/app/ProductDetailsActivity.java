package com.pricetag.app;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Created by SKG on 14-Apr-14.
 */
public class ProductDetailsActivity extends ActionBarActivity {
    private String prodID,prodImg;
    private String baseUrl, sellerSiteUrl;
    ImageView imageView;
    TextView textTitle, textPriceMin;
    WebView textDescription;
    private String[] sellerImage, sellerTitle, sellerPrice, sellerUrl;
    ListView sellerView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        Intent getProductId = getIntent();
        if (getProductId != null) {
            prodImg = getProductId.getStringExtra("productID");
        }
        String[] tokens = prodImg.split("/");
        prodID = tokens[tokens.length-2];

        baseUrl = getResources().getString(R.string.api_url)+prodID;
        imageView = (ImageView) findViewById(R.id.imageView);
        textTitle = (TextView) findViewById(R.id.prod_title);
        textPriceMin = (TextView) findViewById(R.id.min_price);
        textDescription = (WebView) findViewById(R.id.prod_desc);
        sellerView = (ListView) findViewById(R.id.seller_list);
        new HttpAsyncTask().execute(baseUrl);

    }
    class HttpAsyncTask extends AsyncTask<String, Void, String> {
        GetJsonString getJsonString = new GetJsonString();


        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... urls) {
            return getJsonString.GET(urls[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            ConnectivityManager connec = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            if ( connec != null && (connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED) ||((connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED))) {
                //You are connected, do something online.
                try {
                    JSONObject json = new JSONObject(result);
                    JSONObject products = json.getJSONObject("products");
                    setTitle(products.getString("name"));
                    Ion.with(imageView).placeholder(R.drawable.product).error(R.drawable.product).load(products.getString("large_image"));
                    textTitle.setText(products.getString("name"));
                    textDescription.getSettings().setJavaScriptEnabled(true);
                    textDescription.loadDataWithBaseURL("", products.getString("desc"), "text/html", "UTF-8", "");
                    textPriceMin.setText("Rs." + products.getString("price"));
                    textPriceMin.setVisibility(View.VISIBLE);

                    if(products.getString("desc") == "null"){
                        textDescription.loadDataWithBaseURL("", null, "text/html", "UTF-8", "");
                        textDescription.setVisibility(View.INVISIBLE);
                    }

                    JSONObject suppliers = json.getJSONObject("suppliers");
                    JSONArray supplier_details = suppliers.getJSONArray("supplier_details");
                    sellerImage = new String[supplier_details.length()];
                    sellerTitle = new String[supplier_details.length()];
                    sellerPrice = new String[supplier_details.length()];
                    sellerUrl = new String[supplier_details.length()];
                    for (int i = 0; i < supplier_details.length(); i++) {
                        sellerImage[i] = supplier_details.getJSONObject(i).getString("store_image");
                        sellerTitle[i] = supplier_details.getJSONObject(i).getString("name");
                        sellerPrice[i] = "Rs." + supplier_details.getJSONObject(i).getString("price");
                        sellerUrl[i] = supplier_details.getJSONObject(i).getString("url");
                    }

                    // ArrayAdapter<String> adapter = new ArrayAdapter<String>(ProductDetailsActivity.this,android.R.layout.simple_list_item_1,sellerTitle);
                    ListAdapterClass adapter = new ListAdapterClass(ProductDetailsActivity.this, sellerImage, sellerTitle, sellerPrice, sellerUrl);
                    sellerView.setAdapter(adapter);
                    Helper.getListViewSize(sellerView);

                    final ScrollView scrollview = ((ScrollView) findViewById(R.id.scrollView));
                    scrollview.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollview.fullScroll(ScrollView.FOCUS_UP);
                        }
                    });

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }else if (connec.getNetworkInfo(0).getState() == NetworkInfo.State.DISCONNECTED ||  connec.getNetworkInfo(1).getState() == NetworkInfo.State.DISCONNECTED ) {
                //Not connected.
                imageView.setVisibility(View.INVISIBLE);
                imageView.setMaxHeight(0);
                textTitle.setText("Connect to Internet...");
            }
        }
    }
    public void buy(View v){
        Button buttonBuy;
        buttonBuy = (Button) v.findViewById(R.id.button_buy);
        String position = buttonBuy.getTag().toString();
        int i = Integer.parseInt(position);
        sellerSiteUrl = sellerUrl[i];
        new SellerUrlParser().execute(sellerSiteUrl);
    }

    class SellerUrlParser extends AsyncTask<String, Void, Document> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }

        @Override
        protected Document doInBackground(String... urls) {
            Document doc = null;
            try {
                doc = Jsoup.connect(urls[0]).userAgent("Mozilla").get();
            } catch (IOException e) {
                e.printStackTrace();
                return doc;
            }
            return doc;
        }

        @Override
        protected void onPostExecute(Document doc) {
            Elements title_url = doc.select("a");
            String gotoUrl = title_url.attr("abs:href");

            Intent externalActivity = new Intent(Intent.ACTION_VIEW);
            externalActivity.setData(Uri.parse(gotoUrl));
            startActivity(externalActivity);
        }
    }
    public static class Helper {
        public static void getListViewSize(ListView myListView) {
            ListAdapter myListAdapter = myListView.getAdapter();
            if (myListAdapter == null) {
                //do nothing return null
                return;
            }
            //set listAdapter in loop for getting final size
            int totalHeight = 0;
            for (int size = 0; size < myListAdapter.getCount(); size++) {
                View listItem = myListAdapter.getView(size, null, myListView);
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }
            //setting listview item in adapter
            ViewGroup.LayoutParams params = myListView.getLayoutParams();
            params.height = totalHeight + (myListView.getDividerHeight() * (myListAdapter.getCount() -1 ));
            myListView.setLayoutParams(params);
            // print height of adapter on log
            Log.i("height of listItem:", String.valueOf(totalHeight));
        }
    }
}


