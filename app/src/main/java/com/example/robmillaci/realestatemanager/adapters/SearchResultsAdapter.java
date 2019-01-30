package com.example.robmillaci.realestatemanager.adapters;


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
/**
 * The adapter for the {@link com.example.robmillaci.realestatemanager.activities.search_activity.SearchResultsView}<br/>
 * Displays the results of searching the database for listings
 */
public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.MyViewholder> {

    private final WeakReference<Context> mContext; //the weak reference to the activity that instantiated this adapter
    private final ArrayList<Listing> mSearchResults; //An arraylist of listing results obtained whilst searching the database
    private final SearchResultsAdapterCallback mCallback; //the callback
    private View mPreviouslySelectedHolder = null; //a reference to a previously selected viewholder

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
        Listing thisListing = mSearchResults.get(position); //get the specific listing for this position

        if (thisListing.getLocalDbPhotos() != null && thisListing.getLocalDbPhotos().size() > 0) { //we are working with local DB photos (byte[]s)
            try {
                Glide.with(mContext.get())
                        .asBitmap()
                        .load(thisListing.getLocalDbPhotos().get(0)) //call getLocalDbPhotos().get(0) to get the first photo for this listing
                        .into(holder.mainImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (thisListing.getFirebasePhotos() != null && thisListing.getFirebasePhotos().size() > 0) {  //we are working with Firebase photos (Arraylist<String>)
            try {
                Glide.with(mContext.get())
                        .asBitmap()
                        .load(thisListing.getFirebasePhotos().get(0)) //call getFirebasePhotos().get(0) to get the first photo for this listing
                        .into(holder.mainImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            holder.mainImage.setImageResource(R.drawable.placeholder_image);
        }

        holder.price.setText(String.format("%s %s", mContext.get().getString(R.string.currency_symbol), thisListing.getFormattedPrice())); //set the price of the listing

        holder.address.setText(String.format("%s,%s, %s.", thisListing.getAddress_street(), thisListing.getAddress_town(), //update the address for the listing
                thisListing.getAddress_postcode().toUpperCase()));

        holder.desc.setText(String.format("%d %s %s.", thisListing.getNumbOfBedRooms(),mContext.get().getString(R.string.bedrooms), thisListing.getType())); //get the type of listing

        setOnClickEvent(holder.itemView, holder.getAdapterPosition()); //set the viewholders on click event
    }

    @Override
    public int getItemCount() {
        return mSearchResults == null ? 0 : mSearchResults.size();
    }


    /**
     * The on click event for the viewholders. Creates a fragment displaying the full details of a specific listing when clicked.
     * If the users device is a tablet,we will update the selected view holders background colour.
     * @param holder the view to be updated
     * @param position the position of the view
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
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

                    if (mPreviouslySelectedHolder != null){
                        mPreviouslySelectedHolder.setBackgroundColor(Color.WHITE);
                    }
                    mPreviouslySelectedHolder = holder;
                }
            }
        });
    }


    static class MyViewholder extends RecyclerView.ViewHolder {
        final ImageView mainImage;
        final TextView price;
        final TextView address;
        final TextView desc;

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


