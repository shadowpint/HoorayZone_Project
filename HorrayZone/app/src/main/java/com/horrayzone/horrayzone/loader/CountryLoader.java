package com.horrayzone.horrayzone.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.horrayzone.horrayzone.model.Country;
import com.horrayzone.horrayzone.model.Country;
import com.horrayzone.horrayzone.util.SyncUtils;

import java.util.List;

import retrofit.RetrofitError;

public class CountryLoader extends AsyncTaskLoader<List<Country>> {

    private static final String LOG_TAG = "BlogFeedLoader";

    private List<Country> mCountryCache;

    public CountryLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        Log.d(LOG_TAG, "onStartLoading()");

        if (mCountryCache != null) {
            // Deliver cached data.
            deliverResult(mCountryCache);
        } else {
            // We have no data, so start loading it.
            forceLoad();
        }
    }

    @Override
    public List<Country> loadInBackground() {
        List<Country> feed = null;

        try {
            feed = SyncUtils.sWebService.getCountrys();
        } catch (RetrofitError error) {
            error.printStackTrace();
        }

        return feed;
    }

    @Override
    public void deliverResult(List<Country> feed) {
        if (isReset()) {
            // An async query came in while the loader is stopped
            mCountryCache = null;
            return;
        }

        mCountryCache = feed;

        if (isStarted()) {
            super.deliverResult(mCountryCache);
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
        mCountryCache = null;
    }

}
