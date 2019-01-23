package com.example.robmillaci.realestatemanager.databases.local_database;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import static com.example.robmillaci.realestatemanager.databases.local_database.AppProvider.CONTENT_AUTHORITY;

public class UserDatabaseContract {
    public static final String _ID = BaseColumns._ID;
    public static final String TABLE_NAME = "user";
    public static final String TITLE = "title";
    public static final String FORENAME = "forename";
    public static final String SURNAME = "surname";
    public static final String DOB = "dob";
    public static final String POSTCODE = "postcode";
    public static final String HOUSENUMBER = "houseNumber";
    public static final String STREET = "street";
    public static final String TOWN = "town";
    public static final String COUNTY = "county";
    public static final String EMAIL = "email";
    public static final String HOME_PHONE = "homePhone";
    public static final String MOBILE = "mobileNo";
    public static final String PRIMARY_CONTACT = "primaryContact";
    public static final String AUTHENTICATED = "authenticated";

    /**
     * The URI to access the listings table
     */
    private static final Uri CONTENT_URI = Uri.withAppendedPath(AppProvider.CONTENT_AUTHORITY_URI, TABLE_NAME);

    static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + CONTENT_AUTHORITY + "." + TABLE_NAME;
    static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + CONTENT_AUTHORITY + "." + TABLE_NAME;

    public static Uri buildTaskUri(long listingId) {
        return ContentUris.withAppendedId(CONTENT_URI, listingId);
    }

    static long getListingsId(Uri uri) {
        return ContentUris.parseId(uri);
    }
}
