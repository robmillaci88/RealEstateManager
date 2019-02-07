package com.example.robmillaci.realestatemanager.activities.splash_screen;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.robmillaci.realestatemanager.R;
import com.example.robmillaci.realestatemanager.activities.BaseActivity;
import com.example.robmillaci.realestatemanager.activities.main_activity.MainActivityView;
import com.example.robmillaci.realestatemanager.utils.Utils;

/**
 * A simple splash screen that is displayed to the user when the app starts.
 * Waits for 3 seconds before starting the main activity
 */
public class SplashScreenActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        startTimer();
    }

    private void startTimer() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreenActivity.this, MainActivityView.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        }, 3000);
    }
}
