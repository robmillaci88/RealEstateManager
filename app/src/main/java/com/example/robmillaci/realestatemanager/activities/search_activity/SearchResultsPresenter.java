package com.example.robmillaci.realestatemanager.activities.search_activity;

import android.content.Context;
import android.os.Bundle;

import com.example.robmillaci.realestatemanager.data_objects.Listing;
import com.example.robmillaci.realestatemanager.databases.firebase.FirebaseHelper;
import com.example.robmillaci.realestatemanager.databases.local_database.MyDatabaseHelper;
import com.example.robmillaci.realestatemanager.utils.Utils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import static com.example.robmillaci.realestatemanager.activities.search_activity.SearchResultsView.SORT_CHOICE_HIGHEST_PRICE;
import static com.example.robmillaci.realestatemanager.activities.search_activity.SearchResultsView.SORT_CHOICE_LOWEST_PRICE;
import static com.example.robmillaci.realestatemanager.activities.search_activity.SearchResultsView.SORT_CHOICE_NEWEST;
import static com.example.robmillaci.realestatemanager.activities.search_activity.SearchResultsView.SORT_CHOICE_OLDEST;

/**
 * The Presenter for {@link SearchResultsView}
 * Handles getting data from the database and returning it to the mView. Also filters and sorts data as per the views request and returns this data so the UI can be updated
 */
public class SearchResultsPresenter implements MyDatabaseHelper.Model, FirebaseHelper.Model {

    private final View mView; //this presenters mView
    private final WeakReference<Context> mContextWeakReference;


    SearchResultsPresenter(View view, WeakReference<Context> mWeakContext) {
        this.mView = view;
        this.mContextWeakReference = mWeakContext;
    }


    /**
     * Called from the mView to search and return listings from either Firebase or the local db
     *
     * @param extras the search parameters
     */
    void searchDatabase(Bundle extras) {
        if (Utils.CheckConnectivity(mContextWeakReference.get())) {
            FirebaseHelper.getInstance().setPresenter(this).searchForSaleListings(extras);
        } else {
            MyDatabaseHelper.getInstance(mContextWeakReference.get()).setPresenter(this).searchLocalDB(mContextWeakReference.get(), extras);
        }
    }


    /**
     * Callback from {@link MyDatabaseHelper#searchLocalDB(Context, Bundle)}
     * Sends the listings back to the mView
     *
     * @param listings the returned listings
     */
    @Override
    public void gotDataFromLocalDb(ArrayList<Listing> listings) {
        mView.gotAllListing(listings);
    }


    /**
     * Callback from {@link FirebaseHelper#getAllListings()}
     * * Sends the listings back to the mView
     *
     * @param listings the returned listings
     */
    @Override
    public void gotListingsFromFirebase(ArrayList<Listing> listings) {
        mView.gotAllListing(listings);
    }


    /**
     * Called from {@link SearchResultsView} to filter and return the listings
     *
     * @param selectedValue    the chosen filter option
     * @param originalListings the origional listings to be filtered
     */
    void filterData(String selectedValue, ArrayList<Listing> originalListings) {
        ArrayList<Listing> filteredListings = new ArrayList<>();

        if (selectedValue.equals("Any")) {
            mView.filteredListings(originalListings);
        } else {
            for (Listing l : originalListings) {
                if (l.getType().equals(selectedValue)) {
                    filteredListings.add(l);
                }
            }
            mView.filteredListings(filteredListings);
        }

    }


    /**
     * Called from {@link SearchResultsView} to sort and return the listings
     *
     * @param sortCondition    the chosen sort criteria
     * @param originalListings the origional listings to be filtered
     */
    void sortData(ArrayList<Listing> originalListings, int sortCondition) {
        switch (sortCondition) {
            case SORT_CHOICE_HIGHEST_PRICE:
                Collections.sort(originalListings, new Comparator<Listing>() {
                    @Override
                    public int compare(Listing o1, Listing o2) {
                        return Double.compare(o2.getPrice(), o1.getPrice());
                    }
                });
                mView.sortedListings(originalListings);
                break;


            case SORT_CHOICE_LOWEST_PRICE:
                Collections.sort(originalListings, new Comparator<Listing>() {
                    @Override
                    public int compare(Listing o1, Listing o2) {
                        return Double.compare(o1.getPrice(), o2.getPrice());
                    }
                });
                mView.sortedListings(originalListings);
                break;


            case SORT_CHOICE_NEWEST:
                Collections.sort(originalListings, new Comparator<Listing>() {
                    @Override
                    public int compare(Listing o1, Listing o2) {
                        Date firstDate = Utils.stringToDate(o1.getPostedDate());
                        Date secondDate = Utils.stringToDate(o2.getPostedDate());

                        return (firstDate != null && secondDate != null) ? secondDate.compareTo(firstDate) : 0;
                    }
                });

                mView.sortedListings(originalListings);
                break;


            case SORT_CHOICE_OLDEST:
                Collections.sort(originalListings, new Comparator<Listing>() {
                    @Override
                    public int compare(Listing o1, Listing o2) {
                        Date firstDate = Utils.stringToDate(o1.getPostedDate());
                        Date secondDate = Utils.stringToDate(o2.getPostedDate());

                        return (firstDate != null && secondDate != null) ? firstDate.compareTo(secondDate) : 0;
                    }
                });

                mView.sortedListings(originalListings);
                break;
        }
    }


    //The views interface methods
    public interface View {
        void gotAllListing(ArrayList<Listing> listings);

        void filteredListings(ArrayList<Listing> returnedListings);

        void sortedListings(ArrayList<Listing> sortedListings);
    }
}
