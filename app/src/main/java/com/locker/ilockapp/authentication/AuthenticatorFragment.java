package com.locker.ilockapp.authentication;

import android.accounts.AccountManager;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.locker.ilockapp.R;
import com.locker.ilockapp.dao.JsonItem;
import com.locker.ilockapp.dao.QueryPreferences;
import com.locker.ilockapp.abstracts.FragmentAbstract;
import com.locker.ilockapp.toolbox.Logs;
import com.locker.ilockapp.toolbox.Toolbox;

import static com.locker.ilockapp.authentication.AccountGeneral.ARG_IS_ADDING_NEW_ACCOUNT;
import static com.locker.ilockapp.authentication.AccountGeneral.sServerAuthenticate;

/**
 * Created by sredorta on 2/6/2017.
 */
public class AuthenticatorFragment extends FragmentAbstract {
    private AccountGeneral myAccountGeneral;
    private final int REQ_SIGNIN = 1;
    private final int REQ_SIGNIN_WITH_ACCOUNTS = 2;
    private final int REQ_SIGNUP = 3;
    private Intent myIntent;
    private User user;
    private boolean mIsDone = false;
    // Constructor
    public static AuthenticatorFragment newInstance() {
        return new AuthenticatorFragment();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //This is a first fragment so we load the fragment in the container
        //replaceFragment(AuthenticatorFragment.this,"test",true);
        QueryPreferences.setDefaultValues(mActivity.getApplicationContext());
        myAccountGeneral = new AccountGeneral(getContext());
        user = new User();
        user.init(getContext());
        myIntent = mActivity.getIntent();
        setRetainInstance(true);   //We want to retain this fragment
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_wait, container, false);
        final ImageView myLogo = (ImageView) v.findViewById(R.id.fragment_wait_imageView);
        if (!mIsDone) {
        ObjectAnimator fadeInOutAnimator = ObjectAnimator.ofFloat(this, "alpha", 1, 0, 1).setDuration(2000);
        fadeInOutAnimator.setRepeatCount(1);
        fadeInOutAnimator.setTarget(myLogo);
        final ObjectAnimator fadeInOutInfiniteAnimator = ObjectAnimator.ofFloat(this, "alpha", 1, 0, 1).setDuration(2000);
        fadeInOutInfiniteAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        fadeInOutAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                fadeInOutInfiniteAnimator.start();
                if (myIntent.hasExtra(ARG_IS_ADDING_NEW_ACCOUNT)) {
                    startSignUp();
                } else {
                    if (myAccountGeneral.getAccount(user) != null) {
                        checkFastLogin();
                    } else
                        startSignIn();
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        fadeInOutAnimator.start();
        }
        mIsDone = true;  //We need to ensure that we only execute this code once... so when we come back we don't redo it
        return v;
    }

    //We have an account so we just need to validate that our token is still valid and if not go to SignIn
    private void checkFastLogin() {
        user.print("CheckFastLogin user details:");
        //Get the account token in the device

        new AsyncTask<String, Void, Bundle>() {
            @Override
            protected Bundle doInBackground(String... params) {

                Boolean isValidToken =false;
                if (user.getToken() !=null) {
                    JsonItem item = sServerAuthenticate.isTokenValid(user);
                    isValidToken = item.getResult();
                }
                Bundle data = new Bundle();
                data.putString(AccountManager.KEY_ACCOUNT_NAME, user.getName());
                data.putString(AccountManager.KEY_ACCOUNT_TYPE, user.getType());
                data.putString(AccountManager.KEY_AUTHTOKEN, user.getToken());
                data.putBoolean("isValidToken", isValidToken);
                return data;
            }

            @Override
            protected void onPostExecute(Bundle data) {
                user.print("This is the user we are trying to log in:");
                Logs.i("We are checking if Token is valid ! onPostExecute", AuthenticatorActivity.class);
                if (data.getBoolean("isValidToken",false)) {
                    mActivity.getIntent().putExtras(data);
                    mActivity.setResult(Activity.RESULT_OK);
                    mActivity.finish();
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
            SignInWithAccountsFragment fragment = SignInWithAccountsFragment.newInstance();
            fragment.setTargetFragment(AuthenticatorFragment.this, REQ_SIGNIN_WITH_ACCOUNTS);
            //Now replace the AuthenticatorFragment with the SignInWithAccountsFragment
            replaceFragment(fragment,"test",true);  //This comes from abstract
        } else {
            SignInFragment fragment = SignInFragment.newInstance();
            fragment.setTargetFragment(AuthenticatorFragment.this, REQ_SIGNIN);
            //Now replace the AuthenticatorFragment with the SignInFragment
            replaceFragment(fragment,"test",true);  //This comes from abstract
        }
    }

    //Start signIn fragment for result
    private void startSignUp() {
        Logs.i("Found IS_ADDING :", this.getClass());
        SignUpFragment fragment = SignUpFragment.newInstance();
        fragment.setTargetFragment(AuthenticatorFragment.this, REQ_SIGNUP);
        //Now replace the AuthenticatorFragment with the SignInFragment
        replaceFragment(fragment,"test",true);  //This comes from abstract
    }

    @Override
    public void onResume() {
        super.onResume();
        Logs.i("On resume of AuthentFragment");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_CANCELED) {
            Logs.i("AuthenticatorFragment, result is CANCELED");
            mActivity.setResult(Activity.RESULT_CANCELED);
            mActivity.finish();
        } else
            super.onActivityResult(requestCode, resultCode, data);
    }



}
