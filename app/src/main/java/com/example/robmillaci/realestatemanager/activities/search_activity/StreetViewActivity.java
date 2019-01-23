package com.example.robmillaci.realestatemanager.activities.search_activity;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.robmillaci.realestatemanager.R;
import com.example.robmillaci.realestatemanager.activities.BaseActivity;
import com.example.robmillaci.realestatemanager.data_objects.Listing;
import com.example.robmillaci.realestatemanager.fragments.ListingItemFragment;
import com.example.robmillaci.realestatemanager.utils.ToastModifications;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class StreetViewActivity extends BaseActivity implements OnStreetViewPanoramaReadyCallback {
    private LatLng thisListingLoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_street_view);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.street_view_activity_title));

        Listing thisListing;
        Bundle intentBundle = getIntent().getExtras();
        if (intentBundle != null) {
            thisListing = (Listing) intentBundle.getSerializable(ListingItemFragment.LISTING_BUNDLE_KEY);

            String addressString = "";
            if (thisListing != null) {
                addressString = String.format("%s %s %s %s", thisListing.getAddress_number(),
                        thisListing.getAddress_street(),
                        thisListing.getAddress_town(),
                        thisListing.getAddress_postcode());

                getListingLatLng(addressString);
                getStreetView(savedInstanceState, thisListingLoc);

                setTitle(getString(R.string.street_view_title) + addressString);
            }
        }
    }


    public void getListingLatLng(String strAddress) {
        Geocoder coder = new Geocoder(getApplicationContext());
        List<Address> address;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address != null) {
                Address location = address.get(0);
                thisListingLoc = new LatLng(location.getLatitude(), location.getLongitude());
            }
        } catch (Exception e) {
            ToastModifications.createToast(getApplicationContext(), getString(R.string.could_not_find_address_on_map), Toast.LENGTH_LONG);
        }
    }


    private void getStreetView(@Nullable Bundle savedInstanceState, final LatLng listingLoc) {
        StreetViewPanoramaFragment streetViewPanoramaFragment =
                (StreetViewPanoramaFragment) getFragmentManager()
                        .findFragmentById(R.id.streetviewpanorama);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);

    }


    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {
        streetViewPanorama.setPosition(thisListingLoc);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
