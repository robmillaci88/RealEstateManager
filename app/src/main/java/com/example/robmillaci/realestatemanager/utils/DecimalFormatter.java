package com.example.robmillaci.realestatemanager.utils;

import java.text.DecimalFormat;

public class DecimalFormatter {
    public static String formatNumber(int number){
        DecimalFormat formatter = new DecimalFormat("#,###,###");
        return formatter.format(number);
    }
}
