package com.example.robmillaci.realestatemanager.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.robmillaci.realestatemanager.data_objects.Listing;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;

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
import static com.example.robmillaci.realestatemanager.utils.network_utils.NetworkListener.LASTSYNCKEY;

public class SharedPreferenceHelper {
    private static final String LISTING_KEY = "listing";
    private static final String SHARED_PREFERENCES_NAME = "myprefs";
    private static final String FIRST_LOGIN = "firstLogin";


    private SharedPreferences sharedPreferences;

    public SharedPreferenceHelper(Context mContext) {
        sharedPreferences = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    @SuppressLint("ApplySharedPref")
    public void addListingToSharedPref(Listing listing) {
        Gson gson = new Gson();
        sharedPreferences.edit().putString(LISTING_KEY, gson.toJson(listing)).commit();
    }


    public Listing getListingFromSharedPrefs() {
        Gson gson = new Gson();
        Type type = new TypeToken<Listing>() {
        }.getType();

        return gson.fromJson(sharedPreferences.getString(LISTING_KEY, ""), type);
    }


    public HashMap<String, String> getUsersDetails() {
        HashMap<String, String> userDetails = new HashMap<>();

        userDetails.put(USER_TITLE, sharedPreferences.getString(USER_TITLE, ""));
        userDetails.put(USER_FORENAME, sharedPreferences.getString(USER_FORENAME, ""));
        userDetails.put(USER_SURNAME, sharedPreferences.getString(USER_SURNAME, ""));
        userDetails.put(USER_DOB, sharedPreferences.getString(USER_DOB, ""));
        userDetails.put(USER_POSTCODE, sharedPreferences.getString(USER_POSTCODE, ""));
        userDetails.put(USER_HOUSE_NAME_NUMBER, sharedPreferences.getString(USER_HOUSE_NAME_NUMBER, ""));
        userDetails.put(USER_HOUSE_STREET, sharedPreferences.getString(USER_HOUSE_STREET, ""));
        userDetails.put(USER_TOWN, sharedPreferences.getString(USER_TOWN, ""));
        userDetails.put(USER_COUNTY, sharedPreferences.getString(USER_COUNTY, ""));
        userDetails.put(USER_EMAIL, sharedPreferences.getString(USER_EMAIL, ""));
        userDetails.put(USER_HOME_NUMBER, sharedPreferences.getString(USER_HOME_NUMBER, ""));
        userDetails.put(USER_MOBILE, sharedPreferences.getString(USER_MOBILE, ""));
        userDetails.put(USER_PRIMARY_CONTACT_NUMBER, sharedPreferences.getString(USER_PRIMARY_CONTACT_NUMBER, ""));

        return userDetails;

    }


    @SuppressLint("ApplySharedPref")
    public void saveUserDetailsToSharedPreferences(String title, String forename, String surname, String dob, String postCode, String houseNameNumb,
                                                   String street, String town, String county, String homeNumber, String mobile, String primaryContactNumb) {

        SharedPreferences.Editor spEditor = sharedPreferences.edit();
        spEditor.putString(USER_TITLE, title);
        spEditor.putString(USER_FORENAME, forename);
        spEditor.putString(USER_SURNAME, surname);
        spEditor.putString(USER_DOB, dob);
        spEditor.putString(USER_POSTCODE, postCode);
        spEditor.putString(USER_HOUSE_NAME_NUMBER, houseNameNumb);
        spEditor.putString(USER_HOUSE_STREET, street);
        spEditor.putString(USER_TOWN, town);
        spEditor.putString(USER_COUNTY, county);
        spEditor.putString(USER_HOME_NUMBER, homeNumber);
        spEditor.putString(USER_MOBILE, mobile);
        spEditor.putString(USER_PRIMARY_CONTACT_NUMBER, primaryContactNumb);

        spEditor.commit();
    }


    @SuppressLint("ApplySharedPref")
    public void updateLastSyncDate() {
        SharedPreferences.Editor spEditor = sharedPreferences.edit();
        spEditor.putLong(LASTSYNCKEY, System.currentTimeMillis());
        spEditor.commit();
    }

    public long getLastSyncTime() {
        return sharedPreferences.getLong(LASTSYNCKEY, new Date(0L).getTime());
    }


    public boolean isFirstLogin() {
        return sharedPreferences.getBoolean(FIRST_LOGIN, true);
    }

    @SuppressLint("ApplySharedPref")
    public void setPreviousLogin() {
        sharedPreferences.edit().putBoolean(FIRST_LOGIN, false).commit();
    }
}
