package com.example.robmillaci.realestatemanager.databases.firebase;

/**
 * Contract class for Firebase field references
 */
public class FirebaseContract {
    static final String USER_DATABASE_COLLECTION_PATH = "users";
    static final String USER_DATABASE_NAME_FIELD = "username";
    static final String USER_DATABASE_EMAIL_FIELD = "userEmail";
    static final String USER_DATABASE_PICTURE_FIELD = "picture";
    static final String USER_DATABASE_UNIQUE_ID_FIELD = "uniqueID";
    public static final String USER_DATABASE_ISADMIN_FIELD = "isAdmin";
    static final String USER_DATABASE_TOKEN_PATH = "token";

    static final String LISTING_DATABASE_COLLECTION_PATH = "listings";
    public static final String USER_TITLE = "title";
    public static final String USER_FORENAME = "forename";
    public static final String USER_SURNAME = "surname";
    public static final String USER_DOB = "dob";
    public static final String USER_POSTCODE = "postcode";
    public static final String USER_HOUSE_NAME_NUMBER = "houseNameNumber";
    public static final String USER_HOUSE_STREET = "street";
    public static final String USER_TOWN = "town";
    public static final String USER_COUNTY = "county";
    public static final String USER_EMAIL = "email";
    public static final String USER_HOME_NUMBER = "homeNumber";
    public static final String USER_MOBILE = "mobile";
    public static final String USER_PRIMARY_CONTACT_NUMBER = "primary_contact_number";
    static final String IMAGE_URI_PATH = "imageUrls";
}
