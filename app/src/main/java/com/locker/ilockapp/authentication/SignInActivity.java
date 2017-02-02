package com.locker.ilockapp.authentication;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.locker.ilockapp.activity.SingleFragmentActivity;

public class SignInActivity extends SingleFragmentActivity {
    @Override
    public Fragment createFragment() {
        return SignInFragment.newInstance();
    }
    //Finish if we backpressed here
    @Override
    public void onBackPressed() {
        Log.i("SERGI", "OnBackPressed was done, and we are now sending RESULT_CANCELED");
        setResult(RESULT_CANCELED);
        finish();
    }


}
