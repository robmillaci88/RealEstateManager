package com.example.robmillaci.realestatemanager.Adapters;

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

public class ImagesViewPagerAdapter extends PagerAdapter {
    private static final int PHOTOS_FROM_FIREBASE = 0;
    private static final int PHOTOS_FROM_LOCAL_DB = 1;
    private static final int NO_PHOTOS = -1;

    private Listing thisListing;
    private WeakReference<Context> mContextWeakReference;
    private int photoSource;


    public ImagesViewPagerAdapter(WeakReference<Context> c, Listing l) {
        thisListing = l;
        mContextWeakReference = c;

        if (l.getPhotos() == null && l.getFirebasePhotos() != null) {
            photoSource = PHOTOS_FROM_FIREBASE;
        } else if (l.getFirebasePhotos() == null && l.getPhotos() != null) {
            photoSource = PHOTOS_FROM_LOCAL_DB;
        } else if (l.getPhotos() == null && l.getFirebasePhotos() == null) {
            photoSource = NO_PHOTOS;
        } else {
            photoSource = NO_PHOTOS;
        }
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View imageLayout = LayoutInflater.from(mContextWeakReference.get()).inflate(R.layout.images_viewpager_layout, container, false);
        final ImageView imageView = imageLayout
                .findViewById(R.id.imageviewpageritem);

        final TextView imageDesc = imageLayout.findViewById(R.id.image_descr_tv);
        String[] imageDescription = thisListing.getPhotoDescriptions();

        switch (photoSource) {
            case PHOTOS_FROM_FIREBASE:
                Glide.with(mContextWeakReference.get())
                        .asBitmap()
                        .load(thisListing.getFirebasePhotos().get(position))
                        .into(imageView);
                break;

            case PHOTOS_FROM_LOCAL_DB:
                Glide.with(mContextWeakReference.get())
                        .asBitmap()
                        .load(thisListing.getPhotos().get(position))
                        .into(imageView);
                break;

            case NO_PHOTOS:
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
        switch (photoSource) {
            case PHOTOS_FROM_FIREBASE:
                return thisListing.getFirebasePhotos().size();

            case PHOTOS_FROM_LOCAL_DB:
                return thisListing.getPhotos().size();

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
