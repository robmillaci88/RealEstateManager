package com.example.robmillaci.realestatemanager.activities.feedback_activities;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.robmillaci.realestatemanager.R;
import com.example.robmillaci.realestatemanager.activities.viewings_activities.BaseViewingActivity;

/**
 * This class is responsible for any feedback awaiting action on the user
 */
public class FeedBackAwaitingAction extends BaseViewingActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(getString(R.string.awaiting_action));
        action_message.setText(R.string.no_feedback);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
