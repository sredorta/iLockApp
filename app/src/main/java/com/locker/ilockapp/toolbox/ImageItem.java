package com.locker.ilockapp.toolbox;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Base64;
import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sredorta on 2/20/2017.
 */
public class ImageItem {
    private String mExtension;                          //Contains the extension of the file transferred

    @SerializedName("stream")
    private String mStream;

    public String getStream() {
        return mStream;
    }


    public void setStream(String str) {
        mStream = str;
    }

    //Generates a base64 encoded string from an image bitmap
    public void setStream(Bitmap bitmap) {
        String result = null;
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOS);
        result =  Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
        result = "data:image/" + Bitmap.CompressFormat.JPEG +";base64," + result;
        mStream = result;
    }

    public String getStreamString() {
        return mStream;
    }

    public Bitmap getBitmap() {
        byte[] bitmapBytes = getDecodedStream();
        if (bitmapBytes != null) {

          final Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapBytes,0, bitmapBytes.length);
          return bitmap;
        } else
            return null;
    }


    //Returns the base64 decoded stream
    public byte[] getDecodedStream() {
        if (mStream!= null) {
            String image = this.parseBase64();
            Logs.i("This is the image we try to decode :" + image);
            return Base64.decode(this.parseBase64(), Base64.DEFAULT);
        } else
            return null;
    }

    // Parses the mStream and returns the stream without header
    //     it extracts all info from the header
    private String parseBase64() {
        String streamHeaderFree;
        String streamHeaderOnly;
        String streamExtension;
        String pattern;
        Pattern r;
        Matcher m;

        //Remove the header first
        pattern = "^.*base64,";
        r = Pattern.compile(pattern);
        m = r.matcher(mStream);
        streamHeaderFree = m.replaceAll("");
//        Logs.i("Stream without header : " + streamHeaderFree);
        //Get the header
        pattern = ";base64,.*$";
        r = Pattern.compile(pattern);
        m = r.matcher(mStream);
        streamHeaderOnly = m.replaceAll("");
//        Logs.i("Header : " + streamHeaderOnly);

        //Get the file extension and save it into the object
        pattern = "^.*/";
        r = Pattern.compile(pattern);
        m = r.matcher(streamHeaderOnly);
        streamExtension = m.replaceAll("");
//        Logs.i("Extension : " + streamExtension);

        this.mExtension = streamExtension;
        //Returns now the stream without header
        return streamHeaderFree;
    }













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
        if (dimension > 300) dimension = 300;
        return dimension;
    }




    //Converts image into rounded bitmap
    public static Bitmap getRoundedBitmap(Bitmap src) {
        RoundedBitmapDrawable dr =
                RoundedBitmapDrawableFactory.create(null, src);
        dr.setCornerRadius(Math.min(src.getWidth(), src.getHeight()) / 2.0f);
        return dr.getBitmap();
    }





    private static void saveFile(Uri sourceUri, File destination,Context context) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            ContentResolver content = context.getContentResolver();
            inputStream = content.openInputStream(sourceUri);

            outputStream = new FileOutputStream( destination); // filename.png, .mp3, .mp4 ...
            if(outputStream != null){
                Logs.i( "Output Stream Opened successfully");
            }

            byte[] buffer = new byte[1000];
            int bytesRead = 0;
            while ( ( bytesRead = inputStream.read( buffer, 0, buffer.length ) ) >= 0 ) {
                outputStream.write( buffer, 0, buffer.length );
            }
        } catch (Exception e) {
            Logs.i("Caught exception : " + e);
        }
    }

    public static Bitmap rotateImage(Bitmap bitmap, Uri contentURI,Context context, Activity mActivity) {
        File externalFilesDir = mActivity.getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File myFile = new File(externalFilesDir, "tmp.jpg");
        ImageItem.saveFile(contentURI, myFile,context);
        Logs.i("myFile :" + myFile.getAbsolutePath());
        Bitmap result = rotateImage(bitmap,myFile.getAbsolutePath());
        myFile.delete();
        return result;
    }


    public static Bitmap rotateImage(Bitmap bitmap, String filePath) {
        Bitmap resultBitmap = bitmap;

        try {
            ExifInterface exifInterface = new ExifInterface(filePath);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            Logs.i("Orientation is: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                matrix.postRotate(90);
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                matrix.postRotate(180);
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                matrix.postRotate(270);
            } else {
                matrix.postRotate(0);
            }
            // Rotate the bitmap
            resultBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }
        catch (Exception exception) {
            Logs.i("Could not rotate the image");
        }
        return resultBitmap;
    }





}
