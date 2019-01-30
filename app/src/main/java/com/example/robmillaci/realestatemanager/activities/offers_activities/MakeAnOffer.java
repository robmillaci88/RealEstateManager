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

import com.example.robmillaci.realestatemanager.R;
import com.example.robmillaci.realestatemanager.activities.BaseActivity;
import com.example.robmillaci.realestatemanager.activities.valuations_activities.ConfirmationActivity;
import com.example.robmillaci.realestatemanager.adapters.ImagesViewPagerAdapter;
import com.example.robmillaci.realestatemanager.data_objects.Listing;
import com.example.robmillaci.realestatemanager.utils.ToastModifications;
import com.example.robmillaci.realestatemanager.utils.Utils;
import com.jakewharton.rxbinding3.view.RxView;

import java.lang.ref.WeakReference;
import java.util.Objects;

import io.reactivex.functions.Consumer;
import kotlin.Unit;
import me.relex.circleindicator.CircleIndicator;

import static com.example.robmillaci.realestatemanager.fragments.ListingItemFragment.LISTING_BUNDLE_KEY;

/**
 * This class is responsible for the Make an offer activity
 */
public class MakeAnOffer extends BaseActivity {
    private TextView mAskingPriceAmount; //the asking price of the listings
    private EditText mOfferPrice; //the price the user will offer for the listings
    private ViewPager mImageViewPager; //view pager to store the listings images
    private CircleIndicator mViewPagerIndicator; //view indicator to highlight the number of images in the view pager
    private Listing mThisListing; //the current listing the user is viewing
    private TextWatcher mOfferTextWatcher; //Text watcher for the offer to append relevant commas
    private Button mSubmitOfferBtn; //the button to submit an offer

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_offer);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN); //prevent the keyboard from displaying when the activity is created.


        //Get the listing that this activity is created in relation to.
        Bundle intentExtras = getIntent().getExtras();
        if (intentExtras != null) {
            mThisListing = (Listing) intentExtras.getSerializable(LISTING_BUNDLE_KEY);
            if (mThisListing != null) {
                setTitle(String.format("%s %s %s", mThisListing.getAddress_number(), mThisListing.getAddress_street(), mThisListing.getAddress_town()));
            }
        }

        initializeViews();
        updateViews();
        createTextWatcher();
        setListeners();

    }


    /**
     * Creates a text watcher for the offers edit text
     * This text watcher formats the entered value such that relevant commas are inserted into the offer amount
     */
    private void createTextWatcher() {
        mOfferTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String replacementString = Utils.formatNumber(Integer.valueOf(s.toString().replaceAll(",", "")));
                mOfferPrice.removeTextChangedListener(mOfferTextWatcher);
                mOfferPrice.setText(replacementString);
                mOfferPrice.setSelection(replacementString.length());
                mOfferPrice.addTextChangedListener(mOfferTextWatcher);
            }
        };
    }


    @SuppressLint("CheckResult")
    private void setListeners() {
        mOfferPrice.addTextChangedListener(mOfferTextWatcher); //add the text watcher to the offer edit text


        //noinspection ResultOfMethodCallIgnored
        RxView.clicks(mSubmitOfferBtn).subscribe(new Consumer<Unit>() {
            @Override
            public void accept(Unit unit) {
                if (mOfferPrice.getText().toString().equals("")) {
                    ToastModifications.createToast(MakeAnOffer.this, getString(R.string.enter_offer_value), Toast.LENGTH_LONG);
                } else {
                    Intent makeAnOfferIntent = new Intent(getApplicationContext(), ConfirmationActivity.class);
                    makeAnOfferIntent.putExtra(ConfirmationActivity.BUNDLE_KEY, ConfirmationActivity.CALLED_FROM_OFFER);
                    startActivity(makeAnOfferIntent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            }
        });
    }


    private void initializeViews() {
        mAskingPriceAmount = findViewById(R.id.asking_price_amount);
        mOfferPrice = findViewById(R.id.offer_edit_text);
        mImageViewPager = findViewById(R.id.image_viewpager);
        mViewPagerIndicator = findViewById(R.id.image_indicator);
        mSubmitOfferBtn = findViewById(R.id.submit_offer_button);
    }

    private void updateViews() {
        if (mThisListing != null) {
            mAskingPriceAmount.setText(String.format("%s %s", getString(R.string.currency_symbol), mThisListing.getFormattedPrice()));
            createViewPager();
        }
    }

    private void createViewPager() {
        ImagesViewPagerAdapter adapter = new ImagesViewPagerAdapter(new WeakReference<Context>(this), mThisListing);
        mImageViewPager.setAdapter(adapter);
        mViewPagerIndicator.setViewPager(mImageViewPager);

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
