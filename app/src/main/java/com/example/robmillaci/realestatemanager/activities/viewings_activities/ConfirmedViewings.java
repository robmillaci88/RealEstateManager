package com.example.robmillaci.realestatemanager.activities.viewings_activities;

import android.os.Bundle;
import android.view.MenuItem;

public class ConfirmedViewings extends BaseViewingActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Confirmed Viewings");
        action_message.setText("You do not have any confirmed viewings");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
