package com.example.robmillaci.realestatemanager.fragments;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapViewFragment extends BaseFragment {
    public static final int DEFAULT_ZOOM = 17;

    private Listing thisListing;
    private GoogleMap mGoogleMap;
    private LatLng thisListingLoc;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.map_view_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getListing();

        String addressString = String.format("%s %s %s %s", thisListing.getAddress_number(),
                thisListing.getAddress_street(),
                thisListing.getAddress_town(),
                thisListing.getAddress_postcode());

        getListingLatLng(addressString);

        createMap(view, savedInstanceState);
    }


    private void getListing() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            thisListing = (Listing) bundle.getSerializable(ListingItemFragment.LISTING_BUNDLE_KEY);
        }
    }


    private void createMap(View view, @Nullable Bundle savedInstanceState) {
        final MapView mapView = view.findViewById(R.id.mapView);

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mGoogleMap = googleMap;
                mapView.onResume();

                MarkerOptions markerOptions = new MarkerOptions();

                if (thisListingLoc != null) {
                    markerOptions.position(thisListingLoc);

                    mGoogleMap.addMarker(markerOptions);

                    CameraPosition cameraPosition = new CameraPosition.Builder().target(thisListingLoc).zoom(DEFAULT_ZOOM).build();
                    mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }else{
                    Toast.makeText(getContext(),"Could not find location. Is GPS/Internet available?",Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    public void getListingLatLng(String strAddress) {
        @SuppressWarnings("ConstantConditions") Geocoder coder = new Geocoder(getActivity().getApplicationContext());
        List<Address> address;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address != null) {
                Address location = address.get(0);
                thisListingLoc = new LatLng(location.getLatitude(), location.getLongitude());
            }
        } catch (Exception e) {
            ToastModifications.createToast(getActivity().getApplicationContext(), getString(R.string.could_not_find_address_on_map), Toast.LENGTH_LONG);
        }
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

}
