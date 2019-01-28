package com.example.robmillaci.realestatemanager.activities.valuations_activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.robmillaci.realestatemanager.R;
import com.jakewharton.rxbinding3.view.RxView;

import io.reactivex.functions.Consumer;
import kotlin.Unit;

/**
 * This class is responsible for the upcoming valuations activity
 */
public class UpcomingValuations extends AppCompatActivity {
private Button refresh;
private TextView evaluations_message;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming_valuations);
        setTitle(getString(R.string.upcoming_valuations_activity_title));
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        refresh = findViewById(R.id.refresh_btn);
        evaluations_message = findViewById(R.id.message);

        setViewVisibility(false);
        startCountdown();

        //noinspection ResultOfMethodCallIgnored
        RxView.clicks(refresh)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) {
                        setViewVisibility(false);
                        startCountdown();
                    }
                });
    }

    private void startCountdown() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.searching_pd_message));
        pd.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                pd.dismiss();
                setViewVisibility(true);
            }
        },3000);
    }

    private void setViewVisibility(boolean viewVisibility) {
        if (viewVisibility){
            evaluations_message.setVisibility(View.VISIBLE);
            refresh.setVisibility(View.VISIBLE);
        }else {
            evaluations_message.setVisibility(View.GONE);
            refresh.setVisibility(View.GONE);
        }
    }
}
