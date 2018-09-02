package com.horrayzone.horrayzone.sync;

import com.horrayzone.horrayzone.model.Address;
import com.horrayzone.horrayzone.model.AuthTokenResponse;
import com.horrayzone.horrayzone.model.BlogEntry;
import com.horrayzone.horrayzone.model.CartItem;
import com.horrayzone.horrayzone.model.City;
import com.horrayzone.horrayzone.model.ClientTokenResponse;
import com.horrayzone.horrayzone.model.Commodity;
import com.horrayzone.horrayzone.model.Country;
import com.horrayzone.horrayzone.model.Event;
import com.horrayzone.horrayzone.model.Order;
import com.horrayzone.horrayzone.model.Price;
import com.horrayzone.horrayzone.model.TransactionResponse;
import com.horrayzone.horrayzone.model.UserInfo;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit.client.Response;
import retrofit.http.DELETE;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

public interface WebService {
    String PATH_AUTH_GOOGLE = "/user/google-token-verify/";
    String PATH_AUTH_TOKEN = "/auth/token/";
    String PATH_CREATE_ACCOUNT = "/user/signup/";
    String PATH_CREATE_PASSWORD = "/user/google-signup/";
    String PATH_USER_INFO = "/auth/user";
    String PATH_COUNTRY = "/minpol/api/countrys/";
    String PATH_COMMODITY = "/minpol/api/predict/";
    String PATH_CATEGORIES = "/api/shop/categories/";
    String PATH_SUBCATEGORIES = "/api/shop/subcategories/";
    String PATH_BRANDS = "/api/shop/brands/";
    String PATH_PRODUCT = "/api/shop/product/";
    String PATH_PRODUCT_STOCK = "/api/shop/product/stock/";
    String PATH_PRODUCTS = "/api/shop/product/";

    String PATH_CITY = "/api/shop/city/";
    String PATH_EVENT = "/api/shop/event/{id}/";
    String PATH_EVENT_NEARBY = "/api/shop/event_nearby/";
    String PATH_PRICE = "/api/shop/price/{id}/";
    String PATH_PRODUCT_DETAIL = "/api/shop/productdetail/";
    String PATH_STORE = "/api/shop/product/";
    String PATH_BLOG_FEED = "/news/blog/";
    String PATH_SHOPPING_CART = "/api/shop/shoppingCart/";
    String PATH_SHOPPING_CART_DELETE = "/api/shop/shoppingCart/{id}/";
    String PATH_WISHLIST = "/api/shop/wishlist/";
    String PATH_ADDRESS_BOOK = "/api/shop/address-book/";
    String PATH_ORDERS = "/api/shop/orders/";
    String PATH_ORDER_DETAILS = "/api/shop/order-details/";
    String PATH_PLACE_ORDER = "/api/shop/place-order/";
    String PATH_PAY_TOKEN = "/api/payment/getclienttoken/";
    String PATH_ADD_TICKET= "/api/payment/addticket/";
    String PATH_BOOKING= "/api/payment/bookings/";
    String PATH_PAY = "/api/payment/checkout/";
    String HEADER_IF_MODIFIED_SINCE = "If-Modified-Since";
    String HEADER_AUTHORIZATION = "Authorization";

    String FIELD_EMAIL = "email";
    String FIELD_ID = "user_id";
    String FIELD_USERNAME = "username";
    String FIELD_PASSWORD = "password";
    String FIELD_REFRESH_TOKEN = "refresh_token";
    String FIELD_FIRST_NAME = "firstName";
    String FIELD_LAST_NAME = "lastName";
    String FIELD_COMPANY = "companyName";
    String FIELD_CLIENT_ID = "client_id";
    String FIELD_CLIENT_SECRET = "client_secret";
    String FIELD_GRANT_TYPE = "grant_type";

    @FormUrlEncoded
    @POST(PATH_AUTH_TOKEN)
    AuthTokenResponse getAuthToken(
            @Field(FIELD_USERNAME) String email,
            @Field(FIELD_PASSWORD) String password,
            @Field(FIELD_CLIENT_ID) String clientId,
            @Field(FIELD_CLIENT_SECRET) String clientSecret,
            @Field(FIELD_GRANT_TYPE) String grantType);

    @FormUrlEncoded
    @POST(PATH_AUTH_TOKEN)
    AuthTokenResponse refreshAccessToken(
            @Field(FIELD_REFRESH_TOKEN) String refreshToken,
            @Field(FIELD_CLIENT_ID) String clientId,
            @Field(FIELD_CLIENT_SECRET) String clientSecret,
            @Field(FIELD_GRANT_TYPE) String grantType);

    @GET(PATH_USER_INFO)
    UserInfo getUserInfo(
            @Header(HEADER_AUTHORIZATION) String authToken,
            @Query(FIELD_EMAIL) String email);


    @FormUrlEncoded
    @POST(PATH_AUTH_GOOGLE)
    String verifyGoogleToken(
            @Field("token") String idToken

    );








    @FormUrlEncoded
    @POST(PATH_CREATE_ACCOUNT)
    Response createAccount(
            @Field(FIELD_CLIENT_ID) String clientId,
            @Field(FIELD_CLIENT_SECRET) String clientSecret,
            @Field(FIELD_GRANT_TYPE) String grantType,
            @Field(FIELD_FIRST_NAME) String fistName,
            @Field(FIELD_LAST_NAME) String lastName,
            @Field(FIELD_USERNAME) String username,
            @Field(FIELD_EMAIL) String email,
            @Field(FIELD_PASSWORD) String password,
            @Field(FIELD_COMPANY) String company
    );

    @FormUrlEncoded
    @POST(PATH_CREATE_PASSWORD)
    String createPassword(
            @Field(FIELD_CLIENT_ID) String clientId,
            @Field(FIELD_CLIENT_SECRET) String clientSecret,
            @Field(FIELD_GRANT_TYPE) String grantType,
            @Field("username") String username,
            @Field("email") String email,
            @Field("password") String password

    );
    @FormUrlEncoded
    @POST(PATH_ADD_TICKET)
    JsonObject addTicket(
            @Header(HEADER_AUTHORIZATION) String authToken,
            @Field("price_id") String price,
            @Field("transaction_id") String transaction_id,
            @Field("event_id") String event_id
          );

    @GET(PATH_CATEGORIES)
    JsonArray  getCategories();

    @GET(PATH_SUBCATEGORIES)
    JsonArray  getSubcategories(
    );

    @GET(PATH_BRANDS)
    JsonArray getBrands(
    );
    @GET(PATH_CITY)
    List<City> getCitys();

    @GET(PATH_EVENT)
    List<Event> getEvents(
            @Path("id") String cityId
    );

    @GET(PATH_EVENT_NEARBY)
    List<Event> getNearbyEvents(
            @Query("lat") double lat,
            @Query("lng") double lng

    );
    @GET(PATH_PRICE)
    List<Price> getPrices(
            @Path("id") String eventId
    );
    @GET(PATH_PRODUCTS)

    JsonArray getProducts();






    @GET(PATH_BOOKING)
    JsonArray getBooking(@Header(HEADER_AUTHORIZATION) String authToken);
    @GET(PATH_COUNTRY)
    List<Country> getCountrys();
    @GET(PATH_COMMODITY)
    List<Commodity> getCommodity(  @Header(HEADER_AUTHORIZATION) String authToken,String commodity_type, String commodity_name, String id, String start_date, String end_date);
    @GET(PATH_PRODUCT_DETAIL)
    JsonObject getProductDetails(@Query("code") String code);

    @GET(PATH_PRODUCT_STOCK)
    JsonObject getProductStock(
            @Query("productCode") String code,
            @Query("colorId") long colorId,
            @Query("sizeId") long sizeId);

    @GET(PATH_STORE)
    JsonObject getStoreData(
            @Header(HEADER_IF_MODIFIED_SINCE) String lastModifiedDate
    );

    @GET(PATH_BLOG_FEED)
    List<BlogEntry> getBlogFeed(@Header(HEADER_AUTHORIZATION) String authToken);

    @GET(PATH_SHOPPING_CART)
    List<CartItem> getShoppingCart(

            @Header(HEADER_AUTHORIZATION) String authToken
    );

    @FormUrlEncoded
    @POST(PATH_SHOPPING_CART)
    JsonObject addToCart(
            @Header(HEADER_AUTHORIZATION) String authToken,

            @Field("productCode") String code,

            @Field("productId") String productId,
            @Field("quantity") String quantity);


    @FormUrlEncoded
    @POST(PATH_ORDERS)
    Response addOrder(
            @Header(HEADER_AUTHORIZATION) String authToken,



            @Field("product") String productId,
            @Field("reference") String reference,
            @Field("date") String date,
            @Field("quantity") String quantity,
            @Field("subtotal") String subtotal,
            @Field("shippingPrice") String shippingPrice,
            @Field("tax") String tax);
    @DELETE(PATH_SHOPPING_CART_DELETE)
    Response removeFromCart(
            @Header(HEADER_AUTHORIZATION) String authToken,

            @Path("id") String cartItemId

    );

    @FormUrlEncoded
    @POST(PATH_SHOPPING_CART)
    Response updateCartItem(
            @Header(HEADER_AUTHORIZATION) String authToken,
            @Field(FIELD_EMAIL) String email,
            @Field("cartId") String cartItemId,
            @Field("quantity") String newQuantity
    );

    @GET(PATH_WISHLIST)
    String getWishlist(
            @Header(HEADER_AUTHORIZATION) String authToken
    );

    @PUT(PATH_WISHLIST)
    void addToWishlist(
            @Header(HEADER_AUTHORIZATION) String authToken
    );

    @DELETE(PATH_WISHLIST)
    void removeFromWishlist(
            @Header(HEADER_AUTHORIZATION) String authToken
    );

    @GET(PATH_ADDRESS_BOOK)
    List<Address> getAddressBook(
            @Header(HEADER_AUTHORIZATION) String authToken
    );

    @POST(PATH_ADDRESS_BOOK)
    Response updateAddress(
            @Header(HEADER_AUTHORIZATION) String authToken,
            @Field(FIELD_EMAIL) String email,
            @Field("addressId") String addressId,
            @Field("fullName") String fullName,
            @Field("lineOne") String addressLine1,
            @Field("lineTwo") String addressLine2,
            @Field("city") String city,
            @Field("state") String state,
            @Field("zip") String zipCode,
            @Field("country") String country,
            @Field("phoneNumber") String phone);

    @FormUrlEncoded
    @PUT(PATH_ADDRESS_BOOK)
    JsonObject addNewAddress(
            @Header(HEADER_AUTHORIZATION) String authToken,
            @Field(FIELD_EMAIL) String email,
            @Field("fullName") String fullName,
            @Field("lineOne") String addressLine1,
            @Field("lineTwo") String addressLine2,
            @Field("city") String city,
            @Field("state") String state,
            @Field("zip") String zipCode,
            @Field("country") String country,
            @Field("phoneNumber") String phone);

    @DELETE(PATH_ADDRESS_BOOK)
    Response removeAddress(
            @Header(HEADER_AUTHORIZATION) String authToken,
            @Query(FIELD_EMAIL) String email,
            @Query("addressId") String addressId);

    @GET(PATH_ORDERS)
    List<Order> getOrders(
            @Header(HEADER_AUTHORIZATION) String authToken);


    @GET(PATH_ORDER_DETAILS)
    JsonObject getOrderDetails(
            @Header(HEADER_AUTHORIZATION) String authToken,
            @Query(FIELD_EMAIL) String email,
            @Query("orderId") String orderId);

    @GET(PATH_PAY_TOKEN)
    ClientTokenResponse getClientToken(
            @Header(HEADER_AUTHORIZATION) String authToken);



    @FormUrlEncoded
    @POST(PATH_PAY)
    TransactionResponse checkout(
            @Header(HEADER_AUTHORIZATION) String authToken,

            @Field("payment_method_nonce") String paymentmethod, @Field("amount") String amount);
}
