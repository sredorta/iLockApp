package com.locker.ilockapp.authentication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.locker.ilockapp.R;
import com.locker.ilockapp.toolbox.Logs;

/**
 * Created by sredorta on 1/31/2017.
 */
public class SignInWithAccountsFragment extends Fragment {
    private AccountGeneral myAccountGeneral;
    private View mView;
    private User myUserTmp;
    private User user;
    private final int REQ_SIGNIN = 1;
    private final int REQ_SIGNUP = 2;

    // Constructor
    public static SignInWithAccountsFragment newInstance() {
        return new SignInWithAccountsFragment();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get account details from Singleton either from intent or from account of the device
        myAccountGeneral = new AccountGeneral(getContext());
        user = new User();
        user.init(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_signin_with_accounts, container, false);
        final EditText password = (EditText) v.findViewById(R.id.fragment_signin_with_accounts_EditText_password);

        mView = v;

        //Re-enter credentials
        v.findViewById(R.id.fragment_signin_with_accounts_Button_connect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logs.i("Submitting credentials to account manager !", this.getClass());
                user = AccountListFragment.myUserSelected;
                user.print("This is wat we have for user now:");
                //If we are restoring an account then we get from user input the account name
                if (checkFields()) {
                    setUserFieldsFromInputs();
                    password.setText("");
                    myAccountGeneral.submitCredentials(getActivity(),user);
                }

            }
        });

        //LogIn with credentials
        v.findViewById(R.id.fragment_signin_with_accounts_Button_credentials).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logs.i("Starting new activity to create account !", this.getClass());
                // Since there can only be one AuthenticatorActivity, we call the sign up activity, get his results,
                // and return them in setAccountAuthenticatorResult(). See finishLogin().
                Intent signin = new Intent(getActivity().getBaseContext(), SignInActivity.class);
                //Forward extras if necessary
                if (getActivity().getIntent().getExtras() != null) {
                    signin.putExtras(getActivity().getIntent().getExtras());
                    Logs.i("When starting signup extras where found !", this.getClass());
                } else {
                    Logs.i("When starting signup no extras found !", this.getClass());
                }
                startActivityForResult(signin, REQ_SIGNIN);
/*
                Logs.i("Submitting credentials to account manager !", this.getClass());
                //If we are restoring an account then we get from user input the account name
                if (checkFields()) {
                    setUserFieldsFromInputs();
                    password.setText("");
                    myAccountGeneral.submitCredentials(getActivity());
                }
*/
            }
        });

        //Create new user account
        v.findViewById(R.id.fragment_signin_with_accounts_Button_create).setOnClickListener(new View.OnClickListener() {
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
        final EditText password = (EditText) mView.findViewById(R.id.fragment_signin_with_accounts_EditText_password);

        if (!myAccountGeneral.checkPasswordInput(password.getText().toString())) {
            password.setText("");
            password.setHintTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
            Toast.makeText(getActivity(), "Password must be at least 8 chars !", Toast.LENGTH_LONG).show();
            fieldsOk = false;
        }

        return fieldsOk;

    }

    //Sets the user singleton with the inputs
    private void setUserFieldsFromInputs() {
        final EditText password           = (EditText) mView.findViewById(R.id.fragment_signin_with_accounts_EditText_password);
        user.setPassword(password.getText().toString());

        user.print("Before running sign-in:");
    }





    //This is to inflate the Child fragment that contains the account details
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Fragment childFragment;
        childFragment = new AccountListFragment();

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container_account_display, childFragment).commit();
    }


    //When we come back from new account creation we fall here
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Logs.i("onActivityResult", this.getClass());
        // The sign up activity returned that the user has successfully created an account
        if (requestCode == REQ_SIGNUP && resultCode == Activity.RESULT_OK) {
            getActivity().setResult(Activity.RESULT_OK, data);
            getActivity().finish();
        } else if (requestCode == REQ_SIGNIN && resultCode == Activity.RESULT_OK) {
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
