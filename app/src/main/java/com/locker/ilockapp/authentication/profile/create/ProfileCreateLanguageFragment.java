package com.locker.ilockapp.authentication.profile.create;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.locker.ilockapp.Internationalization.CountryPickerFragment;
import com.locker.ilockapp.Internationalization.Internationalization;
import com.locker.ilockapp.Internationalization.LanguagePickerFragment;
import com.locker.ilockapp.R;
import com.locker.ilockapp.abstracts.FragmentAbstract;
import com.locker.ilockapp.toolbox.Logs;

import java.util.Locale;

/**
 * Created by sredorta on 2/23/2017.
 */
public class ProfileCreateLanguageFragment extends FragmentAbstract {
    private View mView;
    private static int REQUEST_LANGUAGE = 0;
    public static final String FRAGMENT_INPUT_PARAM_USER_LANGUAGE = "user.language.in";
    public static final String FRAGMENT_OUTPUT_PARAM_USER_LANGUAGE = "user.language.out";

    private String mLanguage;                //Contains input phone number
    private String mFinalLanguage;

    private Locale mLocale;                     //Current country Locale of the phone number
    private LanguagePickerFragment dialog;       //Dialog to choose another country

    // Constructor
    public static ProfileCreateLanguageFragment newInstance() {
        return new ProfileCreateLanguageFragment();
    }
    // Constructor with input arguments
    public static ProfileCreateLanguageFragment newInstance(Bundle data) {
        ProfileCreateLanguageFragment fragment = ProfileCreateLanguageFragment.newInstance();
        fragment.setArguments(data);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLocale = (Locale) getInputParam(ProfileCreateLanguageFragment.FRAGMENT_INPUT_PARAM_USER_LANGUAGE);
        //In case we could not parse the phone or it was empty
        if (mLocale == null) {
            mLocale = Locale.getDefault();
            Logs.i("Language set to: " + mLocale.getDisplayLanguage());
        }
    }

    private void updateCurrentLanguage() {
        final TextView mCountryTextView = (TextView) mView.findViewById(R.id.internationalization_country_display_country_name);
        mCountryTextView.setText(mLocale.getDisplayLanguage());

        final ImageView mCountryFlag = (ImageView) mView.findViewById(R.id.internationalization_country_display_country_flag);
        mCountryFlag.setImageBitmap(Internationalization.getCountryFlagBitmapFromAsset(getContext(),mLocale));

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.profile_create_language_fragment, container, false);
        mView = v;
        //Update the current country
        updateCurrentLanguage();

        //Show CountryPicker Dialog if we click
        final LinearLayout mLinearCountry = (LinearLayout) v.findViewById(R.id.profile_create_language_display);
        mLinearCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logs.i("You clicked !");
                Bundle bundle = new Bundle();
                bundle.putSerializable(CountryPickerFragment.FRAGMENT_INPUT_PARAM_CURRENT_PHONE_COUNTRY, mLocale);
                FragmentManager fm = getFragmentManager();
                dialog = LanguagePickerFragment.newInstance(bundle);
                dialog.setTargetFragment(ProfileCreateLanguageFragment.this, REQUEST_LANGUAGE);
                dialog.show(fm, "DIALOG");
            }
        });


        final Button nextButton = (Button) v.findViewById(R.id.profile_create_language_button);


        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //putOutputParam(FRAGMENT_OUTPUT_PARAM_USER_PHONE_COUNTRY, mLocale);
                putOutputParam(FRAGMENT_OUTPUT_PARAM_USER_LANGUAGE, mLocale);
                sendResult(Activity.RESULT_OK);
                // Remove our fragment
                removeFragment(ProfileCreateLanguageFragment.this, true);  //This comes from abstract
            }
        });
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Logs.i("We are back in Profile Phone ss!");
        // Reload our fragment
        replaceFragment(ProfileCreateLanguageFragment.this, "test", true);

        if (resultCode == Activity.RESULT_OK && requestCode == ProfileCreateLanguageFragment.REQUEST_LANGUAGE) {
            mLocale = (Locale) data.getSerializableExtra(LanguagePickerFragment.FRAGMENT_OUTPUT_PARAM_SELECTED_LANGUAGE);
            dialog.dismiss();
            //dialog.dismiss();
            updateCurrentLanguage();

        }

    }




}

