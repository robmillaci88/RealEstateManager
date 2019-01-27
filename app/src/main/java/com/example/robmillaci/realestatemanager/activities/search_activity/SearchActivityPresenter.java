package com.example.robmillaci.realestatemanager.activities.search_activity;

import android.annotation.SuppressLint;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.example.robmillaci.realestatemanager.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * The presenter for {@link SearchActivityView} responsible for getting the users last known location and returning the postcode
 */
 class SearchActivityPresenter {
    private View view;


    SearchActivityPresenter(View view) {
        this.view = view;
    }


    @SuppressLint("MissingPermission")
    void getLastKnownLocation() {
        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // GPS location can be null if GPS is switched off
                        if (location != null) {
                            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                            List<Address> addresses = new ArrayList<>();
                            try {
                                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            view.gotLocation(addresses.get(0).getPostalCode() != null ? addresses.get(0).getPostalCode() : "");
                        }else {
                            Toast.makeText(getApplicationContext(), R.string.error_getting_gps, Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), R.string.error_getting_gps, Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                });
    }


    interface View {
        void gotLocation(String postCode);
    }
}
