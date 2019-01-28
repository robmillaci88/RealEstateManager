package com.example.robmillaci.realestatemanager.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.robmillaci.realestatemanager.R;
import com.example.robmillaci.realestatemanager.data_objects.Listing;

import java.lang.ref.WeakReference;

/**
 * The page adapter class for <br/>{@link com.example.robmillaci.realestatemanager.fragments.ListingItemFragment} <br/> and <br/>
 * {@link com.example.robmillaci.realestatemanager.activities.offers_activities.MakeAnOffer }
 * Populate the view pager with a listings images to provide image swipe change abilities
 */
public class ImagesViewPagerAdapter extends PagerAdapter {
    private static final int PHOTOS_FROM_FIREBASE = 0; //identifier that photos are from firebase
    private static final int PHOTOS_FROM_LOCAL_DB = 1; //identifier that photos are from the local DB
    private static final int NO_PHOTOS = -1; //identifier that no photos are available

    private final Listing mThisListing; //the listing of which the photos are part of
    private final WeakReference<Context> mContextWeakReference; //the weak reference to the activity that instantiated this Page Adapter
    private final int mPhotosource; //the source of the photos


    public ImagesViewPagerAdapter(WeakReference<Context> c, Listing l) {
        mThisListing = l;
        mContextWeakReference = c;

        if (l.getLocalDbPhotos() == null && l.getFirebasePhotos() != null) { //Photos are firebase photos (URI strings)
            mPhotosource = PHOTOS_FROM_FIREBASE;
        } else if (l.getFirebasePhotos() == null && l.getLocalDbPhotos() != null) { //Photos are local DB photos (byte[]s)
            mPhotosource = PHOTOS_FROM_LOCAL_DB;
        } else if (l.getLocalDbPhotos() == null && l.getFirebasePhotos() == null) { //there are no photos - this is unlikely to happen unless there is an error with the data
            mPhotosource = NO_PHOTOS;
        } else {
            mPhotosource = NO_PHOTOS;
        }
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View imageLayout = LayoutInflater.from(mContextWeakReference.get()).inflate(R.layout.images_viewpager_layout, container, false);
        final ImageView imageView = imageLayout
                .findViewById(R.id.imageviewpageritem);

        final TextView imageDesc = imageLayout.findViewById(R.id.image_descr_tv);
        String[] imageDescription = mThisListing.getPhotoDescriptions();


        switch (mPhotosource) {
            case PHOTOS_FROM_FIREBASE: //if the photos are Firebase photos , call getFirebasePhotos()
                Glide.with(mContextWeakReference.get())
                        .asBitmap()
                        .load(mThisListing.getFirebasePhotos().get(position))
                        .into(imageView);
                break;

            case PHOTOS_FROM_LOCAL_DB://if the photos are local db photos , call getLocalDbPhotos()
                Glide.with(mContextWeakReference.get())
                        .asBitmap()
                        .load(mThisListing.getLocalDbPhotos().get(position))
                        .into(imageView);
                break;

            case NO_PHOTOS: //no photos, do nothing
                break;

            default:
                break;
        }

        if (imageDescription != null) {
            try {
                imageDesc.setText(imageDescription[position]);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        container.addView(imageLayout, 0);
        return imageLayout;
    }

    @Override
    public int getCount() {
        switch (mPhotosource) {
            case PHOTOS_FROM_FIREBASE:
                return mThisListing.getFirebasePhotos().size();

            case PHOTOS_FROM_LOCAL_DB:
                return mThisListing.getLocalDbPhotos().size();

            case NO_PHOTOS:
                return 0;

            default:
                return 0;
        }
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
