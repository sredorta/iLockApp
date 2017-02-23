package com.locker.ilockapp.authentication.profile.create;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
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
import com.locker.ilockapp.R;
import com.locker.ilockapp.abstracts.FragmentAbstract;
import com.locker.ilockapp.Internationalization.Internationalization;
import com.locker.ilockapp.authentication.AccountListFragment;
import com.locker.ilockapp.toolbox.Logs;

import java.util.Locale;

/**
 * Created by sredorta on 2/21/2017.
 */
public class ProfileCreatePhoneFragment extends FragmentAbstract {
    private View mView;
    private static int REQUEST_COUNTRY = 0;
    public static final String FRAGMENT_INPUT_PARAM_USER_PHONE_NUMBER = "user.phone_number.in";
    public static final String FRAGMENT_OUTPUT_PARAM_USER_PHONE_NUMBER = "user.phone_number.out";

    private String mPhoneNumber;                //Contains input phone number
    private String mFinalPhoneNumber = new String();

    private boolean isPhoneNumberCorrect;       //Contains if typed in number is correct
    private Locale mLocale;                     //Current country Locale of the phone number
    private CountryPickerFragment dialog;       //Dialog to choose another country

    // Constructor
    public static ProfileCreatePhoneFragment newInstance() {
        return new ProfileCreatePhoneFragment();
    }
    // Constructor with input arguments
    public static ProfileCreatePhoneFragment newInstance(Bundle data) {
        ProfileCreatePhoneFragment fragment = ProfileCreatePhoneFragment.newInstance();
        fragment.setArguments(data);
        return fragment;
    }

    private Locale getLocaleFromNumber(String number) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        String country = "";
        try {
            Phonenumber.PhoneNumber inputNumber = phoneUtil.parse(number, null);
            country = phoneUtil.getRegionCodeForNumber(inputNumber);
        } catch (NumberParseException e) {
            Logs.i("Caught exception : " + e);

        }
        if (country.equals("")) return null;
        return new Locale("",country);
    }

    private String getNumberWithoutCountry(String number) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        String country = "";
        try {
            Phonenumber.PhoneNumber inputNumber = phoneUtil.parse(number, null);
            return String.valueOf(inputNumber.getNationalNumber());
        } catch (NumberParseException e) {
            Logs.i("Caught exception : " + e);
            return "";
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLocale = getLocaleFromNumber((String) getInputParam(ProfileCreatePhoneFragment.FRAGMENT_INPUT_PARAM_USER_PHONE_NUMBER));
        mPhoneNumber = getNumberWithoutCountry((String) getInputParam(ProfileCreatePhoneFragment.FRAGMENT_INPUT_PARAM_USER_PHONE_NUMBER));
        //In case we could not parse the phone or it was empty
        if (mLocale == null) {
            mLocale = Locale.getDefault();
            mPhoneNumber = "";
        }
        //We start with wrong number
        isPhoneNumberCorrect = false;
    }

    private void updateCurrentCountry() {
        final TextView mCountryTextView = (TextView) mView.findViewById(R.id.internationalization_country_display_country_name);
        mCountryTextView.setText(mLocale.getDisplayCountry());

        final ImageView mCountryFlag = (ImageView) mView.findViewById(R.id.internationalization_country_display_country_flag);
        mCountryFlag.setImageBitmap(Internationalization.getCountryFlagBitmapFromAsset(getContext(),mLocale));

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.profile_create_phone_fragment, container, false);
        mView =v;
        //Update the current country
        updateCurrentCountry();

        //Show CountryPicker Dialog if we click
        final LinearLayout mLinearCountry = (LinearLayout) v.findViewById(R.id.profile_create_phone_country_display);
        mLinearCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logs.i("You clicked !");
                Bundle bundle = new Bundle();
                bundle.putSerializable(CountryPickerFragment.FRAGMENT_INPUT_PARAM_CURRENT_PHONE_COUNTRY, mLocale);
                FragmentManager fm = getFragmentManager();
                dialog = CountryPickerFragment.newInstance(bundle);
                dialog.setTargetFragment(ProfileCreatePhoneFragment.this, REQUEST_COUNTRY);
                dialog.show(fm,"DIALOG");
            }
        });


        final EditText mNumberEditText = (EditText) v.findViewById(R.id.profile_create_phone_editText_number);
        Logs.i("Setting number to: " + mPhoneNumber);
        mNumberEditText.setText(mPhoneNumber);

        final Button nextButton = (Button) v.findViewById(R.id.profile_create_phone_button);

        //We add a listener to verify that we have correct number
        mNumberEditText.addTextChangedListener(new PhoneNumberFormattingTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                //Return if no characters are here
                if (s.toString().length()== 0) return;
                if ((s.toString().matches("[0-9]+") && s.toString().length() > 0)) {
                    mNumberEditText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                    mNumberEditText.setTypeface(mNumberEditText.getTypeface(), Typeface.NORMAL);
                    //We only start checking if number is valid once length is larger than 2 to avoid crash
                    if (s.toString().length()>2) {
                        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
                        Phonenumber.PhoneNumber inputNumber;
                        try {
                            inputNumber = phoneUtil.parse(s.toString(), mLocale.getCountry());
                        } catch (NumberParseException e) {
                            inputNumber = null;
                            Logs.i("Caught exception :" + e);
                        }
                        isPhoneNumberCorrect = phoneUtil.isValidNumber(inputNumber);
                        if (isPhoneNumberCorrect) {
                            mNumberEditText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                            mNumberEditText.setTypeface(mNumberEditText.getTypeface(), Typeface.BOLD);
                            mFinalPhoneNumber = phoneUtil.format(inputNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
                            hideInputKeyBoard();
                            Logs.i("Is Valid = : " + isPhoneNumberCorrect);
                        }
                    }

                } else {
                    //The input character was not a number so we remove it
                    s.delete(s.length()-1,s.length());
                }

            }

        });


        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Hide keyboard if exists
                hideInputKeyBoard();
                if (!isPhoneNumberCorrect) {
                    //mNumberEditText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                    Snackbar snackbar = Snackbar.make(mView, "Invalid telephone format !", Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else {
                            //putOutputParam(FRAGMENT_OUTPUT_PARAM_USER_PHONE_COUNTRY, mLocale);
                            putOutputParam(FRAGMENT_OUTPUT_PARAM_USER_PHONE_NUMBER, mFinalPhoneNumber);
                            sendResult(Activity.RESULT_OK);
                            // Remove our fragment
                            removeFragment(ProfileCreatePhoneFragment.this,true);  //This comes from abstract
                }

            }
        });
        return v;
    }

/*
    @Override
    public void onBackPressed() {
        //if (hiddenPanel.getVisibility() == View.VISIBLE) slideUpDown(mView);
        // else {
        super.onBackPressed();
        //}
    }
*/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Logs.i("We are back in Profile Phone ss!");
        // Reload our fragment
        replaceFragment(ProfileCreatePhoneFragment.this, "test", true);

        if (resultCode == Activity.RESULT_OK && requestCode == ProfileCreatePhoneFragment.REQUEST_COUNTRY) {
            mLocale = (Locale) data.getSerializableExtra(CountryPickerFragment.FRAGMENT_OUTPUT_PARAM_SELECTED_PHONE_COUNTRY);
            dialog.dismiss();
            //dialog.dismiss();
            updateCurrentCountry();

        }

    }




}
