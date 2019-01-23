package com.example.robmillaci.realestatemanager.Adapters;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.robmillaci.realestatemanager.R;
import com.example.robmillaci.realestatemanager.data_objects.Listing;
import com.example.robmillaci.realestatemanager.utils.SharedPreferenceHelper;
import com.example.robmillaci.realestatemanager.utils.Utils;
import com.jakewharton.rxbinding3.view.RxView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import io.reactivex.functions.Consumer;
import kotlin.Unit;

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.MyViewholder> {

    private WeakReference<Context> mContext;
    private ArrayList<Listing> mSearchResults;
    private SearchResultsAdapterCallback mCallback;
    private View previouslySelectedHolder = null;

    public SearchResultsAdapter(WeakReference<Context> context, ArrayList<Listing> searchResults, SearchResultsAdapterCallback callback) {
        this.mContext = context;
        this.mSearchResults = searchResults;
        this.mCallback = callback;
    }

    @NonNull
    @Override
    public MyViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_results_item, parent, false);
        return new MyViewholder(v);//returns a new object of static inner class to the 'OnBindViewHolder method
    }

    @SuppressLint({"CheckResult", "DefaultLocale", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull MyViewholder holder, final int position) {
        Listing thisListing = mSearchResults.get(position);

        if (thisListing.getPhotos() != null && thisListing.getPhotos().size() > 0) {
            try {
                Glide.with(mContext.get())
                        .asBitmap()
                        .load(thisListing.getPhotos().get(0))
                        .into(holder.mainImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (thisListing.getFirebasePhotos() != null && thisListing.getFirebasePhotos().size() > 0) {
            try {
                Glide.with(mContext.get())
                        .asBitmap()
                        .load(thisListing.getFirebasePhotos().get(0))
                        .into(holder.mainImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            holder.mainImage.setImageResource(R.drawable.placeholder_image);
        }

        holder.price.setText("£" + String.format("%.0f", thisListing.getPrice()));

        holder.address.setText(String.format("%s,%s, %s.", thisListing.getAddress_street(), thisListing.getAddress_town(),
                thisListing.getAddress_postcode().toUpperCase()));

        holder.desc.setText(String.format("%d %s %s.", thisListing.getNumbOfBedRooms(),mContext.get().getString(R.string.bedrooms), thisListing.getType()));

        setOnClickEvent(holder.itemView, holder.getAdapterPosition());
    }

    @Override
    public int getItemCount() {
        return mSearchResults == null ? 0 : mSearchResults.size();
    }

    @SuppressLint("CheckResult")
    private void setOnClickEvent(final View holder, final int position) {
        RxView.clicks(holder).subscribe(new Consumer<Unit>() {
            @Override
            public void accept(Unit unit) {
                Listing thisListing = mSearchResults.get(position);
                new SharedPreferenceHelper(mContext.get()).addListingToSharedPref(thisListing);
                mCallback.setFragment(thisListing.getAddress_number() + " " + thisListing.getAddress_street());

                if (Utils.isTablet(mContext.get())){
                    holder.setBackgroundColor(mContext.get().getResources().getColor(R.color.colorPrimaryDark));

                    if (previouslySelectedHolder != null){
                        previouslySelectedHolder.setBackgroundColor(Color.WHITE);
                    }
                    previouslySelectedHolder = holder;
                }
            }
        });
    }


    static class MyViewholder extends RecyclerView.ViewHolder {
        ImageView mainImage;
        TextView price;
        TextView address;
        TextView desc;

        MyViewholder(View v) {
            super(v);
            mainImage = v.findViewById(R.id.mainImage);
            price = v.findViewById(R.id.price);
            address = v.findViewById(R.id.address);
            desc = v.findViewById(R.id.desc);
        }
    }

    public interface SearchResultsAdapterCallback {
        void setFragment(String listingAddress);
    }
}


