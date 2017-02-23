package com.locker.ilockapp.authentication.profile.create;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.locker.ilockapp.R;
import com.locker.ilockapp.abstracts.FragmentAbstract;
import com.locker.ilockapp.authentication.AccountGeneral;
import com.locker.ilockapp.authentication.AccountListFragment;
import com.locker.ilockapp.authentication.EditNameFragment;
import com.locker.ilockapp.authentication.User;
import com.locker.ilockapp.toolbox.ImageItem;
import com.locker.ilockapp.toolbox.Logs;

import java.io.IOException;
import java.util.Locale;

/**
 * Created by sredorta on 2/21/2017.
 */
public class ProfileCreateStartFragment extends FragmentAbstract {
    private View mView;
    private static int REQUEST_DEFINE_LANGUAGE = 0;
    private static int REQUEST_DEFINE_NAMES = 1;
    private static int REQUEST_DEFINE_PHONE = 2;
    private static int REQUEST_DEFINE_EMAIL = 3;
    private static int REQUEST_DEFINE_PASSWORD = 4;
    private static int REQUEST_DEFINE_AVATAR = 5;

    private User myUser = new User();
    // Constructor
    public static ProfileCreateStartFragment newInstance() {
        return new ProfileCreateStartFragment();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.profile_create_start_fragment, container, false);
        mView =v;
        Button nextButton = (Button) v.findViewById(R.id.profile_create_start_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO pass input language
                ProfileCreateLanguageFragment fragment = ProfileCreateLanguageFragment.newInstance();
                fragment.setTargetFragment(ProfileCreateStartFragment.this, REQUEST_DEFINE_NAMES);
                //Now replace the AuthenticatorFragment with the SignInFragment
                replaceFragment(fragment,"test",true);  //This comes from abstract
            }
        });
        return v;
    }


    @Override
    public void onBackPressed() {
        //if (hiddenPanel.getVisibility() == View.VISIBLE) slideUpDown(mView);
       // else {
            super.onBackPressed();
        //}
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_DEFINE_LANGUAGE) {
                //myUser.setLanguage();
                ProfileCreateNamesFragment fragment = ProfileCreateNamesFragment.newInstance();
                fragment.setTargetFragment(ProfileCreateStartFragment.this, REQUEST_DEFINE_NAMES);
                //Now replace the AuthenticatorFragment with the SignInFragment
                replaceFragment(fragment,"test",true);  //This comes from abstract
            } else if (requestCode == REQUEST_DEFINE_NAMES) {
                myUser.setFirstName((String) data.getSerializableExtra(ProfileCreateNamesFragment.FRAGMENT_OUTPUT_PARAM_USER_FIRST_NAME));
                myUser.setLastName((String) data.getSerializableExtra(ProfileCreateNamesFragment.FRAGMENT_OUTPUT_PARAM_USER_LAST_NAME));
                myUser.print("This is what we have after names:");
                //We now start the fragment for entering phone details
                Bundle bundle = new Bundle();
                //bundle.putSerializable(ProfileCreatePhoneFragment.FRAGMENT_INPUT_PARAM_USER_PHONE_NUMBER,"+33623133212");
                ProfileCreatePhoneFragment fragment = ProfileCreatePhoneFragment.newInstance(bundle);
                fragment.setTargetFragment(ProfileCreateStartFragment.this, REQUEST_DEFINE_PHONE);
                //Now replace the AuthenticatorFragment with the SignInFragment
                replaceFragment(fragment,"test",true);  //This comes from abstract
            } else if( requestCode == REQUEST_DEFINE_PHONE) {
                myUser.setPhone((String) data.getSerializableExtra(ProfileCreatePhoneFragment.FRAGMENT_OUTPUT_PARAM_USER_PHONE_NUMBER));
                myUser.print("after phone :");
                ProfileCreateEmailFragment fragment = ProfileCreateEmailFragment.newInstance();
                fragment.setTargetFragment(ProfileCreateStartFragment.this, REQUEST_DEFINE_EMAIL);
                replaceFragment(fragment, "test", true);

            } else if( requestCode == REQUEST_DEFINE_EMAIL) {
                myUser.setEmail((String) data.getSerializableExtra(ProfileCreateEmailFragment.FRAGMENT_OUTPUT_PARAM_USER_EMAIL));
                myUser.print("after email :");
                ProfileCreatePasswordFragment fragment = ProfileCreatePasswordFragment.newInstance();
                fragment.setTargetFragment(ProfileCreateStartFragment.this, REQUEST_DEFINE_PASSWORD);
                replaceFragment(fragment, "test", true);
            } else if( requestCode == REQUEST_DEFINE_PASSWORD) {
                myUser.setPassword((String) data.getSerializableExtra(ProfileCreatePasswordFragment.FRAGMENT_OUTPUT_PARAM_USER_PASSWORD));
                myUser.print("after password :");
/*                ProfileCreateAvatarFragment fragment = ProfileCreateAvatarFragment.newInstance();
                fragment.setTargetFragment(ProfileCreateStartFragment.this, REQUEST_DEFINE_AVATAR);
                replaceFragment(fragment, "test", true);*/

            }

        } else {
            // Reload our fragment
            replaceFragment(this, "test", true);
        }

    }




}
