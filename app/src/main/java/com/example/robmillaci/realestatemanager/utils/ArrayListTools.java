package com.example.robmillaci.realestatemanager.utils;

import android.graphics.Bitmap;

import com.example.robmillaci.realestatemanager.utils.image_tools.ImageTools;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ArrayListTools {

    public static ArrayList<byte[]> BitmapsToByteArray(ArrayList<Bitmap> images) {
        final ArrayList<byte[]> imagesArray = new ArrayList<>();

        for (Bitmap b : images) {
            imagesArray.add(ImageTools.BitMapToByteArray(b));
        }
        return imagesArray;
    }

}
