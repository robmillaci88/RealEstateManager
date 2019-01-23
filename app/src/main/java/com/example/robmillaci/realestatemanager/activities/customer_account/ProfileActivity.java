package com.example.robmillaci.realestatemanager.activities.customer_account;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.robmillaci.realestatemanager.R;
import com.example.robmillaci.realestatemanager.activities.BaseActivity;
import com.example.robmillaci.realestatemanager.databases.firebase.FirebaseHelper;
import com.example.robmillaci.realestatemanager.utils.SharedPreferenceHelper;
import com.jakewharton.rxbinding3.view.RxView;

import java.util.HashMap;

import io.reactivex.functions.Consumer;
import kotlin.Unit;

import static com.example.robmillaci.realestatemanager.databases.firebase.FirebaseContract.USER_COUNTY;
import static com.example.robmillaci.realestatemanager.databases.firebase.FirebaseContract.USER_DOB;
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

public class ProfileActivity extends BaseActivity {
    EditText user_title;
    EditText user_forename;
    EditText user_surname;
    EditText user_dob;
    EditText user_postcode;
    EditText user_houseNameNumb;
    EditText user_street;
    EditText user_town;
    EditText user_email;
    EditText user_county;
    EditText user_home_numb;
    EditText user_mobile_numb;
    EditText user_primary_contact_numb;
    Button saveBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Your Profile");

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initializeViews();
        setOnClicks();
        restoreValues();
    }


    @SuppressLint("CheckResult")
    private void setOnClicks() {
        RxView.clicks(saveBtn).subscribe(new Consumer<Unit>() {
            @Override
            public void accept(Unit unit){
                saveValues();
                onBackPressed();
            }
        });
    }

    private void restoreValues() {
        HashMap<String,String> userDetails =  new SharedPreferenceHelper(getApplicationContext()).getUsersDetails();

        user_title.setText(userDetails.get(USER_TITLE));
        user_forename.setText(userDetails.get(USER_FORENAME));
        user_surname.setText(userDetails.get(USER_SURNAME));
        user_dob.setText(userDetails.get(USER_DOB));
        user_postcode.setText(userDetails.get(USER_POSTCODE));
        user_houseNameNumb.setText(userDetails.get(USER_HOUSE_NAME_NUMBER));
        user_street.setText(userDetails.get(USER_HOUSE_STREET));
        user_town.setText(userDetails.get(USER_TOWN));
        user_home_numb.setText(userDetails.get(USER_HOME_NUMBER));
        user_mobile_numb.setText(userDetails.get(USER_MOBILE));
        user_primary_contact_numb.setText(userDetails.get(USER_PRIMARY_CONTACT_NUMBER));
        user_county.setText(userDetails.get(USER_COUNTY));

    }

    private void saveValues() {
        SharedPreferenceHelper spHelper = new SharedPreferenceHelper(getApplicationContext());

        String title = user_title.getText().toString();
        String forename = user_forename.getText().toString();
        String surname = user_surname.getText().toString();
        String dob = user_dob.getText().toString();
        String postCode = user_postcode.getText().toString();
        String houseNameNumb = user_houseNameNumb.getText().toString();
        String street = user_street.getText().toString();
        String town = user_town.getText().toString();
        String county = user_county.getText().toString();
        String homeNumber = user_home_numb.getText().toString();
        String mobile = user_mobile_numb.getText().toString();
        String primaryContactNumb = user_primary_contact_numb.getText().toString();

        spHelper.saveUserDetailsToSharedPreferences(title,forename,surname,dob,postCode,houseNameNumb,street,town,county,homeNumber,mobile,primaryContactNumb);

        FirebaseHelper.updateUserProfileDetails(new SharedPreferenceHelper(getApplicationContext()).getUsersDetails());
    }

    private void initializeViews() {
        user_title = findViewById(R.id.title_et);
        user_forename = findViewById(R.id.forename_et);
        user_surname = findViewById(R.id.surname_et);
        user_dob = findViewById(R.id.dob_et);
        user_postcode = findViewById(R.id.postcode_et);
        user_houseNameNumb = findViewById(R.id.housename_numb_et);
        user_street = findViewById(R.id.street_et);
        user_town = findViewById(R.id.town_et);

        user_email = findViewById(R.id.email_et);
        user_email.setFocusable(false);
        user_email.setTextIsSelectable(false);

        user_home_numb = findViewById(R.id.home_phone_et);
        user_mobile_numb = findViewById(R.id.mobile_num_et);
        user_primary_contact_numb = findViewById(R.id.primary_contact_num_et);
        user_county = findViewById(R.id.county_et);

        saveBtn=findViewById(R.id.savebtn);
    }
}
