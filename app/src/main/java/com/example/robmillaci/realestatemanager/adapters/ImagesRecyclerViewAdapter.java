package com.example.robmillaci.realestatemanager.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.robmillaci.realestatemanager.R;
import com.example.robmillaci.realestatemanager.activities.full_screen_photo_activity.FullScreenPhotoActivity;
import com.example.robmillaci.realestatemanager.utils.image_tools.ImageTools;
import com.jakewharton.rxbinding3.view.RxView;

import java.util.ArrayList;

import io.reactivex.functions.Consumer;
import kotlin.Unit;

public class ImagesRecyclerViewAdapter extends RecyclerView.Adapter<ImagesRecyclerViewAdapter.MyViewholder> {
    private ArrayList<Bitmap> images;
    private ArrayList<String> images_descr;
    private Context mContext;
    private IactivityCallback mIactivityCallback;

    public ImagesRecyclerViewAdapter(Context context, ArrayList<Bitmap> images, ArrayList<String> imageDesc, IactivityCallback activityCallback) {
        this.images = images;
        this.mContext = context;
        images_descr = imageDesc;
        mIactivityCallback = activityCallback;
    }

    @NonNull
    @Override
    public MyViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_listing_recyclerview_viewholder, parent, false);
        return new MyViewholder(v);//returns a new object of static inner class to the 'OnBindViewHolder method
    }


    @SuppressLint("CheckResult")
    @Override
    public void onBindViewHolder(@NonNull final MyViewholder holder, final int position) {
        Glide.with(mContext)
                .load(images.get(holder.getAdapterPosition()))
                .into(holder.mImageView);

        try {
            holder.pic_desc_et.setText(images_descr.get(holder.getAdapterPosition()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.pic_desc_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mIactivityCallback.changedImageDescr(holder.pic_desc_et.getText().toString(), holder.getAdapterPosition());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        RxView.clicks(holder.mImageView).subscribe(new Consumer<Unit>() {
            @Override
            public void accept(Unit unit) {
                String filePath = ImageTools.saveBitmapToJpeg(mContext, images.get(holder.getAdapterPosition()));
                Intent i = new Intent(mContext, FullScreenPhotoActivity.class);
                i.putExtra("image", filePath);
                mContext.startActivity(i);
            }
        });

        RxView.clicks(holder.deleteimg_btn).subscribe(new Consumer<Unit>() {
            @Override
            public void accept(Unit unit) {
                AlertDialog.Builder deleteConfirmation = new AlertDialog.Builder(mContext);
                deleteConfirmation.setMessage(R.string.delete_image_confirm_message);
                deleteConfirmation.setPositiveButton(R.string.delete_btn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mIactivityCallback.deletedImage(holder.getAdapterPosition());
                    }
                });
                deleteConfirmation.setNegativeButton(R.string.no, null);
                deleteConfirmation.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return images == null ? 0 : images.size();
    }


    static class MyViewholder extends RecyclerView.ViewHolder {
        ImageView mImageView;
        EditText pic_desc_et;
        ImageView deleteimg_btn;

        MyViewholder(View v) {
            super(v);
            mImageView = v.findViewById(R.id.picture);
            pic_desc_et = v.findViewById(R.id.pic_desc_et);
            deleteimg_btn = v.findViewById(R.id.deleteimg_btn);

        }
    }

    public interface IactivityCallback {
        void changedImageDescr(String desc, int position);

        void deletedImage(int position);
    }
}
