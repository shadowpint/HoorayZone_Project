package com.horrayzone.horrayzone.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.horrayzone.horrayzone.R;
import com.horrayzone.horrayzone.loader.BookingLoader;
import com.horrayzone.horrayzone.model.City;
import com.horrayzone.horrayzone.model.Event;
import com.horrayzone.horrayzone.model.Ticket;
import com.horrayzone.horrayzone.ui.widget.EmptyView;
import com.horrayzone.horrayzone.util.OnItemClickListener;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.text.WordUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MyBookingFragment extends BaseNavigationFragment implements LoaderManager.LoaderCallbacks<JsonArray>{
    private static final String LOG_TAG = "EventFeedFragment";
    private List<Ticket> ticketList=new ArrayList<Ticket>();

    public static MyBookingFragment newInstance() {
        return new MyBookingFragment();
    }
    /*enum UiState {
        LOADING,
        EMPTY,
        ERROR,
        DISPLAY_CONTENT
    }*/

    private RecyclerView mEventRecyclerView;
    private EventFeedAdapter mEventFeedAdapter;
    private ProgressBar mProgressbar;
    private ViewStub mEmptyViewStub;
    private EmptyView mEmptyView;
    private City city;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            if (bundle.getParcelable("city") != null) {
                city = bundle.getParcelable("city");
                Log.e("event_fragment_city", "city " + city.getName());
            }

        }
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("My Bookings");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_mybooking, container, false);
        getActivity().setTitle("your name");

        mEmptyViewStub = (ViewStub) rootView.findViewById(R.id.stub_empty_view);
        mProgressbar = (ProgressBar) rootView.findViewById(R.id.progress);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mEventFeedAdapter = new EventFeedAdapter(null);
        mEventFeedAdapter.setOnItemClickListener((itemView, position) -> {
            Log.d(LOG_TAG, "onItemClick: " + position);
            Ticket entry = mEventFeedAdapter.getBooking(position);
Log.e("Event",entry.getId());
            Intent intent = new Intent(getActivity(), TicketDetailActivity.class);

            intent.putExtra("ticket", entry);


            getActivity().startActivity(intent);
        });

        mEventRecyclerView = (RecyclerView) view.findViewById(R.id.blog_feed_recycler_view);

        mEventRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mEmptyView = null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<JsonArray> onCreateLoader(int id, Bundle args) {
        mProgressbar.setVisibility(View.VISIBLE);
        mEventRecyclerView.setVisibility(View.GONE);
        setEmptyViewVisibility(View.GONE);
        return new BookingLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<JsonArray> loader, JsonArray entries) {
        mProgressbar.setVisibility(View.GONE);

        if (entries == null) {
            mEventRecyclerView.setVisibility(View.GONE);
            showErrorEmptyView();
        } else if (entries.size() == 0) {
            mEventRecyclerView.setVisibility(View.GONE);
            showEmptyFeedView();
        } else {
            processOrderDetailsData(entries);

            setEmptyViewVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader<JsonArray> loader) {
        mEventFeedAdapter.setBookingEntries(null);
    }

    private void reloadFeedData() {
        getLoaderManager().restartLoader(0, null, MyBookingFragment.this);
    }

    private void setEmptyViewVisibility(int visibility) {
        if (mEmptyView != null) {
            mEmptyView.setVisibility(visibility);
        }
    }
    private void processOrderDetailsData(JsonArray data) {
        Log.e("booking", String.valueOf(data));
        ticketList.clear();
        for(int i=0;i<data.size();i++){
            ticketList.add(new Ticket(data.get(i).getAsJsonObject().get("price").getAsJsonObject().get("name").getAsString(),
                    data.get(i).getAsJsonObject().get("price").getAsJsonObject().get("price").getAsString(),
                    data.get(i).getAsJsonObject().get("transaction_id").getAsString(),
                    data.get(i).getAsJsonObject().get("event").getAsJsonObject().get("name").getAsString(),
                    data.get(i).getAsJsonObject().get("event").getAsJsonObject().get("date").getAsString(),
                    data.get(i).getAsJsonObject().get("event").getAsJsonObject().get("leadImageUrl").getAsString(),
                    data.get(i).getAsJsonObject().get("event").getAsJsonObject().get("tags").getAsString(),
                    data.get(i).getAsJsonObject().get("event").getAsJsonObject().get("address").getAsString(),
                    data.get(i).getAsJsonObject().get("id").getAsString()
                    ));
        }
        mEventRecyclerView.setVisibility(View.VISIBLE);
        mEventRecyclerView.setAdapter(mEventFeedAdapter);
        mEventFeedAdapter.setBookingEntries(ticketList);
        mEventFeedAdapter.notifyDataSetChanged();
        JsonObject summary = data.get(0).getAsJsonObject();
//        JsonArray items = data.get("items").getAsJsonArray();

        Log.e("price", (data.get(0).getAsJsonObject().get("event").getAsJsonObject().get("date")).getAsString());
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

        private List<Ticket> mEventEntries;
        private OnItemClickListener mItemClickListener;

        public EventFeedAdapter(List<Ticket> entries) {
            mEventEntries = entries;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = getActivity().getLayoutInflater()
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
        public void onBindViewHolder(ViewHolder holder, int position) {
            Ticket entry = mEventEntries.get(position);

            holder.mTitleView.setText(WordUtils.capitalizeFully(entry.getEventName()));
            holder.mGenreView.setText(WordUtils.capitalizeFully(entry.getEventGenre()));
            holder.mTicketName.setText(WordUtils.capitalizeFully(entry.getPriceName()));
            holder.mVenueView.setText(WordUtils.capitalizeFully(entry.getEventVenue()));
            Picasso.with(getActivity())
                    .load(entry.getEventImage())
                    .placeholder(R.drawable.image_placeholder)
                    .into(holder.mImageView);


            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH);
            try {
                Date d= formatter.parse(entry.getEventTime().replaceAll("Z$", "+0000"));
                DateFormat date = new SimpleDateFormat("MM/dd/yyyy");
                DateFormat time = new SimpleDateFormat("hh:mm:ss a");

                holder.mDateView.setText(date.format(d));
                holder.mTimeView.setText(time.format(d));

            } catch (ParseException e) {
                e.printStackTrace();
            }













//            holder.mExcerptView.setText(entry.getDescription());


        }

        @Override
        public int getItemCount() {
            return mEventEntries != null ? mEventEntries.size() : 0;
        }

        public void setBookingEntries(List<Ticket> entries) {
            mEventEntries = entries;
            notifyDataSetChanged();
        }

        public Ticket getBooking(int position) {
            return mEventEntries.get(position);
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            mItemClickListener = listener;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public ImageView mImageView;
            public TextView mTitleView;
         
            public TextView mDateView;
            public TextView mTimeView;
            public TextView mVenueView;
            public TextView mGenreView;
            public TextView mTicketName;
            public ViewHolder(View itemView) {
                super(itemView);
                mImageView = (ImageView) itemView.findViewById(R.id.event_qr);
                mTitleView = (TextView) itemView.findViewById(R.id.event_title);
                mDateView = (TextView) itemView.findViewById(R.id.event_date);
                mGenreView = (TextView) itemView.findViewById(R.id.event_genre);
                mTimeView = (TextView) itemView.findViewById(R.id.event_time);
                mVenueView = (TextView) itemView.findViewById(R.id.event_venue);
                mTicketName = (TextView) itemView.findViewById(R.id.event_ticket);
            }
        }

    }
}
