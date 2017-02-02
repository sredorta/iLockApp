package com.locker.ilockapp.authentication;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.locker.ilockapp.activity.SingleFragmentActivity;

/**
 * The Authenticator activity.
 * Called by the Authenticator and in charge of identifing the user.
 * It sends back to the Authenticator the result.
 */
public class SignInWithAccountsActivity extends SingleFragmentActivity {
    @Override
    public Fragment createFragment() {
        return SignInWithAccountsFragment.newInstance();
    }
    //Finish if we backpressed here
    @Override
    public void onBackPressed() {
        Log.i("SERGI", "OnBackPressed was done, and we are now sending RESULT_CANCELED");
        setResult(RESULT_CANCELED);
        finish();
    }


}
