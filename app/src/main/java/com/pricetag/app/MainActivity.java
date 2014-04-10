package com.pricetag.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.TextView;

import com.etsy.android.grid.StaggeredGridView;


public class MainActivity extends ActionBarActivity implements AbsListView.OnItemClickListener, AbsListView.OnScrollListener {

    StaggeredGridView myGridView;
    GridAdapterMainActivity myAdapter;
    LayoutInflater getLayoutFooter;
    View footer;
    TextView txtFooterTitle;
    String postUrl;
    String[] shownToDrawerUrl, shownToDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        shownToDrawer = getResources().getStringArray(R.array.drawer_menu);
        shownToDrawerUrl = getResources().getStringArray(R.array.drawer_menu_url);
        myGridView = (StaggeredGridView) findViewById(R.id.grid_view);
        getLayoutFooter = getLayoutInflater();
        footer = getLayoutFooter.inflate(R.layout.list_item_header_footer, null);
        txtFooterTitle =  (TextView) footer.findViewById(R.id.txt_title);
        txtFooterTitle.setText(" ");
        myGridView.addFooterView(footer);
        myAdapter = new GridAdapterMainActivity(this, R.id.txt_line);
        myGridView.setAdapter(myAdapter);
        myGridView.setOnScrollListener(this);
        myGridView.setOnItemClickListener(this);
        String image = null;
        for(int i=0; i<6; i++){
            myAdapter.add(new ProductData(shownToDrawer[i], image, shownToDrawerUrl[i]));
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(position != myAdapter.getCount()) {
            Intent setIntentProdId = new Intent(this, DrawerActivity.class);
            setIntentProdId.putExtra("shownToDrawer",shownToDrawer);
            setIntentProdId.putExtra("shownToDrawerUrl",shownToDrawerUrl);
            setIntentProdId.putExtra("baseUrl", shownToDrawer[position]);
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
}
