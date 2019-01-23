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

import io.reactivex.functions.Consumer;
import kotlin.Unit;

public class BaseViewingActivity extends AppCompatActivity {
    private Button refresh;
    protected TextView action_message;
    IntentFilter intentFilter;
    NetworkListener receiver;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        refresh = findViewById(R.id.refresh_btn);
        action_message = findViewById(R.id.message);

        setViewVisibility(false);

        startSearch();

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

    @Override
    protected void onResume() {
        super.onResume();
        try {
            registerReceiver(receiver, intentFilter);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(receiver);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
