package com.example.robmillaci.realestatemanager.web_services;

import com.example.robmillaci.realestatemanager.json_location_objects.LocationObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetDataService {
    @GET("/maps/api/geocode/json?")
    Call<LocationObject> getDetails(
            @Query("address") String placeAddress,
            @Query("key") String apiKey);
}
