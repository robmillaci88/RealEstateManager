package com.example.robmillaci.realestatemanager.activities.viewings_activities;

import android.os.Bundle;

import com.example.robmillaci.realestatemanager.R;

public class ConfirmedViewings extends BaseViewingActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Confirmed Viewings");
        mActionMessage.setText(R.string.no_confirmed_viewings);
    }

}
