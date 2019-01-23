package com.example.robmillaci.realestatemanager.activities.customer_account;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;

import com.example.robmillaci.realestatemanager.R;
import com.example.robmillaci.realestatemanager.activities.BaseActivity;
import com.example.robmillaci.realestatemanager.activities.contact_activity.ContactActivity;
import com.jakewharton.rxbinding3.view.RxView;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import kotlin.Unit;

public class PrivacySettingsActivity extends BaseActivity {
    private Button contactUs;
    private CompositeDisposable mCompositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_settings);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initializeViews();
        setOnClicks();
    }

    private void initializeViews() {
        contactUs = findViewById(R.id.contact_us_btn);
    }


    private void setOnClicks() {
        mCompositeDisposable = new CompositeDisposable();

        Disposable contactUsDisposable = RxView.clicks(contactUs)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) {
                        startActivity(new Intent(PrivacySettingsActivity.this, ContactActivity.class));
                    }
                });
        mCompositeDisposable.add(contactUsDisposable);
    }


    @Override
    protected void onDestroy() {
        mCompositeDisposable.clear();
        super.onDestroy();
    }
}
