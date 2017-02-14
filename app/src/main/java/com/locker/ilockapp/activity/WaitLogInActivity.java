package com.locker.ilockapp.activity;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.locker.ilockapp.R;
import com.locker.ilockapp.authentication.AuthenticatorActivity;
import com.locker.ilockapp.fragment_debug.FragmentA;
import com.locker.ilockapp.fragment_debug.LogInActivity;
import com.locker.ilockapp.toolbox.Logs;
import com.locker.ilockapp.toolbox.Toolbox;

public class WaitLogInActivity extends AppCompatActivity {
    private String mAccountName;


    public Fragment createFragment() {
        //Send data to MainFragment
        Bundle bundle = new Bundle();
        bundle.putSerializable(MainFragment.FRAGMENT_INPUT_PARAM_USER,mAccountName);
        return MainFragment.newInstance(bundle);
    }
    Fragment fragment;

    private final int REQ_SIGNIN = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Start authentication
        Intent i = new Intent(getApplicationContext(), AuthenticatorActivity.class);
        startActivityForResult(i, REQ_SIGNIN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Logs.i("Back from authentication");
        Toolbox.dumpIntent(data);
        mAccountName = "";
        if (requestCode == REQ_SIGNIN && resultCode == Activity.RESULT_OK) {
            if (data.hasExtra(AccountManager.KEY_ACCOUNT_NAME)) {
                mAccountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            }
        }
        Logs.i("Starting main activity with account :" + mAccountName);
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        i.putExtra(MainActivity.USER_ACCOUNT, mAccountName);
        startActivity(i);
        finish();
    }

}
