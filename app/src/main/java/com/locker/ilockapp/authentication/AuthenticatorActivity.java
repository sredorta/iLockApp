package com.locker.ilockapp.authentication;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.locker.ilockapp.R;
import com.locker.ilockapp.dao.JsonItem;
import com.locker.ilockapp.dao.QueryPreferences;
import com.locker.ilockapp.toolbox.Logs;
import com.locker.ilockapp.toolbox.Toolbox;
import static com.locker.ilockapp.authentication.AccountGeneral.*;

/**
 * Created by sredorta on 1/25/2017.
 */

//We get here by two means:
//    When using settings then we get here thanks to the service
//    When using the app we will send an intent to check if valid auth
public class AuthenticatorActivity extends AccountAuthenticatorActivity {
    private Intent myIntent;
    private AccountGeneral myAccountSettings;
    private AccountGeneral myAccountGeneral;

    private final int REQ_SIGNIN = 1;
    private final int REQ_SIGNIN_WITH_ACCOUNTS = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logs.i("onCreate");

        QueryPreferences.setDefaultValues(getApplicationContext());
        myAccountGeneral = AccountGeneral.getInstance();
        myAccountGeneral.init(getApplicationContext());

        if (myAccountGeneral.getAccount() != null) {
            checkFastLogin();
        } else
            startSignIn();

    }

    //We have an account so we just need to validate that our token is still valid and if not go to SignIn
    private void checkFastLogin() {
        final Account myAccount = myAccountGeneral.getAccount();
        Logs.i("Trying fast login: " + myAccountGeneral.user.getName() + "  id: " + myAccountGeneral.user.getId(), this.getClass());
        //Get the account token in the device
            new AsyncTask<String, Void, Intent>() {
                @Override
                protected Intent doInBackground(String... params) {
                    Bundle data = new Bundle();
                    Logs.i("Account Id: " + myAccountGeneral.user.getId());
                    Logs.i("Token : " + myAccountGeneral.user.getToken());
                    Logs.i("Token type: " + myAccountGeneral.user.getAuthType());
                    Boolean isValidToken =false;
                    if (myAccountGeneral.user.getToken() !=null) {
                        JsonItem item = sServerAuthenticate.isTokenValid();
                        isValidToken = item.getResult();
                    }
                    data.putString(AccountManager.KEY_ACCOUNT_NAME, myAccountGeneral.user.getName());
                    data.putString(AccountManager.KEY_ACCOUNT_TYPE, myAccountGeneral.user.getType());
                    data.putString(AccountManager.KEY_AUTHTOKEN, myAccountGeneral.user.getToken());
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
                        setResult(RESULT_OK);
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
            Intent signIn = new Intent(getBaseContext(), SignInWithAccountsActivity.class);
            //Forward any extras that were passed from LockerAuthenticator when creating account from Settings
            if (getIntent().getExtras() != null)
                signIn.putExtras(myIntent.getExtras());
            Logs.i("Intent details for intent to start SignInWithAccountsActivity:", this.getClass());
            Toolbox.dumpIntent(signIn);
            startActivityForResult(signIn, REQ_SIGNIN_WITH_ACCOUNTS);
        } else {
            Logs.i("No account found so start SignInActivity:", this.getClass());
            Intent signIn = new Intent(getBaseContext(), SignInActivity.class);
            //Forward any extras that were passed from LockerAuthenticator when creating account from Settings
            if (getIntent().getExtras() != null)
                signIn.putExtras(myIntent.getExtras());
            Logs.i("Intent details for intent to start SignInActivity:", this.getClass());
            Toolbox.dumpIntent(signIn);
            startActivityForResult(signIn, REQ_SIGNIN);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_SIGNIN && resultCode == RESULT_OK) {
            finishLogin(data);
        } else if (requestCode == REQ_SIGNIN_WITH_ACCOUNTS && resultCode == RESULT_OK) {
            finishLogin(data);
        } else if (resultCode == RESULT_CANCELED) {
            setResult(RESULT_CANCELED);
            finish();
        } else
            super.onActivityResult(requestCode, resultCode, data);
    }

    private void finishLogin(Intent intent){
        Logs.i("This is what we sent to authenticator !");
        Toolbox.dumpIntent(intent);

        //Save the last successfull login account into the preferences
        if (intent.hasExtra(AccountManager.KEY_ACCOUNT_NAME)) {
            QueryPreferences.setPreference(getApplicationContext(),QueryPreferences.PREFERENCE_ACCOUNT_NAME,intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME));
        }


        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
    }


}
