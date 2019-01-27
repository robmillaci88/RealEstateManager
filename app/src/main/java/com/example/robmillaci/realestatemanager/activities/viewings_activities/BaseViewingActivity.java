package com.example.robmillaci.realestatemanager.activities.viewings_activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.robmillaci.realestatemanager.R;
import com.example.robmillaci.realestatemanager.utils.network_utils.NetworkListener;
import com.jakewharton.rxbinding3.view.RxView;

import java.util.Objects;

import io.reactivex.functions.Consumer;
import kotlin.Unit;

/**
 * The base activity for <br/>{@link com.example.robmillaci.realestatemanager.activities.offers_activities.AcceptedOffers}<br/>
 * {@link AwaitingAction}<br/>
 * {@link ConfirmedViewings}<br/>
 * {@link com.example.robmillaci.realestatemanager.activities.feedback_activities.FeedBackAwaitingAction}<br/>
 * {@link com.example.robmillaci.realestatemanager.activities.feedback_activities.FeedbackPendingReceived}<br/>
 * {@link com.example.robmillaci.realestatemanager.activities.offers_activities.OffersAwaitingAction}<br/>
 * {@link ViewingsHistory}<br/>
 * Currently in this application all the above classes are for visual representational purposes only and don't have functionality
 */
public class BaseViewingActivity extends AppCompatActivity {
    private Button refresh;
    protected TextView action_message;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewings);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        refresh = findViewById(R.id.refresh_btn);
        action_message = findViewById(R.id.message);

        setViewVisibility(false);

        startSearch();

        //noinspection ResultOfMethodCallIgnored
        RxView.clicks(refresh)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) throws Exception {
                        setViewVisibility(false);
                        startSearch();
                    }
                });
    }

    protected void startSearch() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Searching...");
        pd.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                pd.dismiss();
                setViewVisibility(true);
            }
        }, 3000);
    }

    public void setViewVisibility(boolean viewVisibility) {
        if (viewVisibility) {
            action_message.setVisibility(View.VISIBLE);
            refresh.setVisibility(View.VISIBLE);
        } else {
            action_message.setVisibility(View.GONE);
            refresh.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

}
