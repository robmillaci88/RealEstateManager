package com.example.robmillaci.realestatemanager.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.robmillaci.realestatemanager.R;
import com.jakewharton.rxbinding3.view.RxView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import io.reactivex.functions.Consumer;
import kotlin.Unit;

/**
 * The adapter for the {@link com.example.robmillaci.realestatemanager.activities.book_viewing_activity.BookViewingActivity}<br/>
 * Displays a list of available booking mTimes to view a property.
 */
public class BookingTimesAdapter extends RecyclerView.Adapter<BookingTimesAdapter.MyViewholder> {
    private final ArrayList<String> mTimes; //Arraylist of Strings to hold the booking mTimes
    private final WeakReference<Context> mContextWeakReference; //Weak reference to the activity that instantiates this class
    private MyViewholder mSelectedViewHolderRef; //a reference to the selected times viewholder
    private BookingAdapterCallback mBookingAdapterCallback; //callback

    public BookingTimesAdapter(ArrayList<String> times, WeakReference<Context> weakReferenceContext) {
        this.mTimes = times;
        this.mContextWeakReference = weakReferenceContext;
    }

    @NonNull
    @Override
    public MyViewholder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        @SuppressLint("InflateParams") View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.booking_times_item_view, null);
        return new MyViewholder(v);
    }

    @SuppressLint("CheckResult")
    @Override
    public void onBindViewHolder(@NonNull final MyViewholder myViewholder, int position) {
        myViewholder.time.setText(mTimes.get(position));

        //noinspection ResultOfMethodCallIgnored
        RxView.clicks(myViewholder.itemView).subscribe(new Consumer<Unit>() {
            @Override
            public void accept(Unit unit) {
                if (mSelectedViewHolderRef != null) {
                    deSelectViewholder(mSelectedViewHolderRef); //deselect any previously selected time
                }
                selectItem(myViewholder);
                mBookingAdapterCallback.timeSelected();
            }
        });
    }


    /**
     * Selects a specific time which updates the viewholders background and text color. Also sets the reference of the current selected viewholder
     * @param myViewholder the viewholder clicked on
     */
    private void selectItem(MyViewholder myViewholder) {
        myViewholder.itemView.setBackgroundColor(mContextWeakReference.get().getResources().getColor(R.color.colorAccent));
        myViewholder.title.setTextColor(Color.WHITE);
        myViewholder.time.setTextColor(Color.WHITE);
        mSelectedViewHolderRef = myViewholder;
    }


    /**
     * DeSelects the viewholder stores in our mSelectedViewholderRef, removing the background colour and setting the text back to black
     * @param selectedViewHolderRef the viewholder to be de selected
     */
    private void deSelectViewholder(MyViewholder selectedViewHolderRef) {
        selectedViewHolderRef.itemView.setBackgroundColor(Color.TRANSPARENT);
        selectedViewHolderRef.title.setTextColor(Color.BLACK);
        selectedViewHolderRef.time.setTextColor(Color.BLACK);
    }



    @Override
    public int getItemCount() {
        return mTimes.size();
    }



    public void setCallbackListener(BookingAdapterCallback l) {
        mBookingAdapterCallback = l;
    }


    static class MyViewholder extends RecyclerView.ViewHolder {
        final TextView time;
        final TextView title;

        MyViewholder(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.time);
            title = itemView.findViewById(R.id.request_title);
        }
    }

    public interface BookingAdapterCallback {
        void timeSelected();
    }
}
