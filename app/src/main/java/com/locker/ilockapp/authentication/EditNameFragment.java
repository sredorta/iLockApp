package com.locker.ilockapp.authentication;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.locker.ilockapp.R;
import com.locker.ilockapp.abstracts.FragmentAbstract;
import com.locker.ilockapp.toolbox.Logs;

/**
 * Created by sredorta on 2/20/2017.
 */
public class EditNameFragment extends FragmentAbstract {
    public static final String FRAGMENT_INPUT_PARAM_USER_FIRST_NAME = "user.first_name.in";    //String
    public static final String FRAGMENT_INPUT_PARAM_USER_LAST_NAME = "user.last_name.in";
    public static final String FRAGMENT_OUTPUT_PARAM_USER_FIRST_NAME = "user.first_name.out";    //String
    public static final String FRAGMENT_OUTPUT_PARAM_USER_LAST_NAME = "user.last_name.out";

    private String firstName = "";
    private String lastName = "";


    public static EditNameFragment newInstance() {
        return new EditNameFragment();
    }

    // Constructor with input arguments
    public static EditNameFragment newInstance(Bundle data) {
        EditNameFragment fragment = EditNameFragment.newInstance();
        fragment.setArguments(data);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firstName = (String) getInputParam(EditNameFragment.FRAGMENT_INPUT_PARAM_USER_FIRST_NAME);
        lastName = (String) getInputParam(EditNameFragment.FRAGMENT_INPUT_PARAM_USER_LAST_NAME);
        //Get account details from Singleton either from intent or from account of the device
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile_edit_name, container, false);
        final EditText firstNameEditText = (EditText) v.findViewById(R.id.fragment_profile_edit_name_editText_first_name);
        if (!firstName.equals("")) firstNameEditText.setText(firstName);
        final EditText lastNameEditText = (EditText) v.findViewById(R.id.fragment_profile_edit_name_editText_last_name);
        if (!lastName.equals("")) lastNameEditText.setText(lastName);

        final Button submit = (Button) v.findViewById(R.id.fragment_profile_edit_name_button_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Send result to master fragment
                putOutputParam(FRAGMENT_OUTPUT_PARAM_USER_FIRST_NAME, firstNameEditText.getText().toString());
                putOutputParam(FRAGMENT_OUTPUT_PARAM_USER_LAST_NAME, lastNameEditText.getText().toString());
                sendResult(Activity.RESULT_OK);
                removeFragment(EditNameFragment.this,true);
            }
        });



        return v;
    }


}
