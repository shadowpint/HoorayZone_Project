package com.horrayzone.horrayzone.loader;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.horrayzone.horrayzone.model.Commodity;
import com.horrayzone.horrayzone.sync.AccountAuthenticator;
import com.horrayzone.horrayzone.util.AccountUtils;
import com.horrayzone.horrayzone.util.SyncUtils;

import java.util.List;

import retrofit.RetrofitError;

public class CommodityLoader extends AsyncTaskLoader<List<Commodity>> {

    private static final String LOG_TAG = "BlogFeedLoader";
    private Context mContext;

    private List<Commodity> mCommodityCache;
    private String commodity_type;
    private String commodity_name;
    private String id;
    private String start_date;
    private String end_date;


    public CommodityLoader(Context context, String commodity_type, String commodity_name, String id, String start_date, String end_date) {
        super(context);
        mContext = context;
        this.commodity_type = commodity_type;
        this.commodity_name = commodity_name;
        this.id= id;
        this.start_date = start_date;
        this.end_date = end_date;


    }

    @Override
    protected void onStartLoading() {
        Log.d(LOG_TAG, "onStartLoading()");

        if (mCommodityCache != null) {
            // Deliver cached data.
            deliverResult(mCommodityCache);
        } else {
            // We have no data, so start loading it.
            forceLoad();
        }
    }

    @Override
    public List<Commodity> loadInBackground() {
        List<Commodity> feed = null;
        Account account = AccountUtils.getActiveAccount(mContext);
//        if (account == null) {
//            mErrorText = "No active account is present";
//            return null;
//        }
        AccountManager manager = AccountManager.get(mContext);
        String authToken = manager.peekAuthToken(account, AccountAuthenticator.AUTHTOKEN_TYPE);
        try {
            feed = SyncUtils.sWebService.getCommodity("Bearer "+authToken,commodity_type, commodity_name,id,start_date,end_date);
        } catch (RetrofitError error) {
            error.printStackTrace();
        }

        return feed;
    }

    @Override
    public void deliverResult(List<Commodity> feed) {
        if (isReset()) {
            // An async query came in while the loader is stopped
            mCommodityCache = null;
            return;
        }

        mCommodityCache = feed;

        if (isStarted()) {
            super.deliverResult(mCommodityCache);
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
        mCommodityCache = null;
    }

}
