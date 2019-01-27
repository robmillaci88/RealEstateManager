package com.example.robmillaci.realestatemanager.activities.offers_activities;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.robmillaci.realestatemanager.R;
import com.example.robmillaci.realestatemanager.activities.viewings_activities.BaseViewingActivity;

/**
 * This class is responsible for the Accepted offers activity
 */
public class AcceptedOffers extends BaseViewingActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.accepted_offers_title));
        action_message.setText(R.string.no_accepted_offers);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
