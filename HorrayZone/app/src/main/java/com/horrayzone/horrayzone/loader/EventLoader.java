package com.horrayzone.horrayzone.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.horrayzone.horrayzone.model.Event;
import com.horrayzone.horrayzone.util.SyncUtils;

import java.util.List;

import retrofit.RetrofitError;

public class EventLoader extends AsyncTaskLoader<List<Event>> {

    private static final String LOG_TAG = "BlogFeedLoader";

    private List<Event> mEventCache;
private String city_id;
    public EventLoader(Context context,String city_id) {
        super(context);
        this.city_id=city_id;
    }

    @Override
    protected void onStartLoading() {
        Log.d(LOG_TAG, "onStartLoading()");

        if (mEventCache != null) {
            // Deliver cached data.
            deliverResult(mEventCache);
        } else {
            // We have no data, so start loading it.
            forceLoad();
        }
    }

    @Override
    public List<Event> loadInBackground() {
        List<Event> feed = null;

        try {
            feed = SyncUtils.sWebService.getEvents(city_id);
        } catch (RetrofitError error) {
            error.printStackTrace();
        }

        return feed;
    }

    @Override
    public void deliverResult(List<Event> feed) {
        if (isReset()) {
            // An async query came in while the loader is stopped
            mEventCache = null;
            return;
        }

        mEventCache = feed;

        if (isStarted()) {
            super.deliverResult(mEventCache);
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
        mEventCache = null;
    }

}
