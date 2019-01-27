package com.example.robmillaci.realestatemanager.activities.valuations_activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.example.robmillaci.realestatemanager.R;
import com.jakewharton.rxbinding3.view.RxView;

import io.reactivex.functions.Consumer;
import kotlin.Unit;

/**
 * This class is responsible for the SellLetActivity
 */
public class SellLetActivity extends AppCompatActivity {
    private Button continue_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_let);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle b = getIntent().getExtras();
        if (b != null) {
            String evaluationType = b.getString(BookEvaluationActivity.TYPE_KEY, "");
            switch (evaluationType) {
                case BookEvaluationActivity.LETTING_TYPE:
                    setTitle(getString(R.string.let_property_title));
                    break;

                case BookEvaluationActivity.SELLING_TYPE:
                    setTitle(getString(R.string.sell_property_title));
                    break;
            }
        }

        initializeViews();
        setOnClicks();
    }

    @SuppressLint("CheckResult")
    private void setOnClicks() {
        //noinspection ResultOfMethodCallIgnored
        RxView.clicks(continue_btn)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) {
                        Intent sellLetConfirmation = new Intent(getApplicationContext(),ConfirmationActivity.class);
                        sellLetConfirmation.putExtra(ConfirmationActivity.BUNDLE_KEY,ConfirmationActivity.CALLED_FROM_VALUATION);
                        startActivity(sellLetConfirmation);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                });
    }

    private void initializeViews() {
        continue_btn = findViewById(R.id.continue_btn);
    }


}
