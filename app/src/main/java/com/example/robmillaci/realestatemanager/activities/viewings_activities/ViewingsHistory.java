package com.example.robmillaci.realestatemanager.activities.viewings_activities;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.robmillaci.realestatemanager.R;

public class ViewingsHistory extends BaseViewingActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Viewings History");
        action_message.setText(R.string.no_viewings_history);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
