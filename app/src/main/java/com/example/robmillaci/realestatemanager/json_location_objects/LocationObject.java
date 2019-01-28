package com.example.robmillaci.realestatemanager.json_location_objects;

import android.support.annotation.NonNull;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * Part of the JSON data response from {@link com.example.robmillaci.realestatemanager.web_services.GetDataService}
 */
public class LocationObject {

    @SerializedName("results")
    @Expose
    private List<Result> results = null;
    @SerializedName("status")
    @Expose
    private String status;

    public List<Result> getResults() {
        return results;
    }

    @NonNull
    @Override
    public String toString() {
        return status;
    }
}