package com.locker.ilockapp.Internationalization;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.locker.ilockapp.toolbox.Logs;

import java.io.IOException;
import java.io.InputStream;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Created by sredorta on 2/22/2017.
 */
public class Internationalization {
    private static final String FOLDER_COUNTRY_FLAGS = "country_flags";
    private AssetManager mAssets;
    private List<Bitmap> countryFlagsBitmap;

    public Internationalization(Context context) {
        mAssets = context.getAssets();
        //loadCountryFlags();
    }

    public static Bitmap getCountryFlagBitmapFromAsset(Context context, Locale country) {
        AssetManager assetManager = context.getAssets();
        String filePath;

        //Our assets are written with country names in english so we need to get the country name in english
        filePath = FOLDER_COUNTRY_FLAGS + "/" + country.getDisplayCountry(Locale.US).toString() + ".png";
        filePath = filePath.replace(" ","_");

        InputStream istr;
        Bitmap bitmap = null;
        try {
            istr = assetManager.open(filePath);
            bitmap = BitmapFactory.decodeStream(istr);
        } catch (IOException e) {
            // handle exception
        }
        if (bitmap == null) {
            filePath = FOLDER_COUNTRY_FLAGS + "/Default.png";
            try {
                istr = assetManager.open(filePath);
                bitmap = BitmapFactory.decodeStream(istr);
            } catch (IOException e) {
                // handle exception
            }
        }

        return bitmap;
    }

    public static List<Locale> getSortedAvailableLocales(Context context, Locale country) {
        List<Locale> mLocales = new ArrayList<>();
        for (String countryStr : Locale.getISOCountries()) {
            mLocales.add(new Locale("",countryStr));
        }
        final Collator collator = Collator.getInstance(country);
        Collections.sort(mLocales, new Comparator<Locale>() {
            @Override
            public int compare(Locale locale, Locale t1) {
                return collator.compare(locale.getDisplayCountry(),t1.getDisplayCountry());
            }
        });
        return mLocales;
    }

    public static List<Locale> getSupportedLanguageLocales(Context context) {
        List<Locale> mLocales = new ArrayList<>();
        mLocales.add(Locale.FRENCH);
        mLocales.add(Locale.ENGLISH);
        /*
        for (String countryStr : Locale.getISOCountries()) {
            mLocales.add(new Locale("",countryStr));
        }
        final Collator collator = Collator.getInstance(country);
        Collections.sort(mLocales, new Comparator<Locale>() {
            @Override
            public int compare(Locale locale, Locale t1) {
                return collator.compare(locale.getDisplayCountry(),t1.getDisplayCountry());
            }
        });
        */
        return mLocales;
    }


}
