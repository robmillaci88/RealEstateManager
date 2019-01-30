package com.example.robmillaci.realestatemanager.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.text.Editable;
import android.util.Log;

import com.example.robmillaci.realestatemanager.R;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Utilities helper class
 */
@SuppressWarnings("unused")
public class Utils {


    //Converts dollars to Euro
    public static int convertDollarToEuro(int dollars) {
        return (int) Math.round(dollars * 0.87);
    }

    //Converts Euros to dollars
    public static int convertEuroToDollar(int euro) {
        return (int) Math.round(euro * 1.15);
    }


    //Returns today's date in the format dd/MM/YYYY hh:mm:ssss
    public static String getTodayDate() {
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("dd/MM/YYYY hh:mm:ssss");
        return dateFormat.format(new Date());
    }

    //Converts a date string into a Date and returns this date
    public static Date stringToDate(String s) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ssss", Locale.getDefault());
        try {
            return sdf.parse(s);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    //Checks the internet connectivity, returning true if their is network connection available
    public static boolean CheckConnectivity(final Context c) {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);

        //noinspection SimplifiableIfStatement
        if (mConnectivityManager != null) {
            return mConnectivityManager.getActiveNetworkInfo() != null
                    && mConnectivityManager.getActiveNetworkInfo().isAvailable()
                    && mConnectivityManager.getActiveNetworkInfo().isConnected();
        }
        return false;
    }


    //Returns whether a users device is a tablet or not
    public static boolean isTablet(Context c) {
        return c.getResources().getBoolean(R.bool.isTablet);
    }


    /**
     * Formats a given number returning the correct decimal formatting with comma separators
     * @param number the number to be formatted
     */
    public static String formatNumber(int number) {
        DecimalFormat formatter = new DecimalFormat("#,###,###");
        return formatter.format(number);
    }

    public static boolean isPasswordValid(Editable text) {
        //Password criteria
        //at least 8 characters
        //1 Upper case letter
        //1 number
        int numOfUpperLetters = 0;
        int numOfDigits = 0;
        boolean passwordLength = text.length() >= 8;

        byte[] bytes = text.toString().getBytes();
        for (byte tempByte : bytes) {

            char tempChar = (char) tempByte;
            if (Character.isDigit(tempChar)) {
                numOfDigits++;
            }

            if (Character.isUpperCase(tempChar)) {
                numOfUpperLetters++;
            }
        }
        Log.d("isPasswordValid", "isPasswordValid: length is " + passwordLength + " number of digits is " + numOfDigits + " number of upper case is " + numOfUpperLetters);
        return passwordLength && numOfDigits >= 1 && numOfUpperLetters >= 1;
    }
}
