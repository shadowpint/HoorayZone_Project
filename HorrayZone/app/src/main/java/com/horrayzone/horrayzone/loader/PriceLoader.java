package com.horrayzone.horrayzone.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.horrayzone.horrayzone.model.Price;
import com.horrayzone.horrayzone.util.SyncUtils;

import java.util.List;

import retrofit.RetrofitError;

public class PriceLoader extends AsyncTaskLoader<List<Price>> {

    private static final String LOG_TAG = "PriceLoader";

    private List<Price> mPriceCache;
    private String event_id;

    public PriceLoader(Context context, String event_id) {
        super(context);
        this.event_id = event_id;
    }

    @Override
    protected void onStartLoading() {
        Log.d(LOG_TAG, "onStartLoading()");

        if (mPriceCache != null) {
            // Deliver cached data.
            deliverResult(mPriceCache);
        } else {
            // We have no data, so start loading it.
            forceLoad();
        }
    }

    @Override
    public List<Price> loadInBackground() {
        List<Price> feed = null;

        try {
            feed = SyncUtils.sWebService.getPrices(event_id);
        } catch (RetrofitError error) {
            error.printStackTrace();
        }

        return feed;
    }

    @Override
    public void deliverResult(List<Price> feed) {
        if (isReset()) {
            // An async query came in while the loader is stopped
            mPriceCache = null;
            return;
        }

        mPriceCache = feed;

        if (isStarted()) {
            super.deliverResult(mPriceCache);
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
        mPriceCache = null;
    }

}
