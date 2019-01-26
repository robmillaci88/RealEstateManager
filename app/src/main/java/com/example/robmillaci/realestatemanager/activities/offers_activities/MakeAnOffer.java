package com.example.robmillaci.realestatemanager.activities.offers_activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.robmillaci.realestatemanager.adapters.ImagesViewPagerAdapter;
import com.example.robmillaci.realestatemanager.R;
import com.example.robmillaci.realestatemanager.activities.BaseActivity;
import com.example.robmillaci.realestatemanager.activities.valuations_activities.ConfirmationActivity;
import com.example.robmillaci.realestatemanager.data_objects.Listing;
import com.example.robmillaci.realestatemanager.utils.DecimalFormatter;
import com.example.robmillaci.realestatemanager.utils.ToastModifications;
import com.jakewharton.rxbinding3.view.RxView;

import java.lang.ref.WeakReference;

import io.reactivex.functions.Consumer;
import kotlin.Unit;
import me.relex.circleindicator.CircleIndicator;

import static com.example.robmillaci.realestatemanager.fragments.ListingItemFragment.LISTING_BUNDLE_KEY;

public class MakeAnOffer extends BaseActivity {
    private TextView asking_price_amount;
    private EditText offerPrice;
    private ViewPager imageViewPager;
    private CircleIndicator viewPagerIndicator;
    private Listing thisListing;
    private TextWatcher offerTextWatcher;
    private Button submitOfferBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_offer);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        Bundle intentExtras = getIntent().getExtras();
        if (intentExtras != null) {
            thisListing = (Listing) intentExtras.getSerializable(LISTING_BUNDLE_KEY);
            if (thisListing != null){
                setTitle(String.format("%s %s %s", thisListing.getAddress_number(), thisListing.getAddress_street(), thisListing.getAddress_town()));
            }
        }

        initializeViews();
        updateViews();
        createTextWatcher();
        setListeners();

    }

    private void createTextWatcher() {
        offerTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String replacementString = DecimalFormatter.formatNumber(Integer.valueOf(s.toString().replaceAll(",","")));
                offerPrice.removeTextChangedListener(offerTextWatcher);
                offerPrice.setText(replacementString);
                offerPrice.setSelection(replacementString.length());
                offerPrice.addTextChangedListener(offerTextWatcher);
            }
        };
    }

    @SuppressLint("CheckResult")
    private void setListeners() {
        offerPrice.addTextChangedListener(offerTextWatcher);

        RxView.clicks(submitOfferBtn).subscribe(new Consumer<Unit>() {
            @Override
            public void accept(Unit unit) {
                if (offerPrice.getText().toString().equals("")) {
                    ToastModifications.createToast(MakeAnOffer.this, getString(R.string.enter_offer_value), Toast.LENGTH_LONG);
                } else {
                    Intent makeAnOfferIntent = new Intent(getApplicationContext(),ConfirmationActivity.class);
                    makeAnOfferIntent.putExtra(ConfirmationActivity.BUNDLE_KEY,ConfirmationActivity.CALLED_FROM_OFFER);
                    startActivity(makeAnOfferIntent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            }
        });
    }


    private void initializeViews() {
        asking_price_amount = findViewById(R.id.asking_price_amount);
        offerPrice = findViewById(R.id.offer_edit_text);
        imageViewPager = findViewById(R.id.image_viewpager);
        viewPagerIndicator = findViewById(R.id.image_indicator);
        submitOfferBtn = findViewById(R.id.submit_offer_button);
    }

    private void updateViews() {
        if (thisListing != null) {
            asking_price_amount.setText(String.format("%s %s", getString(R.string.currency_symbol), thisListing.getFormattedPrice()));
            createViewPager();
        }
    }

    private void createViewPager() {
        ImagesViewPagerAdapter adapter = new ImagesViewPagerAdapter(new WeakReference<Context>(this), thisListing);
        imageViewPager.setAdapter(adapter);
        viewPagerIndicator.setViewPager(imageViewPager);

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
