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

public class NetworkListener extends BroadcastReceiver implements FirebaseHelper.Model, DbSyncListener {
    WeakReference<Context> mContextWeakReference;

    private SynchListenerCallback mSynchListenerCallback;
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
                FirebaseHelper.getInstance().synchWithLocalDb(context.getApplicationContext());

                FirebaseHelper.getInstance().setPresenter(this).getAllListings();

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

        return timeNow - lastUpdateTime >= 30 * 60 * 1000; //sync every 30 mins without having to manually trigger a sync
    }

    @Override
    public void gotListingsFromFirebase(ArrayList<Listing> listings) {
        MyDatabase.getInstance(mContextWeakReference.get()).setSynchListener(this).syncWithFirebase(listings);
    }

    @Override
    public void syncComplete() {
        Log.d("onNext", "syncComplete: called ");
        mSynchListenerCallback.dismissProgressDialog();
    }

    @Override
    public void updateProgressBarSyncProgress(int count) {
        mSynchListenerCallback.updateProgressDialog(count);
    }

}

