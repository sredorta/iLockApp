package com.locker.ilockapp.authentication.profile.create;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.locker.ilockapp.R;
import com.locker.ilockapp.abstracts.FragmentAbstract;
import com.locker.ilockapp.authentication.AccountGeneral;
import com.locker.ilockapp.toolbox.Logs;

/**
 * Created by sredorta on 2/21/2017.
 */
public class ProfileCreateNamesFragment extends FragmentAbstract {
    private View mView;
    private static int REQUEST_DEFINE_NAMES = 1;
    public static final String FRAGMENT_INPUT_PARAM_USER_FIRST_NAME = "user.first_name.in";    //String
    public static final String FRAGMENT_INPUT_PARAM_USER_LAST_NAME = "user.last_name.in";
    public static final String FRAGMENT_OUTPUT_PARAM_USER_FIRST_NAME = "user.first_name.out";    //String
    public static final String FRAGMENT_OUTPUT_PARAM_USER_LAST_NAME = "user.last_name.out";

    private String mFirstName = new String();
    private String mLastName = new String();

    // Constructor
    public static ProfileCreateNamesFragment newInstance() {
        return new ProfileCreateNamesFragment();
    }
    // Constructor with input arguments
    public static ProfileCreateNamesFragment newInstance(Bundle data) {
        ProfileCreateNamesFragment fragment = ProfileCreateNamesFragment.newInstance();
        fragment.setArguments(data);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirstName = (String) getInputParam(ProfileCreateNamesFragment.FRAGMENT_INPUT_PARAM_USER_FIRST_NAME);
        mLastName = (String) getInputParam(ProfileCreateNamesFragment.FRAGMENT_INPUT_PARAM_USER_LAST_NAME);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.profile_create_names_fragment, container, false);
        mView =v;
        Logs.i("firstName :" + mFirstName);
        final EditText firstNameEditText = (EditText) v.findViewById(R.id.profile_create_names_editText_firstName);
        final EditText lastNameEditText = (EditText) v.findViewById(R.id.profile_create_names_editText_lastName);
        Button nextButton = (Button) v.findViewById(R.id.profile_create_names_button);

        firstNameEditText.setText(mFirstName);
        lastNameEditText.setText(mLastName);


        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Hide keyboard if exists
                hideInputKeyBoard();
                mFirstName = firstNameEditText.getText().toString();
                mLastName = lastNameEditText.getText().toString();
                AccountGeneral ag = new AccountGeneral(getContext());
                if (ag.checkFirstNameInput(firstNameEditText,mView)) {
                    if (ag.checkLastNameInput(lastNameEditText,mView)) {
                        //We return results
                        putOutputParam(FRAGMENT_OUTPUT_PARAM_USER_FIRST_NAME, mFirstName);
                        putOutputParam(FRAGMENT_OUTPUT_PARAM_USER_LAST_NAME, mLastName);
                        sendResult(Activity.RESULT_OK);
                        // Remove our fragment
                        removeFragment(ProfileCreateNamesFragment.this,true);  //This comes from abstract
                    }
                }


            }
        });
        return v;
    }



    @Override
    public void onBackPressed() {
        //if (hiddenPanel.getVisibility() == View.VISIBLE) slideUpDown(mView);
        // else {
        super.onBackPressed();
        //}
    }

}
