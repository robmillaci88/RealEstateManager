package com.example.robmillaci.realestatemanager.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.robmillaci.realestatemanager.R;
import com.example.robmillaci.realestatemanager.adapters.ImagesViewPagerAdapter;
import com.example.robmillaci.realestatemanager.activities.add_listing_activity.AddListingView;
import com.example.robmillaci.realestatemanager.activities.book_viewing_activity.BookViewingActivity;
import com.example.robmillaci.realestatemanager.activities.offers_activities.MakeAnOffer;
import com.example.robmillaci.realestatemanager.activities.search_activity.StreetViewActivity;
import com.example.robmillaci.realestatemanager.data_objects.Listing;
import com.example.robmillaci.realestatemanager.utils.SharedPreferenceHelper;
import com.example.robmillaci.realestatemanager.utils.ToastModifications;
import com.example.robmillaci.realestatemanager.utils.Utils;
import com.jakewharton.rxbinding3.view.RxView;

import java.lang.ref.WeakReference;
import java.util.Locale;

import io.reactivex.functions.Consumer;
import kotlin.Unit;
import me.relex.circleindicator.CircleIndicator;

import static com.example.robmillaci.realestatemanager.activities.search_activity.SearchResultsView.FRAGMENT_TAG;
import static com.example.robmillaci.realestatemanager.databases.firebase.FirebaseContract.USER_DATABASE_ISADMIN_FIELD;

public class ListingItemFragment extends BaseFragment {

    public static final String LISTING_BUNDLE_KEY = "thisListing";
    public static final String EDIT_LISTING_BUNDLE_KEY = "listingToEdit";
    private static final int TABLET_REQUEST_CODE = 1;
    private static final int NON_TABLET_REQUEST_CODE = 0;

    private Listing thisListing;
    private ViewPager pager;
    private TextView price_text_view;
    private TextView type;
    private TextView address;
    private TextView descr;
    private TextView mapview_tv;
    private TextView streetView;
    private TextView posted_date_tv;
    private TextView edit_date_tv;
    private FloatingActionButton edit_listing_fab;
    private CircleIndicator indicator;
    private Button bookViewing;
    private Button makeAnOffer;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //noinspection ConstantConditions
        thisListing = new SharedPreferenceHelper(getContext().getApplicationContext()).getListingFromSharedPrefs();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.listing_item_fragment, container, false);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        initializeViews(view);
        updateViews();

        setOnClicks();

        //noinspection ConstantConditions
        if (Utils.isTablet(getActivity().getApplicationContext())) {
            createMapFragment(TABLET_REQUEST_CODE);
        }

    }

    private void initializeViews(View view) {
        pager = view.findViewById(R.id.images_viewpager_holder);
        price_text_view = view.findViewById(R.id.price_text_view);
        type = view.findViewById(R.id.type);
        address = view.findViewById(R.id.address);
        descr = view.findViewById(R.id.description_tv);
        mapview_tv = view.findViewById(R.id.mapbtn);
        streetView = view.findViewById(R.id.streetView);
        bookViewing = view.findViewById(R.id.book_a_viewing_btn);
        makeAnOffer = view.findViewById(R.id.make_an_offer_button);
        posted_date_tv = view.findViewById(R.id.posted_date_tv);
        edit_date_tv = view.findViewById(R.id.edit_date_tv);
        edit_listing_fab = view.findViewById(R.id.edit_listing_fab);

        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        if (mPreferences.getBoolean(USER_DATABASE_ISADMIN_FIELD, false)) {
            edit_listing_fab.show();
        } else {
            edit_listing_fab.hide();
        }

        indicator = view.findViewById(R.id.image_indicator);
    }


    @SuppressLint("CheckResult")
    private void setOnClicks() {
        final Context c = ListingItemFragment.this.getContext();

        RxView.clicks(descr).subscribe(new Consumer<Unit>() {
            @Override
            public void accept(Unit unit) {
                new AlertDialog.Builder(c)
                        .setTitle(String.format("%s %s %s", getString(R.string.about), thisListing.getAddress_number(), thisListing.getAddress_street()))
                        .setPositiveButton(getString(R.string.close_button), null)
                        .setMessage(thisListing.getDescr())
                        .show();
            }
        });


        RxView.clicks(mapview_tv).subscribe(new Consumer<Unit>() {
            @Override
            public void accept(Unit unit) {
                if (Utils.CheckConnectivity(c)) {
                    createMapFragment(NON_TABLET_REQUEST_CODE);
                }else {
                    ToastModifications.createToast(getContext(),"Internet is not available", Toast.LENGTH_LONG);
                }
            }
        });


        RxView.clicks(streetView).subscribe(new Consumer<Unit>() {
            @Override
            public void accept(Unit unit) {
                if (Utils.CheckConnectivity(c)) {
                    Intent streetViewIntent = new Intent(getContext(), StreetViewActivity.class);
                    streetViewIntent.putExtra(LISTING_BUNDLE_KEY, thisListing);

                    startActivity(streetViewIntent);
                }else {
                    ToastModifications.createToast(getContext(),"Internet is not available", Toast.LENGTH_LONG);
                }
            }
        });


        RxView.clicks(bookViewing).subscribe(new Consumer<Unit>() {
            @Override
            public void accept(Unit unit) {
                Intent bookAViewingIntent = new Intent(c, BookViewingActivity.class);
                bookAViewingIntent.putExtra(ListingItemFragment.LISTING_BUNDLE_KEY, thisListing);
                startActivity(bookAViewingIntent);
            }
        });


        RxView.clicks(edit_listing_fab).subscribe(new Consumer<Unit>() {
            @Override
            public void accept(Unit unit) {
                Log.d("accept", "accept: called");
                Intent editListingIntent = new Intent(c, AddListingView.class);
                editListingIntent.putExtra(EDIT_LISTING_BUNDLE_KEY, thisListing);
                startActivity(editListingIntent);
            }
        });


        RxView.clicks(makeAnOffer).subscribe(new Consumer<Unit>() {
            @Override
            public void accept(Unit unit){
              Intent makeAnOfferIntent = new Intent(getContext(),MakeAnOffer.class);
              makeAnOfferIntent.putExtra(LISTING_BUNDLE_KEY, thisListing);
              startActivity(makeAnOfferIntent);
            }
        });
    }

    @SuppressWarnings("ConstantConditions")
    private void createMapFragment(int requestCode) {
        MapViewFragment mapViewFragment = new MapViewFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(LISTING_BUNDLE_KEY, thisListing);
        mapViewFragment.setArguments(bundle);

        //noinspection ConstantConditions
        switch (requestCode) {
            case TABLET_REQUEST_CODE:
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.map_tablet_container, mapViewFragment, FRAGMENT_TAG)
                        .addToBackStack(null)
                        .commit();
                break;

            case NON_TABLET_REQUEST_CODE:
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, mapViewFragment, FRAGMENT_TAG)
                        .addToBackStack(null)
                        .commit();
                break;
        }
    }


    private void updateViews() {
        //noinspection ConstantConditions
        pager.setAdapter(new ImagesViewPagerAdapter(new WeakReference<>(getActivity().getApplicationContext()), thisListing));

        price_text_view.setText(String.format("%s %s", getString(R.string.currency_symbol), thisListing.getFormattedPrice()));

        type.setText(String.format(Locale.getDefault(), "%d %s %s.",
                thisListing.getNumbOfBedRooms(),
                getResources().getString(R.string.bedrooms),
                thisListing.getType()));

        address.setText(String.format("%s %s, %s, %s.", thisListing.getAddress_number(),
                thisListing.getAddress_street(),
                thisListing.getAddress_town(),
                thisListing.getAddress_postcode().toUpperCase()));

        posted_date_tv.setText(String.format("%s %s" ,
                getString(R.string.posted_on),
                thisListing.getFormattedPostedDate()));

        edit_date_tv.setText(String.format("%s %s",
                getString(R.string.last_edit),
                thisListing.getFormattedLastUpdateTime()));

        indicator.setViewPager(pager);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }
}
