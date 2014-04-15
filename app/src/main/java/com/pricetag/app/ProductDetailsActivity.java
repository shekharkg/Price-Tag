package com.pricetag.app;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by SKG on 14-Apr-14.
 */
public class ProductDetailsActivity extends ActionBarActivity {
    private String prodID,prodImg;
    private String baseUrl;
    ImageView imageView;
    TextView textTitle;
    WebView textDescription;
    private String sellerImage, sellerTitle, sellerPrice, sellerRating, sellerUrl;

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
        textDescription = (WebView) findViewById(R.id.prod_desc);

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
            try {
                JSONObject json = new JSONObject(result);
                JSONObject products = json.getJSONObject("products");
                setTitle(products.getString("name"));
                Ion.with(imageView).placeholder(R.drawable.product).error(R.drawable.product).load(products.getString("large_image"));
                textTitle.setText(products.getString("name"));
                textDescription.getSettings().setJavaScriptEnabled(true);
                textDescription.loadDataWithBaseURL("", products.getString("desc"), "text/html", "UTF-8", "");

                JSONObject suppliers = json.getJSONObject("suppliers");
                JSONArray supplier_details = suppliers.getJSONArray("supplier_details");
                for (int i = 0; i < supplier_details.length(); i++) {
                   sellerImage = supplier_details.getJSONObject(i).getString("store_image");
                   sellerTitle = supplier_details.getJSONObject(i).getString("name");
                   sellerPrice = supplier_details.getJSONObject(i).getString("price");
                   sellerRating = supplier_details.getJSONObject(i).getString("supplier_type");
                   sellerUrl  = supplier_details.getJSONObject(i).getString("url");

                   textTitle.setText(sellerImage + "\n" + sellerTitle + "\n" + sellerPrice + "\n" + sellerRating + "\n" +sellerUrl);
                }

            }catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }
}
