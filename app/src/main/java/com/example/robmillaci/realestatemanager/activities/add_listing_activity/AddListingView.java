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
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.robmillaci.realestatemanager.adapters.ImagesRecyclerViewAdapter;
import com.example.robmillaci.realestatemanager.R;
import com.example.robmillaci.realestatemanager.activities.BaseActivity;
import com.example.robmillaci.realestatemanager.activities.main_activity.MainActivityView;
import com.example.robmillaci.realestatemanager.data_objects.Listing;
import com.example.robmillaci.realestatemanager.databases.firebase.FirebaseHelper;
import com.example.robmillaci.realestatemanager.utils.ArrayListTools;
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
public class AddListingView extends BaseActivity implements AddListingPresenter.View, ImagesRecyclerViewAdapter.IactivityCallback {
    private static final int MAX_NUM_IMAGES = 15; //max number of images for a listing
    private static final int FIREBASE_ID = 1; //Id to represent Firebase
    private static final int LOCAL_DB_ID = 0; //Id to represent local DB

    public static final String SOLD_TAG = "sold"; //the tag to assign if a listing is sold
    public static final String FOR_SALE_TAG = "forSale"; //the tag to assign if a listing is for sale
    private static final String BUY_STRING = "buy"; //the database value if the listing is categorized as BUY
    private static final String LET_STRING = "let";//the database value if the listing is categorized as BUY

    private ArrayList<Bitmap> images; //Arraylist of bitmap images to holder the listings images
    private ArrayList<String> image_description; //Arraylist of Strings = to holder the listings images descriptions
    private ArrayList<View> allEditTexts; //Arraylist to hold all edit texts in this view, in order to perform checks that required information is entered
    private boolean editing = false; //are we editing or adding a new listing?
    private String editingId = null; //the id of the listing that we are editing
    private Listing mListingBeingEdited; //the listing being edited

    private AddListingPresenter mPresenter; //the mPresenter of this class responsible for obtaining or sending any data to the model

    private CompositeDisposable mCompositeDisposable; //holds all disposables

    private RecyclerView mImagesRecyclerView; //the recyclerview to display the listings images
    private ImagesRecyclerViewAdapter mAdapter; //the adapter for the recyclerview

    private Button mAddPictureBtn; //the add button

    private ImageView mSaveButton; //the save button (as an imageview)
    private ImageView mSaleStatusImage; //the sale status

    private Spinner mTypeSpinner; //the type of listing spinner
    private Spinner mBedroomsSpinner; //the number of bedrooms spinner

    private EditText mSurfaceAreaText; //listings surface area
    private EditText mPriceEditText; //listings price
    private EditText mPoiEditText; //listings P.O.I
    private EditText mAddressPostcodeEditText; //listings post code
    private EditText mAddressNumberEditText; //listings address number
    private EditText mAddressStreetEditText; //listings address street
    private EditText mAddressTownEditText;//listings address town
    private EditText mAddressCountyEditText;//listings address county
    private EditText mDescriptionEditText;//listings address description

    private SwitchCompat mBuyOrLetSwitch; //the buy or let switch
    private ProgressDialog mProgressDialog; //the progress dialog displayed when saving a listing


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing); //set the view
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //add the home button to the action bar
        setTitle(getString(R.string.new_listing_activity_title));//set the title of the activity
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN); //dont display the keyboard when activity is created

        this.mPresenter = new AddListingPresenter(this); //create the presented
        mCompositeDisposable = new CompositeDisposable(); //create a composite disposable to hold all disposables

        images = new ArrayList<>(); //create a new arraylist to hold the listings images
        image_description = new ArrayList<>();//create a new arraylist to hold the listings images description

        initializeViews(); //see method comments
        initializeClickEvents();  //see method comments
        initializeRecyclerView();  //see method comments


        /*
         *check to see if we have a listing in the intent that created this activity, if we do then we are editing a pre existing listing
         */
        Bundle intentExtras = getIntent().getExtras();
        if (intentExtras != null) {
            Listing editingListing = (Listing) intentExtras.getSerializable(EDIT_LISTING_BUNDLE_KEY);
            if (editingListing != null){
                prepareEdit(editingListing);
            }
        }
    }


    /**
     * populate the views in this activity with the values defined for an already existing listing
     * @param editingListing the listing that is being edited.
     */
    private void prepareEdit(Listing editingListing) {
        setTitle(String.format("%s %s %s",getString(R.string.editing_title), editingListing.getAddress_number(), editingListing.getAddress_street()));
        int selection = 0;
        editing = true;
        editingId = editingListing.getId();
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
        mPriceEditText.setText(String.valueOf(editingListing.getPrice()).substring(0,String.valueOf(editingListing.getPrice()).indexOf(".")));
        mPoiEditText.setText(editingListing.getPoi());
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

        //todo get an error when trying to edit offline
        if (editingListing.getFirebasePhotos() == null) {
            restoreImages(editingListing.getPhotos(), editingListing.getPhotoDescriptions(), LOCAL_DB_ID);
        } else {
            restoreImages(editingListing.getFirebasePhotos(), editingListing.getPhotoDescriptions(), FIREBASE_ID);
        }
    }


    /**
     * If we are editing a listing, this method will retrieve either a byte[] or arraylist of uris that are used to restore the images for the listing
     * These images are then passed into the recyclerview adapter to display to the user
     * @param photos the photos byte[]'s or the uris (as a string)
     * @param photoDescriptions the descriptions of the photos
     * @param id the id used in the switch to determine if we are working with the local db or with Firebase
     */
    @SuppressWarnings("unchecked")
    private void restoreImages(Object photos, final String[] photoDescriptions, int id) {
        if (photos != null) {
            switch (id) {
                case LOCAL_DB_ID:
                    List<byte[]> localPhotos = (List<byte[]>) photos;
                    images = ImageTools.byteArrayToBitmaps(localPhotos);
                    image_description = new ArrayList<>(Arrays.asList(photoDescriptions));
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
                                    images.add(resource);
                                    image_description = new ArrayList<>(Arrays.asList(photoDescriptions));
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
        allEditTexts = new ArrayList<>();

        mImagesRecyclerView = findViewById(R.id.images_recycler_view);
        mAddPictureBtn = findViewById(R.id.add_picture_btn);

        mSaleStatusImage = findViewById(R.id.sale_status_image);
        mSaleStatusImage.setTag(FOR_SALE_TAG);

        mTypeSpinner = findViewById(R.id.type_spinner);
        mBedroomsSpinner = findViewById(R.id.rooms_spinner);

        mTypeSpinner.setAdapter(new ArrayAdapter<>(this,R.layout.spinner_item,getResources().getStringArray(R.array.spinner_types)));
        mBedroomsSpinner.setAdapter(new ArrayAdapter<>(this,R.layout.spinner_item,getResources().getStringArray(R.array.spinner_number_of_rooms)));

        mSurfaceAreaText = findViewById(R.id.surface_area_text);
        mPriceEditText = findViewById(R.id.price_edit_text);
        mPoiEditText = findViewById(R.id.poi_edit_text);
        mAddressPostcodeEditText = findViewById(R.id.address_postcode_et);
        mAddressNumberEditText = findViewById(R.id.address_number_et);
        mAddressStreetEditText = findViewById(R.id.address_street_et);
        mAddressTownEditText = findViewById(R.id.address_town_et);
        mAddressCountyEditText = findViewById(R.id.address_county_et);
        mDescriptionEditText = findViewById(R.id.description_edit_text);
        mBuyOrLetSwitch = findViewById(R.id.buy_or_let);

        mSaveButton = findViewById(R.id.savebtn);

        allEditTexts.add(mSurfaceAreaText);
        allEditTexts.add(mPriceEditText);
        allEditTexts.add(mPoiEditText);
        allEditTexts.add(mAddressPostcodeEditText);
        allEditTexts.add(mAddressNumberEditText);
        allEditTexts.add(mAddressStreetEditText);
        allEditTexts.add(mAddressTownEditText);
        allEditTexts.add(mAddressCountyEditText);
        allEditTexts.add(mDescriptionEditText);

    }


    //Created the recycler view to display a listings images
    private void initializeRecyclerView() {
        if (images != null) {
            if (images.size() > 0) {
                mImagesRecyclerView.setBackground(null);
            } else {
                mImagesRecyclerView.setBackgroundResource(R.drawable.placeholder_image);
            }

            mImagesRecyclerView.setLayoutManager(new GridLayoutManager(this,3));

            mAdapter = new ImagesRecyclerViewAdapter(this, images, image_description, this);
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
                        if (images.size() < MAX_NUM_IMAGES) {
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
     * update the sold status of a listing, changes the sales status image and tag depending on wether the listing is sold or not
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
     *
     */
    @SuppressLint("CheckResult")
    private void saveData() {
        boolean okToSave = true;

        if (images.size() == 0) {
            okToSave = false;
            ToastModifications.createToast(this, getString(R.string.at_least_one_photo), Toast.LENGTH_LONG);
        } else {
            for (View v : allEditTexts) {
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
                @Override
                public void run() {
                    String[] imageDescrps = image_description.toArray(new String[image_description.size()]);
                    String saleDate = "";

                    if (mListingBeingEdited != null) {
                        String editingListingSoldDate = mListingBeingEdited.getSaleDate();
                        if (!editingListingSoldDate.equals("")) { //it has been sold previously
                            saleDate = determineSaveDate(true);
                        }else { //it has not been sold
                            saleDate = determineSaveDate(false);
                        }

                    }else {
                        saleDate = determineSaveDate(false);
                    }

                    mPresenter.addListing(getApplicationContext(), new Listing(
                            editingId == null ? DEFAULT_LISTING_ID : editingId,
                            mTypeSpinner.getSelectedItem().toString(),
                            Double.valueOf(mPriceEditText.getText().toString()),
                            Double.valueOf(mSurfaceAreaText.getText().toString()),
                            Integer.valueOf(mBedroomsSpinner.getSelectedItem().toString()),
                            mDescriptionEditText.getText().toString(),
                            ArrayListTools.BitmapsToByteArray(images),
                            imageDescrps,
                            mAddressPostcodeEditText.getText().toString(),
                            mAddressNumberEditText.getText().toString(),
                            mAddressStreetEditText.getText().toString(),
                            mAddressTownEditText.getText().toString(),
                            mAddressCountyEditText.getText().toString(),
                            mPoiEditText.getText().toString(),
                            editing ? mListingBeingEdited.getPostedDate() : Utils.getTodayDate(),
                            saleDate,
                            FirebaseHelper.getLoggedInUser() != null ? FirebaseHelper.getLoggedInUser() : getString(R.string.unknown_agent),
                            Utils.getTodayDate(),
                            !mBuyOrLetSwitch.isChecked() ? BUY_STRING : LET_STRING,
                             mSaleStatusImage.getTag().toString().equals(FOR_SALE_TAG)), editing
                    );
                }
            }).start();

            createSaveListingProgressBar(); //create the progress bar to display to the user that saving is taking place
        }
    }


    /**
     * Determines the date we should show for the sale date
     * @param soldPreviously has the property been sold previously and we are editing it to change the sale status?
     * @return the string to be saved relating to the sold date
     */
    private String determineSaveDate(boolean soldPreviously){
        if (soldPreviously){
            switch (mSaleStatusImage.getTag().toString()) {
                case FOR_SALE_TAG:
                   return "";

                case SOLD_TAG:
                    return mListingBeingEdited.getSaleDate();

            }
        }else {
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
                        images.add(BitmapMods.getResizedBitmap(finalBitmap, 400));
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
                        images.add(photo);
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
     * @param requestCode the request code, in this case either PICK_FROM_GALLERY_REQUEST_CODE or PICK_FROM_CAMERA_REQUEST_CODE
     * @param permissions the permissions requested
     * @param grantResults the results of the persmission request
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
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
     * @return this
     */
    @Override
    public Activity getViewActivity() {
        return this;
    }


    /**
     * Callback from the presenter when a listing has been added in order to update the UI
     * @param error wether an error occurred saving the listing
     */
    @Override
    public void addingListingCompleted(boolean error) {
        if (mProgressDialog !=null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }

        if (error){
            ToastModifications.createToast(AddListingView.this, getString(R.string.error_saving), Toast.LENGTH_LONG);
        }else {
            ToastModifications.createToast(AddListingView.this, getString(R.string.listing_saved), Toast.LENGTH_LONG);
            editing = false;
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
     * Called from within the recyclerview adapter when an images description is changes, the presented will then handle updating the arrayList and callback to
     * {@link AddListingView#imageDescriptionsChanged}
     * @param desc the new description
     * @param position the position in the arraylist
     */
    @Override
    public void changedImageDescr(String desc, int position) {
        mPresenter.changedImageDescr(desc, position, image_description);
    }


    @Override
    public void imageDescriptionsChanged(ArrayList<String> image_description) {
        this.image_description = image_description;
    }


    /**
     * Called when an image is removed from within the recyclerview adapter. The neccessary changes are passed onto the presented to handle
     * which will then call back to {@link AddListingView#imageDeleted(ArrayList, ArrayList)}
     * @param position the position of the image in the ArrayList
     */
    @Override
    public void deletedImage(int position) {
        mPresenter.deleteImage(images, image_description, position);
    }



    @Override
    public void imageDeleted(ArrayList<Bitmap> images, ArrayList<String> image_descs) {
        this.images = images;
        this.image_description = image_descs;
        mAdapter.notifyDataSetChanged();
    }


    /**
     * Handles the events of the home button being pressed
     * @param item the menu item selected
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
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
                break;
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

}

