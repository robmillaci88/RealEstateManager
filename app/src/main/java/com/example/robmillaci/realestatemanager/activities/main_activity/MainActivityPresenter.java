package com.example.robmillaci.realestatemanager.activities.main_activity;

import com.example.robmillaci.realestatemanager.data_objects.Listing;
import com.example.robmillaci.realestatemanager.databases.firebase.FirebaseHelper;
import com.example.robmillaci.realestatemanager.databases.local_database.MyDatabase;
import com.example.robmillaci.realestatemanager.utils.SharedPreferenceHelper;
import com.example.robmillaci.realestatemanager.utils.network_utils.DbSyncListener;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

public class MainActivityPresenter implements FirebaseHelper.Model, DbSyncListener {
    private View mView;

    MainActivityPresenter(View view) {
        mView = view;
    }

    public void syncData() {
        FirebaseHelper.getInstance().synchWithLocalDb(getApplicationContext());
        FirebaseHelper.getInstance().setPresenter(this).getAllListings();
        new SharedPreferenceHelper(getApplicationContext()).updateLastSyncDate();
    }

    @Override
    public void gotListingsFromFirebase(ArrayList<Listing> listings) {
        MyDatabase.getInstance(getApplicationContext()).setSynchListener(this).syncWithFirebase(listings);
    }

    @Override
    public void syncComplete() {
        mView.synchDataComplete();
    }

    @Override
    public void updateProgressBarSyncProgress(int count) {
       mView.updateManualSyncProgress(count);
    }

    interface View {
        void synchDataComplete();

        void updateManualSyncProgress(int count);
    }
}
