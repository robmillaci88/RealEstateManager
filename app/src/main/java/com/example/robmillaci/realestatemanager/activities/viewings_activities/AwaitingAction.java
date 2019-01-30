package com.example.robmillaci.realestatemanager.activities.viewings_activities;

import android.os.Bundle;

import com.example.robmillaci.realestatemanager.R;

public class AwaitingAction extends BaseViewingActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Awaiting Action");
        mActionMessage.setText(R.string.no_bookings_requiring_action);
    }

}
