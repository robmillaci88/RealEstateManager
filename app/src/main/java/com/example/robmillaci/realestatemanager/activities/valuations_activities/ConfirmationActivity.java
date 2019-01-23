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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);

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
