package com.example.robmillaci.realestatemanager.activities.add_listing_activity;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.robmillaci.realestatemanager.data_objects.Listing;
import com.example.robmillaci.realestatemanager.databases.firebase.FirebaseHelper;
import com.example.robmillaci.realestatemanager.databases.local_database.MyDatabase;
import com.example.robmillaci.realestatemanager.utils.SharedPreferenceHelper;
import com.example.robmillaci.realestatemanager.utils.Utils;

import static com.example.robmillaci.realestatemanager.activities.add_listing_activity.AddListingPresenter.BROADCAST_ACTION;
import static com.example.robmillaci.realestatemanager.activities.add_listing_activity.AddListingPresenter.EDITING_KEY;


/**
 * Handles the background adding of listings, this is implemented as a service so it will remain running in the background if the user closes the application
 * during database updates
 */
public class AddListingService extends IntentService implements FirebaseHelper.AddListingCallback {
    private static final String SERVICE_NAME = "addListing";
    public static final String RESULTS = "result";

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


    /**
     * Called when saving a listing
     * @param intent the intent used to start this service
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        Listing listingToAdd = new SharedPreferenceHelper(getApplicationContext()).getListingFromSharedPrefs(); //get the listing to add from SharedPreferences

        boolean editingListing = intent.getBooleanExtra(EDITING_KEY, false); //get wether we are editing this listing or if it is a new listing

        if (editingListing) {
            MyDatabase.editListing(listingToAdd); //if we are editing, we remove the old listing from the database which in turn will call add for the new listing
        } else {
            MyDatabase.addListing(listingToAdd); //if we are not editing, we simply add the listing to the database
        }

        if(Utils.CheckConnectivity(getApplicationContext())) { //if we have internet connection, we will now add the listing to firebase,
                                                                // if not we send a broadcast after 5 seconds to the presenter to inform the view that the listing has been saved
            FirebaseHelper.addListing(listingToAdd, this);
        }else {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    sendMyBroadCast(false);
                }
            }, 5000);
        }
    }


    /**
     * Called when the listing has been added to the database and wether there was an error or not
     * @param error true if error false if not
     */
    @Override
    public void dBListingsAddedToFirebase(boolean error) {
        sendMyBroadCast(error);
    }

    @Override
    public void updateProgressBarDbSync(int count, String message) {
        //not used here
    }


    /**
     * Send a broadcast to the presented to inform that the listing has been saved. The presenter will inform the view in order to remove the progress dialog
     * and make relevant UI changes
     * @param error if there was an error saving or not
     */
    private void sendMyBroadCast(boolean error) {
        try {
            Intent broadCastIntent = new Intent();
            broadCastIntent.putExtra(RESULTS,error);
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
