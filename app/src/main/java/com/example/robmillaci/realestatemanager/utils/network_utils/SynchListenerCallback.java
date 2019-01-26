package com.example.robmillaci.realestatemanager.utils.network_utils;

public interface SynchListenerCallback {
    void showProgressDialog();
    void updateProgressDialog(int count, String message);
    void dismissProgressDialog(boolean error);
}
