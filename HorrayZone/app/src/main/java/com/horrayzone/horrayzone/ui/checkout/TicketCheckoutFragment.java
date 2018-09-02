package com.horrayzone.horrayzone.ui.checkout;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.horrayzone.horrayzone.adapter.TicketAdapter;
import com.horrayzone.horrayzone.loader.PriceLoader;
import com.horrayzone.horrayzone.model.Price;
import com.horrayzone.horrayzone.ui.CheckoutActivity;
import com.horrayzone.horrayzone.ui.widget.EmptyView;

import java.util.List;

public class TicketCheckoutFragment extends CheckoutFragment implements LoaderManager.LoaderCallbacks<List<Price>> {
    private static final String TAG = "ShippingFragment";

    private static final String STATE_DATE_BUTTON = "state_payment_button";
    private Price entry;
    private CheckoutActivity mActivity;
    private TicketAdapter mAdapter;
    private ViewStub mEmptyViewStub;
    private EmptyView mEmptyView;
    private Button mProceedToDateButton;
    private RecyclerView mEventRecyclerView;

    public static TicketCheckoutFragment newInstance() {
        return new TicketCheckoutFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (CheckoutActivity) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_checkout_ticket, container, false);

        mAdapter = new TicketAdapter(null, null);
        mAdapter.setOnItemClickListener((itemView, position) -> {
//            Log.d(LOG_TAG, "onItemClick: " + position);
            entry = mAdapter.getPrice(position);
            Log.e("Event", entry.getName());
            mProceedToDateButton.setEnabled(true);
        });
        if (entry != null) {
            mProceedToDateButton.setEnabled(true);
        }
        mEventRecyclerView = (RecyclerView) rootView.findViewById(R.id.list);
        mEventRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        mEventRecyclerView.setAdapter(mAdapter);


        mEmptyViewStub = (ViewStub) rootView.findViewById(R.id.stub_empty_view);

        mProceedToDateButton = (Button) rootView.findViewById(R.id.proceed_to_payment_button);
        mProceedToDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mActivity.proceedToReview(entry);
                mActivity.proceedToDate(entry);
            }
        });

        if (savedInstanceState != null) {
            mProceedToDateButton.setEnabled(savedInstanceState.getBoolean(STATE_DATE_BUTTON));
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_DATE_BUTTON, mProceedToDateButton.isEnabled());
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
        return new PriceLoader(getActivity(), mActivity.getSelectedEvent().getId());


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
        getLoaderManager().restartLoader(0, null, TicketCheckoutFragment.this);
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

    @Override
    public boolean isCompleted() {
        return false;
    }
}
