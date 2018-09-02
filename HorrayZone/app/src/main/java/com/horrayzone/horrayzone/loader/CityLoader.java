package com.horrayzone.horrayzone.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.horrayzone.horrayzone.model.City;
import com.horrayzone.horrayzone.util.SyncUtils;

import java.util.List;

import retrofit.RetrofitError;

public class CityLoader extends AsyncTaskLoader<List<City>> {

    private static final String LOG_TAG = "BlogFeedLoader";

    private List<City> mCityCache;

    public CityLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        Log.d(LOG_TAG, "onStartLoading()");

        if (mCityCache != null) {
            // Deliver cached data.
            deliverResult(mCityCache);
        } else {
            // We have no data, so start loading it.
            forceLoad();
        }
    }

    @Override
    public List<City> loadInBackground() {
        List<City> feed = null;

        try {
            feed = SyncUtils.sWebService.getCitys();
        } catch (RetrofitError error) {
            error.printStackTrace();
        }

        return feed;
    }

    @Override
    public void deliverResult(List<City> feed) {
        if (isReset()) {
            // An async query came in while the loader is stopped
            mCityCache = null;
            return;
        }

        mCityCache = feed;

        if (isStarted()) {
            super.deliverResult(mCityCache);
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
        mCityCache = null;
    }

}
