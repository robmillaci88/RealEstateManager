package com.example.robmillaci.realestatemanager.activities;

import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.example.robmillaci.realestatemanager.utils.Utils;

/**
 * The base activity for <br/>{@link com.example.robmillaci.realestatemanager.activities.about_activity.AboutActivity}<br/>
 * {@link com.example.robmillaci.realestatemanager.activities.customer_account.AccountActivity}<br/>
 * {@link com.example.robmillaci.realestatemanager.activities.add_listing_activity.AddListingView}<br/>
 * {@link com.example.robmillaci.realestatemanager.activities.contact_activity.ContactActivity}<br/>
 * {@link com.example.robmillaci.realestatemanager.activities.full_screen_photo_activity.FullScreenPhotoActivity}<br/>
 * {@link com.example.robmillaci.realestatemanager.activities.main_activity.MainActivityView}<br/>
 * {@link com.example.robmillaci.realestatemanager.activities.offers_activities.MakeAnOffer}<br/>
 * {@link com.example.robmillaci.realestatemanager.activities.customer_account.PrivacySettingsActivity}<br/>
 * {@link com.example.robmillaci.realestatemanager.activities.customer_account.ProfileActivity}<br/>
 * {@link com.example.robmillaci.realestatemanager.activities.search_activity.SearchActivityView}<br/>
 * {@link com.example.robmillaci.realestatemanager.activities.splash_screen.SplashScreenActivity}<br/>
 * {@link com.example.robmillaci.realestatemanager.activities.search_activity.StreetViewActivity}<br/>
 * <p>
 * Sets the activities to full screen and if the users device is not a tablet, removes the screen rotation sensor
 */
public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Utils.immersiveMode(getWindow().getDecorView()); //sets the app's bottom system bar to auto hide to produce a more immersive experience



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        if (!Utils.isTablet(getApplicationContext())) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        }
    }


}
