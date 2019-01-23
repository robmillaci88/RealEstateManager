package com.example.robmillaci.realestatemanager.activities.search_activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.robmillaci.realestatemanager.Adapters.SearchResultsAdapter;
import com.example.robmillaci.realestatemanager.R;
import com.example.robmillaci.realestatemanager.data_objects.Listing;
import com.example.robmillaci.realestatemanager.fragments.ListingItemFragment;
import com.example.robmillaci.realestatemanager.fragments.MapViewFragment;
import com.example.robmillaci.realestatemanager.utils.Utils;
import com.jakewharton.rxbinding3.view.RxView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import io.reactivex.functions.Consumer;
import kotlin.Unit;

public class SearchResultsView extends AppCompatActivity implements SearchResultsPresenter.View, SearchResultsAdapter.SearchResultsAdapterCallback {
    public static final String FRAGMENT_TAG = "addedFragment";

    public static final int SORT_CHOICE_HIGHEST_PRICE = 1;
    public static final int SORT_CHOICE_LOWEST_PRICE = 2;
    public static final int SORT_CHOICE_NEWEST = 3;
    public static final int SORT_CHOICE_OLDEST = 4;

    private SearchResultsPresenter presenter;
    private RecyclerView search_results_recyclerview;
    private TextView search_results_numb;
    private Spinner filterTypeSpinner;
    private ArrayList<View> activityViews;
    private AlertDialog sortAlertDialog;


    private ArrayList<Listing> originalListings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results_view);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.search_results_title));

        if (!Utils.isTablet(getApplicationContext())) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        }

        this.presenter = new SearchResultsPresenter(this, new WeakReference<Context>(this));

        initializeViews();
        configureSpinnerOnSelect();
        search(getIntent().getExtras());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.sort_by:
                showSortDialog();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressLint("CheckResult")
    private void showSortDialog() {
        final AlertDialog.Builder sortByDialog = new AlertDialog.Builder(SearchResultsView.this);
        @SuppressLint("InflateParams") View v = LayoutInflater.from(this).inflate(R.layout.sort_dialog_view, null);

        sortByDialog.setView(v);
        sortByDialog.setTitle(R.string.sort_dialog_title);
        sortByDialog.setPositiveButton(R.string.close_button, null);
        sortAlertDialog = sortByDialog.show();

        TextView highest_price = v.findViewById(R.id.highest_price_sort);
        TextView lowest_price = v.findViewById(R.id.lowest_price_sort);
        TextView newest_to_oldest = v.findViewById(R.id.newest_to_oldest_sort);
        TextView oldest_to_newest = v.findViewById(R.id.oldest_to_newest_sort);

        final int[] sortChoice = {-1};

        RxView.clicks(highest_price).subscribe(new Consumer<Unit>() {
            @Override
            public void accept(Unit unit) {
                sortChoice[0] = SORT_CHOICE_HIGHEST_PRICE;
                presenter.sortData(originalListings, sortChoice[0]);
                sortAlertDialog.dismiss();
            }
        });

        RxView.clicks(lowest_price).subscribe(new Consumer<Unit>() {
            @Override
            public void accept(Unit unit) {
                sortChoice[0] = SORT_CHOICE_LOWEST_PRICE;
                presenter.sortData(originalListings, sortChoice[0]);
                sortAlertDialog.dismiss();
            }
        });

        RxView.clicks(newest_to_oldest).subscribe(new Consumer<Unit>() {
            @Override
            public void accept(Unit unit) {
                sortChoice[0] = SORT_CHOICE_NEWEST;
                presenter.sortData(originalListings, sortChoice[0]);
                sortAlertDialog.dismiss();
            }
        });

        RxView.clicks(oldest_to_newest).subscribe(new Consumer<Unit>() {
            @Override
            public void accept(Unit unit) {
                sortChoice[0] = SORT_CHOICE_OLDEST;
                presenter.sortData(originalListings, sortChoice[0]);
                sortAlertDialog.dismiss();
            }
        });

    }


    private void initializeViews() {
        activityViews = new ArrayList<>();

        search_results_recyclerview = findViewById(R.id.search_results_recyclerview);
        search_results_numb = findViewById(R.id.search_results_numb);
        filterTypeSpinner = findViewById(R.id.type_filter_spinner);

        View search_results_divider = findViewById(R.id.search_results_divider);
        TextView property_type_filter_title = findViewById(R.id.property_type_filter_title);

        activityViews.add(search_results_recyclerview);
        activityViews.add(search_results_numb);
        activityViews.add(filterTypeSpinner);
        activityViews.add(search_results_divider);
        activityViews.add(property_type_filter_title);
    }


    private void configureSpinnerOnSelect() {
        filterTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                presenter.filterData(filterTypeSpinner.getSelectedItem().toString(), originalListings);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    private void initializeRecyclerView(ArrayList<Listing> listings) {
        search_results_recyclerview.setLayoutManager(new LinearLayoutManager(this));
        search_results_recyclerview.setAdapter(new SearchResultsAdapter(new WeakReference<Context>(this), listings, this));
    }


    private void changeActivityViewsVisibility(boolean show) {
        for (View v : activityViews) {
            if (show) {
                v.setVisibility(View.VISIBLE);
            } else {
                v.setVisibility(View.GONE);
            }
        }
    }


    private void search(Bundle extras) {
        presenter.searchDatabase(extras);
    }


    private void setResultNumber(int number, int totalNumber) {
        search_results_numb.setText(String.format("%s of %s %s", number, totalNumber,getApplicationContext().getString(R.string.results)));
    }


    @Override
    public void gotAllListing(ArrayList<Listing> listings) {
        originalListings = listings;
        setResultNumber(listings.size(), originalListings.size());

        initializeRecyclerView(listings);
    }

    @Override
    public void filteredListings(ArrayList<Listing> returnedListings) {
        if (returnedListings != null) {
            setResultNumber(returnedListings.size(), originalListings.size());
            initializeRecyclerView(returnedListings);
        }
    }

    @Override
    public void sortedListings(ArrayList<Listing> sortedListings) {
        if (sortedListings != null) {
            initializeRecyclerView(sortedListings);
        }
    }

    @Override
    public void setFragment(String listingAddress) {
        findViewById(R.id.fragment_container).setBackgroundColor(Color.WHITE);
        setTitle(listingAddress);

        if (!Utils.isTablet(getApplicationContext())) {
            changeActivityViewsVisibility(false);
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ListingItemFragment(), FRAGMENT_TAG).addToBackStack(null).commit();
    }

    @Override
    public void onBackPressed() {
        if(Utils.isTablet(getApplicationContext())){
            finish();
        }else{
            final Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);

            if (fragment instanceof ListingItemFragment) {
            if (fragment.isAdded() && fragment.isVisible()) {
                getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                changeActivityViewsVisibility(true);

                setTitle(getString(R.string.search_results_title));
            } else {
                finish();
            }

        } else if (fragment instanceof MapViewFragment) {
            getSupportFragmentManager().popBackStackImmediate();
        } else {
            finish();
        }

        }
    }
}
