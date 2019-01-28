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

/**
 * Helper class for converting bitmaps to byte[]s, saving bitmaps as jpegs, converting image URLS to byte[]s and converting byte[]s to bitmaps
 */
public class ImageTools {

    /**
     * @param bitmap the bitmap to be converted to a string for storage
     * @return converting bitmap and return a string
     */
    private static byte[] BitMapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, baos);
        return baos.toByteArray();
    }


    /**
     * Opens a connection to a passed URL, reads the data and then returns it as a byte[]
     *
     * @param url the url to open and download data
     * @return byte[] of image data
     */
    public static byte[] UrlToByteArray(String url) {
        try {
            URL imageUrl = new URL(url);
            URLConnection ucon = imageUrl.openConnection();

            InputStream is = ucon.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);

            ByteArrayBuffer baf = new ByteArrayBuffer(500);
            int current;
            while ((current = bis.read()) != -1) {
                baf.append((byte) current);
            }

            return baf.toByteArray();
        } catch (Exception e) {
            Log.d("ImageTools", "Error: " + e.toString());
        }
        return null;
    }


    /**
     * Save a bitmap to a Jpeg and returns the path
     *
     * @param mContext context
     * @param bitmap   the bitmap to be converted
     * @return the path of the converted image
     */
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


    /**
     * Recieves a List<byte[]> of image data, returns an ArrayList<Bitmap> containing the converted images
     *
     * @param images the list of byte[]s containing the image data
     * @return ArrayList<Bitmaps>
     */
    public static ArrayList<Bitmap> byteArrayToBitmaps(List<byte[]> images) {
        ArrayList<Bitmap> returnedBitmaps = new ArrayList<>();

        for (byte[] b : images) {
            returnedBitmaps.add(BitmapFactory.decodeByteArray(b, 0, b.length));
        }

        return returnedBitmaps;
    }


    public static ArrayList<byte[]> BitmapsToByteArray(ArrayList<Bitmap> images) {
        final ArrayList<byte[]> imagesArray = new ArrayList<>();

        for (Bitmap b : images) {
            imagesArray.add(ImageTools.BitMapToByteArray(b));
        }
        return imagesArray;
    }

}
