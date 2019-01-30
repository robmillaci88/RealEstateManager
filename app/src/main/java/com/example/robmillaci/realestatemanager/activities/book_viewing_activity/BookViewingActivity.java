package com.example.robmillaci.realestatemanager.activities.book_viewing_activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.robmillaci.realestatemanager.activities.valuations_activities.ConfirmationActivity;
import com.example.robmillaci.realestatemanager.adapters.BookingTimesAdapter;
import com.example.robmillaci.realestatemanager.R;
import com.example.robmillaci.realestatemanager.data_objects.Listing;
import com.example.robmillaci.realestatemanager.fragments.ListingItemFragment;
import com.jakewharton.rxbinding3.view.RxView;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.HorizontalCalendarView;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;
import io.reactivex.functions.Consumer;
import kotlin.Unit;

/**
 * This class is responsible for displaying the Book a view activity
 */
public class BookViewingActivity extends AppCompatActivity implements BookingTimesAdapter.BookingAdapterCallback {
    private Listing mThisListing; //the listing of which a viewing is being booked
    private ImageView mListingImage; //the imageview to display the listings image
    private RecyclerView mTimesRecyclerView; //the recyclerview to display the available booking times
    private Button mContinueBtn; //the continue button

    private static final int MAX_BOOKINGS_PER_DAY = 25; //the maximum number of bookings per day

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_viewing);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.book_viewing_activity_title)); //the title of the activity

        getListing();
        initializeViews();
        setOnClickListeners();
        loadPhoto();

        createCalendarSelector();
        createTimesRecyclerView();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void createTimesRecyclerView() {
        ArrayList<String> dateTimes = new ArrayList<>();

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 9);
        cal.set(Calendar.MINUTE,0);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");

        for (int i = 0; i < MAX_BOOKINGS_PER_DAY; i++) {
            Date date = cal.getTime();
            dateTimes.add(dateFormat.format(date));
            cal.add(Calendar.MINUTE, 30);
        }

        BookingTimesAdapter timesAdapter = new BookingTimesAdapter(dateTimes, new WeakReference<Context>(this));
        timesAdapter.setCallbackListener(this);

        mTimesRecyclerView.setLayoutManager(new GridLayoutManager(this,4,GridLayoutManager.VERTICAL,false));
        mTimesRecyclerView.setAdapter(timesAdapter);
    }


    /**
     * Get the listing passed into the intent when starting this activity. This is the listing that a viewing is being booked for
     */
    private void getListing() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mThisListing = (Listing) bundle.getSerializable(ListingItemFragment.LISTING_BUNDLE_KEY);
        }
    }

    private void initializeViews() {
        mListingImage = findViewById(R.id.listing_image);
        TextView address_text = findViewById(R.id.address_text);
        mTimesRecyclerView = findViewById(R.id.times_recycler_view);

        mContinueBtn = findViewById(R.id.continue_btn);
        mContinueBtn.setClickable(false);
        mContinueBtn.setBackgroundColor(getResources().getColor(R.color.lightGrey));

        address_text.setText(String.format("%s %s, %s, %s", mThisListing.getAddress_number(),
                mThisListing.getAddress_street(),
                mThisListing.getAddress_county(),
                mThisListing.getAddress_postcode()));
    }

    @SuppressLint("CheckResult")
    private void setOnClickListeners(){
        //noinspection ResultOfMethodCallIgnored
        RxView.clicks(mContinueBtn)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) {
                        Intent bookingConfirmation = new Intent(BookViewingActivity.this, ConfirmationActivity.class);
                        bookingConfirmation.putExtra(ConfirmationActivity.BUNDLE_KEY,ConfirmationActivity.CALLED_FROM_BOOK_VIEWING);
                        startActivity(bookingConfirmation);
                    }
                });
    }


    /**
     * Loads the photo associated with this listing into the listing image
     * First we must determine if the photo data is being obtained from Firebase or from the local DB
     */
    private void loadPhoto() {
        ArrayList<String> firebasePhoto = mThisListing.getFirebasePhotos();
        List<byte[]> localPhoto = mThisListing.getLocalDbPhotos();

        if (firebasePhoto == null || firebasePhoto.size() == 0) {
            Glide.with(getApplicationContext()).asBitmap().load(localPhoto.get(0)).into(mListingImage);
        } else {
            Glide.with(getApplicationContext()).asBitmap().load(firebasePhoto.get(0)).into(mListingImage);
        }
    }


    /**
     * Create a calendar selector which will display the times available for booking
     */
    private void createCalendarSelector() {
        /* ends after 3 month from now */
        Calendar endDate = Calendar.getInstance(); //get a calendar instance and add 3 months to it, this is the maximum number of days that will be displayed
        endDate.add(Calendar.MONTH, 3);

        final HorizontalCalendar horizontalCalendar = new HorizontalCalendar.Builder(this, R.id.calendarView) //create a Horizontal calendar
                .range(Calendar.getInstance(), endDate) //set the range from now to 3 months in the future
                .datesNumberOnScreen(5) //the number of dates on screen at once
                .build();

        horizontalCalendar.getSelectedItemStyle().setBackground(new ColorDrawable(getResources().getColor(R.color.colorAccent))); //set the color when a date is selected

        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {

            }

            @Override
            public void onCalendarScroll(HorizontalCalendarView calendarView,
                                         int dx, int dy) {

            }

            @Override
            public boolean onDateLongClicked(Calendar date, int position) {
                return true;
            }
        });
    }

    @Override
    public void timeSelected() {
        mContinueBtn.setClickable(true);
        mContinueBtn.setBackgroundResource(R.drawable.green_button_round_edges);
    }
}
