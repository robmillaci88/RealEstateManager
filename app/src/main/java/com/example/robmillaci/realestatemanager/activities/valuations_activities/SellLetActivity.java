package com.example.robmillaci.realestatemanager.activities.valuations_activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.robmillaci.realestatemanager.R;
import com.example.robmillaci.realestatemanager.custom_objects.RoundEditText;
import com.example.robmillaci.realestatemanager.utils.SharedPreferenceHelper;
import com.example.robmillaci.realestatemanager.utils.ToastModifications;
import com.jakewharton.rxbinding3.view.RxView;

import java.util.ArrayList;
import java.util.HashMap;

import io.reactivex.functions.Consumer;
import kotlin.Unit;

import static com.example.robmillaci.realestatemanager.databases.firebase.FirebaseContract.USER_COUNTY;
import static com.example.robmillaci.realestatemanager.databases.firebase.FirebaseContract.USER_HOME_NUMBER;
import static com.example.robmillaci.realestatemanager.databases.firebase.FirebaseContract.USER_HOUSE_STREET;
import static com.example.robmillaci.realestatemanager.databases.firebase.FirebaseContract.USER_POSTCODE;
import static com.example.robmillaci.realestatemanager.databases.firebase.FirebaseContract.USER_SURNAME;
import static com.example.robmillaci.realestatemanager.databases.firebase.FirebaseContract.USER_TOWN;

/**
 * This class is responsible for the SellLetActivity
 */
public class SellLetActivity extends AppCompatActivity {
    private Button mContinueBtn;
    private RoundEditText mPostcodeEt;
    private RoundEditText mHouseNameEt;
    private RoundEditText mStreetEt;
    private RoundEditText mTownEt;
    private RoundEditText mCountyEt;
    private ArrayList<EditText> activity_views;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_let);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle b = getIntent().getExtras(); //get the bundle passed to this intent to establish wether we are booking a letting evaluation or a selling evaluation
        if (b != null) {
            String evaluationType = b.getString(BookEvaluationActivity.TYPE_KEY, "");
            switch (evaluationType) {
                case BookEvaluationActivity.LETTING_TYPE: //if letting type set the title to let a property
                    setTitle(getString(R.string.let_property_title));
                    break;

                case BookEvaluationActivity.SELLING_TYPE: //if selling type, set the title to sell a property
                    setTitle(getString(R.string.sell_property_title));
                    break;
            }
        }

        initializeViews(); //initialize the views
        restoreValuesFromProfile(); //restore any values set in the user profile
        setOnClicks(); //create the on click listeners for this activity
    }


    private void initializeViews() {
        activity_views = new ArrayList<>(); //create an arraylist to hold all views so we can loop through to check none are let empty by the user

        mContinueBtn = findViewById(R.id.continue_btn);

        mPostcodeEt = findViewById(R.id.postcode_et);
        mHouseNameEt = findViewById(R.id.surname_et);
        mStreetEt = findViewById(R.id.street_et);
        mTownEt = findViewById(R.id.town_et);
        mCountyEt = findViewById(R.id.county_et);

        activity_views.add(mPostcodeEt);
        activity_views.add(mHouseNameEt);
        activity_views.add(mStreetEt);
        activity_views.add(mTownEt);
        activity_views.add(mCountyEt);
    }


    /**
     * Checks shared preferences for any values the user has already entered into their profile, and update the views if appropriate
     */
    private void restoreValuesFromProfile() {
        HashMap<String, String> userDetails = new SharedPreferenceHelper(SellLetActivity.this).getUsersDetails();

        mPostcodeEt.setText(userDetails.get(USER_POSTCODE) != null ? userDetails.get(USER_POSTCODE) : "");
        mHouseNameEt.setText(userDetails.get(USER_HOME_NUMBER) != null ? userDetails.get(USER_SURNAME) : "");
        mStreetEt.setText(userDetails.get(USER_HOUSE_STREET) != null ? userDetails.get(USER_HOUSE_STREET) : "");
        mTownEt.setText(userDetails.get(USER_TOWN) != null ? userDetails.get(USER_TOWN) : "");
        mCountyEt.setText(userDetails.get(USER_COUNTY) != null ? userDetails.get(USER_COUNTY) : "");

    }

    @SuppressLint("CheckResult")
    private void setOnClicks() {
        //noinspection ResultOfMethodCallIgnored
        RxView.clicks(mContinueBtn)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) {
                        if (canWeConfirm()) {
                            Intent sellLetConfirmation = new Intent(getApplicationContext(), ConfirmationActivity.class);
                            sellLetConfirmation.putExtra(ConfirmationActivity.BUNDLE_KEY, ConfirmationActivity.CALLED_FROM_VALUATION);
                            startActivity(sellLetConfirmation);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        } else {
                            ToastModifications.createToast(SellLetActivity.this, getString(R.string.sell_let_missing_vals), Toast.LENGTH_LONG);
                        }
                    }
                });
    }


    /**
     * Looks through the edit text array list to see if any have been left empty.
     * Only returns true to continue if all field values are populated
     *
     * @return the response to whether we can confirm the booking to the user or not.
     */
    private boolean canWeConfirm() {
        boolean confirm = true;
        for (EditText et : activity_views) {
            if (et.getText().toString().equals("")) {
                confirm = false;
            }
        }
        return confirm;
    }


}
