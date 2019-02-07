package com.example.robmillaci.realestatemanager.activities.search_activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.example.robmillaci.realestatemanager.R;
import com.example.robmillaci.realestatemanager.activities.BaseActivity;
import com.jakewharton.rxbinding3.view.RxView;

import io.reactivex.functions.Consumer;
import kotlin.Unit;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * This class is the view for the search activity. Responsible for initializing the views and onClick methods
 */
public class SearchActivityView extends BaseActivity implements SearchActivityPresenter.View {
    @SuppressWarnings("FieldCanBeLocal")
    private final int REQUEST_LOCATION_PERMISSION = 1; //the request code for location permission request

    private ImageView mYourLocationImg; //the image to call location request

    private EditText mLocationEditText; //the edit text for location search input

    private RadioButton mBuyRadioBtn; //the buy radio button search criteria
    private RadioButton mLetRadioBtn; //the let radio button search criteria
    private RadioButton mSoldRadioBtn;

    private Spinner mPropertyTypeSpinner; //the type of property search criteria
    private Spinner mMinPriceSpinner; //the min price search criteria
    private Spinner mMaxPriceSpinner; //the max price search criteria
    private Spinner mMinBedroomsSpinner; //the min bedroom search criteria
    private Spinner mMaxBedroomsSpinner; //the max bedroom search criteria

    private Button mSearchBtn; //the search button
    private Button mResetBtn; //the reset search criteria button

    private SearchActivityPresenter mPresenter; //this views presenter


    /**
     * The following static final Strings are used to pass the search criteria values
     * to the presenter to perform searching
     */
    public static final String LOCATION_VALUE_KEY = "location";
    public static final String BUY_VALUE_KEY = "buy";
    private static final String LET_VALUE_KEY = "let";
    public static final String PROPERTY_TYPE_VALUE_KEY = "propType";
    public static final String MIN_PRICE_VALUE_KEY = "minPrice";
    public static final String MAX_PRICE_VALUE_KEY = "maxPrice";
    public static final String MIN_BEDROOMS_VALUE_KEY = "minBeds";
    public static final String MAX_BEDROOMS_VALUE_KEY = "maxBeds";
    public static final String SOLD_SEARCH = "soldSearch";


    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //clear the full screen flag set in base activity

        this.mPresenter = new SearchActivityPresenter(this); //initialize the presenter

        initializeViews();
        setOnClicks();
    }

    private void initializeViews() {
        mLocationEditText = findViewById(R.id.location_edit_text);
        mBuyRadioBtn = findViewById(R.id.buy_radio_btn);
        mLetRadioBtn = findViewById(R.id.let_radio_btn);
        mPropertyTypeSpinner = findViewById(R.id.property_type_spinner);
        mMinPriceSpinner = findViewById(R.id.min_price_spinner);
        mMaxPriceSpinner = findViewById(R.id.max_price_spinner);
        mMinBedroomsSpinner = findViewById(R.id.min_bedrooms_spinner);
        mMaxBedroomsSpinner = findViewById(R.id.max_bedrooms_spinner);
        mResetBtn = findViewById(R.id.reset_btn);
        mSearchBtn = findViewById(R.id.search_btn);
        mYourLocationImg = findViewById(R.id.loc_icn);
        mSoldRadioBtn = findViewById(R.id.sold_radio_button);

        mSearchBtn.setClickable(false);
        mBuyRadioBtn.setChecked(true);

        mLocationEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.equals("")) {
                    mSearchBtn.setClickable(false);
                } else {
                    mSearchBtn.setClickable(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }


    /**
     * Set the onclick methods for this view
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @SuppressLint("CheckResult")
    private void setOnClicks() {
        mBuyRadioBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mLetRadioBtn.setChecked(false);
                    mSoldRadioBtn.setChecked(false);
                }
            }
        });


        mLetRadioBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mBuyRadioBtn.setChecked(false);
                    mSoldRadioBtn.setChecked(false);
                }
            }
        });

        mSoldRadioBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mLetRadioBtn.setChecked(false);
                    mBuyRadioBtn.setChecked(false);
                }
            }
        });


        RxView.clicks(mResetBtn).subscribe(new Consumer<Unit>() {
            @Override
            public void accept(Unit unit) {
                resetFields();
            }
        });


        RxView.clicks(mYourLocationImg).subscribe(new Consumer<Unit>() {
            @Override
            public void accept(Unit unit) {
                //request permission for location
                requestLocationPermission();

            }
        });


        RxView.clicks(mSearchBtn).subscribe(new Consumer<Unit>() {
            @Override
            public void accept(Unit unit) {
                String location = mLocationEditText.getText().toString();
                boolean buy = mBuyRadioBtn.isChecked();
                boolean let = mLetRadioBtn.isChecked();
                String propertyType = mPropertyTypeSpinner.getSelectedItem().toString();
                String minPrice = mMinPriceSpinner.getSelectedItem().toString();
                String maxPrice = mMaxPriceSpinner.getSelectedItem().toString();
                String minBedrooms = mMinBedroomsSpinner.getSelectedItem().toString();
                String maxBedrooms = mMaxBedroomsSpinner.getSelectedItem().toString();
                boolean soldSearch = mSoldRadioBtn.isChecked();

                if (soldSearch) {
                    buy = true; //If we are looking for sold properties, we will only return those that were for sale, not for rent
                }

                Intent searchResultsIntent = new Intent(SearchActivityView.this, SearchResultsView.class);

                searchResultsIntent.putExtra(LOCATION_VALUE_KEY, location);
                searchResultsIntent.putExtra(BUY_VALUE_KEY, buy);
                searchResultsIntent.putExtra(LET_VALUE_KEY, let);
                searchResultsIntent.putExtra(PROPERTY_TYPE_VALUE_KEY, propertyType);
                searchResultsIntent.putExtra(MIN_PRICE_VALUE_KEY, minPrice);
                searchResultsIntent.putExtra(MAX_PRICE_VALUE_KEY, maxPrice);
                searchResultsIntent.putExtra(MIN_BEDROOMS_VALUE_KEY, minBedrooms);
                searchResultsIntent.putExtra(MAX_BEDROOMS_VALUE_KEY, maxBedrooms);
                searchResultsIntent.putExtra(SOLD_SEARCH, soldSearch);

                startActivity(searchResultsIntent);
            }
        });

    }


    /**
     * After obtaining location permission from the user, ask the presenter to get the users last known location
     */
    @AfterPermissionGranted(REQUEST_LOCATION_PERMISSION)
    private void requestLocationPermission() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
        if (EasyPermissions.hasPermissions(this, perms)) {
            //permission is granted - get the users location postcode
            mPresenter.getLastKnownLocation();
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.grant_permission), REQUEST_LOCATION_PERMISSION, perms);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    /**
     * Reset the search criteria fields
     */
    private void resetFields() {
        mLocationEditText.setText("");
        mBuyRadioBtn.setChecked(false);
        mLetRadioBtn.setChecked(false);
        mSoldRadioBtn.setChecked(false);
        mPropertyTypeSpinner.setSelection(0);
        mMinPriceSpinner.setSelection(0);
        mMaxPriceSpinner.setSelection(0);
        mMinBedroomsSpinner.setSelection(0);
        mMaxBedroomsSpinner.setSelection(0);
    }


    /**
     * Callback from {@link SearchActivityPresenter#getLastKnownLocation()}
     *
     * @param postCode the returned post code of the user to populate the location search criteria
     */
    @Override
    public void gotLocation(String postCode) {
        mLocationEditText.setText(postCode);
    }
}
