package com.example.robmillaci.realestatemanager.activities.add_listing_activity;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.robmillaci.realestatemanager.data_objects.Listing;
import com.example.robmillaci.realestatemanager.databases.firebase.FirebaseHelper;
import com.example.robmillaci.realestatemanager.databases.local_database.MyDatabase;
import com.example.robmillaci.realestatemanager.utils.SharedPreferenceHelper;

import static com.example.robmillaci.realestatemanager.activities.add_listing_activity.AddListingPresenter.BROADCAST_ACTION;
import static com.example.robmillaci.realestatemanager.activities.add_listing_activity.AddListingPresenter.EDITING_KEY;


/**
 * Handles the background adding of listings, this is implemented as a service so it will remain running in the background if the user closes the application
 * during database updates
 */
public class AddListingService extends IntentService implements FirebaseHelper.AddListingCallback {
    private static final String SERVICE_NAME = "addListing";

    /**
     * Creates an IntentService.
     */
    public AddListingService() {
        super(SERVICE_NAME);
    }

    public void onCreate() {
        super.onCreate();
    }


    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        super.onStartCommand(intent, startId, startId);
        return START_STICKY;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Listing listingToAdd = new SharedPreferenceHelper(getApplicationContext()).getListingFromSharedPrefs();

        boolean editingListing = intent.getBooleanExtra(EDITING_KEY, false);

        if (editingListing) {
            MyDatabase.editListing(listingToAdd);
        } else {
            MyDatabase.addListing(listingToAdd);
        }

        FirebaseHelper.addListing(listingToAdd, this);
    }

    @Override
    public void listingsAdded() {
        sendMyBroadCast();
    }


    private void sendMyBroadCast() {
        try {
            Intent broadCastIntent = new Intent();
            broadCastIntent.setAction(BROADCAST_ACTION);
            sendBroadcast(broadCastIntent);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    interface AddListingServiceCallback {
        void listingAddedComplete();
    }
}
