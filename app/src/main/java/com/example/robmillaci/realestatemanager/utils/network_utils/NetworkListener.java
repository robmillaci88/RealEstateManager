package com.example.robmillaci.realestatemanager.utils.network_utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.robmillaci.realestatemanager.R;
import com.example.robmillaci.realestatemanager.data_objects.Listing;
import com.example.robmillaci.realestatemanager.databases.firebase.FirebaseHelper;
import com.example.robmillaci.realestatemanager.databases.local_database.MyDatabase;
import com.example.robmillaci.realestatemanager.utils.SharedPreferenceHelper;
import com.example.robmillaci.realestatemanager.utils.Utils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class NetworkListener extends BroadcastReceiver implements FirebaseHelper.Model, DbSyncListener, FirebaseHelper.AddListingCallback {
    WeakReference<Context> mContextWeakReference;

    private SynchListenerCallback mSynchListenerCallback;
    @SuppressWarnings("FieldCanBeLocal")
    private final int SYNC_FREQUENCY = 60 * 60 * 1000;
    public static final String LASTSYNCKEY = "lastSync";


    public NetworkListener(SynchListenerCallback callback) {
        this.mSynchListenerCallback = callback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Utils.CheckConnectivity(context)) {
            if (shouldWeSynch(context)) {
                mContextWeakReference = new WeakReference<>(context);

                mSynchListenerCallback.showProgressDialog();
                //we are online - sync local DB with fireStore and then sync firestore with localDB
                FirebaseHelper.getInstance().setAddListingCallback(this).synchWithLocalDb(context.getApplicationContext());

                new SharedPreferenceHelper(context).updateLastSyncDate();

                Toast.makeText(context, R.string.now_online, Toast.LENGTH_LONG).show();
            }
        } else {
            //we are offline - do nothing
            Toast.makeText(context, R.string.now_offline, Toast.LENGTH_LONG).show();

        }
    }

    private boolean shouldWeSynch(Context c) {
        long lastUpdateTime = new SharedPreferenceHelper(c).getLastSyncTime();
        long timeNow = System.currentTimeMillis();

        return timeNow - lastUpdateTime >= SYNC_FREQUENCY; //sync every hour without having to manually trigger a sync
    }

    @Override
    public void gotListingsFromFirebase(ArrayList<Listing> listings) {
        MyDatabase.getInstance(mContextWeakReference.get()).setSynchListener(this).syncWithFirebase(listings);
    }

    @Override
    public void
    syncComplete(boolean error) {
        mSynchListenerCallback.dismissProgressDialog(error);
    }

    @Override
    public void dBListingsAddedToFirebase(boolean error) {
        if (error) {
            mSynchListenerCallback.dismissProgressDialog(true);
        } else {
            FirebaseHelper.getInstance().setPresenter(this).getAllListings();
        }
    }


    @Override
    public void updateProgressBarFirebaseSync(int count, String message) {
        Log.d("updateProgressBarSy", "updateProgressBarFirebaseSync: got here ");
        mSynchListenerCallback.updateProgressDialog(count,message);
        //not used in this class
    }

    @Override
    public void updateProgressBarDbSync(int count, String message) {
        Log.d("updateProgressBarDbSync", "updateProgressBarFirebaseSync: got here ");
        mSynchListenerCallback.updateProgressDialog(count,message);


        //not used in this class
    }

}

