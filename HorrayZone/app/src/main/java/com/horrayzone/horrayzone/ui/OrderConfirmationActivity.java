package com.horrayzone.horrayzone.ui;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.braintreepayments.api.BraintreeFragment;
import com.braintreepayments.api.PayPal;
import com.braintreepayments.api.interfaces.BraintreeCancelListener;
import com.braintreepayments.api.interfaces.BraintreeErrorListener;
import com.braintreepayments.api.interfaces.ConfigurationListener;
import com.braintreepayments.api.interfaces.PaymentMethodNonceCreatedListener;
import com.braintreepayments.api.models.Configuration;
import com.braintreepayments.api.models.PayPalRequest;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.horrayzone.horrayzone.R;
import com.horrayzone.horrayzone.model.Address;
import com.horrayzone.horrayzone.model.CreditCard;
import com.horrayzone.horrayzone.model.Event;
import com.horrayzone.horrayzone.model.Price;
import com.horrayzone.horrayzone.model.RequestError;
import com.horrayzone.horrayzone.model.Ticket;
import com.horrayzone.horrayzone.model.TransactionResponse;
import com.horrayzone.horrayzone.provider.HoorayZoneContract;
import com.horrayzone.horrayzone.sync.AccountAuthenticator;
import com.horrayzone.horrayzone.ui.widget.EmptyView;
import com.horrayzone.horrayzone.ui.worker.GetPaymentTokenWorkerFragment;
import com.horrayzone.horrayzone.util.AccountUtils;
import com.horrayzone.horrayzone.util.SyncUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class OrderConfirmationActivity extends BaseActivity implements GetPaymentTokenWorkerFragment.OnGetPaymentTokenResultCallback,PaymentMethodNonceCreatedListener,ConfigurationListener,BraintreeErrorListener,BraintreeCancelListener {

    public static final String EXTRA_ADDRESS = "extra_address";
    public static final String EXTRA_CREDIT_CARD = "extra+credit_card";
    public static final String EXTRA_AMOUNT = "extra_amount";
    public static final String EXTRA_CART_ID = "extra_cart";
    public static final String EXTRA_PRODUCT_ID = "extra_product";
    public static final String TAG_FRAGMENT_WORKER = "fragment_place_order_worker";
    public static final String EXTRA_PRICE = "extra_price";
    public static final String EXTRA_EVENT = "extra_event";

    private View mProgressContainer;
    private EmptyView mEmptyView;

    private Address mAddress;
    private String mAmount;
    private String mCartId;
    private CreditCard mCreditCard;
    private GetPaymentTokenWorkerFragment mPaymentTokenWorkerFragment;
    private Event mEvent;
    private final Handler mHandler = new Handler();
    private String mproductId;
    private Price mPrice;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirmation);
        mEvent = getIntent().getParcelableExtra(EXTRA_EVENT);
        mAddress = getIntent().getParcelableExtra(EXTRA_ADDRESS);
        mPrice = getIntent().getParcelableExtra(EXTRA_PRICE);
        mAmount= String.valueOf(getIntent().getSerializableExtra(EXTRA_AMOUNT));
        mCartId= String.valueOf(getIntent().getSerializableExtra(EXTRA_CART_ID));
        mproductId= String.valueOf(getIntent().getSerializableExtra(EXTRA_PRODUCT_ID));
        Log.e("amount",mAmount);
        mCreditCard = getIntent().getParcelableExtra(EXTRA_CREDIT_CARD);

        mProgressContainer = findViewById(R.id.progress_container);
        mEmptyView = (EmptyView) findViewById(R.id.empty_view);

        mPaymentTokenWorkerFragment = (GetPaymentTokenWorkerFragment) getSupportFragmentManager()
                .findFragmentByTag(TAG_FRAGMENT_WORKER);

        if (mPaymentTokenWorkerFragment == null) {
            mPaymentTokenWorkerFragment = GetPaymentTokenWorkerFragment.newInstance();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(mPaymentTokenWorkerFragment, TAG_FRAGMENT_WORKER)
                    .commit();
        }

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                getToken();
            }
        });
    }

    private void setProgress(boolean progress) {
        mProgressContainer.setVisibility(progress ? View.VISIBLE : View.GONE);
        mEmptyView.setVisibility(progress ? View.GONE : View.VISIBLE);
    }

    private void getToken() {
        setProgress(true);
        mPaymentTokenWorkerFragment.getToken(AccountUtils.getActiveAccountName(this));
    }

    private void makePayment(String token, String amount) {

        setProgress(true);

        try{
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); //Hiding the keyboard
            BraintreeFragment mBraintreeFragment = BraintreeFragment.newInstance(this, token);

            PayPalRequest request = new PayPalRequest(amount);
            request.currencyCode("INR");
           // request.billingAgreementDescription(et_order_desc.getText().toString());
            request.displayName("HOORAYZONE");
//            PostalAddress postalAddress= new PostalAddress();
//            postalAddress.countryCodeAlpha2(mAddress.getCountry());
//            postalAddress.locality(mAddress.getCity());
//            postalAddress.extendedAddress(mAddress.getAddressLine1());
//            postalAddress.postalCode(mAddress.getZipCode());
//            postalAddress.region(mAddress.getCountry());
//            postalAddress.streetAddress(mAddress.getAddressLine2());
//        request.shippingAddressOverride(postalAddress);
            PayPal.requestOneTimePayment(mBraintreeFragment, request);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    @Override
    public void OnGetPaymentTokenResult(String result) {
        if (result == null) {
            mEmptyView.setTitle("Error");
            mEmptyView.setSubtitle("Please try again.");
            mEmptyView.setAction("Try again", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getToken();
                }
            });








        } else {
            makePayment(result, mAmount);
        }

        setProgress(false);
    }

    @Override
    public void onCancel(int requestCode) {

    }

    @Override
    public void onError(Exception error) {

    }

    @Override
    public void onConfigurationFetched(Configuration configuration) {

    }

    @Override
    public void onPaymentMethodNonceCreated(PaymentMethodNonce paymentMethodNonce) {
        Log.d("PAYMENT_METHOD","Payment   "+paymentMethodNonce.getNonce().toString());
        new makePaymentTask().execute(AccountUtils.getActiveAccountName(this), paymentMethodNonce.getNonce(), mAmount);




    }

    private class makePaymentTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String accountName = params[0];
//            String addressId = params[1];
            String paymentmethod = params[1];
            String amount = params[2];
//            String cardNumber = params[3];
//            String cvc = params[4];
//            String expMonth = params[5];
//            String expYear = params[6];

            Account account = new Account(accountName, AccountAuthenticator.ACCOUNT_TYPE);
            AccountManager manager = AccountManager.get(OrderConfirmationActivity.this);
            String authToken = manager.peekAuthToken(account, AccountAuthenticator.AUTHTOKEN_TYPE);
            String result = null;

            try {
                TransactionResponse response = SyncUtils.sWebService.checkout("Bearer " + authToken, paymentmethod, amount);
                Log.e("json_token", String.valueOf(response));
                result=response.getStatus();
                //long newAddressId = object.get("id").getAsLong();

                //saveNewAddress(newAddressId, fullName, addressLine1, addressLine2, city, state,
                //zipCode, country, phone);

            } catch (RetrofitError error) {

                if (error.getKind() == RetrofitError.Kind.NETWORK) {
                    result = "No connection.";
                } else if (error.getKind() == RetrofitError.Kind.HTTP) {
                    Response response = error.getResponse();
                    String json = new String(((TypedByteArray) response.getBody()).getBytes());
                    RequestError reqError = new Gson().fromJson(json, RequestError.class);
                    result = reqError.getDescription();
                } else {
                    result = error.getLocalizedMessage();
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result.equals("success")) {

                mEmptyView.setTitle("Payment success!");
                mEmptyView.setSubtitle("You Booking has been processed successfully.");
                mEmptyView.setAction("Close", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });

//                Log.e("cart_id",mCartId);
                new AddTicketTask().execute("reewt4yryrggdrhdyryrtyeredrdhfhfh");
//                new UpdateOrderTask().execute(
//
//                        Long.toString(Long.parseLong(mCartId)));

            } else {
                mEmptyView.setTitle("Payment error");
                mEmptyView.setSubtitle("An error has occurred while processing yor payment, check you credit card and try again.");
                mEmptyView.setAction("Try again", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        makePayment(result, mAmount);
                    }
                });
            }

            setProgress(false);
        }
    }

    private class AddTicketTask extends AsyncTask<String, Void, String> {

        public AddTicketTask() {
            super();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
            String transaction_id = params[0];


            Account account = new Account(AccountUtils.getActiveAccountName(OrderConfirmationActivity.this), AccountAuthenticator.ACCOUNT_TYPE);
            AccountManager manager = AccountManager.get(OrderConfirmationActivity.this);
            String authToken = manager.peekAuthToken(account, AccountAuthenticator.AUTHTOKEN_TYPE);
            String result = null;

            try {
                result= String.valueOf(SyncUtils.sWebService.addTicket("Bearer "+authToken,  mPrice.getId(),transaction_id,mEvent.getId()));


            } catch (RetrofitError error) {

                if (error.getKind() == RetrofitError.Kind.NETWORK) {
                    result = "No connection.";
                } else if (error.getKind() == RetrofitError.Kind.HTTP) {
                    Response response = error.getResponse();
                    String json = new String(((TypedByteArray) response.getBody()).getBytes());
                    RequestError reqError = new Gson().fromJson(json, RequestError.class);
                    result = reqError.getDescription();
                } else {
                    result = error.getLocalizedMessage();
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.e("ticket_result",result);
            Intent intent = new Intent(OrderConfirmationActivity.this, TicketDetailActivity.class);
            Ticket ticket = new Ticket(mPrice.getName(),
                    mPrice.getPrice(), "reewt4yryrggdrhdyryrtyeredrdhfhfh"
                    ,
                    mEvent.getName(),
                    mEvent.getDate(),
                    mEvent.getLeadImageUrl(),
                    mEvent.getTag(),
                    mEvent.getAddress(),
                    "1"
            );
            intent.putExtra("ticket", ticket);


            startActivity(intent);

        }

        private void removeItemFromDatabase(String cartItemId) {
            OrderConfirmationActivity.this.getContentResolver().delete(
                    HoorayZoneContract.CartEntry.buildCartItemUri(Long.parseLong(cartItemId)), null, null);
        }

    }
    private class UpdateOrderTask extends AsyncTask<String, Void, String> {

        public UpdateOrderTask() {
            super();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
            String cartItemId = params[0];


            Account account = new Account(AccountUtils.getActiveAccountName(OrderConfirmationActivity.this), AccountAuthenticator.ACCOUNT_TYPE);
            AccountManager manager = AccountManager.get(OrderConfirmationActivity.this);
            String authToken = manager.peekAuthToken(account, AccountAuthenticator.AUTHTOKEN_TYPE);
            String result = null;
            Long tsLong = System.currentTimeMillis()/1000;
            String ts = tsLong.toString();
            try {
                SyncUtils.sWebService.addOrder("Bearer "+authToken,  mproductId,"sajbwvfvwe",ts,mAmount,"0","0","0");
                removeItemFromDatabase(cartItemId);

            } catch (RetrofitError error) {

                if (error.getKind() == RetrofitError.Kind.NETWORK) {
                    result = "No connection.";
                } else if (error.getKind() == RetrofitError.Kind.HTTP) {
                    Response response = error.getResponse();
                    String json = new String(((TypedByteArray) response.getBody()).getBytes());
                    RequestError reqError = new Gson().fromJson(json, RequestError.class);
                    result = reqError.getDescription();
                } else {
                    result = error.getLocalizedMessage();
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);


        }

        private void removeItemFromDatabase(String cartItemId) {
            OrderConfirmationActivity.this.getContentResolver().delete(
                    HoorayZoneContract.CartEntry.buildCartItemUri(Long.parseLong(cartItemId)), null, null);
        }

    }
}
