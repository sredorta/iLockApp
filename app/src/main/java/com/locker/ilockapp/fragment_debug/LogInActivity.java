package com.locker.ilockapp.fragment_debug;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.locker.ilockapp.R;
import com.locker.ilockapp.abstracts.OnBackPressed;

public class LogInActivity extends AppCompatActivity implements OnBackPressed {


    Fragment fragment;

    public Fragment createFragment() {
        //Send data to FragmentA
        Bundle bundle = new Bundle();
        bundle.putSerializable(FragmentA.FRAGMENT_INPUT_PARAM_ARG1,"test");
        bundle.putSerializable(FragmentA.FRAGMENT_INPUT_PARAM_ARG2, 10);
        return FragmentA.newInstance(bundle);
        //return FragmentA.newInstance();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void finish() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        transaction.remove(fragment);
        //transaction.replace(R.id.fragment_container, fragment);
        //transaction..addToBackStack(tag);
        transaction.commit();
        super.finish();
    }


    //Implement the onBackPressed on the fragments itself
    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().getFragments().get(getSupportFragmentManager().getBackStackEntryCount() - 1);
        if (currentFragment instanceof OnBackPressed) {
            ((OnBackPressed) currentFragment).onBackPressed();
        }
        super.onBackPressed();
    }
}

