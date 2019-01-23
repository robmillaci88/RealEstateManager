package com.example.robmillaci.realestatemanager.utils.image_tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.apache.http.util.ByteArrayBuffer;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class ImageTools {


    /**
     * @param bitmap the bitmap to be converted to a string for storage
     * @return converting bitmap and return a string
     */
    public static byte[] BitMapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, baos);
        return baos.toByteArray();
    }

    public static byte[] UrlToByteArray(String url) {
        try {
            URL imageUrl = new URL(url);
            URLConnection ucon = imageUrl.openConnection();

            InputStream is = ucon.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);

            ByteArrayBuffer baf = new ByteArrayBuffer(500);
            int current = 0;
            while ((current = bis.read()) != -1) {
                baf.append((byte) current);
            }

            return baf.toByteArray();
        } catch (Exception e) {
            Log.d("ImageTools", "Error: " + e.toString());
        }
        return null;
    }


    public static String saveBitmapToJpeg(Context mContext, Bitmap bitmap) {
        try {
            //Write file
            String filename = "bitmap.png";
            FileOutputStream stream = mContext.openFileOutput(filename, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

            //Cleanup
            stream.close();

            return filename;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static ArrayList<Bitmap> byteArrayToBitmaps(List<byte[]> images) {
        ArrayList<Bitmap> returnedBitmaps = new ArrayList<>();

        for (byte[] b : images) {
            returnedBitmaps.add(BitmapFactory.decodeByteArray(b, 0, b.length));
        }

        return returnedBitmaps;
    }

}
