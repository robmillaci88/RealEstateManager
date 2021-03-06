package com.example.robmillaci.realestatemanager.activities.feedback_activities;

import android.os.Bundle;

import com.example.robmillaci.realestatemanager.R;
import com.example.robmillaci.realestatemanager.activities.viewings_activities.BaseViewingActivity;

/**
 * This class is responsible for any feedback or pending feedback for the user
 */
public class FeedbackPendingReceived extends BaseViewingActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.pending_and_history));
        mActionMessage.setText(R.string.no_pending_feedback);
    }

}
