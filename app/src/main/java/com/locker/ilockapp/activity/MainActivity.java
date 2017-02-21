package com.locker.ilockapp.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.locker.ilockapp.R;
import com.locker.ilockapp.abstracts.OnBackPressed;
import com.locker.ilockapp.fragment_debug.FragmentA;
import com.locker.ilockapp.toolbox.Logs;

public class MainActivity extends AppCompatActivity {
    public static final String USER_ACCOUNT = "user.account";
    private String mUserAccount;
    Fragment fragment;

    public Fragment createFragment() {
        //Send data to FragmentA
        Bundle bundle = new Bundle();
        bundle.putSerializable(MainFragment.FRAGMENT_INPUT_PARAM_USER, mUserAccount);
        Logs.i("We put in bundle to fragment : " + mUserAccount);
        return MainFragment.newInstance(bundle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserAccount = getIntent().getStringExtra(USER_ACCOUNT);
        Logs.i("MainActivity: onCreate : " + mUserAccount);
        setContentView(R.layout.activity_fragment);
        FragmentManager fm = getSupportFragmentManager();
        fragment = fm.findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            fragment = createFragment();
            replaceFragmentWithAnimation(fragment,"test");
        }

    }
    public void replaceFragmentWithAnimation(android.support.v4.app.Fragment fragment, String tag){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(tag);
        transaction.commit();
    }


/*    //Implement the onBackPressed on the fragments itself
    @Override
    public void onBackPressed() {
        Logs.i("OnBackPressed: Current number of fragments : " + getSupportFragmentManager().getBackStackEntryCount());
        Fragment currentFragment = getSupportFragmentManager().getFragments().get(getSupportFragmentManager().getBackStackEntryCount() - 1);
        if (currentFragment != null)
            if (currentFragment instanceof OnBackPressed) {
                ((OnBackPressed) currentFragment).onBackPressed();
            } else
                super.onBackPressed();
        else
         super.onBackPressed();
    }*/
}
