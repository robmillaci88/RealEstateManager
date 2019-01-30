package com.example.robmillaci.realestatemanager.custom_objects;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatEditText;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import com.example.robmillaci.realestatemanager.R;

/**
 * Custom edit text with a custom background used in {@link com.example.robmillaci.realestatemanager.activities.add_listing_activity.AddListingView}
 */
public class SquareEditText extends AppCompatEditText {
    public SquareEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setProperties();
        setparams();
    }



    private void setProperties() {
        this.setCompoundDrawablePadding(10);
        this.setEms(10);
        this.setPadding(10, 0, 0, 0);
        this.setInputType(InputType.TYPE_CLASS_TEXT);
        this.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        this.setMaxLines(1);
        this.setTextSize(24);
        this.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
    }


    private void setparams() {
        ViewGroup.LayoutParams params = this.getLayoutParams();
        if (params != null) {
            params.width = 0;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        } else {
            params = new ViewGroup.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        this.setLayoutParams(params);

    }


    @Override
    public void setBackgroundDrawable(Drawable background) {
        super.setBackgroundDrawable(getResources().getDrawable(R.drawable.green_outline_button));
    }

}