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

public class AboutActivity extends BaseActivity {
    private static final String ACTIVITY_TITLE = "About";
    private Button licenseBtn;
    private Button backbtn;
    private CompositeDisposable mCompositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setTitle(ACTIVITY_TITLE);

        initializeViews();
        initializeOnClicks();
    }


    private void initializeViews() {
        licenseBtn = findViewById(R.id.license);
        backbtn = findViewById(R.id.backbtn);
    }


    @SuppressLint("CheckResult")
    private void initializeOnClicks() {
        mCompositeDisposable = new CompositeDisposable();

        Disposable licenseBtnDisposable = RxView.clicks(licenseBtn)
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


        Disposable backbtnDisposalbe = RxView.clicks(backbtn)
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
