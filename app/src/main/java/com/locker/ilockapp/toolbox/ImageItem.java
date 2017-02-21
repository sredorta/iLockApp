package com.locker.ilockapp.toolbox;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;

import java.io.FileDescriptor;
import java.io.IOException;

/**
 * Created by sredorta on 2/20/2017.
 */
public class ImageItem {

    //From an uri we return a bitmap
    public static Bitmap getBitmapFromUri(Activity mActivity, Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                mActivity.getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }


    public static int getSquareCropDimensionForBitmap(Bitmap bitmap) {
        int dimension = Math.min(bitmap.getWidth(), bitmap.getHeight());
        if (dimension > 500) dimension = 500;
        return dimension;
    }
    //Converts image into rounded bitmap
    public static Bitmap getRoundedBitmap(Bitmap src) {
        RoundedBitmapDrawable dr =
                RoundedBitmapDrawableFactory.create(null, src);
        dr.setCornerRadius(Math.min(src.getWidth(), src.getHeight()) / 2.0f);
        return dr.getBitmap();
    }

}
