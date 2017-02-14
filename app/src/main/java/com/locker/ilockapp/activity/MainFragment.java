package com.locker.ilockapp.activity;

import android.accounts.AccountManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.locker.ilockapp.R;
import com.locker.ilockapp.abstracts.FragmentAbstract;
import com.locker.ilockapp.authentication.AccountGeneral;
import com.locker.ilockapp.authentication.LockerAuthenticator;
import com.locker.ilockapp.authentication.User;
import com.locker.ilockapp.toolbox.Logs;

import org.w3c.dom.Text;

/**
 * Created by sredorta on 2/9/2017.
 */
public class MainFragment extends FragmentAbstract {
    //Input arguments definition they need to have pattern FRAGMENT_INPUT_PARAM_*
    public static final String FRAGMENT_INPUT_PARAM_USER = "user";    //String
    private String mAccountName;
    private User user;
    private  AccountGeneral myAccountGeneral;
    // Constructor
    public static MainFragment newInstance() {
        return new MainFragment();
    }

    // Constructor with input arguments
    public static MainFragment newInstance(Bundle data) {
        MainFragment fragment = MainFragment.newInstance();
        fragment.setArguments(data);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user_details, container, false);
        mAccountName = (String) getInputParam(FRAGMENT_INPUT_PARAM_USER);
        Logs.i("account : " + mAccountName);
        if (mAccountName == null) {
            //
        } else {
            myAccountGeneral = new AccountGeneral(getContext());
            user = new User();
            user.initEmpty(getContext());
            user.setName(mAccountName);
            user.getDataFromDeviceAccount(myAccountGeneral.getAccount(user));
            LockerAuthenticator am = new LockerAuthenticator(getContext());
            Logs.i("Try to see auth access : " + am.getAuthTokenLabel("toto"));

            final TextView account = (TextView) v.findViewById(R.id.textView10);
            account.setText(user.getName());
            final TextView firstName = (TextView) v.findViewById(R.id.textView11);
            firstName.setText(user.getFirstName());
            final TextView lastName = (TextView) v.findViewById(R.id.textView12);
            lastName.setText(user.getLastName());
        }
        return v;
    }

}
