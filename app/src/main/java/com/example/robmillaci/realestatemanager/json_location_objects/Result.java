package com.example.robmillaci.realestatemanager.json_location_objects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Part of the JSON data response from {@link com.example.robmillaci.realestatemanager.web_services.GetDataService}
 */
public class Result {
    @SerializedName("geometry")
    @Expose
    private Geometry geometry;

    public Geometry getGeometry() {
        return geometry;
    }
}