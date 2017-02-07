package com.locker.ilockapp.authentication;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.locker.ilockapp.R;
import com.locker.ilockapp.dao.JsonItem;
import com.locker.ilockapp.dao.QueryPreferences;
import com.locker.ilockapp.toolbox.Logs;
import com.locker.ilockapp.toolbox.Toolbox;

import static com.locker.ilockapp.authentication.AccountGeneral.sServerAuthenticate;

/**
 * Created by sredorta on 2/6/2017.
 */
public class AuthenticatorFragment extends Fragment {
    private AccountGeneral myAccountGeneral;
    private final int REQ_SIGNIN = 1;
    private final int REQ_SIGNIN_WITH_ACCOUNTS = 2;
    private Intent myIntent;
    private User user;
    // Constructor
    public static AuthenticatorFragment newInstance() {
        return new AuthenticatorFragment();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        QueryPreferences.setDefaultValues(getActivity().getApplicationContext());
        myAccountGeneral = new AccountGeneral(getContext());
        user = new User();
        user.init(getContext());
        //myAccountGeneral.init(getActivity().getApplicationContext());
        myIntent = getActivity().getIntent();
        if (myAccountGeneral.getAccount(user) != null) {
            checkFastLogin();
        } else
            startSignIn();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_wait, container, false);

        return v;
    }

    //We have an account so we just need to validate that our token is still valid and if not go to SignIn
    private void checkFastLogin() {
        user.print("CheckFastLogin user details:");
        //Get the account token in the device
        new AsyncTask<String, Void, Intent>() {
            @Override
            protected Intent doInBackground(String... params) {
                Bundle data = new Bundle();
                Boolean isValidToken =false;
                if (user.getToken() !=null) {
                    JsonItem item = sServerAuthenticate.isTokenValid(user);
                    isValidToken = item.getResult();
                }
                data.putString(AccountManager.KEY_ACCOUNT_NAME, user.getName());
                data.putString(AccountManager.KEY_ACCOUNT_TYPE, user.getType());
                data.putString(AccountManager.KEY_AUTHTOKEN, user.getToken());
                data.putBoolean("isValidToken", isValidToken);
                Logs.i("We are checking if Token is valid !", AuthenticatorActivity.class);
                final Intent res = new Intent();
                res.putExtras(data);

                return res;
            }

            @Override
            protected void onPostExecute(Intent intent) {
                Logs.i("We are checking if Token is valid ! onPostExecute", AuthenticatorActivity.class);
                if (intent.getBooleanExtra("isValidToken",false)) {
                    getActivity().setResult(Activity.RESULT_OK, intent);
                    Logs.i("Exit the activity !", this.getClass());
                    finishLogin(intent);
                } else {
                    startSignIn();
                }
            }
        }.execute();

    }


    //Start signIn acticty for result
    private void startSignIn() {

        if (myAccountGeneral.getAccountsCount() >0) {
            Logs.i("Accounts found so starting SignInWithAccountsActivity:", this.getClass());
            Intent signIn = new Intent(getActivity().getBaseContext(), SignInWithAccountsActivity.class);
            //Forward any extras that were passed from LockerAuthenticator when creating account from Settings
            if (getActivity().getIntent().getExtras() != null)
                signIn.putExtras(myIntent.getExtras());
            Logs.i("Intent details for intent to start SignInWithAccountsActivity:", this.getClass());
            Toolbox.dumpIntent(signIn);
            startActivityForResult(signIn, REQ_SIGNIN_WITH_ACCOUNTS);
        } else {
            Logs.i("No account found so start SignInActivity:", this.getClass());
            Intent signIn = new Intent(getActivity().getBaseContext(), SignInActivity.class);
            //Forward any extras that were passed from LockerAuthenticator when creating account from Settings
            if (getActivity().getIntent().getExtras() != null)
                signIn.putExtras(myIntent.getExtras());
            Logs.i("Intent details for intent to start SignInActivity:", this.getClass());
            Toolbox.dumpIntent(signIn);
            startActivityForResult(signIn, REQ_SIGNIN);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_SIGNIN && resultCode == Activity.RESULT_OK) {
            finishLogin(data);
        } else if (requestCode == REQ_SIGNIN_WITH_ACCOUNTS && resultCode == Activity.RESULT_OK) {
            finishLogin(data);
        } else if (resultCode == Activity.RESULT_CANCELED) {
            getActivity().setResult(Activity.RESULT_CANCELED);
            getActivity().finish();
        } else
            super.onActivityResult(requestCode, resultCode, data);
    }


    private void finishLogin(Intent intent){
        Logs.i("This is what we sent to authenticator !");
        Toolbox.dumpIntent(intent);

        //Save the last successfull login account into the preferences
        if (intent.hasExtra(AccountManager.KEY_ACCOUNT_NAME)) {
            QueryPreferences.setPreference(getActivity().getApplicationContext(),QueryPreferences.PREFERENCE_ACCOUNT_NAME,intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME));
        }
        AuthenticatorActivity.setAccountAuthenticatorResult(intent.getExtras());
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }



}
