package com.locker.ilockapp.authentication.profile.create;

import android.app.Activity;
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
 * Created by sredorta on 2/23/2017.
 */
public class ProfileCreateEmailFragment extends FragmentAbstract {
    private View mView;
    private static int REQUEST_DEFINE_NAMES = 1;
    public static final String FRAGMENT_INPUT_PARAM_USER_EMAIL = "user.email.in";    //String
    public static final String FRAGMENT_OUTPUT_PARAM_USER_EMAIL = "user.email.out";    //String

    private String mEmail;

    // Constructor
    public static ProfileCreateEmailFragment newInstance() {
        return new ProfileCreateEmailFragment();
    }
    // Constructor with input arguments
    public static ProfileCreateEmailFragment newInstance(Bundle data) {
        ProfileCreateEmailFragment fragment = ProfileCreateEmailFragment.newInstance();
        fragment.setArguments(data);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEmail = (String) getInputParam(ProfileCreateEmailFragment.FRAGMENT_INPUT_PARAM_USER_EMAIL);
        if (mEmail == null) mEmail = "";

        //mLastName = (String) getInputParam(ProfileCreateNamesFragment.FRAGMENT_INPUT_PARAM_USER_LAST_NAME);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.profile_create_email_fragment, container, false);
        mView =v;
        final EditText emailEditText = (EditText) v.findViewById(R.id.profile_create_email_editText_email);
        emailEditText.setText(mEmail);
        Button nextButton = (Button) v.findViewById(R.id.profile_create_email_button);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AccountGeneral accountGeneral = new AccountGeneral(getContext());
                //Hide keyboard if exists
                hideInputKeyBoard();
                if (accountGeneral.checkEmailInput(emailEditText,mView)) {
                    putOutputParam(FRAGMENT_OUTPUT_PARAM_USER_EMAIL, emailEditText.getText().toString());
                    sendResult(Activity.RESULT_OK);
                    removeFragment(ProfileCreateEmailFragment.this,true);
                }
            }
        });
        return v;
    }



}
