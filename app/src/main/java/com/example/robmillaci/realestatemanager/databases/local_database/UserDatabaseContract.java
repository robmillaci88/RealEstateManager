package com.example.robmillaci.realestatemanager.databases.local_database;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import static com.example.robmillaci.realestatemanager.databases.local_database.AppProvider.CONTENT_AUTHORITY;

/**
 * Database contract class containing the field names for the users table in both the local DB and firebase. Also contains the CONTENT_URI, CONTENT_TYPE and CONTENT_ITEM_TYPE
 * for the local database users table.
 */
class UserDatabaseContract {
     static final String _ID = BaseColumns._ID;
     static final String TABLE_NAME = "user";
     static final String TITLE = "title";
     static final String FORENAME = "forename";
     static final String SURNAME = "surname";
     static final String DOB = "dob";
     static final String POSTCODE = "postcode";
     static final String HOUSENUMBER = "houseNumber";
     static final String STREET = "street";
     static final String TOWN = "town";
     static final String COUNTY = "county";
     static final String EMAIL = "email";
     static final String HOME_PHONE = "homePhone";
     static final String MOBILE = "mobileNo";
     static final String PRIMARY_CONTACT = "primaryContact";
     static final String AUTHENTICATED = "authenticated";

    /**
     * The URI to access the users table
     */
    private static final Uri CONTENT_URI = Uri.withAppendedPath(AppProvider.CONTENT_AUTHORITY_URI, TABLE_NAME);


    static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + CONTENT_AUTHORITY + "." + TABLE_NAME;
    static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + CONTENT_AUTHORITY + "." + TABLE_NAME;

     static Uri buildTaskUri(long recordId) {
        return ContentUris.withAppendedId(CONTENT_URI, recordId);
    }

    static long getListingsId(Uri uri) {
        return ContentUris.parseId(uri);
    }
}
