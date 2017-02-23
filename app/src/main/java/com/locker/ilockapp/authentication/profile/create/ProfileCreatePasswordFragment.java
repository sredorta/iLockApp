package com.locker.ilockapp.authentication.profile.create;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.locker.ilockapp.R;
import com.locker.ilockapp.abstracts.FragmentAbstract;
import com.locker.ilockapp.authentication.AccountGeneral;
import com.locker.ilockapp.toolbox.Logs;

/**
 * Created by sredorta on 2/23/2017.
 */
public class ProfileCreatePasswordFragment extends FragmentAbstract {
    private View mView;
    public static final String FRAGMENT_OUTPUT_PARAM_USER_PASSWORD = "user.password.out";    //String
    AccountGeneral ag;

    // Constructor
    public static ProfileCreatePasswordFragment newInstance() {
        return new ProfileCreatePasswordFragment();
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
        ag = new AccountGeneral(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.profile_create_password_fragment, container, false);
        mView =v;

        final EditText passwordEditText = (EditText) v.findViewById(R.id.profile_create_password_editText);
        final ImageView passwordImageView = (ImageView) v.findViewById(R.id.profile_create_password_imageView_password);

        final EditText passwordShadowEditText = (EditText) v.findViewById(R.id.profile_create_password_editText_shadow);
        final ImageView passwordShadowImageView = (ImageView) v.findViewById(R.id.profile_create_password_imageView_password_shadow);

        Button nextButton = (Button) v.findViewById(R.id.profile_create_password_button);

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                passwordImageView.setVisibility(View.INVISIBLE);
                if (ag.checkPasswordInput(passwordEditText.getText().toString()))
                    passwordImageView.setVisibility(View.VISIBLE);
            }
        });

        passwordShadowEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                Logs.i("Text changed !");
                passwordShadowImageView.setVisibility(View.INVISIBLE);
                if (passwordEditText.getText().toString().equals(passwordShadowEditText.getText().toString()))
                    if (ag.checkPasswordInput(passwordEditText.getText().toString())) {
                        passwordShadowImageView.setVisibility(View.VISIBLE);
                        //Hide keyboard if exists
                        hideInputKeyBoard();
                    }

            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ag.checkPasswordInput(passwordEditText,mView,mActivity)) {
                    if (passwordEditText.getText().toString().equals(passwordShadowEditText.getText().toString())) {
                        putOutputParam(FRAGMENT_OUTPUT_PARAM_USER_PASSWORD, passwordEditText.getText().toString());
                        sendResult(Activity.RESULT_OK);
                        // Remove our fragment
                        removeFragment(ProfileCreatePasswordFragment.this,true);  //This comes from abstract
                    } else {
                        passwordShadowEditText.setText("");
                    }
                }
            }
        });
        return v;
    }

}
