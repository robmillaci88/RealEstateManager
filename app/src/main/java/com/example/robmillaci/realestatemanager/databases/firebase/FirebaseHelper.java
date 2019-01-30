package com.example.robmillaci.realestatemanager.databases.firebase;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.example.robmillaci.realestatemanager.activities.customer_account.IUserDetailsCallback;
import com.example.robmillaci.realestatemanager.activities.search_activity.SearchActivityView;
import com.example.robmillaci.realestatemanager.data_objects.Listing;
import com.example.robmillaci.realestatemanager.databases.local_database.ListingsDatabaseContract;
import com.example.robmillaci.realestatemanager.databases.local_database.MyDatabase;
import com.example.robmillaci.realestatemanager.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import static com.example.robmillaci.realestatemanager.activities.add_listing_activity.AddListingView.BUY_STRING;
import static com.example.robmillaci.realestatemanager.activities.add_listing_activity.AddListingView.LET_STRING;
import static com.example.robmillaci.realestatemanager.databases.firebase.FirebaseContract.IMAGE_URI_PATH;
import static com.example.robmillaci.realestatemanager.databases.firebase.FirebaseContract.USER_COUNTY;
import static com.example.robmillaci.realestatemanager.databases.firebase.FirebaseContract.USER_DATABASE_COLLECTION_PATH;
import static com.example.robmillaci.realestatemanager.databases.firebase.FirebaseContract.USER_DATABASE_EMAIL_FIELD;
import static com.example.robmillaci.realestatemanager.databases.firebase.FirebaseContract.USER_DATABASE_ISADMIN_FIELD;
import static com.example.robmillaci.realestatemanager.databases.firebase.FirebaseContract.USER_DOB;
import static com.example.robmillaci.realestatemanager.databases.firebase.FirebaseContract.USER_FORENAME;
import static com.example.robmillaci.realestatemanager.databases.firebase.FirebaseContract.USER_HOME_NUMBER;
import static com.example.robmillaci.realestatemanager.databases.firebase.FirebaseContract.USER_HOUSE_NAME_NUMBER;
import static com.example.robmillaci.realestatemanager.databases.firebase.FirebaseContract.USER_HOUSE_STREET;
import static com.example.robmillaci.realestatemanager.databases.firebase.FirebaseContract.USER_MOBILE;
import static com.example.robmillaci.realestatemanager.databases.firebase.FirebaseContract.USER_POSTCODE;
import static com.example.robmillaci.realestatemanager.databases.firebase.FirebaseContract.USER_PRIMARY_CONTACT_NUMBER;
import static com.example.robmillaci.realestatemanager.databases.firebase.FirebaseContract.USER_SURNAME;
import static com.example.robmillaci.realestatemanager.databases.firebase.FirebaseContract.USER_TITLE;
import static com.example.robmillaci.realestatemanager.databases.firebase.FirebaseContract.USER_TOWN;
import static com.example.robmillaci.realestatemanager.databases.local_database.ListingsDatabaseContract.FIREBASE_IMAGE_URLS;
import static com.example.robmillaci.realestatemanager.databases.local_database.MyDatabase.NO_MAX_VALUE;
import static com.example.robmillaci.realestatemanager.databases.local_database.MyDatabase.NO_MIN_VALUE;

@SuppressWarnings({"unchecked", "ConstantConditions"})

/*
 * Helper class responsible for handling requests to Firebase database
 */
public class FirebaseHelper implements MyDatabase.Model {
    private static final String LISTINGS_COLLECTION_PATH = "listings"; //the firebase collection path for our listings
    private static final FirebaseAuth mAuth = FirebaseAuth.getInstance(); //The entry point of the Firebase Authentication SDK

    private static String loggedInUser; //the logged in user
    private static String loggedInEmail; //the logged in users email
    private static String loggedinUserId; //the logged in users picture
    private static FirebaseHelper instance; //this instance of FirebaseHelper class
    private static Map<Integer, String> mImagesUriTreeMap = new TreeMap<>(); //Treemap to hold uris of images for firebase storage. Treemap is used to maintain order base on key value
    private Observer<Integer> mSyncobservable; //observable for emitting listing objects to synch the database with firebase

    private ArrayList<Listing> mReturnedListings; //all listings returned from Firebase
    private ArrayList<Listing> mReturnedDbListings; //all listings returned from Local DB
    private Model mPresenter; //the presenter
    private AdminCheckCallback mAdminCheckCallback; //callback after checking if the user is Admin (represented by 1 = true and 0 = false in firebase
    private AddListingCallback mAddlistingcallback; //callback when we have added listings to firebase


    static {
        FirebaseFirestore.setLoggingEnabled(true);

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();

        FirebaseFirestore.getInstance().setFirestoreSettings(settings);
    }


    //Private constructor so this class cannot be instantiated externally
    private FirebaseHelper() {
    }

    //Returns an instance of this class and ensures only one instance exists
    public static FirebaseHelper getInstance() {
        if (instance == null) {
            instance = new FirebaseHelper();
        }
        return instance;
    }


    /**
     * Returns all the users details held in the Firebase database
     *
     * @param callback the class to receive the call back. The class must implement {@link IUserDetailsCallback}
     */
    public static void getUsersDetails(final IUserDetailsCallback callback) {
        final FirebaseFirestore mFirebaseDatabase;
        mFirebaseDatabase = FirebaseFirestore.getInstance();

        mFirebaseDatabase.collection(FirebaseContract.USER_DATABASE_COLLECTION_PATH)
                .document(loggedinUserId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot d = task.getResult();
                            HashMap<String, String> data = new HashMap<>();

                            data.put(USER_TITLE, d.get(USER_TITLE) != null ? (String) d.get(USER_TITLE) : "");
                            data.put(USER_FORENAME, d.get(USER_FORENAME) != null ? (String) d.get(USER_FORENAME) : "");
                            data.put(USER_SURNAME, d.get(USER_SURNAME) != null ? (String) d.get(USER_SURNAME) : "");
                            data.put(USER_DOB, d.get(USER_DOB) != null ? (String) d.get(USER_DOB) : "");
                            data.put(USER_POSTCODE, d.get(USER_POSTCODE) != null ? (String) d.get(USER_POSTCODE) : "");
                            data.put(USER_HOUSE_NAME_NUMBER, d.get(USER_HOUSE_NAME_NUMBER) != null ? (String) d.get(USER_HOUSE_NAME_NUMBER) : "");
                            data.put(USER_HOUSE_STREET, d.get(USER_HOUSE_STREET) != null ? (String) d.get(USER_HOUSE_STREET) : "");
                            data.put(USER_TOWN, d.get(USER_TOWN) != null ? (String) d.get(USER_TOWN) : "");
                            data.put(USER_COUNTY, d.get(USER_COUNTY) != null ? (String) d.get(USER_COUNTY) : "");
                            data.put(USER_HOME_NUMBER, d.get(USER_HOME_NUMBER) != null ? (String) d.get(USER_HOME_NUMBER) : "");
                            data.put(USER_MOBILE, d.get(USER_MOBILE) != null ? (String) d.get(USER_MOBILE) : "");
                            data.put(USER_DATABASE_EMAIL_FIELD, d.get(USER_DATABASE_EMAIL_FIELD) != null ? (String) d.get(USER_DATABASE_EMAIL_FIELD) : "");

                            callback.gotUserDetails(data);
                        }
                    }
                });

    }


    /**
     * Sets the presenter for this instance of FirebaseHelper such that data retrieved from Firebase can be sent back to this presenter.
     * The calling class must implement {@link Model}
     *
     * @param presenter the presenter, an instance of {@link Model}
     * @return this instance of Firebase helper class
     */
    public FirebaseHelper setPresenter(Model presenter) {
        this.mPresenter = presenter;
        return this;
    }


    /**
     * Set the {@link AddListingCallback} for this instance of FirebaseHelper. This callback returns a boolean depending on wether a listing was added successfully or not
     *
     * @param addlistingcallback the callback class that implements {@link AddListingCallback}
     * @return this instance of Firebase Helper class
     */
    public FirebaseHelper setAddlistingcallback(AddListingCallback addlistingcallback) {
        this.mAddlistingcallback = addlistingcallback;
        return this;
    }


    /**
     * Sets the {@link AdminCheckCallback} for this instance of FirebaseHelper. This callback responds to the calling class that implements {@link AdminCheckCallback}
     * with a boolean to determine if a user is an administrator or not
     *
     * @param callback that class that implements {@link AdminCheckCallback}
     * @return this instance of Firebase helper class
     */
    public FirebaseHelper setAdminCallback(AdminCheckCallback callback) {
        this.mAdminCheckCallback = callback;
        return this;
    }


    /**
     * @return the entry point to Firebase SDK
     */
    public static FirebaseAuth getmAuth() {
        return mAuth;
    }


    /**
     * @return the logged in user
     */
    public static String getLoggedInUser() {
        return loggedInUser;
    }


    /**
     * Returns the last update time for a specific listing to the callback class. A class calling this method must implement {@link AddListingCallback}
     *
     * @param listing  the listing to check the last update time for
     * @param callback the class implementing {@link AddListingCallback}
     */
    private void getListingLastUpdateTime(final Listing listing, final AddListingCallback callback) {
        final String[] lastUpdateTime = new String[1];
        FirebaseFirestore.getInstance()
                .collection(LISTINGS_COLLECTION_PATH)
                .document(listing.getId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<DocumentSnapshot> task) {
                        DocumentSnapshot d = task.getResult();
                        if (d != null) {
                            lastUpdateTime[0] = (String) d.get(ListingsDatabaseContract.UPDATE_TIME);
                        } else {
                            lastUpdateTime[0] = "null";
                        }
                        gotListingUpdateTime(listing, lastUpdateTime[0], callback);

                    }
                });
    }


    /**
     * Gets all the listing from Firebase and passes the document snapshot to {@link #foundAListing(DocumentSnapshot)} in order to create listing objects from the data
     * Once all listings have been created, an ArrayList<Listing> are sent to the presenter
     */
    public void getAllListings() {
        mReturnedListings = new ArrayList<>();

        FirebaseFirestore.getInstance().collection(LISTINGS_COLLECTION_PATH).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                QuerySnapshot taskResults = task.getResult();
                if (taskResults != null) {
                    List<DocumentSnapshot> docList = taskResults.getDocuments();
                    for (DocumentSnapshot s : docList) {
                        foundAListing(s);
                    }
                    if (mPresenter != null) {
                        mPresenter.gotListingsFromFirebase(mReturnedListings);
                    }
                }
            }
        });
    }


    /**
     * Converts document snapshots from Firebase into {@link Listing} objects
     *
     * @param s the document snapshot recieved from Firebase which is to be converted into a {@link Listing} object
     */
    private void foundAListing(DocumentSnapshot s) {
        ArrayList<String> photoDescrList;
        String[] photoDescr;
        if (s.get(ListingsDatabaseContract.PHOTO_DESCR) != null) {
            photoDescrList = (ArrayList<String>) s.get(ListingsDatabaseContract.PHOTO_DESCR);
            photoDescr = photoDescrList.toArray(new String[0]);
        } else {
            photoDescr = null;
        }


        mReturnedListings.add(new Listing(s.getId(),
                s.get(ListingsDatabaseContract.TYPE).toString(),
                s.get(ListingsDatabaseContract.PRICE) != null ? (Double) s.get(ListingsDatabaseContract.PRICE) : 0,
                s.get(ListingsDatabaseContract.SURFACE_AREA) != null ? (Double) s.get(ListingsDatabaseContract.SURFACE_AREA) : 0,
                s.get(ListingsDatabaseContract.NUM_BED_ROOMS) != null ? ((Long) s.get(ListingsDatabaseContract.NUM_BED_ROOMS)).intValue() : 0,
                s.get(ListingsDatabaseContract.DESCRIPTION) != null ? s.get(ListingsDatabaseContract.DESCRIPTION).toString() : "",
                s.get(FIREBASE_IMAGE_URLS) != null ? (ArrayList<String>) s.get(FIREBASE_IMAGE_URLS) : null,
                photoDescr,
                s.get(ListingsDatabaseContract.ADDRESS_POSTCODE) != null ? s.get(ListingsDatabaseContract.ADDRESS_POSTCODE).toString() : "",
                s.get(ListingsDatabaseContract.ADDRESS_NUMBER) != null ? s.get(ListingsDatabaseContract.ADDRESS_NUMBER).toString() : "",
                s.get(ListingsDatabaseContract.ADDRESS_STREET) != null ? s.get(ListingsDatabaseContract.ADDRESS_STREET).toString() : "",
                s.get(ListingsDatabaseContract.ADDRESS_TOWN) != null ? s.get(ListingsDatabaseContract.ADDRESS_TOWN).toString() : "",
                s.get(ListingsDatabaseContract.ADDRESS_COUNTY) != null ? s.get(ListingsDatabaseContract.ADDRESS_COUNTY).toString() : "",
                s.get(ListingsDatabaseContract.POI) != null ? s.get(ListingsDatabaseContract.POI).toString() : "",
                s.get(ListingsDatabaseContract.POSTED_DATE) != null ? s.get(ListingsDatabaseContract.POSTED_DATE).toString() : "",
                s.get(ListingsDatabaseContract.SALE_DATE) != null ? s.get(ListingsDatabaseContract.SALE_DATE).toString() : "",
                s.get(ListingsDatabaseContract.AGENT) != null ? s.get(ListingsDatabaseContract.AGENT).toString() : "",
                s.get(ListingsDatabaseContract.UPDATE_TIME) != null ? s.get(ListingsDatabaseContract.UPDATE_TIME).toString() : "",
                s.get(ListingsDatabaseContract.BUY_LET) != null ? s.get(ListingsDatabaseContract.BUY_LET).toString() : "",
                (boolean) s.get(ListingsDatabaseContract.STATUS)));
    }


    /**
     * Recieves a bundle containing search parameters and uses these to search Firebase for specific listings based on the search criteria.
     * This method passes any returned firebase snapshots to {@link #foundAListing(DocumentSnapshot)} to create the listing objects
     *
     * @param searchParams the search parameters
     */
    public void searchForSaleListings(Bundle searchParams) {
        //Search parameters*************************************************
        final String location = searchParams.getString(SearchActivityView.LOCATION_VALUE_KEY, "");
        final boolean buy = searchParams.getBoolean(SearchActivityView.BUY_VALUE_KEY, true);
        final String propertyType = searchParams.getString(SearchActivityView.PROPERTY_TYPE_VALUE_KEY, "Any");
        final String minPrice = searchParams.getString(SearchActivityView.MIN_PRICE_VALUE_KEY, String.valueOf(Integer.MIN_VALUE));
        final String maxPrice = searchParams.getString(SearchActivityView.MAX_PRICE_VALUE_KEY, String.valueOf(Integer.MAX_VALUE));
        final String minBedrooms = searchParams.getString(SearchActivityView.MIN_BEDROOMS_VALUE_KEY, String.valueOf(Integer.MIN_VALUE));
        final String maxBedrooms = searchParams.getString(SearchActivityView.MAX_BEDROOMS_VALUE_KEY, String.valueOf(Integer.MAX_VALUE));
        final String buyOrLet = buy ? BUY_STRING : LET_STRING;
        final String minSearchPrice = minPrice.equals(NO_MIN_VALUE) ? "£0" : minPrice;
        final String maxSearchprice = maxPrice.equals(NO_MAX_VALUE) ? String.valueOf(Integer.MAX_VALUE) : maxPrice;
        final String minSearchBedrooms = minBedrooms.equals(NO_MIN_VALUE) ? "0" : minBedrooms;
        final String maxSearchBedrooms = maxBedrooms.equals(NO_MAX_VALUE) ? String.valueOf(Integer.MAX_VALUE) : maxBedrooms;
        final boolean soldSearch = searchParams.getBoolean(SearchActivityView.SOLD_SEARCH);
        //End Search parameters*************************************************

        Query q = FirebaseFirestore.getInstance().collection(LISTINGS_COLLECTION_PATH)
                .whereEqualTo(ListingsDatabaseContract.BUY_LET, buyOrLet)
                .whereGreaterThanOrEqualTo(ListingsDatabaseContract.PRICE, Integer.valueOf(minSearchPrice.substring(1)))
                .whereLessThanOrEqualTo(ListingsDatabaseContract.PRICE, Integer.valueOf(maxSearchprice.substring(1)));

        q.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                QuerySnapshot taskResults = task.getResult();

                if (taskResults != null) {
                    List<DocumentSnapshot> docList = taskResults.getDocuments();
                    mReturnedListings = new ArrayList<>();

                    for (DocumentSnapshot s : docList) {
                        String address_postcode = (String) s.get(ListingsDatabaseContract.ADDRESS_POSTCODE);
                        String address_county = (String) s.get(ListingsDatabaseContract.ADDRESS_COUNTY);
                        String address_town = (String) s.get(ListingsDatabaseContract.ADDRESS_TOWN);
                        String address_street = (String) s.get(ListingsDatabaseContract.ADDRESS_STREET);
                        String numberOfBedrooms = String.valueOf((long) s.get(ListingsDatabaseContract.NUM_BED_ROOMS));
                        String address_property_type = (String) s.get(ListingsDatabaseContract.TYPE);
                        boolean forSale = (boolean) s.get(ListingsDatabaseContract.STATUS);

                        if (Integer.valueOf(numberOfBedrooms) >= Integer.valueOf(minSearchBedrooms)
                                && Integer.valueOf(numberOfBedrooms) <= Integer.valueOf(maxSearchBedrooms)) {

                            if (location.toLowerCase().equals("") || ((location.toLowerCase().replaceAll(" ", "").contains(address_postcode.toLowerCase())
                                    || location.toLowerCase().replaceAll(" ", "").contains(address_county.toLowerCase().replaceAll(" ", ""))
                                    || location.toLowerCase().replaceAll(" ", "").contains(address_town.toLowerCase().replaceAll(" ", ""))
                                    || location.toLowerCase().replaceAll(" ", "").contains(address_street.toLowerCase().replaceAll(" ", ""))))

                                    || ((address_postcode.toLowerCase().replaceAll(" ", "").contains(location.toLowerCase())
                                    || address_county.toLowerCase().replaceAll(" ", "").contains(location.toLowerCase())
                                    || address_town.toLowerCase().replaceAll(" ", "").contains(location.toLowerCase())
                                    || address_street.toLowerCase().replaceAll(" ", "").contains(location.toLowerCase())))) {

                                if (propertyType.equals("Any") || address_property_type.equals(propertyType)) {
                                    //noinspection unchecked
                                    if (soldSearch == !forSale) {
                                        foundAListing(s);
                                    }
                                }
                            }
                        }
                    }
                }
                if (mPresenter != null) {
                    mPresenter.gotListingsFromFirebase(mReturnedListings);
                }
            }
        });
    }


    /**
     * Update the static variables of this class with the current logged in users details
     */
    @SuppressWarnings("ConstantConditions")
    public static void updateUserDetails() {
        loggedInUser = mAuth.getCurrentUser().getDisplayName() != null ? mAuth.getCurrentUser().getDisplayName() : "";
        loggedInEmail = mAuth.getCurrentUser().getEmail() != null ? mAuth.getCurrentUser().getEmail() : "";
        loggedinUserId = mAuth.getCurrentUser().getUid();
        FirebaseFirestore.setLoggingEnabled(true);
    }


    /**
     * Updates firebase with the users details from their profile
     *
     * @param userDetails the hashmap passed to this metho containing the users details to update in Firebase
     */
    public static void updateUserProfileDetails(HashMap<String, String> userDetails) {
        final FirebaseFirestore mFirebaseDatabase;
        mFirebaseDatabase = FirebaseFirestore.getInstance();

        Map<String, Object> data = new HashMap<>();

        data.put(USER_TITLE, userDetails.get(USER_TITLE));
        data.put(USER_FORENAME, userDetails.get(USER_FORENAME));
        data.put(USER_SURNAME, userDetails.get(USER_SURNAME));
        data.put(USER_DOB, userDetails.get(USER_DOB));
        data.put(USER_POSTCODE, userDetails.get(USER_POSTCODE));
        data.put(USER_HOUSE_NAME_NUMBER, userDetails.get(USER_HOUSE_NAME_NUMBER));
        data.put(USER_HOUSE_STREET, userDetails.get(USER_HOUSE_STREET));
        data.put(USER_TOWN, userDetails.get(USER_TOWN));
        data.put(USER_COUNTY, userDetails.get(USER_COUNTY));
        data.put(USER_HOME_NUMBER, userDetails.get(USER_HOME_NUMBER));
        data.put(USER_MOBILE, userDetails.get(USER_MOBILE));
        data.put(USER_PRIMARY_CONTACT_NUMBER, userDetails.get(USER_PRIMARY_CONTACT_NUMBER));
        data.put(USER_DATABASE_EMAIL_FIELD, loggedInEmail);

        mFirebaseDatabase.collection(FirebaseContract.USER_DATABASE_COLLECTION_PATH).document(loggedinUserId).update(data);
    }


    /**
     * Called when a new user first users this app. This creates the user entry in Firebase
     * When this update is complete, this metho also get the Token of the user and updates this in the database
     *
     * @param fbUser the firebase user created when authenticated.
     */
    public static void addUserToDB(final FirebaseUser fbUser) {
        final FirebaseFirestore mFirebaseDatabase;
        mFirebaseDatabase = FirebaseFirestore.getInstance();

        Map<String, Object> data = new HashMap<>();

        data.put(FirebaseContract.USER_DATABASE_NAME_FIELD, fbUser.getDisplayName());
        data.put(USER_DATABASE_EMAIL_FIELD, fbUser.getEmail());
        //noinspection ConstantConditions
        data.put(FirebaseContract.USER_DATABASE_PICTURE_FIELD, fbUser.getPhotoUrl() != null ? fbUser.getPhotoUrl().toString() : "");
        data.put(FirebaseContract.USER_DATABASE_UNIQUE_ID_FIELD, fbUser.getUid());
        data.put(USER_DATABASE_ISADMIN_FIELD, 1); //0 = not admin, 1 = admin. Default is no admin access. Change this value in firebase to allow admin access


        mFirebaseDatabase.collection(FirebaseContract.USER_DATABASE_COLLECTION_PATH).document(fbUser.getUid()).set(data);


        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (task.isSuccessful()) {
                    @SuppressWarnings("ConstantConditions") String idToken = task.getResult().getToken();
                    Map<String, Object> data = new HashMap<>();
                    data.put(FirebaseContract.USER_DATABASE_TOKEN_PATH, idToken);
                    mFirebaseDatabase.collection(FirebaseContract.USER_DATABASE_COLLECTION_PATH).document(fbUser.getUid()).update(data);
                }
            }
        });
    }


    /**
     * The entry method when adding a listing to firebase, first we process the listings image files by calling {@link #uploadImageFiles(Listing, AddListingCallback)}
     *
     * @param listingToAdd       the listing being added to firebase
     * @param addListingCallback the callback for when the listing has been added
     */
    public void addListing(final Listing listingToAdd, final AddListingCallback addListingCallback) {
        if (listingToAdd != null) {
            uploadImageFiles(listingToAdd, addListingCallback);
        }
    }


    /**
     * This method uploads the images from the listing to firebase storage and then puts the URIs for the uploaded image into a Treemap.
     * Treemap is used because we need to maintain order of the images and some uploads complete before others
     *
     * @param listingToAdd       the listing of which the photos are being uploaded
     * @param addListingCallback the callback for when the listing has been added
     */
    private void uploadImageFiles(final Listing listingToAdd, final AddListingCallback addListingCallback) {
        final int[] count = {listingToAdd.getLocalDbPhotos().size()};

        for (int i = 0; i < listingToAdd.getLocalDbPhotos().size(); i++) {
            final StorageReference listingRef = FirebaseStorage.getInstance().getReference().child(listingToAdd.getId()); //get a reference to the Storage path for this listing
            final StorageReference pictureRef = listingRef.child(String.valueOf(i)); // get a reference to the child of the listing reference for this particular image
            byte[] thisPhoto = listingToAdd.getLocalDbPhotos().get(i); //get the byte array for the listing that will be uploaded

            pictureRef.putBytes(thisPhoto).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() { //upload the image to firebase
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    pictureRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() { //once the image is uploaded, get the download URL for the image
                        @Override
                        public void onSuccess(Uri uri) {
                            mImagesUriTreeMap.put(Integer.valueOf(pictureRef.getName()), uri.toString()); //add the images URI to a tree map here to maintain sort order
                            count[0]--;

                            if (count[0] == 0) {
                                saveImageDownloadBytes(listingToAdd, addListingCallback); //once all images have been uploaded and we have the URIS
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            addListingCallback.dBListingsAddedToFirebase(true);
                        }
                    });
                }
            });
        }
    }


    /**
     * This method is called once all the listings images have been uploaded to firebase, and we have all the image URIs retrieved.
     * This metho uploads the URI data to Firebase for a listing
     *
     * @param listing            the listing being saved
     * @param addListingCallback the callback for when the listing has been added
     */
    private void saveImageDownloadBytes(final Listing listing,
                                        final AddListingCallback addListingCallback) {
        ArrayList<String> sortedImageUris = new ArrayList<>(mImagesUriTreeMap.values());

        Map<String, Object> data = new HashMap<>();
        data.put(IMAGE_URI_PATH, sortedImageUris);
        FirebaseFirestore.getInstance()
                .collection(FirebaseContract.LISTING_DATABASE_COLLECTION_PATH)
                .document(listing.getId())
                .set(data)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mImagesUriTreeMap = new TreeMap<>();
                        finishAddingListing(listing, addListingCallback); //on complete we call finish adding listing to do the final processing steps
                    }
                });
    }


    /**
     * This is the last step in the chain of methods above to add a listing. This method puts all the other relevant data for a listing into Firebase.
     * If we are adding more than one listing, once this method completed it notifies our synchObservable to process the next listing. If we are adding just a single listing
     * this method calls back straight away to the {@link AddListingCallback} class
     *
     * @param listingToAdd       the listing being added
     * @param addListingCallback the callback for when the listing has been added
     */
    private void finishAddingListing(Listing listingToAdd,
                                     final AddListingCallback addListingCallback) {
        Map<String, Object> data = new HashMap<>();

        ArrayList<String> photoDescr;
        if (listingToAdd.getPhotoDescriptions() != null) {
            photoDescr = new ArrayList<>(Arrays.asList(listingToAdd.getPhotoDescriptions()));
        } else {
            photoDescr = new ArrayList<>();
        }

        data.put(ListingsDatabaseContract.TYPE, listingToAdd.getType());
        data.put(ListingsDatabaseContract.PRICE, listingToAdd.getPrice());
        data.put(ListingsDatabaseContract.SURFACE_AREA, listingToAdd.getSurfaceArea());
        data.put(ListingsDatabaseContract.NUM_BED_ROOMS, listingToAdd.getNumbOfBedRooms());
        data.put(ListingsDatabaseContract.DESCRIPTION, listingToAdd.getDescr());
        data.put(ListingsDatabaseContract.ADDRESS_NUMBER, listingToAdd.getAddress_number());
        data.put(ListingsDatabaseContract.ADDRESS_STREET, listingToAdd.getAddress_street());
        data.put(ListingsDatabaseContract.ADDRESS_TOWN, listingToAdd.getAddress_town());
        data.put(ListingsDatabaseContract.ADDRESS_POSTCODE, listingToAdd.getAddress_postcode().replaceAll(" ", ""));
        data.put(ListingsDatabaseContract.ADDRESS_COUNTY, listingToAdd.getAddress_county());
        data.put(ListingsDatabaseContract.PHOTO_DESCR, photoDescr);
        data.put(ListingsDatabaseContract.POI, listingToAdd.getPoi());
        data.put(ListingsDatabaseContract.STATUS, listingToAdd.isForSale());
        data.put(ListingsDatabaseContract.AGENT, listingToAdd.getAgent());
        data.put(ListingsDatabaseContract.POSTED_DATE, listingToAdd.getPostedDate());
        data.put(ListingsDatabaseContract.UPDATE_TIME, listingToAdd.getLastUpdateTime());
        data.put(ListingsDatabaseContract.BUY_LET, listingToAdd.getBuyOrLet());
        data.put(ListingsDatabaseContract.SALE_DATE, listingToAdd.getSaleDate());

        FirebaseFirestore.getInstance()
                .collection(FirebaseContract.LISTING_DATABASE_COLLECTION_PATH)
                .document(listingToAdd.getId())
                .update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                }).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (mSyncobservable != null) {
                    mSyncobservable.onNext(1);
                } else {
                    addListingCallback.dBListingsAddedToFirebase(false);
                }
            }
        });
    }


    /**
     * First method in the change for synching with the local database.
     * This metho calls {@link MyDatabase#searchLocalDB(Context, Bundle)} and returns the results to this class to the method {@link FirebaseHelper#gotDataFromLocalDb(ArrayList, Context)}
     *
     * @param c the context of the calling class
     */
    public void synchWithLocalDb(Context c) {
        MyDatabase.getInstance(c).setPresenter(this).searchLocalDB(c, null);
    }


    /**
     * Called after we have retrieved all listings from the local database from {@link FirebaseHelper#synchWithLocalDb(Context)}
     * This method also creates and mSyncObservable that is notified each time a listing has been added successfully
     *
     * @param listings Arraylist of {@link Listing} retrieved from the local DB
     * @param c        the context
     */
    public void gotDataFromLocalDb(final ArrayList<Listing> listings, final Context c) {
        final int[] iterationCount = {-1};

        if (listings != null && listings.size() >= 1) {
            final int[] progressBarCount = new int[]{listings.size()};
            mReturnedDbListings = listings;

            mSyncobservable = new Observer<Integer>() {
                @Override
                public void onSubscribe(Disposable d) {
                }

                @Override
                public void onNext(Integer i) {
                    int listingIndex = ++iterationCount[0];

                    progressBarCount[0]--;

                    if (!(listingIndex == (mReturnedDbListings.size() - 1))) {
                        try {
                            FirebaseHelper.getInstance().getListingLastUpdateTime(mReturnedDbListings.get(listingIndex), mAddlistingcallback);
                        } catch (IndexOutOfBoundsException e) {
                            onComplete();
                        }
                    } else {
                        onComplete();
                    }
                }

                @Override
                public void onError(Throwable e) {
                    mAddlistingcallback.dBListingsAddedToFirebase(true);
                    mSyncobservable = null;
                }

                @Override
                public void onComplete() {
                    mAddlistingcallback.dBListingsAddedToFirebase(false);
                    mSyncobservable = null;
                }
            };

            mSyncobservable.onNext(1);

        } else {
            mAddlistingcallback.dBListingsAddedToFirebase(false);

        }
    }


    /**
     * This method is called from {@link FirebaseHelper#getListingLastUpdateTime(Listing, AddListingCallback)}. It checks wether we need to add this listing to Firebase or not
     * by looking at the last update time of the listing we are adding (from the local db) and the last update time in Firebase. If the last update time in Firebase is before the last
     * update time in the local DB, we will add this listing to Firebase, replacing the older one
     *
     * @param listing        the listing being added
     * @param lastUpdatetime the last update time from Firebase for this listing
     * @param callback       the callback
     */
    private void gotListingUpdateTime(Listing listing, String lastUpdatetime, AddListingCallback callback) {

        if (lastUpdatetime == null || lastUpdatetime.equals("null")) {
            FirebaseHelper.getInstance().addListing(listing, callback);
        } else {
            Date lastUpdateDateFromFirebase = Utils.stringToDate(lastUpdatetime);
            Date thisListingDateFromLocal = Utils.stringToDate(listing.getLastUpdateTime());

            if (lastUpdateDateFromFirebase != null && lastUpdateDateFromFirebase.before(thisListingDateFromLocal)) {
                FirebaseHelper.getInstance().addListing(listing, callback);
            } else {
                mSyncobservable.onNext(1);
            }
        }
    }


    /**
     * Checks firebase for whether the logged in user is an administrator or not, returning true for admin or false for a regular user
     */
    public void checkAdminAccess() {
        if (loggedInUser != null) {
            FirebaseFirestore.getInstance().collection(USER_DATABASE_COLLECTION_PATH)
                    .document(loggedinUserId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot results = task.getResult();
                            if (results != null) {
                                long isAdmin = (long) results.get(USER_DATABASE_ISADMIN_FIELD);
                                if (isAdmin == 1) {
                                    mAdminCheckCallback.isAdmin(true);
                                } else {
                                    mAdminCheckCallback.isAdmin(false);
                                }
                            }
                        }
                    });
        }
    }


    /**
     * Interface that must be implemented to retrieve a callback from {@link FirebaseHelper#getAllListings()}
     */
    public interface Model {
        void gotListingsFromFirebase(ArrayList<Listing> listings);
    }

    public interface AdminCheckCallback {
        void isAdmin(boolean result);
    }

    public interface AddListingCallback {
        void dBListingsAddedToFirebase(boolean error);
    }
}
