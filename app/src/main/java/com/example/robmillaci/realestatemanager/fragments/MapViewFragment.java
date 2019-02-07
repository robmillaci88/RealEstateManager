package com.example.robmillaci.realestatemanager.fragments;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.robmillaci.realestatemanager.R;
import com.example.robmillaci.realestatemanager.data_objects.Listing;
import com.example.robmillaci.realestatemanager.utils.ToastModifications;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment to display the mapView of a listing
 */
public class MapViewFragment extends BaseFragment {
    private static final int DEFAULT_ZOOM = 17; //the default zoom for the map

    private Listing mThisListing; //the listing to display the mapview
    private GoogleMap mGoogleMap; //the google map to display the listings marker on
    private LatLng mThisListingLoc; //this listings location
    private ArrayList<Marker> mapMarker; //arraylist to hold markers on the map

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.map_view_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapMarker = new ArrayList<>();

        getListing(); //get the listing passed into the arguments when creating this fragment

        String addressString = String.format("%s %s %s %s", mThisListing.getAddress_number(), //generate an address string used to geo locate the listing
                mThisListing.getAddress_street(),
                mThisListing.getAddress_town(),
                mThisListing.getAddress_postcode());

        getListingLatLng(addressString); //get the lat long of a listing

        createMap(view, savedInstanceState); //create the map view
    }


    private void getListing() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mThisListing = (Listing) bundle.getSerializable(ListingItemFragment.LISTING_BUNDLE_KEY);
        }
    }


    /**
     * Uses {@link Geocoder} to return the latlng of a listing based on the address
     *
     * @param strAddress the address of the listing in order to retrieve the lat lng
     */
    private void getListingLatLng(String strAddress) {
        @SuppressWarnings("ConstantConditions") Geocoder coder = new Geocoder(getActivity().getApplicationContext());
        List<Address> address;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address != null) {
                Address location = address.get(0);
                mThisListingLoc = new LatLng(location.getLatitude(), location.getLongitude());
            }
        } catch (Exception e) {
            ToastModifications.createToast(getContext(), getString(R.string.could_not_find_address_on_map), Toast.LENGTH_LONG);
        }
    }


    /**
     * Creates a map view which displays a single marker related to a listings lat lng location
     */
    private void createMap(View view, @Nullable Bundle savedInstanceState) {
        final MapView mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mGoogleMap = googleMap;
                mapView.onResume();

                MarkerOptions markerOptions = new MarkerOptions();

                if (mThisListingLoc != null) {
                    markerOptions.position(mThisListingLoc);

                    Marker addedMarker = mGoogleMap.addMarker(markerOptions);
                    mapMarker.add(addedMarker);

                    CameraPosition cameraPosition = new CameraPosition.Builder().target(mThisListingLoc).zoom(DEFAULT_ZOOM).build();
                    mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    UiSettings mapUiSettings = mGoogleMap.getUiSettings();
                    mapUiSettings.setZoomControlsEnabled(true);
                    mapUiSettings.setCompassEnabled(true);

                } else {
                    ToastModifications.createToast(getContext(), getString(R.string.no_location), Toast.LENGTH_LONG);
                }
            }

        });
    }

    public ArrayList<Marker> getMapMarker() {
        return mapMarker;
    }
}
