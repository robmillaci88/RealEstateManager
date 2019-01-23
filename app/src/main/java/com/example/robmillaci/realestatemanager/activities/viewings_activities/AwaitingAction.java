package com.example.robmillaci.realestatemanager.activities.viewings_activities;

import android.os.Bundle;
import android.view.MenuItem;

public class AwaitingAction extends BaseViewingActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Awaiting Action");
        action_message.setText("You do not have any viewings booked requiring actions");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
