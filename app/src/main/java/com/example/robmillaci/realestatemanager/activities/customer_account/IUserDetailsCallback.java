package com.example.robmillaci.realestatemanager.activities.customer_account;

import java.util.HashMap;

/**
 * Interface that must be implemented by any class that is requesting user details from Firebase
 */
public interface IUserDetailsCallback {
    void gotUserDetails(HashMap<String,String> userDetails);
}
