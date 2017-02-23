package com.locker.ilockapp.authentication;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.locker.ilockapp.R;
import com.locker.ilockapp.abstracts.FragmentAbstract;
import com.locker.ilockapp.toolbox.Logs;



//Create new user account activity
public class SignUpFragment extends FragmentAbstract {
 /*   private AccountGeneral myAccountGeneral;
    private View mView;
    private User user;
    // Constructor
    public static SignUpFragment newInstance() {
        return new SignUpFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get account details from Singleton
        myAccountGeneral = new AccountGeneral(getContext());
        user = new User();
        user.initEmpty(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_signup, container, false);
        mView = v;
        final Button submitButton   = (Button)   mView.findViewById(R.id.fragment_signup_Button_submit);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logs.i("Submit clicked !", this.getClass());

                if (checkFields()) {
                    setUserFieldsFromInputs();
                    myAccountGeneral.createServerAndDeviceAccount(mActivity, user);
                }
            }
        });

        return v;
    }

    //Verify that the fields entered are correct
    private Boolean checkFields() {
        Boolean fieldsOk = true;
        final EditText firstName    = (EditText) mView.findViewById(R.id.fragment_signup_EditText_FirstName);
        final EditText lastName     = (EditText) mView.findViewById(R.id.fragment_signup_EditText_LastName);
        final EditText phone        = (EditText) mView.findViewById(R.id.fragment_signup_EditText_phone);
        final EditText email        = (EditText) mView.findViewById(R.id.fragment_signup_EditText_email);
        final EditText password     = (EditText) mView.findViewById(R.id.fragment_signup_EditText_password);

        if (firstName.getText().toString().length() == 0) {
            firstName.setHintTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
            fieldsOk = false;
        }
        if (lastName.getText().toString().length() == 0) {
            lastName.setHintTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
            fieldsOk = false;
        }
        if (!myAccountGeneral.checkPasswordInput(password.getText().toString(),mView,mActivity)) {
            fieldsOk = false;
            password.setHintTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
            password.setText("");
        }

        if (!myAccountGeneral.checkEmailInput(email.getText().toString())) {
            fieldsOk = false;
            email.setHintTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
            email.setText("");
        }
        if (!myAccountGeneral.checkPhoneInput(phone.getText().toString())) {
            fieldsOk = false;
            phone.setHintTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
            phone.setText("");
        }
        return fieldsOk;

    }

    //Sets the user singleton with the inputs
    private void setUserFieldsFromInputs() {
        final EditText firstName    = (EditText) mView.findViewById(R.id.fragment_signup_EditText_FirstName);
        final EditText lastName     = (EditText) mView.findViewById(R.id.fragment_signup_EditText_LastName);
        final EditText phone        = (EditText) mView.findViewById(R.id.fragment_signup_EditText_phone);
        final EditText email        = (EditText) mView.findViewById(R.id.fragment_signup_EditText_email);
        final EditText password     = (EditText) mView.findViewById(R.id.fragment_signup_EditText_password);

        user.setName(email.getText().toString());
        user.setFirstName(firstName.getText().toString());
        user.setLastName(lastName.getText().toString());
        user.setPhone(phone.getText().toString());
        user.setEmail(email.getText().toString());
        user.setPassword(password.getText().toString());
        //TODO avatar

    }
    */
/*
    @Override
    public void onBackPressed() {
        Log.i("SERGI", "OnBackPressed was done, and we are now sending RESULT_CANCELED");
        sendResult(Activity.RESULT_CANCELED);
    }
*/
}
