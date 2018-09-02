package com.horrayzone.horrayzone.ui.worker;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.horrayzone.horrayzone.model.ClientTokenResponse;
import com.horrayzone.horrayzone.model.RequestError;
import com.horrayzone.horrayzone.sync.AccountAuthenticator;
import com.horrayzone.horrayzone.util.SyncUtils;
import com.google.gson.Gson;

import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class GetPaymentTokenWorkerFragment extends Fragment {

    public static GetPaymentTokenWorkerFragment newInstance() {

        Bundle args = new Bundle();

        GetPaymentTokenWorkerFragment fragment = new GetPaymentTokenWorkerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public interface OnGetPaymentTokenResultCallback {
        void OnGetPaymentTokenResult(String result);
    }

    private OnGetPaymentTokenResultCallback mGetPaymentTokenResultCallback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mGetPaymentTokenResultCallback = (OnGetPaymentTokenResultCallback) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mGetPaymentTokenResultCallback = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }


    public void getToken(String accountName) {
        new getTokenTask().execute(accountName);
//                address.getServerId().toString());
//                card.getCardHolder(),
//                card.getNumber(),
//                card.getCvv(),
//                card.getExpYear(),
//                card.getExpMonth());
    }

    private class getTokenTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String accountName = params[0];
//            String addressId = params[1];
//            String nameOnCard = params[2];
//            String cardNumber = params[3];
//            String cvc = params[4];
//            String expMonth = params[5];
//            String expYear = params[6];

            Account account = new Account(accountName, AccountAuthenticator.ACCOUNT_TYPE);
            AccountManager manager = AccountManager.get(getActivity());
            String authToken = manager.peekAuthToken(account, AccountAuthenticator.AUTHTOKEN_TYPE);
            String result = null;

            try {
                ClientTokenResponse clientTokenresponse = SyncUtils.sWebService.getClientToken("Bearer "+authToken);
                if(clientTokenresponse.getStatus().equals("success")){
                    result=clientTokenresponse.getToken();
                }
Log.e("json_token", String.valueOf(clientTokenresponse.getToken()));
//                getContext().getContentResolver()
//                        .delete(HoorayZoneContract.CartEntry.CONTENT_URI, null, null);
//Log.e("product_code",getContext().getContentResolver().query());
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
            if (mGetPaymentTokenResultCallback != null) {
                mGetPaymentTokenResultCallback.OnGetPaymentTokenResult(result);
            }
        }
    }

}
