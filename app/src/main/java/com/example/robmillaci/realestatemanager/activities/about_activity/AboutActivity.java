package com.example.robmillaci.realestatemanager.activities.about_activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.Button;

import com.example.robmillaci.realestatemanager.R;
import com.example.robmillaci.realestatemanager.activities.BaseActivity;
import com.jakewharton.rxbinding3.view.RxView;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import kotlin.Unit;

/**
 * This class is responsible for handling the View of 'About Activity'
 */
public class AboutActivity extends BaseActivity {
    private Button mLicenseBtn; //The license button to credit the author of the images in the app
    private Button mBackbtn; //the back button for this activity
    private CompositeDisposable mCompositeDisposable; //to hold and dispose of all disposables when the activity is destroyed

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about); //set the view
        setTitle(R.string.about_activity_title); //set the title

        initializeViews(); //init the views
        initializeOnClicks(); //init the onClick methods
    }


    private void initializeViews() {
        mLicenseBtn = findViewById(R.id.license);
        mBackbtn = findViewById(R.id.backbtn);
    }


    @SuppressLint("CheckResult")
    private void initializeOnClicks() {
        mCompositeDisposable = new CompositeDisposable();

        Disposable licenseBtnDisposable = RxView.clicks(mLicenseBtn)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) {
                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(AboutActivity.this);
                        alertBuilder.setTitle(R.string.licenses);
                        alertBuilder.setMessage(R.string.license_message);
                        alertBuilder.setPositiveButton(R.string.ok_text, null);
                        alertBuilder.show();
                    }
                });
        mCompositeDisposable.add(licenseBtnDisposable);


        Disposable backbtnDisposalbe = RxView.clicks(mBackbtn)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) {
                        finish();
                    }
                });
        mCompositeDisposable.add(backbtnDisposalbe);
    }


    @Override
    protected void onDestroy() {
        mCompositeDisposable.clear();
        super.onDestroy();
    }
}
