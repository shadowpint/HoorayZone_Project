package com.horrayzone.horrayzone.loader;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.horrayzone.horrayzone.model.BlogEntry;
import com.horrayzone.horrayzone.sync.AccountAuthenticator;
import com.horrayzone.horrayzone.util.AccountUtils;
import com.horrayzone.horrayzone.util.SyncUtils;

import java.util.List;

import retrofit.RetrofitError;

public class BlogFeedLoader extends AsyncTaskLoader<List<BlogEntry>> {

    private static final String LOG_TAG = "BlogFeedLoader";

    private List<BlogEntry> mBlogFeedCache;
    private final Context mContext;

    private String mErrorText;
    public BlogFeedLoader(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected void onStartLoading() {
        Log.d(LOG_TAG, "onStartLoading()");

        if (mBlogFeedCache != null) {
            // Deliver cached data.
            deliverResult(mBlogFeedCache);
        } else {
            // We have no data, so start loading it.
            forceLoad();
        }
    }

    @Override
    public List<BlogEntry> loadInBackground() {
        List<BlogEntry> feed = null;

        Account account = AccountUtils.getActiveAccount(mContext);
        if (account == null) {
            mErrorText = "No active account is present";
            return null;
        }

        AccountManager manager = AccountManager.get(mContext);
        String authToken = manager.peekAuthToken(account, AccountAuthenticator.AUTHTOKEN_TYPE);
        try {
            feed = SyncUtils.sWebService.getBlogFeed("Bearer "+authToken);
        } catch (RetrofitError error) {
            error.printStackTrace();
        }

        return feed;
    }

    @Override
    public void deliverResult(List<BlogEntry> feed) {
        if (isReset()) {
            // An async query came in while the loader is stopped
            mBlogFeedCache = null;
            return;
        }

        mBlogFeedCache = feed;

        if (isStarted()) {
            super.deliverResult(mBlogFeedCache);
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
        mBlogFeedCache = null;
    }

}
