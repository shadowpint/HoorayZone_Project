package com.horrayzone.horrayzone;

import android.net.Uri;

public class Config {

    public static final String ENDPOINT_URL = "https://horrayzone-booking.herokuapp.com";
    //public static final String ENDPOINT_URL = "http://192.168.0.100/minris/web/app_dev.php";
    public static final String BASE_URL_IMAGE = "https://horrayzone-booking.herokuapp.com/bundles/assets/basics";
    //public static final String BASE_URL_IMAGE = "http://192.168.0.100/minris/web/bundles/assets/basics";

    public static final String PATH_IMAGE_PRODUCT = "/products";
    public static final String PATH_IMAGE_BLOG_ENTRY = "/reports";
    public static final String PATH_IMAGE_SUBCATEGORIES = "/subcategories";

    public static final String OAUTH_CLIENT_ID = "6J0cM1ksd9MEccKeH4iWH8zMmd7dSRNuDE2JOEPI";
    public static final String OAUTH_CLIENT_SECRET = "ZNX4V8HL9AUeiKQTW0IY6w0tAFSp06P7MTBcJEkASzQYsO4fjNBgiUD5gPZ5MbKunDArNMYlI3XRs5C31kuaeXFmJ4JkIgQEvpgBhvx7y3vVNC2Y8mtNSYnh06aoCc5x";
    public static final String OAUTH_GRANT_TYPE_PASSWORD = "password";
    public static final String OAUTH_GRANT_TYPE_REFRESH_TOKEN = "refresh_token";

    public static String buildProductImageUrl(String imageFilename) {
        return buildImageUrlWithPath(PATH_IMAGE_PRODUCT, imageFilename);
    }

    public static String buildSubcategoryImageUrl(String imageFilename) {
        return buildImageUrlWithPath(PATH_IMAGE_SUBCATEGORIES, imageFilename);
    }

    public static String buildBlogEntryImageUrl(String imageFilename) {
        return buildImageUrlWithPath(PATH_IMAGE_BLOG_ENTRY, imageFilename);
    }

    private static String buildImageUrlWithPath(String path, String imageFilename) {
        if (imageFilename == null) {
            return null;
        }
        return new StringBuilder()
                .append(BASE_URL_IMAGE)
                .append(path)
                .append('/')
                .append(imageFilename)
                .toString();
    }

    public static Uri buildProductImageUrlByCodeAndColor(String productCode, long colorId) {
        return Uri.parse(ENDPOINT_URL + "/web-service/shoppingCart/product/image")
                .buildUpon()
                .appendQueryParameter("productCode", productCode)
                .appendQueryParameter("colorId", Long.toString(colorId))
                .build();
    }

}
