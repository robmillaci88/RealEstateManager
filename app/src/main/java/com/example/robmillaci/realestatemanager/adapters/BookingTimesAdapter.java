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

public class BookingTimesAdapter extends RecyclerView.Adapter<BookingTimesAdapter.MyViewholder> {
    private ArrayList<String> times;
    private WeakReference<Context> mContextWeakReference;
    private MyViewholder selectedViewHolderRef;
    private BookingAdapterCallback mBookingAdapterCallback;

    public BookingTimesAdapter(ArrayList<String> times, WeakReference<Context> weakReferenceContext) {
        this.times = times;
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
        myViewholder.time.setText(times.get(position));

        RxView.clicks(myViewholder.itemView).subscribe(new Consumer<Unit>() {
            @Override
            public void accept(Unit unit) {
                if (selectedViewHolderRef != null) {
                    unselectItem(selectedViewHolderRef);
                }
                selectItem(myViewholder);
                mBookingAdapterCallback.timeSelected();
            }
        });
    }

    private void selectItem(MyViewholder myViewholder) {
        myViewholder.itemView.setBackgroundColor(mContextWeakReference.get().getResources().getColor(R.color.colorAccent));
        myViewholder.title.setTextColor(Color.WHITE);
        myViewholder.time.setTextColor(Color.WHITE);
        selectedViewHolderRef = myViewholder;
    }

    private void unselectItem(MyViewholder selectedViewHolderRef) {
        selectedViewHolderRef.itemView.setBackgroundColor(Color.TRANSPARENT);
        selectedViewHolderRef.title.setTextColor(Color.BLACK);
        selectedViewHolderRef.time.setTextColor(Color.BLACK);
    }

    @Override
    public int getItemCount() {
        return times.size();
    }

    public void setCallbackListener(BookingAdapterCallback l) {
        mBookingAdapterCallback = l;
    }


    static class MyViewholder extends RecyclerView.ViewHolder {
        TextView time;
        TextView title;

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
