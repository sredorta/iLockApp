package com.locker.ilockapp.fragment_debug;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.locker.ilockapp.R;
import com.locker.ilockapp.abstracts.FragmentAbstract;
import com.locker.ilockapp.toolbox.Logs;

/**
 * Created by sredorta on 2/10/2017.
 */
public class FragmentB extends FragmentAbstract {
    //Input arguments definition
    public static final String FRAGMENT_INPUT_PARAM_ARG1 = "arg1";
    //Output arguments
    public static final String FRAGMENT_OUTPUT_PARAM_ARG1_OUT = "FragmentA.arg1_out";
    // Constructor without input arguments
    public static FragmentB newInstance() {
        return new FragmentB();
    }

    // Constructor with input arguments
    public static FragmentB newInstance(Bundle data) {
        FragmentB fragment = FragmentB.newInstance();
        fragment.setArguments(data);
        return fragment;
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_b, container, false);
        final Button myButton = (Button) v.findViewById(R.id.buttonFragmentB);
        Logs.i("We got the following input for ARG1:" + getInputParam(FRAGMENT_INPUT_PARAM_ARG1).toString());
        putOutputParam(FRAGMENT_OUTPUT_PARAM_ARG1_OUT, "This is what we return");
        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendResult(Activity.RESULT_OK);
                //Return to FragmentA
                removeFragment(FragmentB.this, true);  //From Abstract class
            }
        });


        return v;
    }
}

