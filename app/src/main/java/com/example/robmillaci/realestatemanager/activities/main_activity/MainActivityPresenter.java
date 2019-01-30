package com.example.robmillaci.realestatemanager.activities.main_activity;

import com.example.robmillaci.realestatemanager.data_objects.Listing;
import com.example.robmillaci.realestatemanager.databases.firebase.FirebaseHelper;
import com.example.robmillaci.realestatemanager.databases.local_database.MyDatabase;
import com.example.robmillaci.realestatemanager.utils.SharedPreferenceHelper;
import com.example.robmillaci.realestatemanager.utils.network_utils.DbSyncListener;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * This class is the link between {@link MainActivityView} and the databases
 */
public class MainActivityPresenter implements FirebaseHelper.Model, DbSyncListener, FirebaseHelper.AddListingCallback {
    private final View mView;

    MainActivityPresenter(View view) {
        mView = view;
    }

    /**
     * Syncs the local database with firebase
     */
    public void syncData() {
        FirebaseHelper.getInstance().setAddlistingcallback(this).synchWithLocalDb(getApplicationContext());
    }


    /**
     * Callback from {@link FirebaseHelper#getAllListings()}
     * @param listings the listings returned from firebase
     */
    @Override
    public void gotListingsFromFirebase(ArrayList<Listing> listings) {
        MyDatabase.getInstance(getApplicationContext()).setSynchListener(this).syncWithFirebase(listings); //Sync the local database with Firebase
    }

    /**
     * Callback from {@link MyDatabase} when sync is complete.
     * This metho updates the last sync time and called the views {@link MainActivityView#syncDataComplete(boolean)} to update the UI
     * @param error if an error is returned during the sync
     *
     */
    @Override
    public void syncComplete(boolean error) {
        new SharedPreferenceHelper(getApplicationContext()).updateLastSyncDate();
        mView.syncDataComplete(error);
    }



    /**
     * Called from {@link FirebaseHelper} when all local DB listings have been added to Firebase.
     * If there is an error, {@link #syncComplete(boolean)} is called. If there isn't an error we start the since between Firebase and the local DB
     * @param error  whether an error occured while adding the listing to firebase
     */
    @Override
    public void dBListingsAddedToFirebase(boolean error) {
       if (error){
           syncComplete(true);
       }else {
           FirebaseHelper.getInstance().setPresenter(this).getAllListings();
       }
    }


    /**
     * The views interface methods
     */
    interface View {
        void syncDataComplete(boolean error);
    }
}
