package com.example.robmillaci.realestatemanager.activities.listing_map_activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.example.robmillaci.realestatemanager.R;
import com.example.robmillaci.realestatemanager.data_objects.Listing;
import com.example.robmillaci.realestatemanager.databases.firebase.FirebaseHelper;
import com.example.robmillaci.realestatemanager.databases.local_database.MyDatabaseHelper;
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

/**
 * This class is the link between {@link ListingsMapView} and both {@link FirebaseHelper} & {@link MyDatabaseHelper}
 * The listing are obtained from the databases, the users location is also returned to the mView from this class as well as geo locating the listings
 */
public class ListingsMapPresenter implements FirebaseHelper.Model, MyDatabaseHelper.Model {
    private final View mView;

    ListingsMapPresenter(View v) {
        this.mView = v;
    } //the mView for this presenter


    /**
     * Get all the listings from either the local database or from Firebase
     */
    void getAllListings(WeakReference<Context> weakContext) {
        Context c = weakContext.get();
        if (Utils.CheckConnectivity(c)) {
            FirebaseHelper.getInstance().setPresenter(this).getAllListings();
        } else {
            MyDatabaseHelper.getInstance(c).setPresenter(this).searchLocalDB(c, null);
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
                            mView.gotUsersLocation(location.getLatitude(), location.getLongitude());
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


    /**
     * Interface callback method from {@link FirebaseHelper#getAllListings()}
     *
     * @param listings the returned listings
     */
    @Override
    public void gotListingsFromFirebase(ArrayList<Listing> listings) {
        mView.gotAllListings(listings);
    }


    /**
     * Interface callback method from {@link MyDatabaseHelper#searchLocalDB(Context, Bundle)}
     *
     * @param listings listings returned
     */
    @Override
    public void gotDataFromLocalDb(ArrayList<Listing> listings) {
        mView.gotAllListings(listings);
    }


    /**
     * From an address string obtained from a listing, this method returns the listings latitude and longitude so we can show the listing location
     * on the map.
     *
     * @param address     the address of the listing
     * @param markerIndex the index of the marker representing the listing
     */
    void geoLocationListing(String address, final int markerIndex) {
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
                    mView.gotPlaceLatLng(thisLocation.getResults().get(0).getGeometry().getLocation().getLat(),
                            thisLocation.getResults().get(0).getGeometry().getLocation().getLng(), markerIndex);
                } catch (Exception e) {
                    mView.gotPlaceLatLng(0, 0, 0);
                }
            }

            @Override
            public void onFailure(@NonNull Call<LocationObject> call, @NonNull Throwable t) {
                mView.gotPlaceLatLng(0, 0, 0);
            }
        });
    }

    /**
     * the Views interface methods
     */
    interface View {
        void gotAllListings(ArrayList<Listing> listings);

        void gotUsersLocation(double latitude, double longitude);

        void gotPlaceLatLng(double latitude, double longitude, int markerIndex);
    }
}
