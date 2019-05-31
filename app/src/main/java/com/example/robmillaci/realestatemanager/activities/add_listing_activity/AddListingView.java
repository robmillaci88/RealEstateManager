package com.example.robmillaci.realestatemanager.activities.add_listing_activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.robmillaci.realestatemanager.R;
import com.example.robmillaci.realestatemanager.activities.BaseActivity;
import com.example.robmillaci.realestatemanager.activities.main_activity.MainActivityView;
import com.example.robmillaci.realestatemanager.adapters.ImagesRecyclerViewAdapter;
import com.example.robmillaci.realestatemanager.custom_objects.MyTokenizer;
import com.example.robmillaci.realestatemanager.data_objects.Listing;
import com.example.robmillaci.realestatemanager.databases.firebase.FirebaseHelper;
import com.example.robmillaci.realestatemanager.utils.SharedPreferenceHelper;
import com.example.robmillaci.realestatemanager.utils.ToastModifications;
import com.example.robmillaci.realestatemanager.utils.Utils;
import com.example.robmillaci.realestatemanager.utils.image_tools.BitmapMods;
import com.example.robmillaci.realestatemanager.utils.image_tools.ImageFilePath;
import com.example.robmillaci.realestatemanager.utils.image_tools.ImageTools;
import com.jakewharton.rxbinding3.view.RxView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import kotlin.Unit;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static com.example.robmillaci.realestatemanager.activities.add_listing_activity.AddListingPresenter.PICK_FROM_CAMERA_REQUEST_CODE;
import static com.example.robmillaci.realestatemanager.activities.add_listing_activity.AddListingPresenter.PICK_FROM_GALLERY_REQUEST_CODE;
import static com.example.robmillaci.realestatemanager.data_objects.Listing.DEFAULT_LISTING_ID;
import static com.example.robmillaci.realestatemanager.fragments.ListingItemFragment.EDIT_LISTING_BUNDLE_KEY;


/**
 * This class is responsible for handling the view related events of Adding listings
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
public class AddListingView extends BaseActivity implements AddListingPresenter.View, ImagesRecyclerViewAdapter.IactivityCallback {
    private static final int MAX_NUM_IMAGES = 15; //max number of mImages for a listing
    private static final int FIREBASE_ID = 1; //Id to represent Firebase
    private static final int LOCAL_DB_ID = 0; //Id to represent local DB

    private static final String SOLD_TAG = "sold"; //the tag to assign if a listing is sold
    private static final String FOR_SALE_TAG = "forSale"; //the tag to assign if a listing is for sale
    public static final String BUY_STRING = "buy"; //the database value if the listing is categorized as BUY
    public static final String LET_STRING = "let";//the database value if the listing is categorized as BUY

    private ArrayList<Bitmap> mImages; //Arraylist of bitmap mImages to holder the listings mImages
    private ArrayList<String> mImageDescription; //Arraylist of Strings = to holder the listings mImages descriptions
    private ArrayList<View> mAllEditTexts; //Arraylist to hold all edit texts in this view, in order to perform checks that required information is entered
    private boolean mEditing = false; //are we mEditing or adding a new listing?
    private String mEditingId = null; //the id of the listing that we are mEditing
    private Listing mListingBeingEdited; //the listing being edited

    private AddListingPresenter mPresenter; //the mPresenter of this class responsible for obtaining or sending any data to the model

    private CompositeDisposable mCompositeDisposable; //holds all disposables

    private RecyclerView mImagesRecyclerView; //the recyclerview to display the listings mImages
    private ImagesRecyclerViewAdapter mAdapter; //the adapter for the recyclerview

    private Button mAddPictureBtn; //the add button

    private ImageView mSaveButton; //the save button (as an imageview)
    private ImageView mSaleStatusImage; //the sale status

    private Spinner mTypeSpinner; //the type of listing spinner
    private Spinner mBedroomsSpinner; //the number of bedrooms spinner

    private TextInputEditText mSurfaceAreaText; //listings surface area
    private TextInputEditText mPriceEditText; //listings price
    private MultiAutoCompleteTextView mPoiAutocomplete; //listings P.O.I
    private TextInputEditText mAddressPostcodeEditText; //listings post code
    private TextInputEditText mAddressNumberEditText; //listings address number
    private TextInputEditText mAddressStreetEditText; //listings address street
    private TextInputEditText mAddressTownEditText;//listings address town
    private TextInputEditText mAddressCountyEditText;//listings address county
    private EditText mDescriptionEditText;//listings address description

    private SwitchCompat mBuyOrLetSwitch; //the buy or let switch
    private ProgressDialog mProgressDialog; //the progress dialog displayed when saving a listing

    private ArrayList<String> mEnteredPoisArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing); //set the view
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //add the home button to the action bar
        setTitle(getString(R.string.new_listing_activity_title));//set the title of the activity
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN); //dont display the keyboard when activity is created

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS); //remove immersive mode for this activity
        Utils.removeImmersiveMode(getWindow().getDecorView());

        this.mPresenter = new AddListingPresenter(this); //create the presenter
        mCompositeDisposable = new CompositeDisposable(); //create a composite disposable to hold all disposables

        mImages = new ArrayList<>(); //create a new arraylist to hold the listings mImages
        mImageDescription = new ArrayList<>();//create a new arraylist to hold the listings mImages description
        mEnteredPoisArrayList = new ArrayList<>(); //holds the users entered poi's


        initializeViews(); //see method comments
        configurePoiAutoComplete();
        initializeClickEvents();  //see method comments
        initializeRecyclerView();  //see method comments
        restoreEditingListing(); //if we are editing, we restore the listing to be edited

    }

    private void restoreEditingListing() {
        Bundle intentExtras = getIntent().getExtras();
        if (intentExtras != null && intentExtras.getBoolean(EDIT_LISTING_BUNDLE_KEY)) {

            final ProgressDialog pd = new ProgressDialog(this);
            pd.setMessage("Restoring listing..");
            pd.show();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    /*
                     *Grab the listing saved in shared preferences to edit
                     */
                    final Listing editingListing = new SharedPreferenceHelper(AddListingView.this).getListingFromSharedPrefs();
                    if (editingListing != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                prepareEdit(editingListing, pd);
                            }
                        });
                    }
                }
            }).start();
        }

    }

    private void configurePoiAutoComplete() {
        mPoiAutocomplete = findViewById(R.id.poi_multi_autocomplete);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.poi_types));
        mPoiAutocomplete.setDropDownVerticalOffset(100);
        mPoiAutocomplete.setAdapter(adapter);
        mPoiAutocomplete.setTokenizer(new MyTokenizer());


        mPoiAutocomplete.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                boolean sendToast = false;

                if (!b) {
                    // on focus off
                    if (!mPoiAutocomplete.getText().toString().equals("") && mPoiAutocomplete.getText().toString().length() > 2) {
                        String str = mPoiAutocomplete.getText().toString().trim();
                        String[] enteredPoisArray = str.split(",");

                        for (int i = 0; i < enteredPoisArray.length; i++) {
                            enteredPoisArray[i] = enteredPoisArray[i].trim();
                        }

                        if (str.equals("")) {
                            sendToast = true;
                        }

                        String[] allowed_values = getResources().getStringArray(R.array.poi_types);
                        for (String temp : allowed_values) {
                            for (String s : enteredPoisArray) {
                                if (s.trim().compareTo(temp) == 0) {
                                    mEnteredPoisArrayList.add(s.trim() + ",");
                                }
                            }
                        }

                        StringBuilder sb = new StringBuilder();
                        for (String s : mEnteredPoisArrayList) {
                            sb.append(s);
                        }

                        if (str.length() != sb.toString().trim().length()) {
                            //text is being removed
                            sendToast = true;
                        }

                        try {
                            mPoiAutocomplete.setText(sb.toString().substring(0, sb.length() - 1));
                            mEnteredPoisArrayList.clear();

                        } catch (Exception e) {
                            sendToast = true;
                        }

                        if (sendToast) poiError();

                    } else {
                        poiError();
                    }
                }
            }
        });
    }

    private void poiError() {
        mPoiAutocomplete.setText("");
        ToastModifications.createToast(AddListingView.this, getString(R.string.poi_selection_error), Toast.LENGTH_LONG);
    }

    /**
     * populate the views in this activity with the values defined for an already existing listing
     *
     * @param editingListing the listing that is being edited.
     */
    private void prepareEdit(Listing editingListing, ProgressDialog pd) {
        setTitle(String.format("%s %s %s", getString(R.string.editing_title), editingListing.getAddress_number(), editingListing.getAddress_street()));
        int selection = 0;
        mEditing = true;
        mEditingId = editingListing.getId();
        mListingBeingEdited = editingListing;


        switch (editingListing.getType()) {
            case "Flat":
                selection = 0;
                break;
            case "Apartment":
                selection = 1;
                break;
            case "Bungalow":
                selection = 2;
                break;
            case "Cottage":
                selection = 3;
                break;
            case "Terrace House":
                selection = 4;
                break;
            case "Semi-detached house":
                selection = 5;
                break;
            case "Detached house":
                selection = 6;
                break;
        }

        mTypeSpinner.setSelection(selection);
        mBedroomsSpinner.setSelection(editingListing.getNumbOfBedRooms() - 1);
        mSurfaceAreaText.setText(String.valueOf(editingListing.getSurfaceArea()));
        mPriceEditText.setText(String.valueOf(editingListing.getPrice()).substring(0, String.valueOf(editingListing.getPrice()).indexOf(".")));
        mPoiAutocomplete.setText(editingListing.getPoi());
        mAddressPostcodeEditText.setText(editingListing.getAddress_postcode());
        mAddressNumberEditText.setText(editingListing.getAddress_number());
        mAddressStreetEditText.setText(editingListing.getAddress_street());
        mAddressTownEditText.setText(editingListing.getAddress_town());
        mAddressCountyEditText.setText(editingListing.getAddress_county());
        mDescriptionEditText.setText(editingListing.getDescr());

        if (editingListing.getBuyOrLet().toLowerCase().equals("buy")) {
            mBuyOrLetSwitch.setChecked(false);
        } else {
            mBuyOrLetSwitch.setChecked(true);
        }

        if (editingListing.isForSale()) {
            updateListingSoldStatus(false);
        } else {
            updateListingSoldStatus(true);
        }

        if (editingListing.getFirebasePhotos() == null) {
            restoreImages(editingListing.getLocalDbPhotos(), editingListing.getPhotoDescriptions(), LOCAL_DB_ID);
        } else {
            restoreImages(editingListing.getFirebasePhotos(), editingListing.getPhotoDescriptions(), FIREBASE_ID);
        }

        if (pd.isShowing()) {
            pd.dismiss(); //dismiss the progress dialog shown to the user when restoring the listing
        }
    }


    /**
     * If we are mEditing a listing, this method will retrieve either a byte[] or arraylist of uris that are used to restore the mImages for the listing
     * These mImages are then passed into the recyclerview adapter to display to the user
     *
     * @param photos            the photos byte[]'s or the uris (as a string)
     * @param photoDescriptions the descriptions of the photos
     * @param id                the id used in the switch to determine if we are working with the local db or with Firebase
     */
    @SuppressWarnings("unchecked")
    private void restoreImages(Object photos, final String[] photoDescriptions, int id) {
        if (photos != null) {
            switch (id) {
                case LOCAL_DB_ID:
                    List<byte[]> localPhotos = (List<byte[]>) photos;
                    mImages = ImageTools.byteArrayToBitmaps(localPhotos);
                    mImageDescription = new ArrayList<>(Arrays.asList(photoDescriptions));
                    initializeRecyclerView();
                    break;

                case FIREBASE_ID:
                    ArrayList<String> firebasePhotos = (ArrayList<String>) photos;
                    for (String s : firebasePhotos) {
                        try {
                            //noinspection deprecation
                            Glide.with(getApplicationContext()).asBitmap().load(s).into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    mImages.add(resource);
                                    mImageDescription = new ArrayList<>(Arrays.asList(photoDescriptions));
                                    initializeRecyclerView();
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }
    }


    /**
     * Initialize all views in this activity, set any relevant tags and create the spinners
     */
    private void initializeViews() {
        mAllEditTexts = new ArrayList<>();

        mImagesRecyclerView = findViewById(R.id.images_recycler_view);
        mAddPictureBtn = findViewById(R.id.add_picture_btn);

        mSaleStatusImage = findViewById(R.id.sale_status_image);
        mSaleStatusImage.setTag(FOR_SALE_TAG);

        mTypeSpinner = findViewById(R.id.type_spinner);
        mBedroomsSpinner = findViewById(R.id.rooms_spinner);

        mTypeSpinner.setAdapter(new ArrayAdapter<>(this, R.layout.spinner_item, getResources().getStringArray(R.array.spinner_types)));
        mBedroomsSpinner.setAdapter(new ArrayAdapter<>(this, R.layout.spinner_item, getResources().getStringArray(R.array.spinner_number_of_rooms)));

        mSurfaceAreaText = findViewById(R.id.surface_area_text1);
        mPriceEditText = findViewById(R.id.price_edit_text1);
        mAddressPostcodeEditText = findViewById(R.id.address_postcode_et1);
        mAddressNumberEditText = findViewById(R.id.address_number_et1);
        mAddressStreetEditText = findViewById(R.id.address_street_et1);
        mAddressTownEditText = findViewById(R.id.address_town_et1);
        mAddressCountyEditText = findViewById(R.id.address_county_et1);
        mDescriptionEditText = findViewById(R.id.description_edit_text1);
        mBuyOrLetSwitch = findViewById(R.id.buy_or_let);

        mSaveButton = findViewById(R.id.savebtn);

        mAllEditTexts.add(mSurfaceAreaText);
        mAllEditTexts.add(mPriceEditText);
        mAllEditTexts.add(mAddressPostcodeEditText);
        mAllEditTexts.add(mAddressNumberEditText);
        mAllEditTexts.add(mAddressStreetEditText);
        mAllEditTexts.add(mAddressTownEditText);
        mAllEditTexts.add(mAddressCountyEditText);
        mAllEditTexts.add(mDescriptionEditText);

    }


    //Created the recycler view to display a listings mImages
    private void initializeRecyclerView() {
        if (mImages != null) {
            if (mImages.size() > 0) {
                mImagesRecyclerView.setBackground(null);
            } else {
                mImagesRecyclerView.setBackgroundResource(R.drawable.placeholder_image);
            }

            mImagesRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));

            mAdapter = new ImagesRecyclerViewAdapter(this, mImages, mImageDescription, this);
            mImagesRecyclerView.setAdapter(mAdapter);
        }
    }


    /**
     * initialize on click events for this activity
     */
    @SuppressLint("CheckResult")
    private void initializeClickEvents() {
        //create the onlick event for the add picture button
        Disposable pictureClick = RxView.clicks(mAddPictureBtn)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) {
                        if (mImages.size() < MAX_NUM_IMAGES) {
                            displayPictureMethodDialog();
                        } else {
                            Toast.makeText(AddListingView.this, R.string.maximum_images, Toast.LENGTH_LONG).show();
                        }
                    }
                });
        mCompositeDisposable.add(pictureClick);


        //create the on click event for the save button
        final Disposable save = RxView.clicks(mSaveButton)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) {
                        saveData();
                    }
                });
        mCompositeDisposable.add(save);


        //create onclick for the property status image
        Disposable statusChange = RxView.clicks(mSaleStatusImage)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) {
                        switch (mSaleStatusImage.getTag().toString()) {
                            case FOR_SALE_TAG:
                                updateListingSoldStatus(true);
                                break;
                            case SOLD_TAG:
                                updateListingSoldStatus(false);
                        }
                    }
                });
        mCompositeDisposable.add(statusChange);
    }


    /**
     * update the sold status of a listing, changes the sales status image and tag depending on whether the listing is sold or not
     *
     * @param sold true if sold, false if available
     */
    private void updateListingSoldStatus(boolean sold) {
        if (sold) {
            mSaleStatusImage.setBackgroundResource(R.drawable.sold);
            mSaleStatusImage.setTag(SOLD_TAG);
        } else {
            mSaleStatusImage.setBackgroundResource(R.drawable.for_sale);
            mSaleStatusImage.setTag(FOR_SALE_TAG);
        }
    }


    /**
     * Displays an Alert dialog to the user with a choice of choosing an image from the camera or from the gallery
     * The result is passed to the presented to get the photo, the results of which are passed onto {@link AddListingView#onActivityResult}
     */
    @SuppressLint("CheckResult")
    private void displayPictureMethodDialog() {
        final AlertDialog.Builder chooseBuilder = new AlertDialog.Builder(this);
        @SuppressLint("InflateParams") View v = LayoutInflater.from(this).inflate(R.layout.picture_method_chooser, null);
        chooseBuilder.setView(v);
        chooseBuilder.setPositiveButton(R.string.close_button, null);
        final AlertDialog chooseDialog = chooseBuilder.show();

        RxView.clicks(v.findViewById(R.id.camera_choice))
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) {
                        mPresenter.getPhotoFromCamera();
                        chooseDialog.dismiss();
                    }
                });

        RxView.clicks(v.findViewById(R.id.gallery_choice))
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) {
                        mPresenter.getPhotoFromDevice();
                        chooseDialog.dismiss();
                    }
                });
    }


    /**
     * Called from the save button. Checks first to see if we are ok to save based on <br/>
     * Do we have at least one image?<br/>
     * Do all the edit texts have a value? <br/>
     * <br/>
     * If ok to save, a listing object is passed to the presenter to save the data
     */
    @SuppressLint("CheckResult")
    private void saveData() {
        boolean okToSave = true;

        if (mImages.size() == 0) {
            okToSave = false;
            ToastModifications.createToast(this, getString(R.string.at_least_one_photo), Toast.LENGTH_LONG);
        } else {
            for (View v : mAllEditTexts) {
                EditText thisEditText = (EditText) v;
                if (thisEditText.getText().toString().equals("")) {
                    okToSave = false;
                    ToastModifications.createToast(this, getString(R.string.enter_value_for) + thisEditText.getTag(), Toast.LENGTH_SHORT);
                    break;
                }
            }
        }

        if (okToSave) {
            new Thread(new Runnable() {
                @SuppressWarnings("ConstantConditions")
                @Override
                public void run() {
                    String[] imageDescrps = mImageDescription.toArray(new String[0]);
                    String saleDate;

                    if (mListingBeingEdited != null) {
                        String editingListingSoldDate = mListingBeingEdited.getSaleDate();
                        if (!editingListingSoldDate.equals("")) { //it has been sold previously
                            saleDate = determineSaveDate(true);
                        } else { //it has not been sold
                            saleDate = determineSaveDate(false);
                        }

                    } else {
                        saleDate = determineSaveDate(false);
                    }

                    Listing listingToAdd = new Listing(
                            mEditingId == null ? DEFAULT_LISTING_ID : mEditingId,
                            mTypeSpinner.getSelectedItem().toString(),
                            Double.valueOf(mPriceEditText.getText().toString()),
                            Double.valueOf(mSurfaceAreaText.getText().toString()),
                            Integer.valueOf(mBedroomsSpinner.getSelectedItem().toString()),
                            mDescriptionEditText.getText().toString(),
                            ImageTools.BitmapsToByteArray(mImages),
                            imageDescrps,
                            mAddressPostcodeEditText.getText().toString(),
                            mAddressNumberEditText.getText().toString(),
                            mAddressStreetEditText.getText().toString(),
                            mAddressTownEditText.getText().toString(),
                            mAddressCountyEditText.getText().toString(),
                            mPoiAutocomplete.getText().toString(),
                            mEditing ? mListingBeingEdited.getPostedDate() : Utils.getTodayDate(),
                            saleDate,
                            mListingBeingEdited != null ? mListingBeingEdited.getAgent() :
                                    (FirebaseHelper.getLoggedInUser() != null ? FirebaseHelper.getLoggedInUser() : getString(R.string.unknown_agent)),
                            Utils.getTodayDate(),
                            !mBuyOrLetSwitch.isChecked() ? BUY_STRING : LET_STRING,
                            mSaleStatusImage.getTag().toString().equals(FOR_SALE_TAG));

                    if (mEditing) {
                        listingToAdd.setEditingAgent(FirebaseHelper.getLoggedInUser() != null ? FirebaseHelper.getLoggedInUser() : getString(R.string.unknown_agent));
                    }

                    mPresenter.addListing(getApplicationContext(), listingToAdd, mEditing);
                }

            }).start();


            createSaveListingProgressBar(); //create the progress bar to display to the user that saving is taking place
        }
    }


    /**
     * Determines the date we should show for the sale date
     *
     * @param soldPreviously has the property been sold previously and we are mEditing it to change the sale status?
     * @return the string to be saved relating to the sold date
     */
    private String determineSaveDate(boolean soldPreviously) {
        if (soldPreviously) {
            switch (mSaleStatusImage.getTag().toString()) {
                case FOR_SALE_TAG:
                    return "";

                case SOLD_TAG:
                    return mListingBeingEdited.getSaleDate();

            }
        } else {
            switch (mSaleStatusImage.getTag().toString()) {
                case FOR_SALE_TAG:
                    return "";

                case SOLD_TAG:
                    return Utils.getTodayDate();
            }
        }
        return "";
    }


    private void createSaveListingProgressBar() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.saving_listing));
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
    }


    //called from presenters getPhotoFromCamera and getPhotoFromDevice
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PICK_FROM_GALLERY_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Uri targetUri = data.getData();
                    String path = ImageFilePath.getPath(this, data.getData());
                    Bitmap bitmap;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), targetUri);
                        Bitmap finalBitmap = BitmapMods.modifyOrientation(bitmap, path);
                        mImages.add(BitmapMods.getResizedBitmap(finalBitmap, 400));
                        initializeRecyclerView();
                    } catch (Exception e) {
                        onPhotoError();
                        e.printStackTrace();
                    }
                }
                break;

            case PICK_FROM_CAMERA_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    try {
                        @SuppressWarnings("ConstantConditions") Bitmap photo = (Bitmap) data.getExtras().get("data");
                        mImages.add(photo);
                        initializeRecyclerView();
                    } catch (Exception e) {
                        onPhotoError();
                        e.printStackTrace();
                    }
                }
        }
    }


    /**
     * called if ther was an error getting a photo
     */
    private void onPhotoError() {
        ToastModifications.createToast(this, getString(R.string.error_saving_photo), Toast.LENGTH_LONG);
    }


    /**
     * Callback for the result from requesting permissions. This method is invoked for every call on requestPermissions(String[], int).
     *
     * @param requestCode  the request code, in this case either PICK_FROM_GALLERY_REQUEST_CODE or PICK_FROM_CAMERA_REQUEST_CODE
     * @param permissions  the permissions requested
     * @param grantResults the results of the persmission request
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PICK_FROM_GALLERY_REQUEST_CODE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mPresenter.getPhotoFromDevice();
                } else {
                    Snackbar.make(getWindow().getDecorView().getRootView(), R.string.permission_error, Snackbar.LENGTH_LONG)
                            .setAction(R.string.give_access, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (ActivityCompat.shouldShowRequestPermissionRationale(AddListingView.this, READ_EXTERNAL_STORAGE)) {
                                        mPresenter.getPhotoFromDevice();
                                    } else {
                                        //The user has permanently denied permission - so take them to the app settings so they can manually enable the permission
                                        openSettingsforApp();
                                    }
                                }
                            }).show();
                }
                break;

            case PICK_FROM_CAMERA_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mPresenter.getPhotoFromCamera();
                } else {
                    Snackbar.make(getWindow().getDecorView().getRootView(), R.string.permission_error, Snackbar.LENGTH_LONG)
                            .setAction(R.string.give_access, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (ActivityCompat.shouldShowRequestPermissionRationale(AddListingView.this, READ_EXTERNAL_STORAGE)) {
                                        mPresenter.getPhotoFromCamera();
                                    } else {
                                        //The user has permanently denied permission - so take them to the app settings so they can manually enable the permission
                                        openSettingsforApp();
                                    }
                                }
                            }).show();
                }

        }
    }


    /**
     * Called by this activites presenter when required
     *
     * @return this
     */
    @Override
    public Activity getViewActivity() {
        return this;
    }


    /**
     * Callback from the presenter when a listing has been added in order to update the UI
     *
     * @param error whether an error occurred saving the listing
     */
    @Override
    public void addingListingCompleted(boolean error) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }

        if (error) {
            ToastModifications.createToast(AddListingView.this, getString(R.string.error_saving), Toast.LENGTH_LONG);
        } else {
            ToastModifications.createToast(AddListingView.this, getString(R.string.listing_saved), Toast.LENGTH_LONG);
            mEditing = false;
            mListingBeingEdited = null;
            onBackPressed();
        }
    }


    /**
     * The user has permanently denied permission - so take them to the app settings so they can manually enable the permission if required
     */
    private void openSettingsforApp() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", AddListingView.this.getPackageName(), null);
        intent.setData(uri);
        AddListingView.this.startActivity(intent);
    }


    /**
     * Called from the presented to open the camera
     */
    @Override
    public void startCameraIntent() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, PICK_FROM_CAMERA_REQUEST_CODE);
    }


    /**
     * Called from the presented to open the gallery
     */
    @Override
    public void startDeviceImageIntent() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_FROM_GALLERY_REQUEST_CODE);
    }


    /**
     * Called from within the recyclerview adapter when an mImages description is changes, the presented will then handle updating the arrayList and callback to
     * {@link AddListingView#imageDescriptionsChanged}
     *
     * @param desc     the new description
     * @param position the position in the arraylist
     */
    @Override
    public void changedImageDescr(String desc, int position) {
        mPresenter.changedImageDescr(desc, position, mImageDescription);
    }


    @Override
    public void imageDescriptionsChanged(ArrayList<String> image_description) {
        this.mImageDescription = image_description;
    }


    /**
     * Called when an image is removed from within the recyclerview adapter. The neccessary changes are passed onto the presented to handle
     * which will then call back to {@link AddListingView#imageDeleted(ArrayList, ArrayList)}
     *
     * @param position the position of the image in the ArrayList
     */
    @Override
    public void deletedImage(int position) {
        mPresenter.deleteImage(mImages, mImageDescription, position);
    }


    @Override
    public void imageDeleted(ArrayList<Bitmap> images, ArrayList<String> image_descs) {
        this.mImages = images;
        this.mImageDescription = image_descs;
        mAdapter.notifyDataSetChanged();
    }


    /**
     * Handles the events of the home button being pressed
     *
     * @param item the menu item selected
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            AlertDialog.Builder confirmDiagBuilder = new AlertDialog.Builder(AddListingView.this);
            confirmDiagBuilder.setMessage(R.string.want_to_save);
            confirmDiagBuilder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mSaveButton.callOnClick();
                }
            });

            confirmDiagBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    onBackPressed();
                }
            });

            confirmDiagBuilder.show();
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), MainActivityView.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.clear(); //clear any disposables
        mPresenter.unregisterReciever(); //unregister the reciever
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        //prevents the super method being called as this activity has immersive mode disabled
    }

}

