package com.example.robmillaci.realestatemanager.activities.contact_activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.robmillaci.realestatemanager.R;
import com.example.robmillaci.realestatemanager.activities.BaseActivity;

/**
 * This class is responsible for the contact activity
 */
public class ContactActivity extends BaseActivity implements View.OnClickListener {
    private Button enquiries_call_btn; //the button for calling the enquiries
    private Button sold_call_button; //the button for calling sold properties
    private Button lettings_call_btn; //the button for calling lettings
    private Button enquiries_email_btn;//the button for emailing enquiries
    private Button sold_email_btn;//the button for emailing sold properties
    private Button lettings_email_btn;//the button for emailing lettings

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        setTitle(getString(R.string.contact_activity_title));
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
        enquiries_call_btn = findViewById(R.id.enquiries_call_btn);
        sold_call_button = findViewById(R.id.sold_call_button);
        lettings_call_btn = findViewById(R.id.lettings_call_btn);
        enquiries_email_btn = findViewById(R.id.enquiries_email_btn);
        sold_email_btn = findViewById(R.id.sold_email_btn);
        lettings_email_btn = findViewById(R.id.lettings_email_btn);
    }

    private void initializeButtonClicks() {
        enquiries_call_btn.setOnClickListener(this);
        sold_call_button.setOnClickListener(this);
        lettings_call_btn.setOnClickListener(this);
        enquiries_email_btn.setOnClickListener(this);
        sold_email_btn.setOnClickListener(this);
        lettings_email_btn.setOnClickListener(this);
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

}
