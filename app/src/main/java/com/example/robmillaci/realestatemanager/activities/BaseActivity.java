package com.example.robmillaci.realestatemanager.activities;

import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.example.robmillaci.realestatemanager.utils.Utils;

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

       if (!Utils.isTablet(getApplicationContext())) {
           setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
           setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
       }
    }


}
