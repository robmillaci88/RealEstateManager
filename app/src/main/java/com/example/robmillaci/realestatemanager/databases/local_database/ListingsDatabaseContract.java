package com.example.robmillaci.realestatemanager.databases.local_database;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import static com.example.robmillaci.realestatemanager.databases.local_database.AppProvider.CONTENT_AUTHORITY;

/**
 * Database contract class containing the field names for Listings in both the local DB and firebase. Also contains the CONTENT_URI, CONTENT_TYPE and CONTENT_ITEM_TYPE
 * for the local database Listings table.
 */
public class ListingsDatabaseContract {
    //database fields
    static final String TABLE_NAME = "listings";
    static final String _ID = BaseColumns._ID;
    public static final String TYPE = "type";
    public static final String PRICE = "price";
    public static final String SURFACE_AREA = "surfaceArea";
    public static final String NUM_BED_ROOMS = "numbRooms";
    public static final String DESCRIPTION = "descr";
    static final String PHOTO1 = "photo1";
    static final String PHOTO2 = "photo2";
    static final String PHOTO3 = "photo3";
    static final String PHOTO4 = "photo4";
    static final String PHOTO5 = "photo5";
    static final String PHOTO6 = "photo6";
    static final String PHOTO7 = "photo7";
    static final String PHOTO8 = "photo8";
    static final String PHOTO9 = "photo9";
    static final String PHOTO10 = "photo10";
    static final String PHOTO11 = "photo11";
    static final String PHOTO12 = "photo12";
    static final String PHOTO13 = "photo13";
    static final String PHOTO14 = "photo14";
    static final String PHOTO15 = "photo15";
    public static final String PHOTO_DESCR = "photoDescr";
    public static final String ADDRESS_POSTCODE = "address_postcode";
    public static final String ADDRESS_STREET = "address_street";
    public static final String ADDRESS_NUMBER = "address_number";
    public static final String ADDRESS_TOWN = "address_town";
    public static final String ADDRESS_COUNTY = "address_county";
    public static final String POI = "poi";
    public static final String STATUS = "status";
    public static final String POSTED_DATE = "postDate";
    public static final String SALE_DATE = "saleDate";
    public static final String AGENT = "agent";
    public static final String UPDATE_TIME = "updateDateTime";
    public static final String BUY_LET = "buyLet";
    public static final String FIREBASE_IMAGE_URLS = "imageUrls";

    //sort order for the returned database data
    static final String SORT_ORDER_DESCENDING = " DESC";


    static final String[] ALL_LISTINGS_COLUMNS = {
            _ID, TYPE, PRICE, SURFACE_AREA, NUM_BED_ROOMS, DESCRIPTION, PHOTO1, PHOTO2, PHOTO3,
            PHOTO4, PHOTO5, PHOTO6, PHOTO7, PHOTO8, PHOTO9, PHOTO10, PHOTO11, PHOTO12, PHOTO13, PHOTO14, PHOTO15,
            PHOTO_DESCR, ADDRESS_POSTCODE, ADDRESS_STREET, ADDRESS_NUMBER, ADDRESS_TOWN
            , ADDRESS_COUNTY, POI, STATUS, POSTED_DATE, SALE_DATE, AGENT, UPDATE_TIME, BUY_LET
    };


    /**
     * The URI to access the listings table
     */
    static final Uri CONTENT_URI = Uri.withAppendedPath(AppProvider.CONTENT_AUTHORITY_URI, TABLE_NAME);

    static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + CONTENT_AUTHORITY + "." + TABLE_NAME;
    static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + CONTENT_AUTHORITY + "." + TABLE_NAME;

    //creates a URI from a listings ID
    static Uri buildTaskUri(long listingId) {
        return ContentUris.withAppendedId(CONTENT_URI, listingId);
    }

    //Returns a listings ID from a URI
    static long getListingsId(Uri uri) {
        return ContentUris.parseId(uri);
    }
}
