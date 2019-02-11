package com.example.robmillaci.realestatemanager.activities.splash_screen;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;

import com.example.robmillaci.realestatemanager.R;
import com.example.robmillaci.realestatemanager.activities.BaseActivity;
import com.example.robmillaci.realestatemanager.activities.main_activity.MainActivityView;

/**
 * A simple splash screen that is displayed to the user when the app starts.
 * Waits for 3 seconds before starting the main activity
 */
public class SplashScreenActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


        //the splash screen will maintain its portrait orientation even if we are on tablet mode
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        startTimer();
    }

    private void startTimer() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SplashScreenActivity.this.finish();
                startActivity(new Intent(SplashScreenActivity.this, MainActivityView.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        }, 3000);
    }

}
