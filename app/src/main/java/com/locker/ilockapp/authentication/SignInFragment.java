package com.locker.ilockapp.authentication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.locker.ilockapp.R;
import com.locker.ilockapp.abstracts.FragmentAbstract;
import com.locker.ilockapp.toolbox.Logs;

/**
 * Created by sredorta on 2/2/2017.
 */
public class SignInFragment extends FragmentAbstract {
    private AccountGeneral myAccountGeneral;
    private View mView;
    private User user;
//    private final int REQ_SIGNUP = 1;

    // Constructor
    public static SignInFragment newInstance() {
        return new SignInFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Get account details from Singleton either from intent or from account of the device
        myAccountGeneral = new AccountGeneral(getContext());
        user = new User();
        user.initEmpty(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_signin, container, false);
        final EditText password = (EditText) v.findViewById(R.id.fragment_signin_EditText_password);
        mView =v;

        //Re-enter credentials
        v.findViewById(R.id.fragment_signin_Button_connect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logs.i("Submitting credentials to account manager !", this.getClass());
                //hide input keyboard
                hideInputKeyBoard();
                if (checkFields()) {
                    setUserFieldsFromInputs();
                    password.setText("");
                    myAccountGeneral.submitCredentials(mActivity,mView,user);
                }
            }
        });
        //Create new user account
        v.findViewById(R.id.fragment_signin_Button_create).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logs.i("Starting new activity to create account !", this.getClass());
                Logs.i("Found IS_ADDING :", this.getClass());
                SignUpFragment fragment = SignUpFragment.newInstance();
//                fragment.setTargetFragment(SignInFragment.this, REQ_SIGNUP);
                //Now replace the AuthenticatorFragment with the SignInFragment
                replaceFragment(fragment,"test",true);  //This comes from abstract
            }
        });

        return v;
    }


    //Verify that the fields entered are correct
    private Boolean checkFields() {
        Boolean fieldsOk = true;
        final EditText password = (EditText) mView.findViewById(R.id.fragment_signin_EditText_password);
        final EditText email_or_phone = (EditText) mView.findViewById(R.id.fragment_signin_EditText_account);

        if (!myAccountGeneral.checkPasswordInput(password.getText().toString(),mView,mActivity)) {
            password.setText("");
            password.setHintTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
            fieldsOk = false;
        }
        if (!myAccountGeneral.checkEmailOrPhoneInput(email_or_phone.getText().toString())) {
            email_or_phone.setText("");
            email_or_phone.setHintTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
            Snackbar snackbar = Snackbar.make(mView, "Invalid phone or email", Snackbar.LENGTH_LONG);
            snackbar.show();
            fieldsOk = false;
        }

        return fieldsOk;

    }

    //Sets the user singleton with the inputs
    private void setUserFieldsFromInputs() {
        final EditText password           = (EditText) mView.findViewById(R.id.fragment_signin_EditText_password);
        final EditText email_or_phone     = (EditText) mView.findViewById(R.id.fragment_signin_EditText_account);

        if (myAccountGeneral.checkEmailInput(email_or_phone.getText().toString())) {
            user.setEmail(email_or_phone.getText().toString());
        } else
            user.setPhone(email_or_phone.getText().toString());

        user.setPassword(password.getText().toString());
    }

}

