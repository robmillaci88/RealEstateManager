package com.example.robmillaci.realestatemanager.custom_objects;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.widget.MultiAutoCompleteTextView;

/**
 * Custom tokenizer class for POI autocomplete edit text.
 * Overrides the default class which appends a space to the end of the chosen string.
 * This custom class does not return the appended space.
 */
public class MyTokenizer extends MultiAutoCompleteTextView.CommaTokenizer {

    @Override
    public CharSequence terminateToken(CharSequence text) {
        int i = text.length();

        while (i > 0 && text.charAt(i - 1) == ' ') {
            i--;
        }

        if (i > 0 && text.charAt(i - 1) == ',') {
            return text;
        } else {
            if (text instanceof Spanned) {
                SpannableString sp = new SpannableString(text + ", ");
                TextUtils.copySpansFrom((Spanned) text, 0, text.length(),
                        Object.class, sp, 0);
                return sp;
            } else {
                return text + ",";
            }
        }
    }
}

