package com.example.robmillaci.realestatemanager.databases.local_database;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Create by Roberto Millaci 07/12/2018
 * Provider for this app.
 */
public class AppProvider extends android.content.ContentProvider {
    static final String CONTENT_AUTHORITY = "com.example.robmillaci.realestatemanager.provider";
    public static final Uri CONTENT_AUTHORITY_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private MyDatabase mMyDatabaseHelper;
    private static final UriMatcher sUriMatcher = buildURIMatcher();

    //static final ints for URI matcher return codes
    private static final int LISTINGS = 100;
    private static final int LISTINGS_ID = 101;
    private static final int USERS = 200;
    private static final int USERS_ID = 201;


    /**
     * Create a URI matcher that is used to determine how to process queries, inserts, deletes and updates
     * @return a UriMatcher object containing our Uri's
     */
    private static UriMatcher buildURIMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        //eg content://com.example.robmillaci.realestatemanager.provider/listings
        matcher.addURI(CONTENT_AUTHORITY, ListingsDatabaseContract.TABLE_NAME, LISTINGS);
        //eg content://com.example.robmillaci.realestatemanager.provider/listings/1
        matcher.addURI(CONTENT_AUTHORITY, ListingsDatabaseContract.TABLE_NAME + "/#", LISTINGS_ID);

        matcher.addURI(CONTENT_AUTHORITY, UserDatabaseContract.TABLE_NAME, USERS);

        matcher.addURI(CONTENT_AUTHORITY, UserDatabaseContract.TABLE_NAME + "/#", USERS_ID);

        return matcher;
    }

    //get an instance of our app's database. See .getInstance() method comments
    @Override
    public boolean onCreate() {
        mMyDatabaseHelper = MyDatabase.getInstance(getContext());
        return true;
    }


    /**
     * Query the database based on the recieved uri
     * @param uri the uri recieved to determine which table to query
     * @param projection the database columns to return
     * @param selection selection arguments formatted as a WHERE clause
     * @param selectionArgs the WHERE arguments
     * @param sortOrder the sort order of the returned data
     * @return a cursor containing the database records
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final int match = sUriMatcher.match(uri);

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        switch (match) {
            case LISTINGS:
                queryBuilder.setTables(ListingsDatabaseContract.TABLE_NAME);
                break;

            case LISTINGS_ID:
                queryBuilder.setTables(ListingsDatabaseContract.TABLE_NAME);
                long id = ListingsDatabaseContract.getListingsId(uri);
                queryBuilder.appendWhere(ListingsDatabaseContract._ID + " = " + id);
                break;

            case USERS:
                queryBuilder.setTables(UserDatabaseContract.TABLE_NAME);
                break;

            case USERS_ID:
                queryBuilder.setTables(UserDatabaseContract.TABLE_NAME);
                long userId = UserDatabaseContract.getListingsId(uri);
                queryBuilder.appendWhere(UserDatabaseContract._ID + " = " + userId);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        SQLiteDatabase db = mMyDatabaseHelper.getReadableDatabase();
        return queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
    }


    //Returns a String representing the content type based on the passed URI
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case LISTINGS:
                return ListingsDatabaseContract.CONTENT_TYPE;
            case LISTINGS_ID:
                return ListingsDatabaseContract.CONTENT_ITEM_TYPE;
            case USERS:
                return UserDatabaseContract.CONTENT_TYPE;
            case USERS_ID:
                return UserDatabaseContract.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("unknown URI: " + uri);
        }
    }



    /**
     * Insert into the database
     * @param uri the uri recieved used against our matcher to determine which table to insert into
     * @param values the values to insert
     */
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = sUriMatcher.match(uri);
        final SQLiteDatabase db;

        Uri returnUri;
        long recordId;


        switch (match) {
            case LISTINGS:
                db = mMyDatabaseHelper.getWritableDatabase();
                recordId = db.insert(ListingsDatabaseContract.TABLE_NAME, null, values);
                if (recordId >= 0) {
                    returnUri = ListingsDatabaseContract.buildTaskUri(recordId);
                } else {
                    throw new android.database.SQLException("Failed to insert into " + uri.toString());
                }
                break;

            case USERS:
                db = mMyDatabaseHelper.getWritableDatabase();
                recordId = db.insert(UserDatabaseContract.TABLE_NAME, null, values);
                if (recordId >= 0) {
                    returnUri = UserDatabaseContract.buildTaskUri(recordId);
                } else {
                    throw new android.database.SQLException("Failed to insert into " + uri.toString());
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }


        return returnUri;
    }


    /**
     * Delete from the database
     * @param uri the uri recieved used against our matcher to determine which table to insert into
     * @param selection selection arguments formatted as a WHERE clause
     * @param selectionArgs the WHERE arguments
     * @return the number of rows affected
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);

        final SQLiteDatabase db;
        int count;

        String selectionCriteria;

        switch (match) {
            case LISTINGS:
                db = mMyDatabaseHelper.getWritableDatabase();
                count = db.delete(ListingsDatabaseContract.TABLE_NAME, selection, selectionArgs);
                break;

            case LISTINGS_ID:
                db = mMyDatabaseHelper.getWritableDatabase();
                long taskId = ListingsDatabaseContract.getListingsId(uri);
                selectionCriteria = ListingsDatabaseContract._ID + " = " + taskId;

                if (selection != null && (selection.length() > 0)){
                    selectionCriteria += " AND (" + selection + ")";
                }

                count = db.delete(ListingsDatabaseContract.TABLE_NAME, selectionCriteria, selectionArgs);
                break;

            case USERS:
                db = mMyDatabaseHelper.getWritableDatabase();
                count = db.delete(UserDatabaseContract.TABLE_NAME, selection, selectionArgs);
                break;

            case USERS_ID:
                db = mMyDatabaseHelper.getWritableDatabase();
                long userTaskId = UserDatabaseContract.getListingsId(uri);
                selectionCriteria = UserDatabaseContract._ID + " = " + userTaskId;

                if (selection != null && (selection.length() > 0)){
                    selectionCriteria += " AND (" + selection + ")";
                }

                count = db.delete(UserDatabaseContract.TABLE_NAME, selectionCriteria, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        return count;
    }


    /**
     * Updates the database
     * @param uri the uri recieved used against our matcher to determine which table to update
     * @param values the update values
     * @param selection the SQL WHERE statement for which rows to update
     * @param selectionArgs the arguments for the WHERE statements
     * @return count of the rows affected
     */
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);

        final SQLiteDatabase db;
        int count;

        String selectionCriteria;

        switch (match) {
            case LISTINGS:
                db = mMyDatabaseHelper.getWritableDatabase();
                count = db.update(ListingsDatabaseContract.TABLE_NAME, values, selection, selectionArgs);
                break;

            case LISTINGS_ID:
                db = mMyDatabaseHelper.getWritableDatabase();
                long taskId = ListingsDatabaseContract.getListingsId(uri);
                selectionCriteria = ListingsDatabaseContract._ID + " = " + taskId;

                if (selection != null && (selection.length() > 0)){
                    selectionCriteria += " AND (" + selection + ")";
                }

                count = db.update(ListingsDatabaseContract.TABLE_NAME, values, selectionCriteria, selectionArgs);
                break;

            case USERS:
                db = mMyDatabaseHelper.getWritableDatabase();
                count = db.update(UserDatabaseContract.TABLE_NAME, values, selection, selectionArgs);
                break;

            case USERS_ID:
                db = mMyDatabaseHelper.getWritableDatabase();
                long userTaskId = UserDatabaseContract.getListingsId(uri);
                selectionCriteria = UserDatabaseContract._ID + " = " + userTaskId;

                if (selection != null && (selection.length() > 0)){
                    selectionCriteria += " AND (" + selection + ")";
                }

                count = db.update(UserDatabaseContract.TABLE_NAME, values, selectionCriteria, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        return count;
    }
}
