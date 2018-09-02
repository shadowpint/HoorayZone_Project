package com.horrayzone.horrayzone.tabfragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;

import com.horrayzone.horrayzone.R;
import com.horrayzone.horrayzone.adapter.PriceAdapter;
import com.horrayzone.horrayzone.adapter.TicketAdapter;
import com.horrayzone.horrayzone.loader.PriceLoader;
import com.horrayzone.horrayzone.model.Price;
import com.horrayzone.horrayzone.ui.CheckoutActivity;
import com.horrayzone.horrayzone.ui.EventDetailActivity;
import com.horrayzone.horrayzone.ui.checkout.CheckoutFragment;
import com.horrayzone.horrayzone.ui.widget.EmptyView;

import java.util.List;

public class EventPriceFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Price>> {
    private static final String TAG = "ShippingFragment";

    private static final String STATE_DATE_BUTTON = "state_payment_button";
    private Price entry;
    private EventDetailActivity mActivity;
    private PriceAdapter mAdapter;
    private ViewStub mEmptyViewStub;
    private EmptyView mEmptyView;

    private RecyclerView mEventRecyclerView;

    public static EventPriceFragment newInstance() {
        return new EventPriceFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (EventDetailActivity) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_event_price, container, false);

        mAdapter = new PriceAdapter(null, null);

        mEventRecyclerView = (RecyclerView) rootView.findViewById(R.id.list);
        mEventRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        mEventRecyclerView.setAdapter(mAdapter);


        mEmptyViewStub = (ViewStub) rootView.findViewById(R.id.stub_empty_view);



        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
    }


    @Override
    public Loader<List<Price>> onCreateLoader(int id, Bundle args) {

        mEventRecyclerView.setVisibility(View.GONE);
        setEmptyViewVisibility(View.GONE);
        return new PriceLoader(getActivity(), mActivity.event.getId());


    }

    @Override
    public void onLoadFinished(Loader<List<Price>> loader, List<Price> entries) {


        if (entries == null) {
            mEventRecyclerView.setVisibility(View.GONE);
            showErrorEmptyView();
        } else if (entries.size() == 0) {
            mEventRecyclerView.setVisibility(View.GONE);
            showEmptyFeedView();
        } else {
            mEventRecyclerView.setVisibility(View.VISIBLE);
            mAdapter.setPriceEntries(entries);
            setEmptyViewVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Price>> loader) {
        mAdapter.setPriceEntries(null);
    }

    private void reloadFeedData() {
        getLoaderManager().restartLoader(0, null, EventPriceFragment.this);
    }

    private void setEmptyViewVisibility(int visibility) {
        if (mEmptyView != null) {
            mEmptyView.setVisibility(visibility);
        }
    }

    private void showEmptyFeedView() {
        if (mEmptyView == null) {
            mEmptyView = (EmptyView) mEmptyViewStub.inflate();
        }

        // TODO: Change to string resources.
        mEmptyView.reset();
        mEmptyView.setImageResource(R.drawable.ic_empty_newsfeed);
        mEmptyView.setTitle("No Tickets");
        mEmptyView.setSubtitle("There are no Tickets by the time, check later.");
        mEmptyView.setAction("Try Again", v -> reloadFeedData());
        mEmptyView.setVisibility(View.VISIBLE);

    }

    private void showErrorEmptyView() {
        if (mEmptyView == null) {
            mEmptyView = (EmptyView) mEmptyViewStub.inflate();
        }

        // TODO: Change to string resources.
        mEmptyView.reset();
        mEmptyView.setTitle("No connection");
        mEmptyView.setSubtitle("Check your network state and try again.");
        mEmptyView.setAction("Try Again", v -> reloadFeedData());
        mEmptyView.setVisibility(View.VISIBLE);
    }


}
