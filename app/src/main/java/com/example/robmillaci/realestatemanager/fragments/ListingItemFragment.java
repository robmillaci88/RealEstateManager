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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.robmillaci.realestatemanager.R;
import com.example.robmillaci.realestatemanager.adapters.ImagesViewPagerAdapter;
import com.example.robmillaci.realestatemanager.activities.add_listing_activity.AddListingView;
import com.example.robmillaci.realestatemanager.activities.book_viewing_activity.BookViewingActivity;
import com.example.robmillaci.realestatemanager.activities.offers_activities.MakeAnOffer;
import com.example.robmillaci.realestatemanager.activities.search_activity.StreetViewActivity;
import com.example.robmillaci.realestatemanager.data_objects.Listing;
import com.example.robmillaci.realestatemanager.utils.SharedPreferenceHelper;
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

    public static final String LISTING_BUNDLE_KEY = "mThisListing"; //the bundle key for passing a listing through an intent bundle
    public static final String EDIT_LISTING_BUNDLE_KEY = "listingToEdit"; //the bundle key for passing a listing to be edited through an intent bundle
    private static final int TABLET_REQUEST_CODE = 1; //the identifier to determine if the user is requesting the map view from a tablet device
    private static final int NON_TABLET_REQUEST_CODE = 0;//the identifier to determine if the user is requesting the map view from a non-tablet device

    private Listing mThisListing; //the listing this fragment is displaying
    private ViewPager mViewPager; //the viewpager holding the listings images
    private TextView mPriceTextView; //the price of the listing
    private TextView mType; //the type of the listing
    private TextView mAddress; //the address of the listing
    private TextView mDescr; //on click to display the description of the listing
    private TextView mMapviewTv; //on click to display the listing on google maps
    private TextView mStreetView; //on click to display the street view of the listing
    private TextView mPostedDateTv; //the posted date of the listing
    private TextView mEditDateTv; //the last edited time of the listing
    private FloatingActionButton mEditListingFab; //Floating action button for administrators to edit the listing
    private CircleIndicator mCircleIndicator; //the indicator to show the position of the current image in the view pagers
    private Button mBookViewingButton; //button to book a viewing for this listing
    private Button mMakeAnOfferButton; //button to make an offer for this listing

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //noinspection ConstantConditions
        mThisListing = new SharedPreferenceHelper(getContext().getApplicationContext()).getListingFromSharedPrefs(); //get the listing from shared preferences
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

        initializeViews(view);
        updateViews();

        setOnClicks();

        //noinspection ConstantConditions
        if (Utils.isTablet(getActivity().getApplicationContext())) { //if we are using a tablet, create the map fragment and display it as soon as the activity starts
            createMapFragment(TABLET_REQUEST_CODE);
        }

    }

    private void initializeViews(View view) {
        mViewPager = view.findViewById(R.id.images_viewpager_holder);
        mPriceTextView = view.findViewById(R.id.price_text_view);
        mType = view.findViewById(R.id.type);
        mAddress = view.findViewById(R.id.address);
        mDescr = view.findViewById(R.id.description_tv);
        mMapviewTv = view.findViewById(R.id.mapbtn);
        mStreetView = view.findViewById(R.id.streetView);
        mBookViewingButton = view.findViewById(R.id.book_a_viewing_btn);
        mMakeAnOfferButton = view.findViewById(R.id.make_an_offer_button);
        mPostedDateTv = view.findViewById(R.id.posted_date_tv);
        mEditDateTv = view.findViewById(R.id.edit_date_tv);
        mEditListingFab = view.findViewById(R.id.edit_listing_fab);
        mCircleIndicator = view.findViewById(R.id.image_indicator);


        /*
        Check if the user is an administrator, if so display the edit listing FAB
         */
        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        if (mPreferences.getBoolean(USER_DATABASE_ISADMIN_FIELD, false)) {
            mEditListingFab.show();
        } else {
            mEditListingFab.hide();
        }

    }


    @SuppressWarnings("ResultOfMethodCallIgnored")
    @SuppressLint("CheckResult")
    private void setOnClicks() {
        final Context c = ListingItemFragment.this.getContext();

        RxView.clicks(mDescr).subscribe(new Consumer<Unit>() {
            @Override
            public void accept(Unit unit) { //display the description of the listing
                new AlertDialog.Builder(c)
                        .setTitle(String.format("%s %s %s", getString(R.string.about), mThisListing.getAddress_number(), mThisListing.getAddress_street()))
                        .setPositiveButton(getString(R.string.close_button), null)
                        .setMessage(mThisListing.getDescr())
                        .show();
            }
        });


        RxView.clicks(mMapviewTv).subscribe(new Consumer<Unit>() {
            @Override
            public void accept(Unit unit) { //create the map view fragment
                    createMapFragment(NON_TABLET_REQUEST_CODE);
            }
        });


        RxView.clicks(mStreetView).subscribe(new Consumer<Unit>() {
            @Override
            public void accept(Unit unit) { //Start the street view for the listing
                    Intent streetViewIntent = new Intent(getActivity(), StreetViewActivity.class);
                    streetViewIntent.putExtra(LISTING_BUNDLE_KEY, mThisListing);

                    startActivity(streetViewIntent);
            }
        });


        RxView.clicks(mBookViewingButton).subscribe(new Consumer<Unit>() {
            @Override
            public void accept(Unit unit) { //book a viewing for this listing
                Intent bookAViewingIntent = new Intent(getActivity(), BookViewingActivity.class);
                bookAViewingIntent.putExtra(ListingItemFragment.LISTING_BUNDLE_KEY, mThisListing);
                startActivity(bookAViewingIntent);
            }
        });


        RxView.clicks(mEditListingFab).subscribe(new Consumer<Unit>() {
            @Override
            public void accept(Unit unit) { //edit this listing
                Intent editListingIntent = new Intent(getActivity(), AddListingView.class);
                editListingIntent.putExtra(EDIT_LISTING_BUNDLE_KEY, mThisListing);
                startActivity(editListingIntent);
            }
        });


        RxView.clicks(mMakeAnOfferButton).subscribe(new Consumer<Unit>() {
            @Override
            public void accept(Unit unit){ //make an offer on this listing
              Intent makeAnOfferIntent = new Intent(getActivity(),MakeAnOffer.class);
              makeAnOfferIntent.putExtra(LISTING_BUNDLE_KEY, mThisListing);
              startActivity(makeAnOfferIntent);
            }
        });
    }


    /**
     * Creates the map view fragment for this listing.
     * @param requestCode either Tablet or non-tablet
     */
    @SuppressWarnings("ConstantConditions")
    private void createMapFragment(int requestCode) {
        MapViewFragment mapViewFragment = new MapViewFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(LISTING_BUNDLE_KEY, mThisListing);
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


    /**
     * Update this Fragments views
     */
    private void updateViews() {
        //noinspection ConstantConditions
        mViewPager.setAdapter(new ImagesViewPagerAdapter(new WeakReference<>(getActivity().getApplicationContext()), mThisListing));

        mPriceTextView.setText(String.format("%s %s", getString(R.string.currency_symbol), mThisListing.getFormattedPrice()));

        mType.setText(String.format(Locale.getDefault(), "%d %s %s.",
                mThisListing.getNumbOfBedRooms(),
                getResources().getString(R.string.bedrooms),
                mThisListing.getType()));

        mAddress.setText(String.format("%s %s, %s, %s.", mThisListing.getAddress_number(),
                mThisListing.getAddress_street(),
                mThisListing.getAddress_town(),
                mThisListing.getAddress_postcode().toUpperCase()));

        mPostedDateTv.setText(String.format("%s %s" ,
                getString(R.string.posted_on),
                mThisListing.getFormattedPostedDate()));

        mEditDateTv.setText(String.format("%s %s",
                getString(R.string.last_edit),
                mThisListing.getFormattedLastUpdateTime()));

        mCircleIndicator.setViewPager(mViewPager);
    }

}
