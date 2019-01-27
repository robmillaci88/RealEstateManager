package com.example.robmillaci.realestatemanager.activities.customer_account;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;

import com.example.robmillaci.realestatemanager.R;
import com.example.robmillaci.realestatemanager.activities.BaseActivity;
import com.example.robmillaci.realestatemanager.activities.sign_in_activities.StartActivity;
import com.jakewharton.rxbinding3.view.RxView;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import kotlin.Unit;

import static com.example.robmillaci.realestatemanager.activities.sign_in_activities.StartActivity.LOGOUT_INTENT_KEY;
import static com.example.robmillaci.realestatemanager.activities.sign_in_activities.StartActivity.LOGOUT_INTENT_VALUE;

/**
 * This class is responsible for the customer account activity
 */
public class AccountActivity extends BaseActivity {
    private Button yourDetails; //Your details button
    private Button privacySettings; //Privacy settings button
    private Button logout; //logout of the app button
    private CompositeDisposable mCompositeDisposable; //holds any disposables


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        setTitle(getString(R.string.account_activity_title));
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //clears the full screen flag set in the base activity

        initializeViews();
        setOnClicks();

    }

    private void initializeViews() {
        yourDetails = findViewById(R.id.your_details_button);
        privacySettings = findViewById(R.id.privacy_settings_button);
        logout = findViewById(R.id.logout_button);
    }

    private void setOnClicks() {
        mCompositeDisposable = new CompositeDisposable();

        Disposable yourDetailsDisposable = RxView.clicks(yourDetails)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) {
                        startActivity(new Intent(AccountActivity.this, ProfileActivity.class));
                    }
                });
        mCompositeDisposable.add(yourDetailsDisposable);


        Disposable privacyDisposable = RxView.clicks(privacySettings)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) {
                        startActivity(new Intent(AccountActivity.this, PrivacySettingsActivity.class));
                    }
                });
        mCompositeDisposable.add(privacyDisposable);


        Disposable logOutBtn = RxView.clicks(logout)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) {
                        Intent signOutIntent = new Intent(AccountActivity.this,StartActivity.class);
                        signOutIntent.putExtra(LOGOUT_INTENT_KEY,LOGOUT_INTENT_VALUE);
                        startActivity(signOutIntent);
                    }
                });
        mCompositeDisposable.add(logOutBtn);

    }


    @Override
    protected void onDestroy() {
        mCompositeDisposable.clear();
        super.onDestroy();
    }
}
