package com.locker.ilockapp.authentication;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.locker.ilockapp.R;
import com.locker.ilockapp.dao.QueryPreferences;
import com.locker.ilockapp.abstracts.OnBackPressed;
import com.locker.ilockapp.toolbox.Logs;
import com.locker.ilockapp.toolbox.Toolbox;

/**
 * Created by sredorta on 1/25/2017.
 */

//We get here by two means:
//    When using settings then we get here thanks to the service
//    When using the app we will send an intent to check if valid auth
//    In order to use app.v4 fragments we have copied the AccountAuthenticatorActivity abstract
public class AuthenticatorActivity extends AppCompatActivity implements OnBackPressed {

    public static AccountAuthenticatorResponse mAccountAuthenticatorResponse = null;
    public static Bundle mResultBundle = null;
    private Fragment fragment;


 //   @Override
    public Fragment createFragment() {
        return AuthenticatorFragment.newInstance();
    }

    //Sets the bundle with the results that are sent to the authenticator (LockerAuthenticator)
    public static final void setAccountAuthenticatorResult(Bundle result) {
        mResultBundle = result;
    }


    @Override
    public void finish() {
        Logs.i("Input intent before finish:");
        //Save the last successfull login account into the preferences
        if (getIntent().hasExtra(AccountManager.KEY_ACCOUNT_NAME)) {
            QueryPreferences.setPreference(getApplicationContext(),QueryPreferences.PREFERENCE_ACCOUNT_NAME,getIntent().getStringExtra(AccountManager.KEY_ACCOUNT_NAME));
        }
        AuthenticatorActivity.setAccountAuthenticatorResult(getIntent().getExtras());

        Toolbox.dumpIntent(getIntent());
        Logs.i("Finish of AuthenticatorActivity !");
        if (mAccountAuthenticatorResponse != null) {
             // send the result bundle back if set, otherwise send an error.
             if (mResultBundle != null) {
                    mAccountAuthenticatorResponse.onResult(mResultBundle);
                    Logs.i ("Account that we should save : " + getIntent().getStringExtra(AccountManager.KEY_ACCOUNT_NAME));

             } else {
                    mAccountAuthenticatorResponse.onError(AccountManager.ERROR_CODE_CANCELED,
                                     "canceled");
             }
             mAccountAuthenticatorResponse = null;
        }
        //We return to the main activity the response
        Intent i = new Intent();
        if (mResultBundle != null) {
            i.putExtras(mResultBundle);
            this.setResult(RESULT_OK, i);
        } else {
            this.setResult(RESULT_CANCELED, i);
        }
        super.finish();
     }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        Logs.i("We are on onCreate of AuthActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        FragmentManager fm = getSupportFragmentManager();
        fragment = fm.findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            fragment = createFragment();
            replaceFragmentWithAnimation(fragment,"test");
        }

        // Part of the accountAuthActivity
        mAccountAuthenticatorResponse =
                getIntent().getParcelableExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE);

        if (mAccountAuthenticatorResponse != null) {
            mAccountAuthenticatorResponse.onRequestContinued();
        }
    }
    public void replaceFragmentWithAnimation(android.support.v4.app.Fragment fragment, String tag){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(tag);
        transaction.commit();
    }

    //Remove a fragment
    public void removeFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        if (animation)
//            transaction.setCustomAnimations(mAnimEnter, mAnimExit, mAnimPopEnter, mAnimPopExit);
        transaction.remove(fragment);
        transaction.commit();
    }

    //Implement the onBackPressed on the fragments itself
    @Override
    public void onBackPressed() {
        Logs.i("OnBackPressed: Current number of fragments : " + getSupportFragmentManager().getBackStackEntryCount());
        Fragment currentFragment = getSupportFragmentManager().getFragments().get(getSupportFragmentManager().getBackStackEntryCount() - 1);
        Logs.i("Current fragment is :" + currentFragment.getId());
        if (currentFragment != null) {
          if (currentFragment instanceof OnBackPressed) {
            ((OnBackPressed) currentFragment).onBackPressed();
          }
        }

        super.onBackPressed();
    }
}
