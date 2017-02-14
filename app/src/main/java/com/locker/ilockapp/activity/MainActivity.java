package com.locker.ilockapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;

import com.locker.ilockapp.authentication.AuthenticatorActivity;
import com.locker.ilockapp.fragment_debug.LogInActivity;
import com.locker.ilockapp.toolbox.Logs;

public class MainActivity extends SingleFragmentActivity {

    @Override
    public Fragment createFragment() {
        return MainFragment.newInstance();
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
        if (requestCode == REQ_SIGNIN && resultCode == Activity.RESULT_OK) {
//            String accountName = data.getStringExtra("authAccount");
 //           Logs.i("Account name is:" + accountName);
            //final TextView myText = (TextView) findViewById(R.id.textView2);
            //myText.setText(accountName);
            // The sign up activity returned that the user has successfully created an account
            //Toast.makeText(this, "Valid credentials, we start the app !", Toast.LENGTH_LONG).show();
        } else
            Logs.i("Cancel was clicked, so not logged in...");
    }


}
