package com.example.robmillaci.realestatemanager.web_services;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {
    private static Retrofit retrofit;

    private static final String BASE_URL = "https://maps.googleapis.com/";
    private static final String GOOGLE_API_KEY = "AIzaSyCTLuMXfUowBd37AbdHvM9kSQIlmmlHkR4";


    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static String getGoogleAPIKey() {
        return GOOGLE_API_KEY;
    }
}
