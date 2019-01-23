package com.example.robmillaci.realestatemanager.activities.add_listing_activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.robmillaci.realestatemanager.Adapters.ImagesRecyclerViewAdapter;
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

public class AddListingView extends BaseActivity implements AddListingPresenter.View, ImagesRecyclerViewAdapter.IactivityCallback {
    private static final int MAX_NUM_IMAGES = 15;
    private static final int FIREBASE_ID = 1;
    private static final int LOCAL_DB_ID = 0;


    public static final String SOLD_TAG = "sold";
    public static final String FOR_SALE_TAG = "forSale";

    private ArrayList<Bitmap> images;
    private ArrayList<String> image_description;
    private ArrayList<View> allEditTexts;
    private boolean editing = false;
    private AddListingPresenter presenter;
    private String editingId = null;

    private CompositeDisposable mCompositeDisposable;

    private RecyclerView imagesRecyclerView;
    private ImagesRecyclerViewAdapter mAdapter;

    private Button add_picture_btn;

    private ImageView saveButton;
    private ImageView sale_status_image;

    private Spinner type_spinner;
    private Spinner bedrooms_spinner;

    private EditText surface_area_text;
    private EditText price_edit_text;
    private EditText poi_edit_text;
    private EditText address_postcode_editText;
    private EditText address_number_editText;
    private EditText address_street_editText;
    private EditText address_town_editText;
    private EditText address_county_editText;
    private EditText description_edit_text;

    private SwitchCompat buy_or_let;
    private ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.new_listing_activity_title));

        this.presenter = new AddListingPresenter(this);
        mCompositeDisposable = new CompositeDisposable();
        images = new ArrayList<>();

        image_description = new ArrayList<>();

        initializeViews();
        initializeClickEvents();
        initializeRecyclerView();

        Bundle intentExtras = getIntent().getExtras();
        if (intentExtras != null) {
            Listing editingListing = (Listing) intentExtras.getSerializable(EDIT_LISTING_BUNDLE_KEY);
            if (editingListing != null) prepareEdit(editingListing);
        }
    }


    private void prepareEdit(Listing editingListing) {
        int selection = 0;
        editing = true;
        editingId = editingListing.getId();

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

        type_spinner.setSelection(selection);
        bedrooms_spinner.setSelection(editingListing.getNumbOfBedRooms() - 1);
        surface_area_text.setText(String.valueOf(editingListing.getSurfaceArea()));
        price_edit_text.setText(String.valueOf(editingListing.getPrice()).substring(0,String.valueOf(editingListing.getPrice()).indexOf(".")));
        poi_edit_text.setText(editingListing.getPoi());
        address_postcode_editText.setText(editingListing.getAddress_postcode());
        address_number_editText.setText(editingListing.getAddress_number());
        address_street_editText.setText(editingListing.getAddress_street());
        address_town_editText.setText(editingListing.getAddress_town());
        address_county_editText.setText(editingListing.getAddress_county());
        description_edit_text.setText(editingListing.getDescr());

        if (editingListing.getBuyOrLet().toLowerCase().equals("buy")) {
            buy_or_let.setChecked(false);
        } else {
            buy_or_let.setChecked(true);
        }

        if (editingListing.isAvailable()) {
            updateListingSoldStatus(false);
        } else {
            updateListingSoldStatus(true);
        }

        if (editingListing.getFirebasePhotos() == null) {
            restoreImages(editingListing.getPhotos(), editingListing.getPhotoDescriptions(), LOCAL_DB_ID);
        } else {
            restoreImages(editingListing.getFirebasePhotos(), editingListing.getPhotoDescriptions(), FIREBASE_ID);
        }
    }


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


    private void initializeViews() {
        allEditTexts = new ArrayList<>();

        imagesRecyclerView = findViewById(R.id.images_recycler_view);
        add_picture_btn = findViewById(R.id.add_picture_btn);

        sale_status_image = findViewById(R.id.sale_status_image);
        sale_status_image.setTag(FOR_SALE_TAG);

        type_spinner = findViewById(R.id.type_spinner);
        bedrooms_spinner = findViewById(R.id.rooms_spinner);

        type_spinner.setAdapter(new ArrayAdapter<>(this,R.layout.spinner_item,getResources().getStringArray(R.array.spinner_types)));
        bedrooms_spinner.setAdapter(new ArrayAdapter<>(this,R.layout.spinner_item,getResources().getStringArray(R.array.spinner_number_of_rooms)));

        surface_area_text = findViewById(R.id.surface_area_text);
        price_edit_text = findViewById(R.id.price_edit_text);
        poi_edit_text = findViewById(R.id.poi_edit_text);
        address_postcode_editText = findViewById(R.id.address_postcode_et);
        address_number_editText = findViewById(R.id.address_number_et);
        address_street_editText = findViewById(R.id.address_street_et);
        address_town_editText = findViewById(R.id.address_town_et);
        address_county_editText = findViewById(R.id.address_county_et);
        description_edit_text = findViewById(R.id.description_edit_text);
        buy_or_let = findViewById(R.id.buy_or_let);

        saveButton = findViewById(R.id.savebtn);

        allEditTexts.add(surface_area_text);
        allEditTexts.add(price_edit_text);
        allEditTexts.add(poi_edit_text);
        allEditTexts.add(address_postcode_editText);
        allEditTexts.add(address_number_editText);
        allEditTexts.add(address_street_editText);
        allEditTexts.add(address_town_editText);
        allEditTexts.add(address_county_editText);
        allEditTexts.add(description_edit_text);

    }


    private void initializeRecyclerView() {
        if (images != null) {
            if (images.size() > 0) {
                imagesRecyclerView.setBackground(null);
            } else {
                imagesRecyclerView.setBackgroundResource(R.drawable.placeholder_image);
            }

            imagesRecyclerView.setLayoutManager(new GridLayoutManager(this,3));

            mAdapter = new ImagesRecyclerViewAdapter(this, images, image_description, this);
            imagesRecyclerView.setAdapter(mAdapter);
        }
    }


    @SuppressLint("CheckResult")
    private void initializeClickEvents() {
        //create the onlick event for the add picture button
        Disposable pictureClick = RxView.clicks(add_picture_btn)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) {
                        if (images.size() < MAX_NUM_IMAGES) {
                            displayDialog();
                        } else {
                            Toast.makeText(AddListingView.this, R.string.maximum_images, Toast.LENGTH_LONG).show();
                        }
                    }
                });
        mCompositeDisposable.add(pictureClick);


        //create the on click event for the save button
        final Disposable save = RxView.clicks(saveButton)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) {
                        saveData();
                    }
                });
        mCompositeDisposable.add(save);


        //create onclick for the property status image
        Disposable statusChange = RxView.clicks(sale_status_image)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) {
                        switch (sale_status_image.getTag().toString()) {
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


    private void updateListingSoldStatus(boolean sold) {
        if (sold) {
            sale_status_image.setBackgroundResource(R.drawable.sold);
            sale_status_image.setTag(SOLD_TAG);
        } else {
            sale_status_image.setBackgroundResource(R.drawable.for_sale);
            sale_status_image.setTag(FOR_SALE_TAG);
        }
    }

    @SuppressLint("CheckResult")
    private void displayDialog() {
        final AlertDialog.Builder chooseBuilder = new AlertDialog.Builder(this);
        @SuppressLint("InflateParams") View v = LayoutInflater.from(this).inflate(R.layout.picture_method_chooser, null);
        chooseBuilder.setView(v);
        chooseBuilder.setPositiveButton(R.string.close_button, null);
        final AlertDialog chooseDialog = chooseBuilder.show();

        RxView.clicks(v.findViewById(R.id.camera_choice))
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) {
                        presenter.getPhotoFromCamera();
                        chooseDialog.dismiss();
                    }
                });

        RxView.clicks(v.findViewById(R.id.gallery_choice))
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) {
                        presenter.getPhotoFromDevice();
                        chooseDialog.dismiss();
                    }
                });
    }


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

                    presenter.addListing(getApplicationContext(), new Listing(
                            editingId == null ? DEFAULT_LISTING_ID : editingId,
                            type_spinner.getSelectedItem().toString(),
                            Double.valueOf(price_edit_text.getText().toString()),
                            Double.valueOf(surface_area_text.getText().toString()),
                            Integer.valueOf(bedrooms_spinner.getSelectedItem().toString()),
                            description_edit_text.getText().toString(),
                            ArrayListTools.BitmapsToByteArray(images),
                            imageDescrps,
                            address_postcode_editText.getText().toString(),
                            address_number_editText.getText().toString(),
                            address_street_editText.getText().toString(),
                            address_town_editText.getText().toString(),
                            address_county_editText.getText().toString(),
                            poi_edit_text.getText().toString(),
                            sale_status_image.getTag().toString(),
                            Utils.getTodayDate(),
                            "",
                            FirebaseHelper.getLoggedInUser(),
                            Utils.getTodayDate(),
                            !buy_or_let.isChecked() ? "buy" : "let"), editing
                    );
                }
            }).start();

           createSaveListingProgressBar();
        }
    }




    private void createSaveListingProgressBar() {
        pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.saving_listing));
        pd.show();
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


    private void onPhotoError() {
        ToastModifications.createToast(this, getString(R.string.error_saving_photo), Toast.LENGTH_LONG);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PICK_FROM_GALLERY_REQUEST_CODE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    presenter.getPhotoFromDevice();
                } else {
                    Snackbar.make(getWindow().getDecorView().getRootView(), R.string.permission_error, Snackbar.LENGTH_LONG)
                            .setAction(R.string.give_access, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (ActivityCompat.shouldShowRequestPermissionRationale(AddListingView.this, READ_EXTERNAL_STORAGE)) {
                                        presenter.getPhotoFromDevice();
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
                    presenter.getPhotoFromCamera();
                } else {
                    Snackbar.make(getWindow().getDecorView().getRootView(), R.string.permission_error, Snackbar.LENGTH_LONG)
                            .setAction(R.string.give_access, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (ActivityCompat.shouldShowRequestPermissionRationale(AddListingView.this, READ_EXTERNAL_STORAGE)) {
                                        presenter.getPhotoFromCamera();
                                    } else {
                                        //The user has permanently denied permission - so take them to the app settings so they can manually enable the permission
                                        openSettingsforApp();
                                    }
                                }
                            }).show();
                }

        }
    }


    @Override
    public Activity getViewActivity() {
        return this;
    }

    @Override
    public void addingListingCompleted() {
        if (pd.isShowing()) {
            pd.dismiss();
        }
        ToastModifications.createToast(AddListingView.this, getString(R.string.listing_saved), Toast.LENGTH_LONG);
        onBackPressed();
    }

    private void openSettingsforApp() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", AddListingView.this.getPackageName(), null);
        intent.setData(uri);
        AddListingView.this.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.clear();
        presenter.unregisterReciever();
    }


    @Override
    public void startCameraIntent() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, PICK_FROM_CAMERA_REQUEST_CODE);
    }

    @Override
    public void startDeviceImageIntent() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_FROM_GALLERY_REQUEST_CODE);
    }


    @Override
    public void changedImageDescr(String desc, int position) {
        presenter.changedImageDescr(desc, position, image_description);
    }

    @Override
    public void imageDescriptionsChanged(ArrayList<String> image_description) {
        this.image_description = image_description;
    }


    @Override
    public void deletedImage(int position) {
        presenter.deleteImage(images, image_description, position);
    }

    @Override
    public void imageDeleted(ArrayList<Bitmap> images, ArrayList<String> image_descs) {
        this.images = images;
        this.image_description = image_descs;
        mAdapter.notifyDataSetChanged();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                AlertDialog.Builder confirmDiagBuilder = new AlertDialog.Builder(AddListingView.this);
                confirmDiagBuilder.setMessage(R.string.want_to_save);
                confirmDiagBuilder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        saveButton.callOnClick();
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


}

