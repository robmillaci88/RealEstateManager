package com.example.robmillaci.realestatemanager.activities.contact_activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.robmillaci.realestatemanager.R;
import com.example.robmillaci.realestatemanager.activities.BaseActivity;
import com.example.robmillaci.realestatemanager.utils.Utils;

/**
 * This class is responsible for the contact activity
 */
public class ContactActivity extends BaseActivity implements View.OnClickListener {
    private Button mEnquiriesCallBtn; //the button for calling the enquiries
    private Button mSoldCallButton; //the button for calling sold properties
    private Button mLettingsCallBtn; //the button for calling lettings
    private Button mEnquiriesEmailBtn;//the button for emailing enquiries
    private Button mSoldEmailBtn;//the button for emailing sold properties
    private Button mLettingsEmailBtn;//the button for emailing lettings

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        setTitle(getString(R.string.contact_activity_title));
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Utils.removeImmersiveMode(getWindow().getDecorView());

        initializeButtons();
        initializeButtonClicks();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }

        return true;
    }

    private void initializeButtons() {
        mEnquiriesCallBtn = findViewById(R.id.enquiries_call_btn);
        mSoldCallButton = findViewById(R.id.sold_call_button);
        mLettingsCallBtn = findViewById(R.id.lettings_call_btn);
        mEnquiriesEmailBtn = findViewById(R.id.enquiries_email_btn);
        mSoldEmailBtn = findViewById(R.id.sold_email_btn);
        mLettingsEmailBtn = findViewById(R.id.lettings_email_btn);
    }

    private void initializeButtonClicks() {
        mEnquiriesCallBtn.setOnClickListener(this);
        mSoldCallButton.setOnClickListener(this);
        mLettingsCallBtn.setOnClickListener(this);
        mEnquiriesEmailBtn.setOnClickListener(this);
        mSoldEmailBtn.setOnClickListener(this);
        mLettingsEmailBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getTag().toString()) {
            case "call":
                call(((Button) v).getText().toString()); //create a call intent
                break;

            case "email":
                email(); //create an email intent
                break;
        }
    }

    private void call(String number) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + number));
        startActivity(intent);
    }

    private void email() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", "MillApp1@hotmail.com", null));
        startActivity(Intent.createChooser(emailIntent, getString(R.string.send_email)));
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        //prevents the super method being called as this activity has immersive mode disabled
    }
}
