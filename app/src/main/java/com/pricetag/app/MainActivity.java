package com.pricetag.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;

import com.etsy.android.grid.StaggeredGridView;


public class MainActivity extends ActionBarActivity implements AbsListView.OnItemClickListener{

    StaggeredGridView myGridView;
    GridAdapterMainActivity myAdapter;
    String[] shownToDrawerUrl, shownToDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        shownToDrawer = getResources().getStringArray(R.array.drawer_menu);
        shownToDrawerUrl = getResources().getStringArray(R.array.drawer_menu_url);
        myGridView = (StaggeredGridView) findViewById(R.id.grid_view);
        myAdapter = new GridAdapterMainActivity(this, R.id.txt_line);
        myGridView.setAdapter(myAdapter);
        myGridView.setOnItemClickListener(this);
        setTitle("Browse Categories");
        String image = null;
        for(int i=0; i<6; i++){
            myAdapter.add(new ProductData(shownToDrawer[i], image, shownToDrawerUrl[i]));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                Intent intent = new Intent(this,SearchActivity.class);
                startActivity(intent);
                return(true);
        }
        // Handle action buttons
        return super.onOptionsItemSelected(item);

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
}
