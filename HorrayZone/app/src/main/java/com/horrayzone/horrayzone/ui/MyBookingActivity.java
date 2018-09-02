package com.horrayzone.horrayzone.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import com.horrayzone.horrayzone.R;
import com.horrayzone.horrayzone.loader.EventLoader;
import com.horrayzone.horrayzone.model.City;
import com.horrayzone.horrayzone.model.Event;
import com.horrayzone.horrayzone.ui.widget.EmptyView;
import com.horrayzone.horrayzone.util.OnItemClickListener;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;

import org.apache.commons.lang3.text.WordUtils;

public class MyBookingActivity extends FragmentActivity implements
        LoaderManager.LoaderCallbacks<List<Event>>{

    private static final int LOADER_ID = 0x02;
    private CursorAdapter adapter;
    private EventFeedAdapter mEventFeedAdapter;
    private RecyclerView mEventRecyclerView;
    private ViewStub mEmptyViewStub;
    private EmptyView mEmptyView;

    private City city;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_mybooking);


        mEmptyViewStub = (ViewStub) findViewById(R.id.stub_empty_view);
        mEventFeedAdapter = new EventFeedAdapter(null);
        mEventFeedAdapter.setOnItemClickListener((itemView, position) -> {
//            Log.d(LOG_TAG, "onItemClick: " + position);
            Event entry = mEventFeedAdapter.getEvent(position);
            Log.e("Event",entry.getName());
            Intent intent = new Intent(this, EventDetailActivity.class);

            intent.putExtra("event", entry);


          startActivity(intent);
        });

        mEventRecyclerView = (RecyclerView) findViewById(R.id.blog_feed_recycler_view);
        mEventRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        mEventRecyclerView.setAdapter(mEventFeedAdapter);
        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }
    @Override
    public Loader<List<Event>> onCreateLoader(int id, Bundle args) {

        mEventRecyclerView.setVisibility(View.GONE);
        setEmptyViewVisibility(View.GONE);
        return new EventLoader(this,"6");
    }

    @Override
    public void onLoadFinished(Loader<List<Event>> loader, List<Event> entries) {


        if (entries == null) {
            mEventRecyclerView.setVisibility(View.GONE);
            showErrorEmptyView();
        } else if (entries.size() == 0) {
            mEventRecyclerView.setVisibility(View.GONE);
            showEmptyFeedView();
        } else {

            mEventRecyclerView.setVisibility(View.VISIBLE);
            mEventFeedAdapter.setEventEntries(entries);
            setEmptyViewVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Event>> loader) {
        mEventFeedAdapter.setEventEntries(null);
    }

    private void reloadFeedData() {
        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
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
        mEmptyView.setTitle("Nothing to see here");
        mEmptyView.setSubtitle("No tickets booked");
        mEmptyView.setAction("Try Again", v -> reloadFeedData());
        mEmptyView.setVisibility(View.VISIBLE);

    }

    private void showErrorEmptyView() {
        if (mEmptyView == null) {
            mEmptyView = (EmptyView) mEmptyViewStub.inflate();
        }

        // TODO: Change to string resources.
        mEmptyView.reset();
        mEmptyView.setTitle("No Account");
        mEmptyView.setSubtitle("Please sign in");
        mEmptyView.setAction("Try Again", v -> reloadFeedData());
        mEmptyView.setVisibility(View.VISIBLE);
    }



    private class EventFeedAdapter extends RecyclerView.Adapter<EventFeedAdapter.ViewHolder> {

        private List<Event> mEventEntries;
        private OnItemClickListener mItemClickListener;

        public EventFeedAdapter(List<Event> entries) {
            mEventEntries = entries;
        }

        @Override
        public EventFeedAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = getLayoutInflater()
                    .inflate(R.layout.booking_card, parent, false);

            final ViewHolder holder = new ViewHolder(itemView);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();
                    if (mItemClickListener != null && position != RecyclerView.NO_POSITION) {
                        mItemClickListener.onItemClick(holder.itemView, position);
                    }
                }
            });

            return holder;
        }

        @Override
        public void onBindViewHolder(EventFeedAdapter.ViewHolder holder, int position) {
            Event entry = mEventEntries.get(position);

            holder.mTitleView.setText(WordUtils.capitalizeFully(entry.getName()));
            holder.mTagView.setText("#"+entry.getTag());
            holder.mAddressView.setText(entry.getAddress());
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH);
            try {
                Date date = formatter.parse(entry.getDate().replaceAll("Z$", "+0000"));
                holder.mDateView.setText(String.valueOf(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }

//            holder.mExcerptView.setText(entry.getDescription());

            Picasso.with(getApplicationContext())
                    .load(entry.getLeadImageUrl())
                    .placeholder(R.drawable.image_placeholder)
                    .into(holder.mImageView);
        }

        @Override
        public int getItemCount() {
            return mEventEntries != null ? mEventEntries.size() : 0;
        }

        public void setEventEntries(List<Event> entries) {
            mEventEntries = entries;
            notifyDataSetChanged();
        }

        public Event getEvent(int position) {
            return mEventEntries.get(position);
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            mItemClickListener = listener;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public ImageView mImageView;
            public TextView mTitleView;
            public TextView mTagView;
            public TextView mDateView;
            public TextView mAddressView;
//            public TextView mExcerptView;

            public ViewHolder(View itemView) {
                super(itemView);
                mImageView = (ImageView) itemView.findViewById(R.id.image);
                mTitleView = (TextView) itemView.findViewById(R.id.title);
                mTagView = (TextView) itemView.findViewById(R.id.tag);
                mAddressView = (TextView) itemView.findViewById(R.id.address);
                mDateView = (TextView) itemView.findViewById(R.id.date);
//                mExcerptView = (TextView) itemView.findViewById(R.id.excerpt);
            }
        }

    }





    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}