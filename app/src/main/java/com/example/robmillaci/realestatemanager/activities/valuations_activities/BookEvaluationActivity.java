package com.example.robmillaci.realestatemanager.activities.valuations_activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import com.example.robmillaci.realestatemanager.R;
import com.jakewharton.rxbinding3.view.RxView;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import kotlin.Unit;

public class BookEvaluationActivity extends AppCompatActivity {
    private Button sell_property_btn;
    private Button let_property_btn;
    private CompositeDisposable mCompositeDisposable;

    static final String TYPE = "type";
    static final String SELLING_TYPE = "selling";
    static final String LETTING_TYPE = "letting";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_evaluation);
        setTitle(getString(R.string.book_valuation_activity_title));
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initializeViews();
        setOnClicks();
    }

    private void initializeViews() {
        sell_property_btn = findViewById(R.id.sell_property_button);
        let_property_btn = findViewById(R.id.let_property_btn);
    }

    private void setOnClicks() {
        mCompositeDisposable = new CompositeDisposable();

        Disposable sellBtnClick = RxView.clicks(sell_property_btn).
                subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit){
                        Intent i = new Intent(BookEvaluationActivity.this,SellLetActivity.class);
                        i.putExtra(TYPE,SELLING_TYPE);
                        startActivity(i);
                    }
                });



        Disposable letBtnClick = RxView.clicks(let_property_btn).
                subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit){
                        Intent i = new Intent(BookEvaluationActivity.this,SellLetActivity.class);
                        i.putExtra(TYPE,LETTING_TYPE);
                        startActivity(i);
                    }
                });

        mCompositeDisposable.add(sellBtnClick);
        mCompositeDisposable.add(letBtnClick);

    }


    @Override
    protected void onDestroy() {
        mCompositeDisposable.clear();
        super.onDestroy();
    }
}
