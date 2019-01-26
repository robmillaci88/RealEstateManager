package com.example.robmillaci.realestatemanager.activities.main_activity;

import android.util.Log;

import com.example.robmillaci.realestatemanager.data_objects.Listing;
import com.example.robmillaci.realestatemanager.databases.firebase.FirebaseHelper;
import com.example.robmillaci.realestatemanager.databases.local_database.MyDatabase;
import com.example.robmillaci.realestatemanager.utils.SharedPreferenceHelper;
import com.example.robmillaci.realestatemanager.utils.network_utils.DbSyncListener;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

public class MainActivityPresenter implements FirebaseHelper.Model, DbSyncListener, FirebaseHelper.AddListingCallback {
    private View mView;

    MainActivityPresenter(View view) {
        mView = view;
    }

    public void syncData() {
        FirebaseHelper.getInstance().setAddListingCallback(this).synchWithLocalDb(getApplicationContext());
    }

    @Override
    public void gotListingsFromFirebase(ArrayList<Listing> listings) {
        MyDatabase.getInstance(getApplicationContext()).setSynchListener(this).syncWithFirebase(listings);
    }

    @Override
    public void syncComplete(boolean error) {
        new SharedPreferenceHelper(getApplicationContext()).updateLastSyncDate();
        mView.syncDataComplete(error);
    }

    @Override
    public void updateProgressBarFirebaseSync(int count, String message) {
       mView.updateManualSyncProgress(count, message);
    }

    @Override
    public void dBListingsAddedToFirebase(boolean error) {
       if (error){
           syncComplete(true);
       }else {
           FirebaseHelper.getInstance().setPresenter(this).getAllListings();
       }
    }

    @Override
    public void updateProgressBarDbSync(int count, String message) {
        mView.updateManualSyncProgress(count,message);
    }


    interface View {
        void syncDataComplete(boolean error);
        void updateManualSyncProgress(int count,String message);
    }
}
