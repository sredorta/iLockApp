package com.locker.ilockapp.authentication;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.locker.ilockapp.R;
import com.locker.ilockapp.toolbox.Logs;



//Create new user account activity
public class SignUpFragment extends Fragment {
    private AccountGeneral myAccountGeneral;
    private View mView;
    private User myUserTmp;
    // Constructor
    public static SignUpFragment newInstance() {
        return new SignUpFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get account details from Singleton
        myAccountGeneral = myAccountGeneral.getInstance();
        myAccountGeneral.resetUser();
        myAccountGeneral.user.print("at onCreate");
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
                    myAccountGeneral.createServerAndDeviceAccount(getActivity());
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
        if (!myAccountGeneral.checkPasswordInput(password.getText().toString())) {
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

        myAccountGeneral.user.setName(email.getText().toString());
        myAccountGeneral.user.setFirstName(firstName.getText().toString());
        myAccountGeneral.user.setLastName(lastName.getText().toString());
        myAccountGeneral.user.setPhone(phone.getText().toString());
        myAccountGeneral.user.setEmail(email.getText().toString());
        myAccountGeneral.user.setPassword(password.getText().toString());
        //TODO avatar

        myAccountGeneral.user.print("Before running sign-up:");
    }

    //We need to restore the user with the same values we had in case we go to preferences...
    @Override
    public void onStop() {
        super.onStop();
        Logs.i("We are on onStop of SignInWithAccounts !");
        myUserTmp = new User();
        myUserTmp = myAccountGeneral.user;
    }

    @Override
    public void onResume() {
        super.onResume();
        Logs.i("We are in on resume ! of SignInWithAccounts");
        if (myUserTmp != null)
            myAccountGeneral.user = myUserTmp;
    }

}
