package com.locker.ilockapp.fragment_debug;

import android.app.Activity;
import android.content.Intent;
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
public class FragmentA extends FragmentAbstract {
    //Input arguments definition they need to have pattern FRAGMENT_INPUT_PARAM_*
    public static final String FRAGMENT_INPUT_PARAM_ARG1 = "arg1";    //String
    public static final String FRAGMENT_INPUT_PARAM_ARG2 = "arg2";    //Integer
    public static final String FRAGMENT_INPUT_PARAM_TEST = "arg3";    //Integer
    //Requests to other fragments
    private static final int REQUEST_1 = 0;




    // Constructor without input arguments
    public static FragmentA newInstance() {
        return new FragmentA();
    }

    // Constructor with input arguments
    public static FragmentA newInstance(Bundle data) {
        FragmentA fragment = FragmentA.newInstance();
        fragment.setArguments(data);
        return fragment;
    }




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_a, container, false);
        final Button myButton = (Button) v.findViewById(R.id.buttonFragmentA);
        if (getInputParam(FRAGMENT_INPUT_PARAM_TEST) == null )
            Logs.i ("it is null");
        else
          Logs.i("We got the value TEST : " + getInputParam(FRAGMENT_INPUT_PARAM_TEST).toString());
        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(FragmentB.FRAGMENT_INPUT_PARAM_ARG1,"test_to_b");
                FragmentB fragment = FragmentB.newInstance(bundle);
                fragment.setTargetFragment(FragmentA.this, REQUEST_1);
                //Now replace the FragmentA with FragmentB
                replaceFragment(fragment,"test",true);  //This comes from abstract
            }
        });

        return v;
    }

    //Get back arguments from FragmentA
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode!= Activity.RESULT_OK)
            return;
        if (requestCode == REQUEST_1) {
            String test = (String) data.getSerializableExtra(FragmentB.FRAGMENT_OUTPUT_PARAM_ARG1_OUT);
            Logs.i("FRAGMENT_A:: Recieved ARG1 from FragmentB = " + test);
            //Reload FragmentA
            replaceFragment(FragmentA.this,"test",true);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
