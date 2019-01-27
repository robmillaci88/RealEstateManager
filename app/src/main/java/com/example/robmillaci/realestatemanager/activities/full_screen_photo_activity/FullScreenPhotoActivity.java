package com.example.robmillaci.realestatemanager.activities.full_screen_photo_activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.robmillaci.realestatemanager.R;
import com.example.robmillaci.realestatemanager.activities.BaseActivity;

import java.io.FileInputStream;

/**
 * This class is responsible for display a photo in Full screen when adding a listing
 */
public class FullScreenPhotoActivity extends BaseActivity {
    private static final String IMAGE_KEY = "image"; //The dunble key for the image passed to this activity in the intent

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(getString(R.string.photo));

        ImageView fullScreenImageView = findViewById(R.id.fullscreenphoto);

        Bitmap bmp = null;
        String filename = getIntent().getStringExtra(IMAGE_KEY); //the the image file name stored on the local device
        try {
            FileInputStream is = this.openFileInput(filename); //open an input stream to decode the image passing in the name of the file to open
            bmp = BitmapFactory.decodeStream(is); //decode the stream into a bitmap
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (getIntent() != null && getIntent().getExtras() != null) {
            Glide.with(this).load(bmp).into(fullScreenImageView); //load the bitmap into the imageview using Glide
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }

        return true;
    }
}
