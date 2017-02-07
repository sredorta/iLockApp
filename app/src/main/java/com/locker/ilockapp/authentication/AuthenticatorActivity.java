package com.locker.ilockapp.authentication;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.locker.ilockapp.R;
import com.locker.ilockapp.activity.SingleFragmentActivity;
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
//    In order to use app.v4 fragments we have copied the AccountAuthenticatorActivity abstract
public class AuthenticatorActivity extends SingleFragmentActivity {

    public static AccountAuthenticatorResponse mAccountAuthenticatorResponse = null;
    public static Bundle mResultBundle = null;


    @Override
    public Fragment createFragment() {
        return AuthenticatorFragment.newInstance();
    }

    //Sets the bundle with the results that are sent to the authenticator (LockerAuthenticator)
    public static final void setAccountAuthenticatorResult(Bundle result) {
        mResultBundle = result;
    }


    @Override
    public void finish() {
        if (mAccountAuthenticatorResponse != null) {
             // send the result bundle back if set, otherwise send an error.
             if (mResultBundle != null) {
                    mAccountAuthenticatorResponse.onResult(mResultBundle);
             } else {
                    mAccountAuthenticatorResponse.onError(AccountManager.ERROR_CODE_CANCELED,
                                     "canceled");
             }
             mAccountAuthenticatorResponse = null;
        }
     super.finish();
     }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Part of the accountAuthActivity
        mAccountAuthenticatorResponse =
                getIntent().getParcelableExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE);

        if (mAccountAuthenticatorResponse != null) {
            mAccountAuthenticatorResponse.onRequestContinued();
        }
    }

}
