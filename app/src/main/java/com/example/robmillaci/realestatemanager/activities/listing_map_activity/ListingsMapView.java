package com.example.robmillaci.realestatemanager.activities.listing_map_activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.example.robmillaci.realestatemanager.R;
import com.example.robmillaci.realestatemanager.data_objects.Listing;
import com.example.robmillaci.realestatemanager.fragments.ListingItemFragment;
import com.example.robmillaci.realestatemanager.utils.SharedPreferenceHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.example.robmillaci.realestatemanager.activities.search_activity.SearchResultsView.FRAGMENT_TAG;

public class ListingsMapView extends AppCompatActivity implements ListingsMapPresenter.View, GoogleMap.OnMarkerClickListener {
    public static final int REQUEST_LOCATION_PERMISSION = 2;

    private ListingsMapPresenter mPresenter;
    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private LatLng userLocation;
    private ProgressDialog pd;
    private ArrayList<Listing> listings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing_map_view);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.map_view_title));

        this.mPresenter = new ListingsMapPresenter(this);
        listings = new ArrayList<>();

        getUsersLocation();
    }


    private void createMap(final ArrayList<Listing> listings) {
        mMapView = findViewById(R.id.listings_map_view);

        mMapView.onCreate(new Bundle());
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @SuppressLint("MissingPermission")
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mGoogleMap = googleMap;
                mGoogleMap.setOnMarkerClickListener(ListingsMapView.this);
                mMapView.onResume();

                for (int i = 0; i < listings.size(); i++) {
                    Listing l = listings.get(i);
                    mPresenter.geoLocationListing(String.format("%s %s %s %s", l.getAddress_number(), l.getAddress_street(), l.getAddress_town(), l.getAddress_postcode()), i);
                }

                CameraPosition cameraPosition = new CameraPosition.Builder().target(userLocation).zoom(12).build();
                mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                if (EasyPermissions.hasPermissions(ListingsMapView.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    mGoogleMap.setMyLocationEnabled(true);
                }

                if (pd.isShowing()) {
                    pd.dismiss();
                }
            }
        });
    }

    @AfterPermissionGranted(REQUEST_LOCATION_PERMISSION)
    private void getUsersLocation() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
        if (EasyPermissions.hasPermissions(this, perms)) {
            //permission is granted - get the users location postcode
//
            pd = new ProgressDialog(ListingsMapView.this);
            pd.setMessage(getString(R.string.generating_map_message));
            pd.show();

            mPresenter.getLastKnownLocation();
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.grant_permission), REQUEST_LOCATION_PERMISSION, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @Override
    public void gotAllListings(ArrayList<Listing> listings) {
        this.listings = listings;
        createMap(listings);
    }

    @Override
    public void gotUsersLocation(double latitude, double longitude) {
        this.userLocation = new LatLng(latitude, longitude);
        mPresenter.getAllListings(new WeakReference<Context>(this));
    }

    @Override
    public void gotPlaceLatLng(double latitude, double longitude, int markerIndex) {
        if (latitude != 0 && longitude != 0) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(new LatLng(latitude, longitude));

            Marker m = mGoogleMap.addMarker(markerOptions);
            m.setTag(markerIndex);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        int markerTag = (int) marker.getTag();
        Listing clickedListing = listings.get(markerTag);

        findViewById(R.id.fragment_container).setBackgroundColor(Color.WHITE);
        setTitle(clickedListing.getAddress_number() + " " + clickedListing.getAddress_street());

        mMapView.setVisibility(View.GONE);

        // changeActivityViewsVisibility(false);
        new SharedPreferenceHelper(getApplicationContext()).addListingToSharedPref(clickedListing);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new ListingItemFragment(), FRAGMENT_TAG).addToBackStack(null).commit();
        return true;
    }

    @Override
    public void onBackPressed() {
        Fragment f = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);

        if (f != null && f.isAdded() && f.isVisible()) {
            getSupportFragmentManager().beginTransaction().remove(f).commit();
            mMapView.setVisibility(View.VISIBLE);
            setTitle(R.string.map_view_title);
        } else {
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }
}
