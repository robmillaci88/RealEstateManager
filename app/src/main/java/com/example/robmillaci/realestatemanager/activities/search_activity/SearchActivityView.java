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

public class SearchActivityView extends BaseActivity implements SearchActivityPresenter.View {
    @SuppressWarnings("FieldCanBeLocal")
    private final int REQUEST_LOCATION_PERMISSION = 1;

    private ImageView your_location_img;

    private EditText location_edit_text;

    private RadioButton buy_radio_btn;
    private RadioButton let_radio_btn;

    private Spinner property_type_spinner;
    private Spinner min_price_spinner;
    private Spinner max_price_spinner;
    private Spinner min_bedrooms_spinner;
    private Spinner max_bedrooms_spinner;

    private Button search_btn;
    private Button reset_btn;

    private SearchActivityPresenter mPresenter;

    public static final String LOCATION_VALUE_KEY = "location";
    public static final String BUY_VALUE_KEY = "buy";
    public static final String LET_VALUE_KEY = "let";
    public static final String PROPERTY_TYPE_VALUE_KEY = "propType";
    public static final String MIN_PRICE_VALUE_KEY = "minPrice";
    public static final String MAX_PRICE_VALUE_KEY = "maxPrice";
    public static final String MIN_BEDROOMS_VALUE_KEY = "minBeds";
    public static final String MAX_BEDROOMS_VALUE_KEY = "maxBeds";


    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        this.mPresenter = new SearchActivityPresenter(this);

        initializeViews();
        setOnClicks();
    }

    private void initializeViews() {
        location_edit_text = findViewById(R.id.location_edit_text);
        buy_radio_btn = findViewById(R.id.buy_radio_btn);
        let_radio_btn = findViewById(R.id.let_radio_btn);
        property_type_spinner = findViewById(R.id.property_type_spinner);
        min_price_spinner = findViewById(R.id.min_price_spinner);
        max_price_spinner = findViewById(R.id.max_price_spinner);
        min_bedrooms_spinner = findViewById(R.id.min_bedrooms_spinner);
        max_bedrooms_spinner = findViewById(R.id.max_bedrooms_spinner);
        reset_btn = findViewById(R.id.reset_btn);
        search_btn = findViewById(R.id.search_btn);
        your_location_img = findViewById(R.id.loc_icn);

        search_btn.setClickable(false);
        buy_radio_btn.setChecked(true);

        location_edit_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.equals("")) {
                    search_btn.setClickable(false);
                } else {
                    search_btn.setClickable(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @SuppressLint("CheckResult")
    private void setOnClicks() {
        buy_radio_btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    let_radio_btn.setChecked(false);
                } else {
                    let_radio_btn.setChecked(true);
                }
            }
        });


        let_radio_btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    buy_radio_btn.setChecked(false);
                } else {
                    buy_radio_btn.setChecked(true);
                }
            }
        });


        RxView.clicks(reset_btn).subscribe(new Consumer<Unit>() {
            @Override
            public void accept(Unit unit) {
                resetFields();
            }
        });


        RxView.clicks(your_location_img).subscribe(new Consumer<Unit>() {
            @Override
            public void accept(Unit unit) {
                //request permission for location
                requestLocationPermission();

            }
        });


        RxView.clicks(search_btn).subscribe(new Consumer<Unit>() {
            @Override
            public void accept(Unit unit){
                String location = location_edit_text.getText().toString();
                boolean buy = buy_radio_btn.isChecked();
                boolean let = let_radio_btn.isChecked();
                String propertyType = property_type_spinner.getSelectedItem().toString();
                String minPrice = min_price_spinner.getSelectedItem().toString();
                String maxPrice = max_price_spinner.getSelectedItem().toString();
                String minBedrooms = min_bedrooms_spinner.getSelectedItem().toString();
                String maxBedrooms = max_bedrooms_spinner.getSelectedItem().toString();

                Intent searchResultsIntent = new Intent(SearchActivityView.this, SearchResultsView.class);

                searchResultsIntent.putExtra(LOCATION_VALUE_KEY, location);
                searchResultsIntent.putExtra(BUY_VALUE_KEY, buy);
                searchResultsIntent.putExtra(LET_VALUE_KEY, let);
                searchResultsIntent.putExtra(PROPERTY_TYPE_VALUE_KEY, propertyType);
                searchResultsIntent.putExtra(MIN_PRICE_VALUE_KEY, minPrice);
                searchResultsIntent.putExtra(MAX_PRICE_VALUE_KEY, maxPrice);
                searchResultsIntent.putExtra(MIN_BEDROOMS_VALUE_KEY, minBedrooms);
                searchResultsIntent.putExtra(MAX_BEDROOMS_VALUE_KEY, maxBedrooms);

                startActivity(searchResultsIntent);
            }
        });

    }

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


    private void resetFields() {
        location_edit_text.setText("");
        buy_radio_btn.setChecked(false);
        let_radio_btn.setChecked(false);
        property_type_spinner.setSelection(0);
        min_price_spinner.setSelection(0);
        max_price_spinner.setSelection(0);
        min_bedrooms_spinner.setSelection(0);
        max_bedrooms_spinner.setSelection(0);
    }


    @Override
    public void gotLocation(String postCode) {
        location_edit_text.setText(postCode);
    }
}
