package com.example.robmillaci.realestatemanager.activities.search_activity;

import android.content.Context;
import android.os.Bundle;

import com.example.robmillaci.realestatemanager.data_objects.Listing;
import com.example.robmillaci.realestatemanager.databases.firebase.FirebaseHelper;
import com.example.robmillaci.realestatemanager.databases.local_database.MyDatabase;
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

public class SearchResultsPresenter implements MyDatabase.Model, FirebaseHelper.Model {

    private View view;
    private WeakReference<Context> cReference;


    SearchResultsPresenter(View view, WeakReference<Context> mWeakContext) {
        this.view = view;
        this.cReference = mWeakContext;
    }


    public void searchDatabase(Bundle extras) {
        if (Utils.CheckConnectivity(cReference.get())) {
            FirebaseHelper.getInstance().setPresenter(this).getSearchListings(extras);
        } else {
            MyDatabase.getInstance(cReference.get()).setPresenter(this).getFromLocalDb(cReference.get(), extras, 1);
        }
    }


    @Override
    public void gotData(ArrayList<Listing> listings, int requestCode, Context c) {
        view.gotAllListing(listings);
    }

    @Override
    public void gotListingsFromFirebase(ArrayList<Listing> listings) {
        view.gotAllListing(listings);
    }

    public void filterData(String selectedValue, ArrayList<Listing> originalListings) {
        ArrayList<Listing> filteredListings = new ArrayList<>();

        if (selectedValue.equals("Any")) {
            view.filteredListings(originalListings);
        } else {
            for (Listing l : originalListings) {
                if (l.getType().equals(selectedValue)) {
                    filteredListings.add(l);
                }
            }
            view.filteredListings(filteredListings);
        }

    }

    public void sortData(ArrayList<Listing> originalListings, int sortCondition) {
        switch (sortCondition) {
            case SORT_CHOICE_HIGHEST_PRICE:
                Collections.sort(originalListings, new Comparator<Listing>() {
                    @Override
                    public int compare(Listing o1, Listing o2) {
                        return Double.compare(o2.getPrice(), o1.getPrice());
                    }
                });
                view.sortedListings(originalListings);
                break;


            case SORT_CHOICE_LOWEST_PRICE:
                Collections.sort(originalListings, new Comparator<Listing>() {
                    @Override
                    public int compare(Listing o1, Listing o2) {
                        return Double.compare(o1.getPrice(), o2.getPrice());
                    }
                });
                view.sortedListings(originalListings);
                break;


            case SORT_CHOICE_NEWEST:
                Collections.sort(originalListings, new Comparator<Listing>() {
                    @Override
                    public int compare(Listing o1, Listing o2) {
                        Date firstDate = Utils.stringToDate(o1.getPostedDate());
                        Date secondDate = Utils.stringToDate(o2.getPostedDate());

                        return (firstDate != null && secondDate != null) ? firstDate.compareTo(secondDate) : 0;
                    }
                });

                view.sortedListings(originalListings);
                break;


            case SORT_CHOICE_OLDEST:
                Collections.sort(originalListings, new Comparator<Listing>() {
                    @Override
                    public int compare(Listing o1, Listing o2) {
                        Date firstDate = Utils.stringToDate(o1.getPostedDate());
                        Date secondDate = Utils.stringToDate(o2.getPostedDate());

                        return (firstDate != null && secondDate != null) ? secondDate.compareTo(firstDate) : 0;
                    }
                });

                view.sortedListings(originalListings);
                break;
        }
    }


    public interface View {
        void gotAllListing(ArrayList<Listing> listings);

        void filteredListings(ArrayList<Listing> returnedListings);

        void sortedListings(ArrayList<Listing> sortedListings);
    }
}
