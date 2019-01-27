package com.example.robmillaci.realestatemanager.databases.firebase;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.robmillaci.realestatemanager.R;
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

import static com.example.robmillaci.realestatemanager.databases.firebase.FirebaseContract.IMAGE_URI_PATH;
import static com.example.robmillaci.realestatemanager.databases.firebase.FirebaseContract.USER_COUNTY;
import static com.example.robmillaci.realestatemanager.databases.firebase.FirebaseContract.USER_DATABASE_COLLECTION_PATH;
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
public class FirebaseHelper implements MyDatabase.Model {
    private static final String LISTINGS_COLLECTION_PATH = "listings"; //the firebase collection path for our listings

    private static FirebaseAuth mAuth = FirebaseAuth.getInstance(); //The entry point of the Firebase Authentication SDK
    private static String loggedInUser; //the logged in user
    private static String loggedInEmail; //the logged in users email
    private static String loggedinUserId; //the logged in users picture
    private static int dBListingCount = 0; //the number of listings returned from local database

    private ArrayList<Listing> returnedListings; //all listings returned from Firebase
    private ArrayList<Listing> dbListings;

    private Model mPresenter; //the presenter
    private AdminCheckCallback mAdminCheckCallback; //callback after checking if the user is Admin (represented by 1 = true and 0 = false in firebase
    private AddListingCallback addListingCallback; //callback when we have added listings to firebase

    private static FirebaseHelper instance; //this instance of FirebaseHelper class

    private static Map<Integer,String> mImagesUriTreeMap = new TreeMap<>(); //Treemap to hold uris of images for firebase storage. Treemap is used to maintain order base on key value

    private static Observer<Integer> syncObservable;

    static {
        FirebaseFirestore.setLoggingEnabled(true);

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();

        FirebaseFirestore.getInstance().setFirestoreSettings(settings);
    }


    private FirebaseHelper() {
    }

    public static FirebaseHelper getInstance() {
        if (instance == null) {
            instance = new FirebaseHelper();
        }
        return instance;
    }


    public FirebaseHelper setPresenter(Model presenter) {
        this.mPresenter = presenter;
        return this;
    }

    public FirebaseHelper setAddListingCallback(AddListingCallback addListingCallback) {
        this.addListingCallback = addListingCallback;
        return this;
    }

    public FirebaseHelper setAdminCallback(AdminCheckCallback callback) {
        this.mAdminCheckCallback = callback;
        return this;
    }


    public static FirebaseAuth getmAuth() {
        return mAuth;
    }

    public static String getLoggedInUser() {
        return loggedInUser;
    }


    private static void getListingLastUpdateTime(final Listing listing, final AddListingCallback callback) {
        Log.d("getListing", "getListingLastUpdateTime: called");
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


    public void getAllListings() {
        returnedListings = new ArrayList<>();

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

                    mPresenter.gotListingsFromFirebase(returnedListings);
                }
            }
        });
    }


    private void foundAListing(DocumentSnapshot s) {
        ArrayList<String> photoDescrList;
        String[] photoDescr;
        if (s.get(ListingsDatabaseContract.PHOTO_DESCR) != null) {
            photoDescrList = (ArrayList<String>) s.get(ListingsDatabaseContract.PHOTO_DESCR);
            photoDescr = photoDescrList.toArray(new String[photoDescrList.size()]);
        } else {
            photoDescr = null;
        }


        returnedListings.add(new Listing(s.getId(),
                s.get(ListingsDatabaseContract.TYPE).toString(),
                s.get(ListingsDatabaseContract.PRICE) != null ? (Double) s.get(ListingsDatabaseContract.PRICE) : 0,
                s.get(ListingsDatabaseContract.SURFACE_AREA) != null ? (Double) s.get(ListingsDatabaseContract.SURFACE_AREA) : 0,
                s.get(ListingsDatabaseContract.NUM_BED_ROOMS) != null ? ((Long) s.get(ListingsDatabaseContract.NUM_BED_ROOMS)).intValue() : 0,
                s.get(ListingsDatabaseContract.DESCRIPTION) != null ? s.get(ListingsDatabaseContract.DESCRIPTION).toString() : "",
                s.get(FIREBASE_IMAGE_URLS) != null ? (ArrayList<String>) s.get(FIREBASE_IMAGE_URLS) : null, //todo sort this out!
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



    public void searchForSaleListings(Bundle searchParams) {
        //Search parameters*************************************************
        final String location = searchParams.getString(SearchActivityView.LOCATION_VALUE_KEY, "");
        final boolean buy = searchParams.getBoolean(SearchActivityView.BUY_VALUE_KEY, true);
        final String propertyType = searchParams.getString(SearchActivityView.PROPERTY_TYPE_VALUE_KEY, "Any");
        final String minPrice = searchParams.getString(SearchActivityView.MIN_PRICE_VALUE_KEY, String.valueOf(Integer.MIN_VALUE));
        final String maxPrice = searchParams.getString(SearchActivityView.MAX_PRICE_VALUE_KEY, String.valueOf(Integer.MAX_VALUE));
        final String minBedrooms = searchParams.getString(SearchActivityView.MIN_BEDROOMS_VALUE_KEY, String.valueOf(Integer.MIN_VALUE));
        final String maxBedrooms = searchParams.getString(SearchActivityView.MAX_BEDROOMS_VALUE_KEY, String.valueOf(Integer.MAX_VALUE));
        final String buyOrLet = buy ? "buy" : "let";
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
                    returnedListings = new ArrayList<>();

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
                mPresenter.gotListingsFromFirebase(returnedListings);
            }
        });
    }





    @SuppressWarnings("ConstantConditions")
    public static void updateUserDetails() {
        loggedInUser = mAuth.getCurrentUser().getDisplayName() != null ? mAuth.getCurrentUser().getDisplayName() : "";
        loggedInEmail = mAuth.getCurrentUser().getEmail() != null ? mAuth.getCurrentUser().getEmail() : "";
        loggedinUserId = mAuth.getCurrentUser().getUid();
        FirebaseFirestore.setLoggingEnabled(true);
    }


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
        data.put(FirebaseContract.USER_DATABASE_EMAIL_FIELD, loggedInEmail);

        mFirebaseDatabase.collection(FirebaseContract.USER_DATABASE_COLLECTION_PATH).document(loggedinUserId).update(data);
    }


    public static void addUserToDB(final FirebaseUser fbUser) {
        final FirebaseFirestore mFirebaseDatabase;
        mFirebaseDatabase = FirebaseFirestore.getInstance();

        Map<String, Object> data = new HashMap<>();

        data.put(FirebaseContract.USER_DATABASE_NAME_FIELD, fbUser.getDisplayName());
        data.put(FirebaseContract.USER_DATABASE_EMAIL_FIELD, fbUser.getEmail());
        //noinspection ConstantConditions
        data.put(FirebaseContract.USER_DATABASE_PICTURE_FIELD, (fbUser.getPhotoUrl().toString()));
        data.put(FirebaseContract.USER_DATABASE_UNIQUE_ID_FIELD, fbUser.getUid());
        data.put(USER_DATABASE_ISADMIN_FIELD, 0); //0 = not admin, 1 = admin. Default is no admin access. Change this value in firebase to allow admin access


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


    public static void addListing(final Listing listingToAdd, final AddListingCallback addListingCallback) {
        if (listingToAdd != null) {
            uploadImageFiles(listingToAdd, addListingCallback);
        }
    }


    private static void uploadImageFiles(final Listing listingToAdd, final AddListingCallback addListingCallback) {
        final int[] count = {listingToAdd.getPhotos().size()};

        for (int i = 0; i < listingToAdd.getPhotos().size(); i++) {
            final StorageReference listingRef = FirebaseStorage.getInstance().getReference().child(listingToAdd.getId());
            final StorageReference pictureRef = listingRef.child(String.valueOf(i));
            byte[] thisPhoto = listingToAdd.getPhotos().get(i);

            pictureRef.putBytes(thisPhoto).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    pictureRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Log.d("onSuccess", "onSuccess: picture ref is " + pictureRef.getName());
                            mImagesUriTreeMap.put(Integer.valueOf(pictureRef.getName()),uri.toString()); //use a tree map here to maintain sort order
                            count[0]--;

                            if (count[0] == 0) {
                                saveImageDownloadBytes(listingToAdd, addListingCallback);
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


    private static void saveImageDownloadBytes(final Listing listing,
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
                        finishAddingListing(listing, addListingCallback);
                    }
                });
    }


    private static void finishAddingListing(Listing listingToAdd,
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
                if (syncObservable !=null) {
                    syncObservable.onNext(1);
                }else {
                    addListingCallback.dBListingsAddedToFirebase(false);
                }
            }
        });
    }


    public void synchWithLocalDb(Context c) {
        MyDatabase.getInstance(c).setPresenter(this).searchLocalDB(c, null, 1);
    }


    public void gotDataFromLocalDb(final ArrayList<Listing> listings, int requestCode, final Context c) {
        final int[] iterationCount = {-1};

        if (listings != null && listings.size() >= 1) {
            final int[] progressBarCount = new int[]{listings.size()};
            dbListings = listings;


            syncObservable = new Observer<Integer>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(Integer i) {
                    int listingIndex = ++iterationCount[0];

                    addListingCallback.updateProgressBarDbSync(progressBarCount[0], c.getString(R.string.local_db_sync_message));
                    progressBarCount[0]--;

                    if (!(listingIndex == dbListings.size())) {
                        FirebaseHelper.getListingLastUpdateTime(dbListings.get(listingIndex), addListingCallback);
                    } else {
                        onComplete();
                    }
                }

                @Override
                public void onError(Throwable e) {
                    addListingCallback.dBListingsAddedToFirebase(true);
                }

                @Override
                public void onComplete() {
                    addListingCallback.dBListingsAddedToFirebase(false);
                }
            };

            syncObservable.onNext(1);

        } else {
            addListingCallback.dBListingsAddedToFirebase(false);

        }
    }

    private static void gotListingUpdateTime(Listing listing, String lastUpdatetime, AddListingCallback callback) {

        if (lastUpdatetime == null || lastUpdatetime.equals("null")) {
            FirebaseHelper.addListing(listing, callback);
        } else {
            Date lastUpdateDateFromFirebase = Utils.stringToDate(lastUpdatetime);
            Date thisListingDateFromLocal = Utils.stringToDate(listing.getLastUpdateTime());

            if (lastUpdateDateFromFirebase != null && lastUpdateDateFromFirebase.before(thisListingDateFromLocal)) {
                FirebaseHelper.addListing(listing, callback);
            }else {
                syncObservable.onNext(1);
            }
        }
    }

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


    public interface Model {
        void gotListingsFromFirebase(ArrayList<Listing> listings);
    }

    public interface AdminCheckCallback {
        void isAdmin(boolean result);
    }

    public interface AddListingCallback {
        void dBListingsAddedToFirebase(boolean error);

        void updateProgressBarDbSync(int count, String message);
    }
}
