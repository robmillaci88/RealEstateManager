package com.example.robmillaci.realestatemanager.activities.search_activity;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
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

/**
 * This class is responsible for displaying the street view of a specific listing to the user
 */
public class StreetViewActivity extends BaseActivity implements OnStreetViewPanoramaReadyCallback {
    private LatLng mThisListingLoc; //the locations latLng


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_street_view);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.street_view_activity_title));


        //Restore the listing passed into the intent when creating this activity
        Listing thisListing;
        Bundle intentBundle = getIntent().getExtras();
        if (intentBundle != null) {
            thisListing = (Listing) intentBundle.getSerializable(ListingItemFragment.LISTING_BUNDLE_KEY);

            String addressString;
            if (thisListing != null) {
                addressString = String.format("%s %s %s %s", thisListing.getAddress_number(), //Created an address string from the listing
                        thisListing.getAddress_street(),
                        thisListing.getAddress_town(),
                        thisListing.getAddress_postcode());

                getListingLatLng(addressString); //pass the address string into getListingLatLng method
                getStreetView();  //creates the street view using the listings location

                setTitle(getString(R.string.street_view_title) + addressString);
            }
        }
    }


    /**
     * Using the passed in address string, this method sets the listings latitude and longitude so we can create the street view
     * @param strAddress the address of the listing to be Geo coded
     */
    private void getListingLatLng(String strAddress) {
        Geocoder coder = new Geocoder(getApplicationContext());
        List<Address> address;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address != null) {
                Address location = address.get(0);
                mThisListingLoc = new LatLng(location.getLatitude(), location.getLongitude());
            }
        } catch (Exception e) {
            ToastModifications.createToast(getApplicationContext(), getString(R.string.could_not_find_address_on_map), Toast.LENGTH_LONG);
        }
    }


    /**
     * Creates the street view fragment asynchronously, once ready calls {@link OnStreetViewPanoramaReadyCallback}
     */
    private void getStreetView() {
        StreetViewPanoramaFragment streetViewPanoramaFragment =
                (StreetViewPanoramaFragment) getFragmentManager()
                        .findFragmentById(R.id.streetviewpanorama);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);

    }


    /**
     * Once the street view fragment is ready, set the position to be the listings location
     * @param streetViewPanorama the created street view fragment
     */
    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {
        streetViewPanorama.setPosition(mThisListingLoc);
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
