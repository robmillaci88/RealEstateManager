package com.example.robmillaci.realestatemanager.activities.offers_activities;

import android.os.Bundle;

import com.example.robmillaci.realestatemanager.R;
import com.example.robmillaci.realestatemanager.activities.viewings_activities.BaseViewingActivity;

/**
 * This class is responsible for the offers awaiting user action
 */
public class OffersAwaitingAction extends BaseViewingActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.offers_title));
        action_message.setText(R.string.no_offers);
    }

}

