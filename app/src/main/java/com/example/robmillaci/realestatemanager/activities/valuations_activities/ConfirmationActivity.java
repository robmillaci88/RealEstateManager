package com.example.robmillaci.realestatemanager.activities.valuations_activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import com.example.robmillaci.realestatemanager.R;
import com.example.robmillaci.realestatemanager.activities.main_activity.MainActivityView;
import com.jakewharton.rxbinding3.view.RxView;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import kotlin.Unit;

public class ConfirmationActivity extends AppCompatActivity {
private Button return_btn;
public static final int CALLED_FROM_OFFER = 1;
public static final int CALLED_FROM_VALUATION = 2;
public static String BUNDLE_KEY = "confirmationActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int callingFrom = getIntent().getExtras().getInt(BUNDLE_KEY);

        switch (callingFrom) {
            case CALLED_FROM_OFFER:
                setContentView(R.layout.activity_confirmation_make_an_offer);
                break;

            case CALLED_FROM_VALUATION:
                setContentView(R.layout.activity_confirmation);
                break;
        }

        initializeViews();
        setOnClicks();
    }

    private void initializeViews() {
        return_btn = findViewById(R.id.return_btn);
    }

    private void setOnClicks() {
        Disposable returnbutton = RxView.clicks(return_btn)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) throws Exception {
                        startActivity(new Intent(getApplicationContext(), MainActivityView.class));
                        finish();
                    }
                });
    }


}
