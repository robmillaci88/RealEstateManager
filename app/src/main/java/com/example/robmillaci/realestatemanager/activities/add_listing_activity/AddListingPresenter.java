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


public class AddListingPresenter extends BroadcastReceiver {
    static final int PICK_FROM_GALLERY_REQUEST_CODE = 0;
    static final int PICK_FROM_CAMERA_REQUEST_CODE = 1;
    static final String EDITING_KEY = "editing";
    AddListingPresenter myBroadCastReceiver;
    static final String BROADCAST_ACTION = "com.example.robmillaci.realestatemanager.AddListingPresenter";


    private AddListingPresenter.View view;

    AddListingPresenter(AddListingPresenter.View view) {
        this.view = view;
    }


    public void addListing(Context context, Listing listing, boolean editing) {
        Log.d("addlisting", "addListing called");

        registerMyReceiver();

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
            view.getViewActivity().registerReceiver(myBroadCastReceiver, intentFilter);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }


    public void getPhotoFromDevice() {
        try {
            if (ActivityCompat.checkSelfPermission(view.getViewActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((view.getViewActivity()), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICK_FROM_GALLERY_REQUEST_CODE);
            } else {
                view.startDeviceImageIntent();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void getPhotoFromCamera() {
        if (ActivityCompat.checkSelfPermission(view.getViewActivity(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((view.getViewActivity()), new String[]{Manifest.permission.CAMERA},
                    PICK_FROM_CAMERA_REQUEST_CODE);
        } else {
            view.startCameraIntent();
        }
    }

    public void changedImageDescr(String desc, int position, ArrayList<String> image_description) {
        try {
            image_description.set(position, desc);
        } catch (Exception e) {
            image_description.add(desc);
        }

        view.imageDescriptionsChanged(image_description);

    }

    public void deleteImage(ArrayList<Bitmap> images, ArrayList<String> image_description, int position) {
        images.remove(position);
        try {
            image_description.remove(position);
        } catch (Exception e) {
            e.printStackTrace();
        }
        view.imageDeleted(images, image_description);
    }


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


    public interface View {
        void startCameraIntent();

        void startDeviceImageIntent();

        void imageDescriptionsChanged(ArrayList<String> images_descs);

        void imageDeleted(ArrayList<Bitmap> images, ArrayList<String> image_descs);

        Activity getViewActivity();

        void addingListingCompleted(boolean error);
    }
}
