package com.locker.ilockapp.activity;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.locker.ilockapp.R;
import com.locker.ilockapp.abstracts.FragmentAbstract;
import com.locker.ilockapp.authentication.AccountGeneral;
import com.locker.ilockapp.authentication.AccountListFragment;
import com.locker.ilockapp.authentication.EditNameFragment;
import com.locker.ilockapp.authentication.LockerAuthenticator;
import com.locker.ilockapp.authentication.SignUpFragment;
import com.locker.ilockapp.authentication.User;
import com.locker.ilockapp.toolbox.ImageItem;
import com.locker.ilockapp.toolbox.Logs;

import org.w3c.dom.Text;

import java.io.FileDescriptor;
import java.io.IOException;

/**
 * Created by sredorta on 2/9/2017.
 */
public class MainFragment extends FragmentAbstract {
    //Input arguments definition they need to have pattern FRAGMENT_INPUT_PARAM_*
    public static final String FRAGMENT_INPUT_PARAM_USER = "user";    //String
    private static final int REQUEST_SELECT_PICTURE = 1;
    private static final int REQUEST_EDIT_NAME = 2;


    private String mAccountName;
    private User user;
    private  AccountGeneral myAccountGeneral;
    private View hiddenPanel;
    private View mView;
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        defineInputParams();
        defineOutputParams();
        getInputArgs();
        mAccountName = (String) getInputParam(MainFragment.FRAGMENT_INPUT_PARAM_USER);
        Logs.i("account : " + mAccountName);
        if (mAccountName == null || mAccountName.equals("")) {
            //
        } else {
            myAccountGeneral = new AccountGeneral(getContext());
            user = new User();
            user.initEmpty(getContext());
            user.setName(mAccountName);
            user.getDataFromDeviceAccount(myAccountGeneral.getAccount(user));
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user_details, container, false);
        mView =v;


            final TextView name = (TextView) v.findViewById(R.id.fragment_user_details_textView_name);
            name.setText(user.getFirstName()+ " " + user.getLastName());

            final TextView email = (TextView) v.findViewById(R.id.fragment_user_details_textView_email);
            email.setText(user.getEmail());

            final TextView phone = (TextView) v.findViewById(R.id.fragment_user_details_textView_phone);
            phone.setText(user.getPhone());

            final ImageView editAvatar = (ImageView) v.findViewById(R.id.fragment_user_details_edit_avatar);
            editAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Pop-up a bottom fragment
                    hiddenPanel = mActivity.findViewById(R.id.hidden_panel);
                    slideUpDown(mView);
                }
            });

            final ImageView editName = (ImageView) v.findViewById(R.id.fragment_user_details_edit_name);
            editName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(EditNameFragment.FRAGMENT_INPUT_PARAM_USER_FIRST_NAME, user.getFirstName());
                    bundle.putSerializable(EditNameFragment.FRAGMENT_INPUT_PARAM_USER_LAST_NAME, user.getLastName());
                    EditNameFragment fragment = EditNameFragment.newInstance(bundle);
                    fragment.setTargetFragment(MainFragment.this, REQUEST_EDIT_NAME);
                    replaceFragment(fragment,"test",false);
                }
            });





            final ImageView openGallery = (ImageView) v.findViewById(R.id.fragment_user_details_ImageView_gallery);
            openGallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // in onCreate or any event where your want the user to
                    // select a file
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_SELECT_PICTURE);
                }
            });


        return v;
    }

    public void slideUpDown(final View view) {
        if (hiddenPanel.getVisibility() == View.INVISIBLE || hiddenPanel.getVisibility() == View.GONE) {
            // Show the panel
            Animation bottomUp = AnimationUtils.loadAnimation(getContext(), R.anim.enter_from_bottom);

            hiddenPanel.startAnimation(bottomUp);
            hiddenPanel.setVisibility(View.VISIBLE);
        }
        else {
            // Hide the Panel
            Animation bottomDown = AnimationUtils.loadAnimation(getContext(), R.anim.exit_to_bottom);
            hiddenPanel.startAnimation(bottomDown);
            hiddenPanel.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        if (hiddenPanel.getVisibility() == View.VISIBLE) slideUpDown(mView);
        else {
            super.onBackPressed();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Reload our fragment
        replaceFragment(this, "test", true);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    ImageView avatar = (ImageView) mView.findViewById(R.id.fragment_user_details_avatar);
                    try {
                        Bitmap selectedBitmap = ImageItem.getBitmapFromUri(mActivity,selectedImageUri);
                        Integer dimension = ImageItem.getSquareCropDimensionForBitmap(selectedBitmap);
                        Bitmap myBitmap = ThumbnailUtils.extractThumbnail(selectedBitmap, dimension, dimension);
                        myBitmap = ImageItem.getRoundedBitmap(myBitmap);
                        avatar.setImageBitmap(myBitmap);
                        Logs.i("Width: " + myBitmap.getWidth());
                    } catch (IOException e) {
                        Logs.i("Caught exception: " + e);
                    }
                }
            } else if (requestCode == REQUEST_EDIT_NAME) {
                user.setFirstName(((String) data.getSerializableExtra(EditNameFragment.FRAGMENT_OUTPUT_PARAM_USER_FIRST_NAME)));
                user.setLastName(((String) data.getSerializableExtra(EditNameFragment.FRAGMENT_OUTPUT_PARAM_USER_LAST_NAME)));
                final TextView name = (TextView) mView.findViewById(R.id.fragment_user_details_textView_name);
                name.setText(user.getFirstName()+ " " + user.getLastName());

            }
        }

    }



}
