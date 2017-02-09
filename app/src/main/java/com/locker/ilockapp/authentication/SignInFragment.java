package com.locker.ilockapp.authentication;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.locker.ilockapp.R;
import com.locker.ilockapp.dao.CloudFetchr;
import com.locker.ilockapp.dao.JsonItem;
import com.locker.ilockapp.toolbox.Logs;

import static com.locker.ilockapp.authentication.AccountGeneral.sServerAuthenticate;

/**
 * Created by sredorta on 2/2/2017.
 */
public class SignInFragment extends Fragment {
    private AccountGeneral myAccountGeneral;
    private View mView;
    private User user;
    private User myUserTmp;
    private final int REQ_SIGNUP = 1;

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
                if (checkFields()) {
                    setUserFieldsFromInputs();
                    password.setText("");
                    myAccountGeneral.submitCredentials(getActivity(),mView,user);
                }
            }
        });
        //Re-enter credentials
        v.findViewById(R.id.fragment_signin_Button_connect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logs.i("Submitting credentials to account manager !", this.getClass());
                if (checkFields()) {
                    setUserFieldsFromInputs();
                    password.setText("");
                    myAccountGeneral.submitCredentials(getActivity(),mView,user);
                }
            }
        });
        //Create new user account
        v.findViewById(R.id.fragment_signin_Button_create).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logs.i("Starting new activity to create account !", this.getClass());
                // Since there can only be one AuthenticatorActivity, we call the sign up activity, get his results,
                // and return them in setAccountAuthenticatorResult(). See finishLogin().
                Intent signup = new Intent(getActivity().getBaseContext(), SignUpActivity.class);
                //Forward extras if necessary
                if (getActivity().getIntent().getExtras() != null) {
                    signup.putExtras(getActivity().getIntent().getExtras());
                    Logs.i("When starting signup extras where found !", this.getClass());
                } else {
                    Logs.i("When starting signup no extras found !", this.getClass());
                }
                startActivityForResult(signup, REQ_SIGNUP);
            }
        });

        return v;
    }


    //Verify that the fields entered are correct
    private Boolean checkFields() {
        Boolean fieldsOk = true;
        final EditText password = (EditText) mView.findViewById(R.id.fragment_signin_EditText_password);
        final EditText email_or_phone = (EditText) mView.findViewById(R.id.fragment_signin_EditText_account);

        if (!myAccountGeneral.checkPasswordInput(password.getText().toString(),mView,getActivity())) {
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

        user.print("Before running sign-in:");
    }


    //When we come back from new account creation we fall here
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Logs.i("onActivityResult", this.getClass());
        // The sign up activity returned that the user has successfully created an account
        if (requestCode == REQ_SIGNUP && resultCode == Activity.RESULT_OK) {
            getActivity().setResult(Activity.RESULT_OK, data);
            getActivity().finish();
        } else
            super.onActivityResult(requestCode, resultCode, data);
    }

    //We need to restore the user with the same values we had in case we go to preferences...
    @Override
    public void onStop() {
        super.onStop();
        Logs.i("We are on onStop of SignInWithAccounts !");
    }

    @Override
    public void onResume() {
        super.onResume();
        Logs.i("We are in on resume ! of SignInWithAccounts");
    }


}

