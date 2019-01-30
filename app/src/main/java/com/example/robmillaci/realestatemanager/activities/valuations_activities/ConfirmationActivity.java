package com.example.robmillaci.realestatemanager.activities.valuations_activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.example.robmillaci.realestatemanager.R;
import com.example.robmillaci.realestatemanager.activities.main_activity.MainActivityView;
import com.jakewharton.rxbinding3.view.RxView;

import java.util.Objects;

import io.reactivex.functions.Consumer;
import kotlin.Unit;

/**
 * This class is responsible for the confirmation activity when a user chooses to buy,sell or make an offer on a listing
 */
public class ConfirmationActivity extends AppCompatActivity {
    private Button mReturnBtn;
    public static final int CALLED_FROM_OFFER = 1; //int to determine wether this activity was created from the Offer activity
    public static final int CALLED_FROM_VALUATION = 2; //int to determine wether this activity was created from the valuation activity
    public static final int CALLED_FROM_BOOK_VIEWING = 3;//int to determiner whether this activity was created from book a viewing activity
    public static final String BUNDLE_KEY = "confirmationActivity"; //the bundle key when passing data into the intent to start this activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int callingFrom = Objects.requireNonNull(getIntent().getExtras()).getInt(BUNDLE_KEY); //determine which activity created the ConfirmationActivity

        switch (callingFrom) {
            case CALLED_FROM_OFFER:
                setContentView(R.layout.activity_confirmation_make_an_offer);
                break;

            case CALLED_FROM_VALUATION:
                setContentView(R.layout.activity_confirmation);
                break;

            case CALLED_FROM_BOOK_VIEWING:
                setContentView(R.layout.activity_book_a_viewing_confirmation);

        }

        initializeViews();
        setOnClicks();
    }

    private void initializeViews() {
        mReturnBtn = findViewById(R.id.return_btn);
    }

    @SuppressLint("CheckResult")
    private void setOnClicks() {
        //noinspection ResultOfMethodCallIgnored
        RxView.clicks(mReturnBtn)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) {
                        startActivity(new Intent(getApplicationContext(), MainActivityView.class));
                        finish();
                    }
                });
    }


}
