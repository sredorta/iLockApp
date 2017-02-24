package com.locker.ilockapp.Internationalization;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by sredorta on 2/23/2017.
 */
public class LanguagePickerFragment extends DialogAbstract {
/*
    public static final String FRAGMENT_INPUT_PARAM_CURRENT_LANGUAGE = "user.current.language";    //Locale
    public static final String FRAGMENT_OUTPUT_PARAM_SELECTED_LANGUAGE = "user.selected.language"; //Locale

    private LanguageListAdapter mAdapter;
    private RecyclerView mLanguageRecycleView;
    private List<Locale> mLocales = new ArrayList<>();
    private Locale mCurrentLanguageLocale;

    // Constructor
    public static LanguagePickerFragment newInstance() {
        return new LanguagePickerFragment();
    }

    // Constructor with input arguments
    public static LanguagePickerFragment newInstance(Bundle data) {
        LanguagePickerFragment fragment = LanguagePickerFragment.newInstance();
        fragment.setArguments(data);
        return fragment;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.internationalization_country_list_display,null);
        //Get input Locale
        mCurrentLanguageLocale = (Locale) getInputParam(FRAGMENT_INPUT_PARAM_CURRENT_LANGUAGE);
        if (mCurrentLanguageLocale == null)
            mCurrentLanguageLocale = Locale.getDefault();

        //Set the current Country selected
        final TextView mCurrentCountryTextView = (TextView) v.findViewById(R.id.internationalization_country_display_country_name);
        mCurrentCountryTextView.setText(mCurrentLanguageLocale.getDisplayLanguage());

        final ImageView mCurrentLanguageImageView = (ImageView) v.findViewById(R.id.internationalization_country_display_country_flag);
        mCurrentLanguageImageView.setImageBitmap(Internationalization.getCountryFlagBitmapFromAsset(getContext(),mCurrentLanguageLocale));

        final LinearLayout ll = (LinearLayout) v.findViewById(R.id.internationalization_view_country_display);
        ll.setBackgroundColor(getResources().getColor(R.color.md_green_50, null));

        //Update the RecycleView
        mLanguageRecycleView = (RecyclerView) v.findViewById(R.id.internationalization_country_recycleview);
        mLanguageRecycleView.setLayoutManager(new LinearLayoutManager(mActivity));
        updateUI();

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .create();

    }

    //Updates the recycleview
    private void updateUI() {
        mLocales = Internationalization.getSupportedLanguageLocales(getContext(), Locale.FRANCE);
        mAdapter = new LanguageListAdapter(mLocales);
        mLanguageRecycleView.setAdapter(mAdapter);
    }

    private class LanguageListHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Locale mLocale;
        private TextView mLanguageTextView;
        private ImageView mLanguageFlagImageView;


        private LanguageListHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mLanguageTextView = (TextView) itemView.findViewById(R.id.internationalization_country_display_country_name);
            mLanguageFlagImageView = (ImageView) itemView.findViewById(R.id.internationalization_country_display_country_flag);
        }


        @Override
        public void onClick(View view) {
            //We need to update the user with the account data that has been selected
            putOutputParam(LanguagePickerFragment.FRAGMENT_OUTPUT_PARAM_SELECTED_LANGUAGE, mLocale);
            sendResult(Activity.RESULT_OK);
        }


        public void bindItem(Locale locale, LanguageListHolder holder ) {
            mLocale = locale;
            mLanguageTextView.setText(locale.getDisplayLanguage());
            mLanguageFlagImageView.setImageBitmap(Internationalization.getLanguageFlagBitmapFromAsset(getContext(),locale));
        }
    }

    private class LanguageListAdapter extends RecyclerView.Adapter<LanguageListHolder> {

        public LanguageListAdapter(List<Locale> locales) {
            mLocales = locales;
        }

        @Override
        public LanguageListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(mActivity);
            View view = layoutInflater.inflate(R.layout.internationalization_country_display, parent,false);
            return new LanguageListHolder(view);
        }

        @Override
        public void onBindViewHolder(LanguageListHolder holder, int position) {
            Locale locale = mLocales.get(position);
            holder.bindItem(locale,holder);
        }

        @Override
        public int getItemCount() {
            return mLocales.size();
        }

    }
    */
}
