package com.pricetag.app;

/**
 * Created by SKG on 24-Mar-14.
 */
public class ProductData {
    String title;
    String image;
    String url;

    public ProductData(String title, String image, String url) {
        this.title = title;
        this.image = image;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
