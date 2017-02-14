package com.locker.ilockapp.authentication;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.locker.ilockapp.R;
import com.locker.ilockapp.abstracts.FragmentAbstract;
import com.locker.ilockapp.toolbox.Logs;

/**
 * Created by sredorta on 1/31/2017.
 */
public class SignInWithAccountsFragment extends FragmentAbstract {
    private AccountGeneral myAccountGeneral;
    private View mView;
    private User user;
    private boolean mUpdatePositions = true;
    private static final String KEY_SAVED_USER = "saved_user";                  //Input string with tarting selected account input
    private static final String KEY_UPDATE_POSITION = "update_user_position";   //Input boolean to know if we come back from rotation
//    private final int REQ_SIGNIN = 1;
//    private final int REQ_SIGNUP = 2;
    //Requests to other fragments
    private static final int REQUEST_SELECTION = 0;                             //Request to recycleview so that returns selected user
    public static final String FRAGMENT_OUTPUT_PARAM_SELECTED_USER = "selected_user";    //Returns selected user

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
        //If we had an user on the bundle it means that screen was rotated, so we restore
        if (savedInstanceState != null) {
            if (savedInstanceState.getString(KEY_SAVED_USER)!= null)
                user.setName(savedInstanceState.getString(KEY_SAVED_USER));
            mUpdatePositions = savedInstanceState.getBoolean(KEY_UPDATE_POSITION,true);
        }
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
                user.print("This is wat we have for user now:");
                //hide input keyboard
                hideInputKeyBoard();

                //If we are restoring an account then we get from user input the account name
                if (checkFields()) {
                    setUserFieldsFromInputs();
                    password.setText("");
                    user.print("User data :");
                    myAccountGeneral.submitCredentials(mActivity,mView,user);
                }
            }
        });

        //LogIn with credentials
        v.findViewById(R.id.fragment_signin_with_accounts_Button_credentials).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logs.i("Starting new activity to create account !", this.getClass());

                SignInFragment fragment = SignInFragment.newInstance();
//                fragment.setTargetFragment(SignInWithAccountsFragment.this, REQ_SIGNIN);
                //Now replace the AuthenticatorFragment with the SignInWithAccountsFragment
                replaceFragment(fragment,"test",true);  //This comes from abstract
            }
        });

        //Create new user account
        v.findViewById(R.id.fragment_signin_with_accounts_Button_create).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logs.i("Starting new activity to create account !", this.getClass());
                SignUpFragment fragment = SignUpFragment.newInstance();
//                fragment.setTargetFragment(SignInWithAccountsFragment.this, REQ_SIGNUP);
                //Now replace the AuthenticatorFragment with the SignInFragment
                replaceFragment(fragment,"test",true);  //This comes from abstract
            }
        });

        return v;
    }

    //Verify that the fields entered are correct
    private Boolean checkFields() {
        Boolean fieldsOk = true;
        final EditText password = (EditText) mView.findViewById(R.id.fragment_signin_with_accounts_EditText_password);

        if (!myAccountGeneral.checkPasswordInput(password.getText().toString(),mView,mActivity)) {
            password.setText("");
            password.setHintTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));

            fieldsOk = false;
        }
        return fieldsOk;

    }

    //Sets the user singleton with the inputs
    private void setUserFieldsFromInputs() {
        final EditText password           = (EditText) mView.findViewById(R.id.fragment_signin_with_accounts_EditText_password);
        user.setPassword(password.getText().toString());
    }


    //This is to inflate the Child fragment that contains the account details
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(AccountListFragment.FRAGMENT_INPUT_PARAM_USER, user.getName());
        bundle.putSerializable(AccountListFragment.FRAGMENT_INPUT_PARAM_UPDATE_POSITIONS, mUpdatePositions);
        Logs.i("Creating recycleView with: " + user.getName() + " and " + mUpdatePositions);
        AccountListFragment childFragment = AccountListFragment.newInstance(bundle);
        setContainer(R.id.fragment_container_account_display);
        childFragment.setTargetFragment(SignInWithAccountsFragment.this, REQUEST_SELECTION);
        setAddToBackStack(false);   //We don't want to add into the backstack
        replaceFragment(childFragment,"test",false);
        setContainer(R.id.fragment_container);
        setAddToBackStack(true);
    }


    //When we come back from new account creation we fall here
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Logs.i("onActivityResult of SignInWithAccounts !");
        if (requestCode == REQUEST_SELECTION && resultCode == Activity.RESULT_OK) {
            //We get result from child fragment and update user.setName
            User newUser = new User();
            newUser.initEmpty(getContext());
            newUser.setName((String) data.getSerializableExtra(AccountListFragment.FRAGMENT_OUTPUT_PARAM_SELECTED_USER));
            user = newUser;
        } else if (requestCode == REQUEST_SELECTION && resultCode == Activity.RESULT_CANCELED) {
            mUpdatePositions = false;
        } else
            super.onActivityResult(requestCode, resultCode, data);
    }

    //When rotation happens store the current user name and that we have rotated screen, so no updates on list order
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Logs.i("Saved selected user : " + user.getName());
        outState.putString(KEY_SAVED_USER,user.getName());
        outState.putBoolean(KEY_UPDATE_POSITION,false);
    }

}
