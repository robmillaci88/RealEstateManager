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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.robmillaci.realestatemanager.R;
import com.example.robmillaci.realestatemanager.adapters.SearchResultsAdapter;
import com.example.robmillaci.realestatemanager.data_objects.Listing;
import com.example.robmillaci.realestatemanager.fragments.ListingItemFragment;
import com.example.robmillaci.realestatemanager.fragments.MapViewFragment;
import com.example.robmillaci.realestatemanager.utils.Utils;
import com.jakewharton.rxbinding3.view.RxView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import io.reactivex.functions.Consumer;
import kotlin.Unit;

/**
 * The view for the search results. Called from {@link SearchActivityView} when a user searches for listings
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
public class SearchResultsView extends AppCompatActivity implements SearchResultsPresenter.View, SearchResultsAdapter.SearchResultsAdapterCallback {
    public static final String FRAGMENT_TAG = "addedFragment"; //the tag for the the listing item fragment

    public static final int SORT_CHOICE_HIGHEST_PRICE = 1; //represents the user sorting by highest price
    public static final int SORT_CHOICE_LOWEST_PRICE = 2;//represents the user sorting by lowest price
    public static final int SORT_CHOICE_NEWEST = 3;//represents the user sorting by newest to oldest
    public static final int SORT_CHOICE_OLDEST = 4;//represents the user sorting by oldest to newest

    private SearchResultsPresenter presenter; //this views presenter

    private RecyclerView search_results_recyclerview; //the recyclerview to display the search results
    private TextView search_results_numb; //the text view to display the number of results found
    private Spinner filterTypeSpinner; //the spinner to filter out types of listings
    private ArrayList<View> activityViews; //arraylist to hold this activities views, so we can hide and unhide easily
    private AlertDialog sortAlertDialog; //the alter dialog displaying the sorting options


    private ArrayList<Listing> originalListings; //arraylist to hold the origional returned listings, so they can be restored after filtering is completed

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results_view);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.search_results_title));

        if (!Utils.isTablet(getApplicationContext())) { //if we are not on a tablet, remove the ability for screen rotation
            //We have a different view for tablets
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


    /**
     * Display the alert dialog to display the sorting options to the user
     * Passes the users choice to the presenter to perform the search {@link SearchResultsPresenter#sortData(ArrayList, int)}
     */
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


    /**
     * The listing type spinner passes the chosen value to the presenter to filter the data and return it to the view to
     * update the recyclerview {@link SearchResultsPresenter#filterData(String, ArrayList)}
     */
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


    /**
     * Show or hide this activities views depending on whether {@link ListingItemFragment} is shown
     *
     * @param show boolean show or not
     */
    private void changeActivityViewsVisibility(boolean show) {
        for (View v : activityViews) {
            if (show) {
                v.setVisibility(View.VISIBLE);
            } else {
                v.setVisibility(View.GONE);
            }
        }
    }


    /**
     * Called in onCreate to search the database for listings passed in the intent when creating this activity
     * The search parameters are passed to the presenter and returned to {@link SearchResultsView#gotAllListing(ArrayList)}
     *
     * @param extras the search parameters passed into the intent when creating this activity
     */
    private void search(Bundle extras) {
        presenter.searchDatabase(extras);
    }


    /**
     * Sets the number of results returned to display to the user
     *
     * @param number      the number of results returned
     * @param totalNumber the total number of available results
     */
    private void setResultNumber(int number, int totalNumber) {
        search_results_numb.setText(String.format("%s of %s %s", number, totalNumber, getApplicationContext().getString(R.string.results)));
    }


    /**
     * Callback from {@link SearchResultsPresenter#searchDatabase(Bundle)}
     * This updates the recyclerview with the returned listings
     *
     * @param listings the returned listings from either firebase or the local db
     */
    @Override
    public void gotAllListing(ArrayList<Listing> listings) {
        originalListings = listings;
        setResultNumber(listings.size(), originalListings.size());

        initializeRecyclerView(listings);
    }


    /**
     * The callback from {@link SearchResultsPresenter#filterData(String, ArrayList)}
     * This updates the recyclerview with the filtered listings
     *
     * @param returnedListings the listings returned after filtering
     */
    @Override
    public void filteredListings(ArrayList<Listing> returnedListings) {
        if (returnedListings != null) {
            setResultNumber(returnedListings.size(), originalListings.size());
            initializeRecyclerView(returnedListings);
        }
    }


    /**
     * Callback from {@link SearchResultsPresenter#sortData(ArrayList, int)}
     * This updates the recycler view with the listings sorted in a specific order
     *
     * @param sortedListings the returned sorted Arraylist of listings
     */
    @Override
    public void sortedListings(ArrayList<Listing> sortedListings) {
        if (sortedListings != null) {
            initializeRecyclerView(sortedListings);
        }
    }


    /**
     * Called from {@link SearchResultsAdapter}. The recycler views on click methods for the listing.
     * The listing details are displayed in a fragment to the user.<br/>
     * If we are using a tablet, this activities views are not hidden, otherwise they are hidden
     * @param listingAddress the address of the listing
     */
    @Override
    public void setFragment(String listingAddress) {
        findViewById(R.id.fragment_container).setBackgroundColor(Color.WHITE);
        setTitle(listingAddress);

        if (!Utils.isTablet(getApplicationContext())) {
            changeActivityViewsVisibility(false);
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ListingItemFragment(), FRAGMENT_TAG).addToBackStack(null).commit();
    }


    /**
     * Handles the user clicking back in the application
     * Different behaviour depending on whether the device is a tablet or not
     */
    @Override
    public void onBackPressed() {
        if (Utils.isTablet(getApplicationContext())) {
            finish();
        } else {
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
