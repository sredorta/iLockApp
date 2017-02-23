package com.locker.ilockapp.abstracts;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.AnimRes;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.locker.ilockapp.R;
import com.locker.ilockapp.toolbox.Logs;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sredorta on 2/23/2017.
 */
public class DialogAbstract extends DialogFragment implements OnBackPressed {
    //Container where the fragment needs to be expanded
    private @IdRes
    int mContainer = R.id.fragment_container;

    //Animation for transitions
    private @AnimRes
    int mAnimEnter     = R.anim.enter_from_right;
    private @AnimRes int mAnimExit      = R.anim.exit_to_right;
    private @AnimRes int mAnimPopEnter  = R.anim.enter_from_left;
    private @AnimRes int mAnimPopExit   = R.anim.exit_to_right;

    //Define if addToBackStack is required
    private boolean mAddToBackStack = true;
    protected FragmentActivity mActivity;



    //Map for parsing input parameters
    private Map<String,Object> inputParams;

    //Map for parsing output parameters
    private Map<String,Object> outputParams;

    //View for access
    private View mView;

    private void putInputParam( String key, Object value ) {
        inputParams.put( key, value );
    }

    //Get the value of an input parameter (needs to be casted to right type)
    public Object getInputParam( String key ) {
        return inputParams.get(key);
    }

    private List<Field> getAllModelFields(Class aClass) {
        List<Field> fields = new ArrayList<>();
        do {
            Collections.addAll(fields, aClass.getDeclaredFields());
            aClass = aClass.getSuperclass();
        } while (aClass != null);
        return fields;
    }

    //We initialize the inputParams HashMap with empty Strings just in case
    public void defineInputParams() {
        Pattern p = Pattern.compile("^FRAGMENT_INPUT_PARAM.*");
        Matcher m;

        List<Field> fields = getAllModelFields(getClass());
        Map<String,Object> temp = new HashMap<String,Object>();
        for (Field field : fields) {
            m = p.matcher(field.getName());
            if (m.matches()) {
                try {
                    temp.put(field.get(field.getName()).toString(), null);
                    Logs.i("Found input parameter : " + field.getName());
                } catch (IllegalAccessException e) {
                    Logs.i("Caught exception: " + e);
                }
            }
        }
        inputParams = temp;
    }

    //We initialize the inputParams HashMap with empty Strings just in case
    public void defineOutputParams() {
        Pattern p = Pattern.compile("^FRAGMENT_OUTPUT_PARAM.*");
        Matcher m;

        List<Field> fields = getAllModelFields(getClass());
        Map<String,Object> temp = new HashMap<String,Object>();
        for (Field field : fields) {
            m = p.matcher(field.getName());
            if (m.matches()) {
                try {
                    temp.put(field.get(field.getName()).toString(), new String(""));
                } catch (IllegalAccessException e) {
                    Logs.i("Caught exception: " + e);
                }
            }
        }
        outputParams = temp;
    }

    //Sets the parameter as response
    public void putOutputParam( String key, Object value ) {
        outputParams.put( key, value );
    }


    //Send result to the calling Fragment
    public void sendResult(int resultCode) {
        if(getTargetFragment() == null) {
            return;
        }
        Intent intent = new Intent();
        for (String key : outputParams.keySet()) {
            Object value = outputParams.get(key);
            Logs.i("Sending argument : " + key + " : " + value.toString());
            intent.putExtra(key, (Serializable) value);
/*            switch (value.getClass().getSimpleName()) {
                case "String":
                    intent.putExtra(key, (String)  value);
                    break;
                case "Integer":
                    intent.putExtra(key, (Integer)  value);
                    break;
                case "Boolean":
                    intent.putExtra(key, (Boolean)  value);
                    break;
                default:
                    intent.putExtra(key, (String) value);
            }*/
        }
        getTargetFragment().onActivityResult(getTargetRequestCode(),resultCode, intent);
    }

    //Parsing of input arguments and setting inputParams
    public void getInputArgs() {
        if (getArguments() != null) {
            Set<String> keys = getArguments().keySet();
            for (String key : keys) {
                Object o = getArguments().getSerializable(key);
                putInputParam(key, o);
                if (o != null) Logs.i("Got input argument : " + key + " : " + o.toString());
            }
        }
    }

    //Sets the fragment container
    public void setContainer(@IdRes int container) {
        Logs.i("Set container to another id");
        mContainer = container;
    }

    public void setAddToBackStack(boolean add) {
        mAddToBackStack = add;
    }



    //To store the activity holding the fragment... and avoid nulls when transaction has not been completed
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (FragmentActivity) getActivity();
    }

    //Sets the fragment animations
    protected void setAnimations(@AnimRes int enter, @AnimRes int exit, @AnimRes int popenter, @AnimRes int popexit) {
        mAnimEnter = enter;
        mAnimExit = exit;
        mAnimPopEnter = popenter;
        mAnimPopExit = exit;
    }

/*    //Setter for the current view
    protected void setView(View view) {
        mView = view;
    }

    //Getter for the current view
    protected View getView() { return mView;}
  */


    //Replace container with new fragment
    public void replaceFragment(Fragment fragment, String tag, boolean animation){
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        if (animation)
            transaction.setCustomAnimations(mAnimEnter, mAnimExit, mAnimPopEnter, mAnimPopExit);
        transaction.replace(mContainer, fragment);
        if (mAddToBackStack)
            transaction.addToBackStack(tag);
        transaction.commit();
    }

    //Remove a fragment
    public void removeFragment(Fragment fragment, boolean animation) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        if (animation)
            transaction.setCustomAnimations(mAnimEnter, mAnimExit, mAnimPopEnter, mAnimPopExit);
        transaction.remove(fragment);
        transaction.commit();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Initialize all input arguments
        defineInputParams();
        defineOutputParams();
        getInputArgs();
    }


    @Override
    public void onBackPressed() {
        Log.i("SERGI", "OnBackPressed was done, and we are now sending RESULT_CANCELED");
        sendResult(Activity.RESULT_CANCELED);
        //  removeFragment(this, true);
    }

    //Hide input keyboard
    public void hideInputKeyBoard() {
        // Check if no view has focus:
        if (mActivity != null) {
            View view = mActivity.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }




}
