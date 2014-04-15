package com.pricetag.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;

public class ListAdapterClass extends ArrayAdapter<String>{
  Context context;
    private String[] sellerImage, sellerTitle, sellerPrice, sellerUrl;
  
 
  
  ListAdapterClass(Context c,String[] sellerImage,String[] sellerTitle,String[] sellerPrice, String [] sellerUrl){
    super(c,R.layout.seller_list_item,R.id.seller_title,sellerTitle);
    this.context= c;
    this.sellerImage = sellerImage;
    this.sellerTitle = sellerTitle;
    this.sellerPrice = sellerPrice;
    this.sellerUrl = sellerUrl;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent){
      View row = convertView;
      LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      row = inflater.inflate(R.layout.seller_list_item, parent,false);
      ImageView myImage;
      TextView myTitle;
      TextView  myPrice;
      Button buttonBuy;
      myImage = (ImageView) row.findViewById(R.id.seller_image);
      myTitle =  (TextView) row.findViewById(R.id.seller_title);
      myPrice =  (TextView) row.findViewById(R.id.seller_price);
      buttonBuy = (Button) row.findViewById(R.id.button_buy);

      Ion.with(myImage).placeholder(R.drawable.ic_launcher).error(R.drawable.ic_launcher).load(sellerImage[position]);
      myTitle.setText(sellerTitle[position]);
      myPrice.setText(sellerPrice[position]);
      buttonBuy.setTag(position);
      return row;
    
  }
}
