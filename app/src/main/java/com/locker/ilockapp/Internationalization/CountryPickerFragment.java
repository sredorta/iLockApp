package com.locker.ilockapp.Internationalization;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.locker.ilockapp.R;
import com.locker.ilockapp.abstracts.DialogAbstract;
import com.locker.ilockapp.toolbox.Logs;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;



/**
 * Created by sredorta on 2/22/2017.
 */
public class CountryPickerFragment  extends DialogAbstract {

    public static final String FRAGMENT_INPUT_PARAM_CURRENT_PHONE_COUNTRY = "user.current.phone.country";    //Locale
    public static final String FRAGMENT_OUTPUT_PARAM_SELECTED_PHONE_COUNTRY = "user.selected.phone.country"; //Locale

    private CountryListAdapter mAdapter;
    private RecyclerView mCountryRecycleView;
    private List<Locale> mLocales = new ArrayList<>();
    private Locale mCurrentLocale;

    // Constructor
    public static CountryPickerFragment newInstance() {
        return new CountryPickerFragment();
    }

    // Constructor with input arguments
    public static CountryPickerFragment newInstance(Bundle data) {
        CountryPickerFragment fragment = CountryPickerFragment.newInstance();
        fragment.setArguments(data);
        return fragment;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.internationalization_country_list_display,null);
        //Get input Locale
        mCurrentLocale = (Locale) getInputParam(FRAGMENT_INPUT_PARAM_CURRENT_PHONE_COUNTRY);

        //Set the current Country selected
        final TextView mCurrentCountryTextView = (TextView) v.findViewById(R.id.internationalization_country_display_country_name);
        mCurrentCountryTextView.setText(mCurrentLocale.getDisplayCountry());

        final ImageView mCurrentCountryImageView = (ImageView) v.findViewById(R.id.internationalization_country_display_country_flag);
        mCurrentCountryImageView.setImageBitmap(Internationalization.getCountryFlagBitmapFromAsset(getContext(),mCurrentLocale));

        final LinearLayout ll = (LinearLayout) v.findViewById(R.id.internationalization_view_country_display);
        ll.setBackgroundColor(getResources().getColor(R.color.md_green_50, null));

        //Update the RecycleView
        mCountryRecycleView = (RecyclerView) v.findViewById(R.id.internationalization_country_recycleview);
        mCountryRecycleView.setLayoutManager(new LinearLayoutManager(mActivity));
        updateUI();
        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .create();

    }

    //Updates the recycleview
    private void updateUI() {
        mLocales = Internationalization.getSortedAvailableLocales(getContext(),Locale.FRANCE);
        mAdapter = new CountryListAdapter(mLocales);
        mCountryRecycleView.setAdapter(mAdapter);
    }

    private class CountryListHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Locale mLocale;
        private TextView mCountryTextView;
        private ImageView mCountryFlagImageView;


        private CountryListHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mCountryTextView = (TextView) itemView.findViewById(R.id.internationalization_country_display_country_name);
            mCountryFlagImageView = (ImageView) itemView.findViewById(R.id.internationalization_country_display_country_flag);
        }


        @Override
        public void onClick(View view) {
            //We need to update the user with the account data that has been selected
            putOutputParam(CountryPickerFragment.FRAGMENT_OUTPUT_PARAM_SELECTED_PHONE_COUNTRY, mLocale);
            sendResult(Activity.RESULT_OK);
        }


        public void bindAccount(Locale locale, CountryListHolder holder ) {
            mLocale = locale;
            mCountryTextView.setText(locale.getDisplayCountry());
            mCountryFlagImageView.setImageBitmap(Internationalization.getCountryFlagBitmapFromAsset(getContext(),locale));
        }
    }

    private class CountryListAdapter extends RecyclerView.Adapter<CountryListHolder> {

        public CountryListAdapter(List<Locale> locales) {
            mLocales = locales;
        }

        @Override
        public CountryListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(mActivity);
            View view = layoutInflater.inflate(R.layout.internationalization_country_display, parent,false);
            return new CountryListHolder(view);
        }

        @Override
        public void onBindViewHolder(CountryListHolder holder, int position) {
            Locale locale = mLocales.get(position);
            holder.bindAccount(locale,holder);
        }

        @Override
        public int getItemCount() {
            return mLocales.size();
        }

    }


}
