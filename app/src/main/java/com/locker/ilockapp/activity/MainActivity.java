package com.locker.ilockapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.locker.ilockapp.R;
import com.locker.ilockapp.authentication.AuthenticatorActivity;

public class MainActivity extends AppCompatActivity {
    private final int REQ_SIGNIN = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button myb = (Button) findViewById(R.id.button);

        myb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), AuthenticatorActivity.class);
                startActivityForResult(i, REQ_SIGNIN);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQ_SIGNIN && resultCode == Activity.RESULT_OK) {
            // The sign up activity returned that the user has successfully created an account
            Toast.makeText(this, "Valid credentials, we start the app !", Toast.LENGTH_LONG).show();
        }
    }
}
