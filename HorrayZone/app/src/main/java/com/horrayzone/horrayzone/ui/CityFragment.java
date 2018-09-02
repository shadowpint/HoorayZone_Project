package com.horrayzone.horrayzone.ui;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.horrayzone.horrayzone.R;
import com.horrayzone.horrayzone.adapter.CityAdapter;
import com.horrayzone.horrayzone.adapter.HorizontalAdapter;
import com.horrayzone.horrayzone.loader.CityLoader;
import com.horrayzone.horrayzone.loader.NearbyEventLoader;
import com.horrayzone.horrayzone.model.City;
import com.horrayzone.horrayzone.model.Event;
import com.horrayzone.horrayzone.ui.widget.EmptyView;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.takusemba.multisnaprecyclerview.MultiSnapRecyclerView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static android.support.constraint.Constraints.TAG;

public class CityFragment extends BaseNavigationFragment implements LoaderManager.LoaderCallbacks<List<City>>, CityAdapter.CityAdapterListener, HorizontalAdapter.HorizontalAdapterListener, GoogleApiClient.ConnectionCallbacks,
        LocationListener, GoogleApiClient.OnConnectionFailedListener {
    private static final String LOG_TAG = "CityFragment";
    public static final long UPDATE_INTERVAL = 2000;
    public static final long UPDATE_FASTEST_INTERVAL = UPDATE_INTERVAL / 2;
    private final int REQUEST_LOCATION = 110;

    public static CityFragment newInstance() {
        return new CityFragment();
    }

    private final int REQUEST_CHECK_SETTINGS = 215;
    private MultiSnapRecyclerView firstRecyclerView;
    private HorizontalAdapter mHorizontalAdapter;
    private TextView mNearbytextView;
    private View divider;
    private LoaderManager.LoaderCallbacks<List<Event>> mNearbyEventCallbacks;
    /*enum UiState {
        LOADING,
        EMPTY,
        ERROR,
        DISPLAY_CONTENT
    }*/
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest locationRequest;
    private LocationSettingsRequest.Builder builder;
    private Handler handler = new Handler();
    private ShimmerFrameLayout mShimmerViewContainer;
    private RecyclerView mCityRecyclerView;
    private CityAdapter mCityAdapter;
    private ProgressBar mProgressBar;
    private ViewStub mEmptyViewStub;
    private EmptyView mEmptyView;
    private List<City> entries;
    private SearchView searchView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setupGoogleApiClient();
        setupLocationRequest();
        setupLocationSettings();
        setHasOptionsMenu(false);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_city, container, false);
        mShimmerViewContainer = (ShimmerFrameLayout) rootView.findViewById(R.id.shimmer_view_container);
        mEmptyViewStub = (ViewStub) rootView.findViewById(R.id.empty_view);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("   Select City");
        mNearbytextView = (TextView) rootView.findViewById(R.id.event_nearby);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


//        mBlogFeedAdapter.setOnItemClickListener((itemView, position) -> {
//            Log.d(LOG_TAG, "onItemClick: " + position);
//            City entry = mBlogFeedAdapter.getCity(position);
////
////            Intent intent = new Intent(getActivity(), CityReaderActivity.class);
////            intent.putExtra(CityReaderActivity.EXTRA_ENTRY_TITLE, entry.getTitle());
////            intent.putExtra(CityReaderActivity.EXTRA_ENTRY_CONTENT, entry.getContent());
////            intent.putExtra(CityReaderActivity.EXTRA_ENTRY_IMAGE_URL, entry.getImageUrl());
////
////            getActivity().startActivity(intent);
//        });
        divider = (View) view.findViewById(R.id.divider);
        firstRecyclerView = (MultiSnapRecyclerView) view.findViewById(R.id.first_recycler_view);
        LinearLayoutManager firstManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        firstRecyclerView.setLayoutManager(firstManager);

        mCityRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mCityRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

    }

    private void setupGoogleApiClient() {
        // Establecer punto de entrada para la API de ubicación
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .enableAutoManage(getActivity(), this)
                .build();
    }


    private void setupLocationRequest() {
        // Crear configuración de peticiones
        locationRequest = new LocationRequest()
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(UPDATE_FASTEST_INTERVAL)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    private void setupLocationSettings() {
        // Crear opciones de peticiones
        builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
                .setAlwaysShow(true);

        // Verificar ajustes de ubicación actuales
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(
                mGoogleApiClient, builder.build()
        );

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                Status status = result.getStatus();

                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.d(TAG, "Los ajustes de ubicación satisfacen la configuración.");
                        processLastLocation();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            Log.d(TAG, "Los ajustes de ubicación no satisfacen la configuración. " +
                                    "Se mostrará un diálogo de ayuda.");
                            status.startResolutionForResult(
                                    getActivity(),
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.d(TAG, "El Intent del diálogo no funcionó.");
                            // Sin operaciones
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.d(TAG, "Los ajustes de ubicación no son apropiados.");
                        break;

                }
            }
        });
    }


    private void processLastLocation() {
        getLastLocation();
        if (mLastLocation != null) {

        }
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        //if (Dexter.)
        if (isLocationPermissionGranted()) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        } else {
            manageDeniedPermission();
        }
    }

    private boolean isLocationPermissionGranted() {
        int permission = ActivityCompat.checkSelfPermission(
                getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permission == PackageManager.PERMISSION_GRANTED;
    }


    private void manageDeniedPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(Objects.requireNonNull(getActivity()),
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Aquí muestras confirmación explicativa al usuario
            // por si rechazó los permisos anteriormente
        } else {
            ActivityCompat.requestPermissions(
                    getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        }
    }


//    private void saveLocationInSession() {
//        sessionManager.setLastLatitude(String.valueOf(mLastLocation.getLatitude()));
//        sessionManager.setLastLongitude(String.valueOf(mLastLocation.getLongitude()));
//    }

    private void procesarOnconectedLocation() {
        Log.d(TAG, "onConnected");

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Aquí muestras confirmación explicativa al usuario
                // por si rechazó los permisos anteriormente
            } else {
                ActivityCompat.requestPermissions(
                        getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_LOCATION);
            }
        } else {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {

                mNearbyEventCallbacks = new LoaderManager.LoaderCallbacks<List<Event>>() {
                    @Override
                    public Loader<List<Event>> onCreateLoader(int id, Bundle args) {
                        Log.d(TAG, "onCreateLoader: NearbyEvents");
                        firstRecyclerView.setVisibility(View.GONE);
                        return new NearbyEventLoader(getActivity(), mLastLocation);
                    }

                    @Override
                    public void onLoadFinished(Loader<List<Event>> loader, List<Event> data) {
                        Log.d(TAG, "onLoadFinished: ProductDetails");
                        if (data == null) {
                            firstRecyclerView.setVisibility(View.GONE);
                            mNearbytextView.setVisibility(View.GONE);
                            divider.setVisibility(View.GONE);
                            ViewGroup.MarginLayoutParams marginLayoutParams =
                                    (ViewGroup.MarginLayoutParams) mCityRecyclerView.getLayoutParams();
                            marginLayoutParams.setMargins(0, 90, 0, 0);
                            mCityRecyclerView.setLayoutParams(marginLayoutParams);
                        } else if (data.size() == 0) {
                            firstRecyclerView.setVisibility(View.GONE);
                            mNearbytextView.setVisibility(View.GONE);
                            divider.setVisibility(View.GONE);
                            firstRecyclerView.setVisibility(View.GONE);
                            ViewGroup.MarginLayoutParams marginLayoutParams =
                                    (ViewGroup.MarginLayoutParams) mCityRecyclerView.getLayoutParams();
                            marginLayoutParams.setMargins(15, 90, 0, 0);
                            mCityRecyclerView.setLayoutParams(marginLayoutParams);

                        } else {
                            RelativeLayout.LayoutParams params= new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                            params.addRule(RelativeLayout.BELOW, R.id.first_recycler_view);

                            mCityRecyclerView.setLayoutParams(params);
                            ViewGroup.MarginLayoutParams marginLayoutParams =
                                    (ViewGroup.MarginLayoutParams) mCityRecyclerView.getLayoutParams();
                            marginLayoutParams.setMargins(20, 70, 20, 0);
                            mCityRecyclerView.setLayoutParams(marginLayoutParams);


                            firstRecyclerView.setVisibility(View.VISIBLE);


                            mHorizontalAdapter = new HorizontalAdapter(getContext(), data, CityFragment.this::onEventSelected);
                            firstRecyclerView.setAdapter(mHorizontalAdapter);
                            mHorizontalAdapter.notifyDataSetChanged();

                        }
                    }

                    @Override
                    public void onLoaderReset(Loader<List<Event>> loader) {

                    }
                };


                //getSupportLoaderManager().initLoader(ProductMultimediaQuery.LOADER_ID, null, this);
                getLoaderManager().initLoader(0x3, null, mNearbyEventCallbacks);

                // Toast.makeText(getActivity(), "Ubicación encontrada" + mLastLocation.getLatitude(), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getActivity(), "Ubicación no encontrada", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "ON START MAIN ACTIVITY");
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "ON STOP MAIN ACTIVITY");
        //EventBus.getDefault().unregister(this);
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.disconnect();
        Log.d(TAG, "ON DESTROY MAIN ACTIVITY");
    }
    @Override
    public void onPause() {
        super.onPause();
        mGoogleApiClient.stopAutoManage(getActivity());
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mGoogleApiClient.disconnect();
    }

    private void updateUI() {
        if (mLastLocation != null) {

            (new GetAddressTask(getActivity())).execute(mLastLocation);
        }
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<List<City>> onCreateLoader(int id, Bundle args) {
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmerAnimation();
        mCityRecyclerView.setVisibility(View.GONE);
        firstRecyclerView.setVisibility(View.GONE);
        setEmptyViewVisibility(View.GONE);
        return new CityLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<City>> loader, List<City> entries) {
        mShimmerViewContainer.setVisibility(View.GONE);
        mShimmerViewContainer.startShimmerAnimation();
        if (entries == null) {
            mCityRecyclerView.setVisibility(View.GONE);

            showErrorEmptyView();
        } else if (entries.size() == 0) {
            mCityRecyclerView.setVisibility(View.GONE);

            showEmptyFeedView();
        } else {
            mCityRecyclerView.setVisibility(View.VISIBLE);

            this.entries=entries;
            mCityAdapter = new CityAdapter(getContext(), entries,this);
            mCityRecyclerView.setAdapter(mCityAdapter);
            mCityAdapter.notifyDataSetChanged();


            setEmptyViewVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<City>> loader) {
        mCityAdapter = new CityAdapter(getContext(), null, this);
        mCityAdapter.notifyDataSetChanged();

    }

    private void reloadFeedData() {
        getLoaderManager().restartLoader(0, null, CityFragment.this);
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
        mEmptyView.setTitle("Server error");
        mEmptyView.setSubtitle("We will be back Again, check later.");
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
    public void onCitySelected(City city) {
Log.e("city",city.getName());
        Bundle i = new Bundle();
        i.putParcelable("city",city);

        EventFragment frag = new EventFragment();
        frag.setArguments(i);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , frag,"event_fragment")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onCreateOptionsMenu( Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.activity_search, menu);
        super.onCreateOptionsMenu(menu, inflater);



    }



    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_search:

                searchView = (SearchView) item.getActionView();
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        // filter recycler view when query submitted
                        mCityAdapter.getFilter().filter(query);
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String query) {
                        // filter recycler view when text is changed
                        mCityAdapter.getFilter().filter(query);
                        return false;
                    }
                });
                // Do Fragment menu item stuff here
                return true;

            default:
                break;
        }

        return false;
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        procesarOnconectedLocation();
        updateUI();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "onConnectionSuspended");
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, String.format("Nueva ubicación: (%s, %s)",
                location.getLatitude(), location.getLongitude()));
        mLastLocation = location;
        updateUI();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.d(TAG, "El usuario permitió el cambio de ajustes de ubicación.");
                        processLastLocation();
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.d(TAG, "El usuario no permitió el cambio de ajustes de ubicación");
                        setupLocationSettings();
                        break;
                }
                break;
        }

    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onEventSelected(Event event) {

        Log.e("Event", event.getName());
        Intent intent = new Intent(getActivity(), EventDetailActivity.class);

        intent.putExtra("event", event);


        getActivity().startActivity(intent);
    }

    private class GetAddressTask extends AsyncTask<Location, Void, String> {
        Context mContext;

        public GetAddressTask(Context context) {
            super();
            mContext = context;
        }

        @Override
        protected String doInBackground(Location... params) {
            Geocoder geocoder =
                    new Geocoder(mContext, Locale.getDefault());
            // Get the current location from the input parameter list
            Location loc = params[0];
            // Create a list to contain the result address
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(loc.getLatitude(),
                        loc.getLongitude(), 1);
            } catch (IOException e1) {
                Log.e("LocationSampleActivity",
                        "IO Exception in getFromLocation()");
                e1.printStackTrace();
                return ("IO Exception trying to get address");
            } catch (IllegalArgumentException e2) {
                // Error message to post in the log
                String errorString = "Illegal arguments " +
                        Double.toString(loc.getLatitude()) +
                        " , " +
                        Double.toString(loc.getLongitude()) +
                        " passed to address service";
                Log.e("LocationSampleActivity", errorString);
                e2.printStackTrace();
                return errorString;
            }
            // If the reverse geocode returned an address
            if (addresses != null && addresses.size() > 0) {
                // Get the first address
                Address address = addresses.get(0);
                /*
                 * Format the first line of address (if available),
                 * city, and country name.
                 */
                String addressText = String.format(
                        "%s, %s, %s",
                        // If there's a street address, add it
                        address.getMaxAddressLineIndex() > 0 ?
                                address.getAddressLine(0) : "",
                        // Locality is usually a city
                        address.getLocality(),
                        // The country of the address
                        address.getCountryName());
                // Return the text
                return address.getLocality();
            } else {
                return "No address found";
            }
        }

        @Override
        protected void onPostExecute(String address) {
            mNearbytextView.setText("See Events Nearby " + address);
//            Toast.makeText(mContext, address, Toast.LENGTH_SHORT).show();
        }
    }

}
