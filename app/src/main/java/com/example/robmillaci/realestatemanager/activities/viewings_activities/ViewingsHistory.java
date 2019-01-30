package com.example.robmillaci.realestatemanager.activities.viewings_activities;

import android.os.Bundle;

import com.example.robmillaci.realestatemanager.R;

public class ViewingsHistory extends BaseViewingActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Viewings History");
        mActionMessage.setText(R.string.no_viewings_history);
    }

}
