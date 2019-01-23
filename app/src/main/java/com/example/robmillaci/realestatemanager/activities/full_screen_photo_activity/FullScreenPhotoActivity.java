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

public class FullScreenPhotoActivity extends BaseActivity {
    private static final String IMAGE_KEY = "image";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(getString(R.string.photo));

        ImageView fullScreenImageView = findViewById(R.id.fullscreenphoto);

        Bitmap bmp = null;
        String filename = getIntent().getStringExtra(IMAGE_KEY);
        try {
            FileInputStream is = this.openFileInput(filename);
            bmp = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (getIntent() != null && getIntent().getExtras() != null) {
            Glide.with(this).load(bmp).into(fullScreenImageView);
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
