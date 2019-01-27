package com.example.robmillaci.realestatemanager.json_location_objects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Part of the JSON data response from {@link com.example.robmillaci.realestatemanager.web_services.GetDataService}
 */
public class Geometry {
    @SerializedName("location")
    @Expose
    private Location location;

    public Location getLocation() {
        return location;
    }
}