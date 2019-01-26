package com.example.robmillaci.realestatemanager.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.example.robmillaci.realestatemanager.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {

        public static int convertDollarToEuro(int dollars){
            return (int) Math.round(dollars * 0.87);
        }

        public static int convertEuroToDollar(int euro){
            return (int) Math.round(euro * 1.15);
        }


        public static String getTodayDate(){
            @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("dd/MM/YYYY hh:mm:ssss");
            return dateFormat.format(new Date());
        }

        public static Date stringToDate(String s){
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ssss",Locale.getDefault());
            try {
                return sdf.parse(s);
            } catch (ParseException ex) {
                ex.printStackTrace();
            }
            return null;
        }


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


    public static boolean isTablet(Context c){
            return c.getResources().getBoolean(R.bool.isTablet);
    }
}
