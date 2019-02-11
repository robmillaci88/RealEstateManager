package com.example.robmillaci.realestatemanager.activities.customer_account;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;

import com.example.robmillaci.realestatemanager.R;
import com.example.robmillaci.realestatemanager.activities.BaseActivity;
import com.example.robmillaci.realestatemanager.custom_objects.RoundEditText;
import com.example.robmillaci.realestatemanager.databases.firebase.FirebaseHelper;
import com.example.robmillaci.realestatemanager.utils.SharedPreferenceHelper;
import com.example.robmillaci.realestatemanager.utils.Utils;
import com.jakewharton.rxbinding3.view.RxView;

import java.util.HashMap;

import io.reactivex.functions.Consumer;
import kotlin.Unit;

import static com.example.robmillaci.realestatemanager.databases.firebase.FirebaseContract.USER_COUNTY;
import static com.example.robmillaci.realestatemanager.databases.firebase.FirebaseContract.USER_DOB;
import static com.example.robmillaci.realestatemanager.databases.firebase.FirebaseContract.USER_EMAIL;
import static com.example.robmillaci.realestatemanager.databases.firebase.FirebaseContract.USER_FORENAME;
import static com.example.robmillaci.realestatemanager.databases.firebase.FirebaseContract.USER_HOME_NUMBER;
import static com.example.robmillaci.realestatemanager.databases.firebase.FirebaseContract.USER_HOUSE_NAME_NUMBER;
import static com.example.robmillaci.realestatemanager.databases.firebase.FirebaseContract.USER_HOUSE_STREET;
import static com.example.robmillaci.realestatemanager.databases.firebase.FirebaseContract.USER_MOBILE;
import static com.example.robmillaci.realestatemanager.databases.firebase.FirebaseContract.USER_POSTCODE;
import static com.example.robmillaci.realestatemanager.databases.firebase.FirebaseContract.USER_PRIMARY_CONTACT_NUMBER;
import static com.example.robmillaci.realestatemanager.databases.firebase.FirebaseContract.USER_SURNAME;
import static com.example.robmillaci.realestatemanager.databases.firebase.FirebaseContract.USER_TITLE;
import static com.example.robmillaci.realestatemanager.databases.firebase.FirebaseContract.USER_TOWN;

/**
 * This class is responsible for the users profile
 */
@SuppressWarnings({"ResultOfMethodCallIgnored", "FieldCanBeLocal"})
public class ProfileActivity extends BaseActivity implements IUserDetailsCallback {
    private RoundEditText mUserTitle; //the title of the user
    private RoundEditText mUserForename; //the forename of the user
    private RoundEditText mUserSurname; //the surname of the user
    private RoundEditText mUserDob; //the users DOB
    private RoundEditText mUserPostcode; //the users postcode
    private RoundEditText mUserHouseNameNumb; //the users house number
    private RoundEditText mUserStreet; //the users house address street
    private RoundEditText mUserTown; //the users house address street
    @SuppressWarnings("FieldCanBeLocal")
    private RoundEditText mUserEmail; //the users email
    private RoundEditText mUserCounty;//the users house address county
    private RoundEditText mUserHomeNumb; // the users home phone number
    private RoundEditText mUserMobileNumb; //the users mobile number
    private RoundEditText mUserPrimaryContactNumb; //the users primary contact number
    private Button mSaveBtn; //the save button


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Your Profile");

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        Utils.removeImmersiveMode(getWindow().getDecorView());

        initializeViews();
        setOnClicks();
        restoreValues();
    }


    @SuppressLint("CheckResult")
    private void setOnClicks() {
        RxView.clicks(mSaveBtn).subscribe(new Consumer<Unit>() {
            @Override
            public void accept(Unit unit) {
                saveValues();
                onBackPressed();
            }
        });
    }

    /**
     * Restore any users values stored in Firebase (if we are online) or shared preferences if we are offline
     */
    private void restoreValues() {
        if (Utils.CheckConnectivity(ProfileActivity.this)) {
            FirebaseHelper.getUsersDetails(this);
        } else {
            HashMap<String, String> userDetails = new SharedPreferenceHelper(getApplicationContext()).getUsersDetails();
            setUserDetails(userDetails);
        }
    }


    /**
     * Update the profile views with the users details returned from either shared preferences or {@link IUserDetailsCallback#gotUserDetails(HashMap)}
     *
     * @param userDetails the returned user information
     */
    private void setUserDetails(HashMap<String, String> userDetails) {
        mUserTitle.setText(userDetails.get(USER_TITLE));
        mUserForename.setText(userDetails.get(USER_FORENAME));
        mUserSurname.setText(userDetails.get(USER_SURNAME));
        mUserDob.setText(userDetails.get(USER_DOB));
        mUserPostcode.setText(userDetails.get(USER_POSTCODE));
        mUserHouseNameNumb.setText(userDetails.get(USER_HOUSE_NAME_NUMBER));
        mUserStreet.setText(userDetails.get(USER_HOUSE_STREET));
        mUserTown.setText(userDetails.get(USER_TOWN));
        mUserHomeNumb.setText(userDetails.get(USER_HOME_NUMBER));
        mUserMobileNumb.setText(userDetails.get(USER_MOBILE));
        mUserPrimaryContactNumb.setText(userDetails.get(USER_PRIMARY_CONTACT_NUMBER));
        mUserCounty.setText(userDetails.get(USER_COUNTY));
        mUserEmail.setText(userDetails.get(USER_EMAIL));
    }


    /**
     * save the users values to shared preferences and to firebase
     */
    @SuppressWarnings("ConstantConditions")
    private void saveValues() {
        SharedPreferenceHelper spHelper = new SharedPreferenceHelper(getApplicationContext());

        String title = mUserTitle.getText() != null ? mUserTitle.getText().toString() : "";
        String forename = mUserForename.getText() != null ? mUserForename.getText().toString() : "";
        String surname = mUserSurname.getText() != null ? mUserSurname.getText().toString() : "";
        String dob = mUserDob.getText() != null ? mUserDob.getText().toString() : "";
        String postCode = mUserPostcode.getText() != null ? mUserPostcode.getText().toString() : "";
        String houseNameNumb = mUserHouseNameNumb.getText() != null ? mUserHouseNameNumb.getText().toString() : "";
        String street = mUserStreet.getText() != null ? mUserStreet.getText().toString() : "";
        String town = mUserTown.getText() != null ? mUserTown.getText().toString() : "";
        String county = mUserCounty.getText() != null ? mUserCounty.getText().toString() : "";
        String homeNumber = mUserHomeNumb.getText() != null ? mUserHomeNumb.getText().toString() : "";
        String mobile = mUserMobileNumb.getText() != null ? mUserMobileNumb.getText().toString() : "";
        String primaryContactNumb = mUserPrimaryContactNumb.getText() != null ? mUserPrimaryContactNumb.getText().toString() : "";
        String userEmail = mUserEmail.getText() != null ? mUserEmail.getText().toString() : "";

        spHelper.saveUserDetailsToSharedPreferences(title, forename, surname, dob, postCode, houseNameNumb, street, town, county, homeNumber, mobile, primaryContactNumb,userEmail);

        FirebaseHelper.updateUserProfileDetails(new SharedPreferenceHelper(getApplicationContext()).getUsersDetails());

        onBackPressed();
    }

    private void initializeViews() {
        mUserTitle = findViewById(R.id.title_et);
        mUserForename = findViewById(R.id.forename_et);
        mUserSurname = findViewById(R.id.surname_et);
        mUserDob = findViewById(R.id.dob_et);
        mUserPostcode = findViewById(R.id.postcode_et);
        mUserHouseNameNumb = findViewById(R.id.housename_numb_et);
        mUserStreet = findViewById(R.id.street_et);
        mUserTown = findViewById(R.id.town_et);
        mUserEmail = findViewById(R.id.email_et);
        mUserHomeNumb = findViewById(R.id.home_phone_et);
        mUserMobileNumb = findViewById(R.id.mobile_num_et);
        mUserPrimaryContactNumb = findViewById(R.id.primary_contact_num_et);
        mUserCounty = findViewById(R.id.county_et);

        mSaveBtn = findViewById(R.id.savebtn);
    }


    /**
     * Callback method from {@link FirebaseHelper#getUsersDetails(IUserDetailsCallback)}
     *
     * @param userDetails the returned user details. This method then passes the user details to {@link #setUserDetails(HashMap)}
     */
    @Override
    public void gotUserDetails(HashMap<String, String> userDetails) {
        setUserDetails(userDetails);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(ProfileActivity.this, AccountActivity.class));
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        //prevents the super method being called as this activity has immersive mode disabled
    }
}


