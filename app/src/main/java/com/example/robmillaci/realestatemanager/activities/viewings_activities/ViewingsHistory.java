package com.example.robmillaci.realestatemanager.activities.viewings_activities;

import android.os.Bundle;
import android.view.MenuItem;

public class ViewingsHistory extends BaseViewingActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Viewings History");
        action_message.setText("You do not have any viewings history");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
