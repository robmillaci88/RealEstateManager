package com.example.robmillaci.realestatemanager.utils.network_utils;

public interface SynchListenerCallback {
    void showProgressDialog();

    void dismissProgressDialog(boolean error);
}
