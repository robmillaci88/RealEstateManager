package com.example.robmillaci.realestatemanager.utils.network_utils;

public interface DbSyncListener {
    void syncComplete();
    void updateProgressBarSyncProgress(int count);
}
