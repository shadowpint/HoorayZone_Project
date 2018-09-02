package com.horrayzone.horrayzone.loader;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.horrayzone.horrayzone.model.Booking;
import com.horrayzone.horrayzone.sync.AccountAuthenticator;
import com.horrayzone.horrayzone.util.AccountUtils;
import com.horrayzone.horrayzone.util.SyncUtils;
import com.google.gson.JsonArray;

import java.util.List;

import retrofit.RetrofitError;

public class BookingLoader extends AsyncTaskLoader<JsonArray> {

    private static final String LOG_TAG = "BookingFeedLoader";

    private JsonArray mBookingFeedCache;
    private final Context mContext;
 
    private String mErrorText;
    public BookingLoader(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected void onStartLoading() {
        Log.d(LOG_TAG, "onStartLoading()");

        if (mBookingFeedCache != null) {
            // Deliver cached data.
            deliverResult(mBookingFeedCache);
        } else {
            // We have no data, so start loading it.
            forceLoad();
        }
    }

    @Override
    public JsonArray loadInBackground() {
        JsonArray feed = null;

        Account account = AccountUtils.getActiveAccount(mContext);
        if (account == null) {
            mErrorText = "No active account is present";
            return null;
        }

        AccountManager manager = AccountManager.get(mContext);
        String authToken = manager.peekAuthToken(account, AccountAuthenticator.AUTHTOKEN_TYPE);
        try {
            feed = SyncUtils.sWebService.getBooking("Bearer "+authToken);
        } catch (RetrofitError error) {
            error.printStackTrace();
        }

        return feed;
    }

    @Override
    public void deliverResult(JsonArray feed) {
        if (isReset()) {
            // An async query came in while the loader is stopped
            mBookingFeedCache = null;
            return;
        }

        mBookingFeedCache = feed;

        if (isStarted()) {
            super.deliverResult(mBookingFeedCache);
        }
    }


    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    protected void onReset() {
        super.onReset();

        onStopLoading();
        mBookingFeedCache = null;
    }

}
