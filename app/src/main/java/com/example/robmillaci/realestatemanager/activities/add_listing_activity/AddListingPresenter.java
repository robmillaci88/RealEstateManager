package com.example.robmillaci.realestatemanager.activities.add_listing_activity;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.example.robmillaci.realestatemanager.data_objects.Listing;
import com.example.robmillaci.realestatemanager.databases.firebase.FirebaseHelper;
import com.example.robmillaci.realestatemanager.utils.ArrayListTools;
import com.example.robmillaci.realestatemanager.utils.SharedPreferenceHelper;
import com.example.robmillaci.realestatemanager.utils.Utils;

import java.util.ArrayList;

import static com.example.robmillaci.realestatemanager.activities.add_listing_activity.AddListingService.RESULTS;
import static com.example.robmillaci.realestatemanager.data_objects.Listing.DEFAULT_LISTING_ID;

/**
 * This class is the presenter layer between {@link AddListingView} and {@link AddListingService}
 *
 */
public class AddListingPresenter extends BroadcastReceiver {
    static final int PICK_FROM_GALLERY_REQUEST_CODE = 0; //request code for Gallery images
    static final int PICK_FROM_CAMERA_REQUEST_CODE = 1; //request code for camera images
    static final String EDITING_KEY = "editing"; //bundle key for editing listing
    AddListingPresenter myBroadCastReceiver; //Variable to hold the reference to this class acting as a broadcast reciever in order to unregister
    static final String BROADCAST_ACTION = "com.example.robmillaci.realestatemanager.AddListingPresenter"; //the broadcast action


    private AddListingPresenter.View view; //this presenters view in order to communicate back

    AddListingPresenter(AddListingPresenter.View view) {
        this.view = view;
    }


    /**
     * Called from {@link AddListingView} when saving a listing
     * @param context the context of the calling activity
     * @param listing the listing to add
     * @param editing are we editing this listing or is it new?
     */
    public void addListing(Context context, Listing listing, boolean editing) {
        registerMyReceiver(); //register this class as a broadcast receiver

        Intent addListingServiceIntent = new Intent(context, AddListingService.class);
        addListingServiceIntent.putExtra(EDITING_KEY, editing);
        new SharedPreferenceHelper(context).addListingToSharedPref(listing);
        context.startService(addListingServiceIntent);
    }

    private void registerMyReceiver() {
        try {
            myBroadCastReceiver = this;
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(BROADCAST_ACTION);
            view.getViewActivity().registerReceiver(this, intentFilter);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }


    /**
     * Called from {@link AddListingView#displayPictureMethodDialog()} to perform permission checks and then inform the view of the action to take
     */
    public void getPhotoFromDevice() {
        try {
            if (ActivityCompat.checkSelfPermission(view.getViewActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((view.getViewActivity()), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PICK_FROM_GALLERY_REQUEST_CODE);
            } else {
                view.startDeviceImageIntent();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Called from {@link AddListingView#displayPictureMethodDialog()} to perform permission checks and then inform the view of the action to take
     */
    public void getPhotoFromCamera() {
        if (ActivityCompat.checkSelfPermission(view.getViewActivity(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((view.getViewActivity()), new String[]{Manifest.permission.CAMERA},
                    PICK_FROM_CAMERA_REQUEST_CODE);
        } else {
            view.startCameraIntent();
        }
    }


    /**
     * Called from {@link AddListingView#changedImageDescr(String, int)} to update the arraylist of image descriptions for a listing and return the new list
     */
    public void changedImageDescr(String desc, int position, ArrayList<String> image_description) {
        try {
            image_description.set(position, desc);
        } catch (Exception e) {
            image_description.add(desc);
        }

        view.imageDescriptionsChanged(image_description);

    }


    /**
     * Called from {@link AddListingView#deletedImage(int)} to update the arraylist of images for a listing and return the new list
     */
    public void deleteImage(ArrayList<Bitmap> images, ArrayList<String> image_description, int position) {
        images.remove(position);
        try {
            image_description.remove(position);
        } catch (Exception e) {
            e.printStackTrace();
        }
        view.imageDeleted(images, image_description);
    }


    /**
     * Called on reciept of a broadcast from {@link AddListingService} which is sent when a listing has been added to the database
     * This method calls back to the view to update the UI when a listing is saved
     * @param context the context of the broadcast
     * @param intent the intent passed
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        //notify the view that the listing has been added
        boolean error = intent.getBooleanExtra(RESULTS,false);
        unregisterReciever();
        view.addingListingCompleted(error);
    }

     void unregisterReciever() {
        if (myBroadCastReceiver!= null){
            try {
                view.getViewActivity().unregisterReceiver(myBroadCastReceiver);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }



    //The views interface methods
    public interface View {
        void startCameraIntent();

        void startDeviceImageIntent();

        void imageDescriptionsChanged(ArrayList<String> images_descs);

        void imageDeleted(ArrayList<Bitmap> images, ArrayList<String> image_descs);

        Activity getViewActivity();

        void addingListingCompleted(boolean error);
    }
}
