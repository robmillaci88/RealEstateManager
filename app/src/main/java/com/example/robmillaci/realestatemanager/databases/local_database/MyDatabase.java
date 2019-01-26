package com.example.robmillaci.realestatemanager.databases.local_database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;

import com.example.robmillaci.realestatemanager.R;
import com.example.robmillaci.realestatemanager.activities.search_activity.SearchActivityView;
import com.example.robmillaci.realestatemanager.data_objects.Listing;
import com.example.robmillaci.realestatemanager.utils.Utils;
import com.example.robmillaci.realestatemanager.utils.image_tools.ImageTools;
import com.example.robmillaci.realestatemanager.utils.network_utils.DbSyncListener;

import java.util.ArrayList;
import java.util.Date;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

import static com.example.robmillaci.realestatemanager.databases.local_database.ListingsDatabaseContract.ADDRESS_COUNTY;
import static com.example.robmillaci.realestatemanager.databases.local_database.ListingsDatabaseContract.ADDRESS_POSTCODE;
import static com.example.robmillaci.realestatemanager.databases.local_database.ListingsDatabaseContract.ADDRESS_STREET;
import static com.example.robmillaci.realestatemanager.databases.local_database.ListingsDatabaseContract.ADDRESS_TOWN;
import static com.example.robmillaci.realestatemanager.databases.local_database.ListingsDatabaseContract.BUY_LET;
import static com.example.robmillaci.realestatemanager.databases.local_database.ListingsDatabaseContract.NUM_BED_ROOMS;
import static com.example.robmillaci.realestatemanager.databases.local_database.ListingsDatabaseContract.PRICE;
import static com.example.robmillaci.realestatemanager.databases.local_database.ListingsDatabaseContract.SORT_ORDER_DESCENDING;
import static com.example.robmillaci.realestatemanager.databases.local_database.ListingsDatabaseContract.STATUS;
import static com.example.robmillaci.realestatemanager.databases.local_database.ListingsDatabaseContract.TYPE;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Basic database class for the application. The only class that should use this is the app provider class<br>
 * Implemented as a singleton.
 */
public class MyDatabase extends SQLiteOpenHelper{
    private static final String DATABASE_NAME = "listings.db";
    private static final int DATABASE_VERSION = 1;

    private static final int ID_COLUMN_DATA = 0;
    private static final int TYPE_COLUMN_DATA = 1;
    private static final int PRICE_COLUMN_DATA = 2;
    private static final int SURFACEAREA_COLUMN_DATA = 3;
    private static final int NUM_BEDROOMS_COLUMN_DATA = 4;
    private static final int DESCR_COLUMN_DATA = 5;
    private static final int PHOTODESC_COLUMN_DATA = 21;
    private static final int ADDRESS_POSTCODE_COLUMN_DATA = 22;
    private static final int ADDRESS_STREET_COLUMN_DATA = 23;
    private static final int ADDRESS_NUMBER_COLUMN_DATA = 24;
    private static final int ADDRESS_TOWN_COLUMN_DATA = 25;
    private static final int ADDRESS_COUNTY_COLUMN_DATA = 26;
    private static final int POI_COLUMN_DATA = 27;
    private static final int AVAILABLE_COLUMN_DATA = 28;
    private static final int POSTED_DATE_COLUMN_DATA = 29;
    private static final int SALE_DATE_COLUMN_DATA = 30;
    private static final int AGENT_COLUMN_DATA = 31;
    private static final int LASTUPDATE_COLUMN_DATA = 32;
    private static final int BUY_OR_LET_COLUMN_DATA = 33;

    public static final String NO_MAX_VALUE = "No Max";
    public static final String NO_MIN_VALUE = "No Min";
    private static final String RESULT_SOLD = "true";
    private static final String RESULT_FOR_SALE = "true";

    private static final int PHOTOS_START_INDEX = 6;
    private static final int PHOTOS_END_INDEX = 20;

    private static MyDatabase instance = null;
    private Model mPresenter;

    private DbSyncListener mDbSyncListener;

    private MyDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    /**
     * Get an instance of the apps singleton database helper object
     *
     * @param context the content providers context
     * @return a SQlite database helper object
     */
    public static MyDatabase getInstance(Context context) {
        if (instance == null) {
            instance = new MyDatabase(context.getApplicationContext());
        }
        return instance;
    }

    public MyDatabase setPresenter(Model presenter) {
        this.mPresenter = presenter;
        return this;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        createListingsTable(db);
        createUserTable(db);
    }


    private void createUserTable(SQLiteDatabase db) {
        String createStatement = "CREATE TABLE IF NOT EXISTS " + UserDatabaseContract.TABLE_NAME +
                "(" +
                UserDatabaseContract._ID + " INTEGER PRIMARY KEY NOT NULL, " +
                UserDatabaseContract.TITLE + " TEXT, " +
                UserDatabaseContract.FORENAME + " TEXT, " +
                UserDatabaseContract.SURNAME + " TEXT, " +
                UserDatabaseContract.DOB + " TEXT, " +
                UserDatabaseContract.POSTCODE + " TEXT, " +
                UserDatabaseContract.HOUSENUMBER + " TEXT, " +
                UserDatabaseContract.STREET + " TEXT, " +
                UserDatabaseContract.TOWN + " TEXT, " +
                UserDatabaseContract.COUNTY + " TEXT, " +
                UserDatabaseContract.EMAIL + " TEXT, " +
                UserDatabaseContract.HOME_PHONE + " TEXT, " +
                UserDatabaseContract.MOBILE + " TEXT, " +
                UserDatabaseContract.PRIMARY_CONTACT + " TEXT, " +
                UserDatabaseContract.AUTHENTICATED + " TEXT "
                + ");";

        db.execSQL(createStatement);
    }


    private void createListingsTable(SQLiteDatabase db) {
        String createStatement = "CREATE TABLE IF NOT EXISTS " + ListingsDatabaseContract.TABLE_NAME +
                "(" +
                ListingsDatabaseContract._ID + " TEXT PRIMARY KEY NOT NULL, " +
                TYPE + " TEXT, " +
                ListingsDatabaseContract.PRICE + " DOUBLE, " +
                ListingsDatabaseContract.SURFACE_AREA + " DOUBLE, " +
                ListingsDatabaseContract.NUM_BED_ROOMS + " INTEGER, " +
                ListingsDatabaseContract.DESCRIPTION + " TEXT, " +
                ListingsDatabaseContract.PHOTO1 + " TEXT, " +
                ListingsDatabaseContract.PHOTO2 + " TEXT, " +
                ListingsDatabaseContract.PHOTO3 + " TEXT, " +
                ListingsDatabaseContract.PHOTO4 + " TEXT, " +
                ListingsDatabaseContract.PHOTO5 + " TEXT, " +
                ListingsDatabaseContract.PHOTO6 + " TEXT, " +
                ListingsDatabaseContract.PHOTO7 + " TEXT, " +
                ListingsDatabaseContract.PHOTO8 + " TEXT, " +
                ListingsDatabaseContract.PHOTO9 + " TEXT, " +
                ListingsDatabaseContract.PHOTO10 + " TEXT, " +
                ListingsDatabaseContract.PHOTO11 + " TEXT, " +
                ListingsDatabaseContract.PHOTO12 + " TEXT, " +
                ListingsDatabaseContract.PHOTO13 + " TEXT, " +
                ListingsDatabaseContract.PHOTO14 + " TEXT, " +
                ListingsDatabaseContract.PHOTO15 + " TEXT, " +
                ListingsDatabaseContract.PHOTO_DESCR + " TEXT, " +
                ListingsDatabaseContract.ADDRESS_POSTCODE + " TEXT, " +
                ListingsDatabaseContract.ADDRESS_NUMBER + " TEXT, " +
                ListingsDatabaseContract.ADDRESS_STREET + " TEXT, " +
                ListingsDatabaseContract.ADDRESS_TOWN + " TEXT, " +
                ListingsDatabaseContract.ADDRESS_COUNTY + " TEXT, " +
                ListingsDatabaseContract.POI + " TEXT, " +
                STATUS + " BOOLEAN, " +
                ListingsDatabaseContract.POSTED_DATE + " DATE, " +
                ListingsDatabaseContract.SALE_DATE + " DATE, " +
                ListingsDatabaseContract.AGENT + " TEXT, " +
                ListingsDatabaseContract.UPDATE_TIME + " DATE," +
                BUY_LET + " TEXT "
                + ");";

        db.execSQL(createStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1:
                //upgrade logic from version 1
                break;
            default:
                throw new IllegalStateException("onUpgrade() with unknown version: " + newVersion);
        }
    }



    public void searchLocalDB(Context context, Bundle searchParams, int requestCode) {
        ArrayList<Listing> databaseListings = new ArrayList<>();
        String[] data;

        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor;

        if (searchParams != null) {
            String location = searchParams.getString(SearchActivityView.LOCATION_VALUE_KEY, "");
            boolean buy = searchParams.getBoolean(SearchActivityView.BUY_VALUE_KEY, true);
            String propertyType = searchParams.getString(SearchActivityView.PROPERTY_TYPE_VALUE_KEY, "Any");
            String minPrice = searchParams.getString(SearchActivityView.MIN_PRICE_VALUE_KEY, String.valueOf(Integer.MIN_VALUE));
            String maxPrice = searchParams.getString(SearchActivityView.MAX_PRICE_VALUE_KEY, String.valueOf(Integer.MAX_VALUE));
            String minBedrooms = searchParams.getString(SearchActivityView.MIN_BEDROOMS_VALUE_KEY, String.valueOf(Integer.MIN_VALUE));
            String maxBedrooms = searchParams.getString(SearchActivityView.MAX_BEDROOMS_VALUE_KEY, String.valueOf(Integer.MAX_VALUE));
            boolean soldSearch = searchParams.getBoolean(SearchActivityView.SOLD_SEARCH,false);

            StringBuilder selection = new StringBuilder();
            StringBuilder selectionArgsString = new StringBuilder();

            String locationWildcard = "%" + location + "%";

            selection.append("(" + ADDRESS_POSTCODE + " LIKE ?" + " OR " +
                    ADDRESS_STREET + " LIKE ?" + " OR " +
                    ADDRESS_TOWN + " LIKE ?" + " OR " +
                    ADDRESS_COUNTY + " LIKE ?)");
            selection.append(" AND ");

            selectionArgsString.append(locationWildcard).append(",")
                    .append(locationWildcard).append(",")
                    .append(locationWildcard).append(",")
                    .append(locationWildcard); //4 location arguments for wildcard above

            selection.append(BUY_LET + " =?");
            if (buy) {
                selectionArgsString.append(",").append("buy");
            } else {
                selectionArgsString.append(",").append("let");
            }

            if (soldSearch){
                selection.append(" AND ");
                selection.append(STATUS + " = ").append("?");
                selectionArgsString.append(",").append(0);
            }else {
                selection.append(" AND ");
                selection.append(STATUS + " = ").append("?");
                selectionArgsString.append(",").append(1);
            }

            if (!propertyType.equals("Any")) {
                selection.append(" AND ");
                selection.append(TYPE + " = ").append("?");
                selectionArgsString.append(",").append(propertyType);
            }


            if (!maxPrice.equals(NO_MAX_VALUE)) {
                selection.append(" AND ");
                selection.append(PRICE + " <= ").append("?");
                selectionArgsString.append(",").append(maxPrice.substring(1));
            }

            if (!minPrice.equals(NO_MIN_VALUE)) {
                selection.append(" AND ");
                selection.append(PRICE + " >= ").append("?");
                selectionArgsString.append(",").append(minPrice.substring(1));
            }

            if (!maxBedrooms.equals(NO_MAX_VALUE)) {
                selection.append(" AND ");
                selection.append(NUM_BED_ROOMS + " <= ").append("?");
                selectionArgsString.append(",").append(maxBedrooms);
            }

            if (!minBedrooms.equals(NO_MIN_VALUE)) {
                selection.append(" AND ");
                selection.append(NUM_BED_ROOMS + " >= ").append("?");
                selectionArgsString.append(",").append(minBedrooms);
            }

            String[] selectionArgs = selectionArgsString.toString().split(",");

            cursor = contentResolver.query(ListingsDatabaseContract.CONTENT_URI, ListingsDatabaseContract.ALL_LISTINGS_COLUMNS, selection.toString(), selectionArgs, ListingsDatabaseContract.POSTED_DATE + SORT_ORDER_DESCENDING);
        } else {
            cursor = contentResolver.query(ListingsDatabaseContract.CONTENT_URI, null, null, null, ListingsDatabaseContract.POSTED_DATE + SORT_ORDER_DESCENDING);
        }

        if (cursor != null && cursor.getCount() >= 1) {
            data = new String[cursor.getColumnCount()];
            while (cursor.moveToNext()) {
                ArrayList<byte[]> photoData = new ArrayList<>();
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    if (i >= PHOTOS_START_INDEX && i <= PHOTOS_END_INDEX) {
                        if (cursor.getBlob(i) != null) {
                            photoData.add(cursor.getBlob(i));
                        }
                    }else if (i == AVAILABLE_COLUMN_DATA ){
                        int soldStatus = cursor.getInt(i);
                        data[i] = soldStatus == 1 ? RESULT_SOLD : RESULT_FOR_SALE;
                    } else {
                        data[i] = cursor.getString(i);
                    }
                }


                String photoDesc = "";
                try {
                    photoDesc = data[PHOTODESC_COLUMN_DATA];
                } catch (Exception e) {
                    e.printStackTrace();
                }


                databaseListings.add(new Listing(
                        data[ID_COLUMN_DATA],
                        data[TYPE_COLUMN_DATA],
                        Double.parseDouble(data[PRICE_COLUMN_DATA]),
                        Double.parseDouble(data[SURFACEAREA_COLUMN_DATA]),
                        Integer.valueOf(data[NUM_BEDROOMS_COLUMN_DATA]),
                        data[DESCR_COLUMN_DATA],
                        photoData,
                        photoDesc != null ? photoDesc.split(",") : null,
                        data[ADDRESS_POSTCODE_COLUMN_DATA],
                        data[ADDRESS_NUMBER_COLUMN_DATA],
                        data[ADDRESS_STREET_COLUMN_DATA],
                        data[ADDRESS_TOWN_COLUMN_DATA],
                        data[ADDRESS_COUNTY_COLUMN_DATA],
                        data[POI_COLUMN_DATA],
                        data[POSTED_DATE_COLUMN_DATA],
                        data[SALE_DATE_COLUMN_DATA],
                        data[AGENT_COLUMN_DATA],
                        data[LASTUPDATE_COLUMN_DATA],
                        data[BUY_OR_LET_COLUMN_DATA],
                        data[AVAILABLE_COLUMN_DATA].equals(RESULT_FOR_SALE))
                );

            }
            cursor.close();
            mPresenter.gotDataFromLocalDb(databaseListings, requestCode, context.getApplicationContext());
        }else {
            //no data returned
            mPresenter.gotDataFromLocalDb(databaseListings, requestCode, context.getApplicationContext());
        }
    }


   


    public void syncWithFirebase(ArrayList<Listing> firebaseListings) {
        mDbSyncListener.updateProgressBarFirebaseSync(firebaseListings.size(),"Syncing firebase...");
        if (firebaseListings.size() >= 1) {
            final int[] listingCount = new int[1];
            listingCount[0] = firebaseListings.size();

            final ContentResolver contentResolver = getApplicationContext().getContentResolver();
            final boolean[] error = {false};
            Observable.fromIterable(firebaseListings).subscribeOn(Schedulers.io())
                    .doFinally(new Action() {
                        @Override
                        public void run() {
                            mDbSyncListener.syncComplete(error[0]);
                        }
                    })

                    .subscribe(new Observer<Listing>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            mDbSyncListener.updateProgressBarFirebaseSync(listingCount[0],getApplicationContext().getString(R.string.synching_with_firebase));
                        }

                        @Override
                        public void onNext(Listing listing) {
                            listingCount[0]--;
                            mDbSyncListener.updateProgressBarFirebaseSync(listingCount[0],getApplicationContext().getString(R.string.synching_with_firebase));

                            String[] projection = new String[1];
                            projection[0] = ListingsDatabaseContract.UPDATE_TIME;

                            String selection = ListingsDatabaseContract._ID + " = ?";
                            String[] selectionargs = new String[1];

                            selectionargs[0] = listing.getId();

                            Cursor cursor = contentResolver.query(ListingsDatabaseContract.CONTENT_URI, projection, selection, selectionargs,
                                    ListingsDatabaseContract.POSTED_DATE);

                            String lastUpdateTime = "";
                            boolean newRecord = false;

                            if (cursor != null && cursor.getCount() >= 1) {
                                while (cursor.moveToNext()) {
                                    lastUpdateTime = cursor.getString(0);
                                }
                            } else {
                                newRecord = true;
                            }

                            Date thisListingDate = Utils.stringToDate(listing.getLastUpdateTime());

                            Date localDBlistingDate = null;

                            if (!lastUpdateTime.equals("")) {
                                localDBlistingDate = Utils.stringToDate(lastUpdateTime);
                            }

                            if (newRecord) {
                                addNewRecord(listing);
                            } else {
                                if (thisListingDate != null && thisListingDate.after(localDBlistingDate)) {
                                    //delete the record
                                    contentResolver.delete(ListingsDatabaseContract.CONTENT_URI, selection, selectionargs);
                                    //add the new record
                                    addNewRecord(listing);
                                }

                            }

                            if ((cursor != null && cursor.isClosed())) {
                                cursor.close();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.d("onError", "onError: is " + e.getLocalizedMessage());
                            error[0] = true;
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }else {
            //no data
            mDbSyncListener.syncComplete(false);
        }

    }


    private void addNewRecord(Listing listing) {
        ArrayList<byte[]> listingImagesByteArray = new ArrayList<>();
        if (listing.getFirebasePhotos() != null) {
            for (String s : listing.getFirebasePhotos()) {
                byte[] imageBytes = ImageTools.UrlToByteArray(s);
                if (imageBytes != null) {
                    listingImagesByteArray.add(imageBytes);
                }
            }
            listing.setFirebasePhotos(null);
            listing.setPhotos(listingImagesByteArray);
        }

        MyDatabase.addListing(listing);
    }


    public static void addListing(Listing listingToAdd) {
        ContentResolver contentResolver = getApplicationContext().getContentResolver();

        ContentValues values = new ContentValues();
        if (listingToAdd != null) {
            values.put(ListingsDatabaseContract._ID, listingToAdd.getId());
            values.put(ListingsDatabaseContract.TYPE, listingToAdd.getType());
            values.put(ListingsDatabaseContract.PRICE, listingToAdd.getPrice());
            values.put(ListingsDatabaseContract.SURFACE_AREA, listingToAdd.getSurfaceArea());
            values.put(ListingsDatabaseContract.NUM_BED_ROOMS, listingToAdd.getNumbOfBedRooms());
            values.put(ListingsDatabaseContract.DESCRIPTION, listingToAdd.getDescr());

            try {
                values.put(ListingsDatabaseContract.PHOTO1, listingToAdd.getPhotos().get(0));
                values.put(ListingsDatabaseContract.PHOTO2, listingToAdd.getPhotos().get(1));
                values.put(ListingsDatabaseContract.PHOTO3, listingToAdd.getPhotos().get(2));
                values.put(ListingsDatabaseContract.PHOTO4, listingToAdd.getPhotos().get(3));
                values.put(ListingsDatabaseContract.PHOTO5, listingToAdd.getPhotos().get(4));
                values.put(ListingsDatabaseContract.PHOTO6, listingToAdd.getPhotos().get(5));
                values.put(ListingsDatabaseContract.PHOTO7, listingToAdd.getPhotos().get(6));
                values.put(ListingsDatabaseContract.PHOTO8, listingToAdd.getPhotos().get(7));
                values.put(ListingsDatabaseContract.PHOTO9, listingToAdd.getPhotos().get(8));
                values.put(ListingsDatabaseContract.PHOTO10, listingToAdd.getPhotos().get(9));
                values.put(ListingsDatabaseContract.PHOTO11, listingToAdd.getPhotos().get(10));
                values.put(ListingsDatabaseContract.PHOTO12, listingToAdd.getPhotos().get(11));
                values.put(ListingsDatabaseContract.PHOTO13, listingToAdd.getPhotos().get(12));
                values.put(ListingsDatabaseContract.PHOTO14, listingToAdd.getPhotos().get(13));
                values.put(ListingsDatabaseContract.PHOTO15, listingToAdd.getPhotos().get(14));
            } catch (Exception e) {
                e.printStackTrace();
            }

            values.put(ListingsDatabaseContract.ADDRESS_POSTCODE, listingToAdd.getAddress_postcode().replaceAll(" ", ""));
            values.put(ListingsDatabaseContract.ADDRESS_NUMBER, listingToAdd.getAddress_number());
            values.put(ListingsDatabaseContract.ADDRESS_STREET, listingToAdd.getAddress_street());
            values.put(ListingsDatabaseContract.ADDRESS_TOWN, listingToAdd.getAddress_town());
            values.put(ListingsDatabaseContract.ADDRESS_COUNTY, listingToAdd.getAddress_county());
            values.put(ListingsDatabaseContract.POI, listingToAdd.getPoi());
            values.put(ListingsDatabaseContract.STATUS, listingToAdd.isForSale());
            values.put(ListingsDatabaseContract.AGENT, listingToAdd.getAgent());
            values.put(ListingsDatabaseContract.POSTED_DATE, listingToAdd.getPostedDate());
            values.put(ListingsDatabaseContract.UPDATE_TIME, listingToAdd.getLastUpdateTime());
            values.put(ListingsDatabaseContract.BUY_LET, listingToAdd.getBuyOrLet());
            values.put(ListingsDatabaseContract.SALE_DATE, listingToAdd.getSaleDate());

            StringBuilder sb = new StringBuilder();
            int count = 0;

            if (listingToAdd.getPhotoDescriptions() != null) {
                for (String s : listingToAdd.getPhotoDescriptions()) {
                    if (count == 0) {
                        sb.append(s);
                        count++;
                    } else {
                        sb.append(",").append(s);
                    }
                }
                values.put(ListingsDatabaseContract.PHOTO_DESCR, sb.toString());
            }
            contentResolver.insert(ListingsDatabaseContract.CONTENT_URI, values);
        }
    }

    public MyDatabase setSynchListener(DbSyncListener dbSyncListener) {
        this.mDbSyncListener = dbSyncListener;
        return this;
    }

    public static void editListing(Listing listingToEdit) {
        ContentResolver contentResolver = getApplicationContext().getContentResolver();
        String selection = ListingsDatabaseContract._ID + " = \"" + listingToEdit.getId() + "\"";

        contentResolver.delete(ListingsDatabaseContract.CONTENT_URI, selection, null);

        addListing(listingToEdit);
    }


    public interface Model {
        void gotDataFromLocalDb(ArrayList<Listing> listings, int requestCode, Context c);
    }

}






