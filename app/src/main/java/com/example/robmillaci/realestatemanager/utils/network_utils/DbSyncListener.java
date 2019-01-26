package com.example.robmillaci.realestatemanager.utils.network_utils;

public interface DbSyncListener {
    void syncComplete(boolean error);
    void updateProgressBarFirebaseSync(int count, String message);
}
