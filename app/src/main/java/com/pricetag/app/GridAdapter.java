package com.pricetag.app;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.etsy.android.grid.util.DynamicHeightImageView;
import com.etsy.android.grid.util.DynamicHeightTextView;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by SKG on 24-Mar-14.
 */
public class GridAdapter extends ArrayAdapter<ProductData> {

    private static final String TAG = "GridAdapter";
    static class ViewHolder {
        DynamicHeightTextView txtLineOne;
        Button btnGo;
        DynamicHeightImageView productImg;
    }

    LayoutInflater myLayoutInflater;
    Random myRandom;
    SparseArray<Double> sPositionHeightRatios = new SparseArray<Double>();
    ArrayList<Integer> mBackgroundColors;
    List<ProductData> myData;

    public GridAdapter(Context context, int resource) {
        super(context, resource);
        myLayoutInflater = LayoutInflater.from(context);
        myRandom = new Random();
        mBackgroundColors = new ArrayList<Integer>();
        mBackgroundColors.add(R.color.black);
    }



    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        ViewHolder vh;
        if (convertView == null) {
            convertView = myLayoutInflater.inflate(R.layout.list_item, parent, false);
            vh = new ViewHolder();
            vh.txtLineOne = (DynamicHeightTextView) convertView.findViewById(R.id.txt_line);
            vh.btnGo = (Button) convertView.findViewById(R.id.btn_go);
            vh.productImg = (DynamicHeightImageView) convertView.findViewById(R.id.product_img);

            convertView.setTag(vh);
        }
        else {
            vh = (ViewHolder) convertView.getTag();
        }

        double positionHeight = getPositionRatio(position);
        int backgroundIndex = position >= mBackgroundColors.size() ?
                position % mBackgroundColors.size() : position;

        convertView.setBackgroundResource(mBackgroundColors.get(backgroundIndex));

        Log.d(TAG, "getView position:" + position + " h:" + positionHeight);

        vh.productImg.setHeightRatio(positionHeight);
        vh.txtLineOne.setText(getItem(position).getTitle());
        //vh.txtLineOne.setText(getItem(position).getTitle() + position);  =============  for checking items position. remove upper line if use this
        //vh.btnGo.setText(getItem(position).getId());
        Ion.with(vh.productImg).placeholder(R.drawable.product).error(R.drawable.product).load(getItem(position).getImage());

              vh.btnGo.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(final View v) {
                      Toast.makeText(getContext(), "Price : " +
                              getItem(position).getUrl(), Toast.LENGTH_SHORT).show();
                  }
              });

        return convertView;
    }
    private double getPositionRatio(final int position) {
        double ratio = sPositionHeightRatios.get(position, 0.0);
        // if not yet done generate and stash the columns height
        // in our real world scenario this will be determined by
        // some match based on the known height and width of the image
        // and maybe a helpful way to get the column height!
        if (ratio == 0) {
            ratio = getRandomHeightRatio();
            sPositionHeightRatios.append(position, ratio);
            Log.d(TAG, "getPositionRatio:" + position + " ratio:" + ratio);
        }
        return ratio;
    }

    private double getRandomHeightRatio() {
        return (myRandom.nextDouble() / 2.0) + 0.6; // height will be 1.0 - 1.5 the width
    }
 }
