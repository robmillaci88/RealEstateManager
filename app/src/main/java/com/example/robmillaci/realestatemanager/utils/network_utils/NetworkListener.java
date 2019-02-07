package com.example.robmillaci.realestatemanager.utils.network_utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.robmillaci.realestatemanager.R;
import com.example.robmillaci.realestatemanager.data_objects.Listing;
import com.example.robmillaci.realestatemanager.databases.firebase.FirebaseHelper;
import com.example.robmillaci.realestatemanager.databases.local_database.MyDatabaseHelper;
import com.example.robmillaci.realestatemanager.utils.SharedPreferenceHelper;
import com.example.robmillaci.realestatemanager.utils.Utils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * This class is responsible for handling internet state changes. Receives a broadcast when network state changes and if connection is available
 * will sync the local DB and Firebase
 */
public class NetworkListener extends BroadcastReceiver implements FirebaseHelper.Model, DbSyncListener, FirebaseHelper.AddListingCallback {
    private WeakReference<Context> mContextWeakReference; //weak reference to the context of the activity instantiating this class
    private final SynchListenerCallback mSynchListenerCallback; //the callback for synching updates
    @SuppressWarnings("FieldCanBeLocal")
    private final int SYNC_FREQUENCY = 60 * 60 * 1000; //the frequency at which we auto sync the databases
    public static final String LASTSYNCKEY = "lastSync"; //the key to store the last sync time in shared prefs


    public NetworkListener(SynchListenerCallback callback) {
        this.mSynchListenerCallback = callback;
    }

    @Override
    public void onReceive(Context context, Intent intent) { //called when network state changes
        if (Utils.CheckConnectivity(context)) { //do we have network connection?
            if (shouldWeSynch(context)) { //is it an hour since the last since?

                mContextWeakReference = new WeakReference<>(context);
                mSynchListenerCallback.showProgressDialog(); //call back to the view to show the progress bar

                //we are online - sync local DB with fireStore and then sync firestore with localDB
                FirebaseHelper.getInstance().setAddlistingcallback(this).synchWithLocalDb(context.getApplicationContext());

                new SharedPreferenceHelper(context).updateLastSyncDate(); //update the last time we have synched the databases

                Toast.makeText(context, R.string.now_online, Toast.LENGTH_LONG).show();
            }
        } else {
            //we are offline - do nothing
            Toast.makeText(context, R.string.now_offline, Toast.LENGTH_LONG).show();

        }
    }


    /**
     * Checks to see if the last time we synced the databases is at least an hour sinsce now
     *
     * @param c context
     * @return true - we can sync, false - we don't sync
     */
    private boolean shouldWeSynch(Context c) {
        long lastUpdateTime = new SharedPreferenceHelper(c).getLastSyncTime();
        long timeNow = System.currentTimeMillis();

        return timeNow - lastUpdateTime >= SYNC_FREQUENCY; //sync every hour without having to manually trigger a sync
    }


    /**
     * callback from {@link FirebaseHelper#getAllListings()}
     *
     * @param listings the listings returned from Firebase
     */
    @Override
    public void gotListingsFromFirebase(ArrayList<Listing> listings) {
        //Update the local DB with the listings from firebase if required
        MyDatabaseHelper.getInstance(mContextWeakReference.get()).setSynchListener(this).syncWithFirebase(listings);
    }


    //callback when database sync is completed
    @Override
    public void
    syncComplete(boolean error) {
        mSynchListenerCallback.dismissProgressDialog(error); //dismiss the progress bar on sync complete
    }


    /**
     * Callback when a listings are added from the local DB to firebase
     *
     * @param error wether an error occurred or not whilst performing the upload to Firebase
     */
    @Override
    public void dBListingsAddedToFirebase(boolean error) {
        if (error) {
            mSynchListenerCallback.dismissProgressDialog(true); //if an error, dismiss the progress dialog with an error
        } else {
            FirebaseHelper.getInstance().setPresenter(this).getAllListings(); //else proceed with now syncing the local DB with any other firebase listings
        }
    }


}

