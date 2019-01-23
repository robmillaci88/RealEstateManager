package com.example.robmillaci.realestatemanager.utils.network_utils;

public interface SynchListenerCallback {
    void showProgressDialog();
    void updateProgressDialog(int count);
    void dismissProgressDialog();
}
