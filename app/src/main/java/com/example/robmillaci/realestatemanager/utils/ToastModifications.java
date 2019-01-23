package com.example.robmillaci.realestatemanager.utils;

import android.content.Context;
import android.support.design.widget.BaseTransientBottomBar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.robmillaci.realestatemanager.R;

public class ToastModifications {
    public static void createToast(Context context,String message, int duration){
        Toast toast = new Toast(context);
        toast.setDuration(duration);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressWarnings("ConstantConditions") View view = inflater.inflate(R.layout.toast_layout, null);
        TextView tv = view.findViewById(R.id.toast_message_view);
        toast.setView(view);
        toast.show();
        tv.setText(message);
    }
}
