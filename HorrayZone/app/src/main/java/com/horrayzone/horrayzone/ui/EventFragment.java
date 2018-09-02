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
import android.widget.ToggleButton;

import com.horrayzone.horrayzone.R;
import com.horrayzone.horrayzone.loader.EventLoader;
import com.horrayzone.horrayzone.model.City;
import com.horrayzone.horrayzone.model.Event;
import com.horrayzone.horrayzone.ui.widget.EmptyView;
import com.horrayzone.horrayzone.util.OnItemClickListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.text.WordUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EventFragment extends BaseNavigationFragment implements LoaderManager.LoaderCallbacks<List<Event>>, OnMapReadyCallback{
    private static final String LOG_TAG = "EventFeedFragment";
    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private ToggleButton mTogglebutton;
    public static EventFragment newInstance() {
        return new EventFragment();
    }
    /*enum UiState {
        LOADING,
        EMPTY,
        ERROR,
        DISPLAY_CONTENT
    }*/

    private RecyclerView mEventRecyclerView;
    private EventFeedAdapter mEventFeedAdapter;
    private ProgressBar mProgressBar;
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
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Events in " + city.getName());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_event, container, false);
        getActivity().setTitle("your name");
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progress);
        mEmptyViewStub = (ViewStub) rootView.findViewById(R.id.stub_empty_view);

        mMapView = (MapView) rootView.findViewById(R.id.map_view);

        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mEventFeedAdapter = new EventFeedAdapter(null);
        mEventFeedAdapter.setOnItemClickListener((itemView, position) -> {
            Log.d(LOG_TAG, "onItemClick: " + position);
            Event entry = mEventFeedAdapter.getEvent(position);
Log.e("Event",entry.getName());
            Intent intent = new Intent(getActivity(), EventDetailActivity.class);

            intent.putExtra("event", entry);


            getActivity().startActivity(intent);
        });

        mEventRecyclerView = (RecyclerView) view.findViewById(R.id.blog_feed_recycler_view);
        mTogglebutton = (ToggleButton) view.findViewById(R.id.tb2);
        mTogglebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mTogglebutton.isChecked()){

                    mEventRecyclerView.setVisibility(View.GONE);
                    mMapView.setVisibility(View.VISIBLE);




                }
                else{
                    mEventRecyclerView.setVisibility(View.VISIBLE);
                    mMapView.setVisibility(View.GONE);


                }
            }
        });
        mEventRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        mEventRecyclerView.setAdapter(mEventFeedAdapter);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
//        mGoogleMap.addMarker(new MarkerOptions().position(/*some location*/));


        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(city.getLat(), city.getLng()), 10));
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
        mMapView.setVisibility(View.GONE);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
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
    public Loader<List<Event>> onCreateLoader(int id, Bundle args) {
        mProgressBar.setVisibility(View.VISIBLE);
        mEventRecyclerView.setVisibility(View.GONE);
        setEmptyViewVisibility(View.GONE);
        return new EventLoader(getActivity(),city.getId());
    }

    @Override
    public void onLoadFinished(Loader<List<Event>> loader, List<Event> entries) {
        mProgressBar.setVisibility(View.GONE);

        if (entries == null) {
            mEventRecyclerView.setVisibility(View.GONE);
            showErrorEmptyView();
        } else if (entries.size() == 0) {
            mEventRecyclerView.setVisibility(View.GONE);
            showEmptyFeedView();
        } else {
            mTogglebutton.setVisibility(View.VISIBLE);
            for(Event event:entries){
                mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(event.getLat(),event.getLng()))
                        .title(event.getName()));
                mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {

                        Intent intent = new Intent(getActivity(), EventDetailActivity.class);

                        intent.putExtra("event", event);


                        getActivity().startActivity(intent);
                        return false;
                    }
                });
            }
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
        getLoaderManager().restartLoader(0, null, EventFragment.this);
    }

    private void setEmptyViewVisibility(int visibility) {
        if (mEmptyView != null) {
            mEmptyView.setVisibility(visibility);
        }
    }

    private void showEmptyFeedView() {
        mTogglebutton.setVisibility(View.GONE);
        if (mEmptyView == null) {
            mEmptyView = (EmptyView) mEmptyViewStub.inflate();
        }

        // TODO: Change to string resources.
        mEmptyView.reset();
        mEmptyView.setImageResource(R.drawable.ic_empty_newsfeed);
        mEmptyView.setTitle("No Events");
        mEmptyView.setSubtitle("There are no upcoming events by the time, check later.");
        mEmptyView.setAction("Try Again", v -> reloadFeedData());
        mEmptyView.setVisibility(View.VISIBLE);

    }

    private void showErrorEmptyView() {
        mTogglebutton.setVisibility(View.GONE);
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




    private class EventFeedAdapter extends RecyclerView.Adapter<EventFeedAdapter.ViewHolder> {

        private List<Event> mEventEntries;
        private OnItemClickListener mItemClickListener;

        public EventFeedAdapter(List<Event> entries) {
            mEventEntries = entries;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = getActivity().getLayoutInflater()
                    .inflate(R.layout.event_card, parent, false);

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

            Picasso.with(getActivity())
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
}
