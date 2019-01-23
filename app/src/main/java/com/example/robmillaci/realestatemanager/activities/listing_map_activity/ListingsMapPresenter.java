package com.example.robmillaci.realestatemanager.activities.listing_map_activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.example.robmillaci.realestatemanager.R;
import com.example.robmillaci.realestatemanager.data_objects.Listing;
import com.example.robmillaci.realestatemanager.databases.firebase.FirebaseHelper;
import com.example.robmillaci.realestatemanager.databases.local_database.MyDatabase;
import com.example.robmillaci.realestatemanager.json_location_objects.LocationObject;
import com.example.robmillaci.realestatemanager.utils.Utils;
import com.example.robmillaci.realestatemanager.web_services.GetDataService;
import com.example.robmillaci.realestatemanager.web_services.ServiceGenerator;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.facebook.FacebookSdk.getApplicationContext;

public class ListingsMapPresenter implements FirebaseHelper.Model, MyDatabase.Model {
    private View view;

    ListingsMapPresenter(View v) {
        this.view = v;
    }

    void getAllListings(WeakReference<Context> weakContext) {
        Context c = weakContext.get();
        if (Utils.CheckConnectivity(c)) {
            FirebaseHelper.getInstance().setPresenter(this).getAllListings();
        } else {
            MyDatabase.getInstance(c).setPresenter(this).getFromLocalDb(c, null, 1);
        }
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
                            view.gotUsersLocation(location.getLatitude(), location.getLongitude());
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


    @Override
    public void gotListingsFromFirebase(ArrayList<Listing> listings) {
        view.gotAllListings(listings);
    }

    @Override
    public void gotData(ArrayList<Listing> listings, int requestCode, Context c) {
        view.gotAllListings(listings);
    }

    public void geoLocationListing(String address, final int markerIndex) {
        GetDataService service = ServiceGenerator.getRetrofitInstance().create(GetDataService.class);
        retrofit2.Call<LocationObject> call = service.getDetails(address, ServiceGenerator.getGoogleAPIKey());

        call.enqueue(new Callback<LocationObject>() {
            @Override
            public void onResponse(@NonNull Call<LocationObject> call, @NonNull Response<LocationObject> response) {
                LocationObject thisLocation = response.body();

                if (thisLocation != null && thisLocation.toString().toLowerCase().contains("limit")) {
                    Toast.makeText(getApplicationContext(), R.string.api_limit_reached, Toast.LENGTH_SHORT).show();
                }

                try {
                    //noinspection ConstantConditions
                    view.gotPlaceLatLng(thisLocation.getResults().get(0).getGeometry().getLocation().getLat(),
                            thisLocation.getResults().get(0).getGeometry().getLocation().getLng(), markerIndex);
                } catch (Exception e) {
                    view.gotPlaceLatLng(0, 0, 0);
                }
            }

            @Override
            public void onFailure(@NonNull Call<LocationObject> call, @NonNull Throwable t) {
                view.gotPlaceLatLng(0, 0, 0);
            }
        });
    }


    interface View {
        void gotAllListings(ArrayList<Listing> listings);

        void gotUsersLocation(double latitude, double longitude);

        void gotPlaceLatLng(double latitude, double longitude, int markerIndex);
    }
}
