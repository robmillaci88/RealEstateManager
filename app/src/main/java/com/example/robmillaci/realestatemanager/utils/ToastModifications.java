package com.example.robmillaci.realestatemanager.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.robmillaci.realestatemanager.R;

/**
 * Class responsible for creating and displaying a custom toast message
 */
public class ToastModifications {
    public static void createToast(Context context,String message, int duration){
        Toast toast = new Toast(context);
        toast.setDuration(duration);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") @SuppressWarnings("ConstantConditions") View view = inflater.inflate(R.layout.toast_layout, null);
        TextView tv = view.findViewById(R.id.toast_message_view);
        toast.setView(view);
        toast.show();
        tv.setText(message);
    }
}
