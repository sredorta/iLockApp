package com.locker.ilockapp.authentication.profile.create;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.locker.ilockapp.Internationalization.Internationalization;
import com.locker.ilockapp.Internationalization.LanguagePickerFragment;
import com.locker.ilockapp.R;
import com.locker.ilockapp.abstracts.FragmentAbstract;
import com.locker.ilockapp.authentication.EditNameFragment;
import com.locker.ilockapp.toolbox.ImageItem;
import com.locker.ilockapp.toolbox.Logs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Locale;

/**
 * Created by sredorta on 2/23/2017.
 */
public class ProfileCreateAvatarFragment extends FragmentAbstract {
    private View mView;
    private static int REQUEST_AVATAR = 0;
    private static final int REQUEST_SELECT_PICTURE = 1;
    private static final int REQUEST_TAKE_PICTURE = 2;

    public static final String FRAGMENT_INPUT_PARAM_USER_AVATAR = "user.avatar.in"; //Stream in
    public static final String FRAGMENT_OUTPUT_PARAM_USER_AVATAR = "user.avatar.out"; //Stream out

    private ImageItem imageItem;
    private View hiddenPanel;
    private Bitmap mAvatar;                     //Current country Locale of the phone number
    private File mPhotoFile;                    //Stores a photo of avatar if we take photo

    // Constructor
    public static ProfileCreateAvatarFragment newInstance() {
        return new ProfileCreateAvatarFragment();
    }
    // Constructor with input arguments
    public static ProfileCreateAvatarFragment newInstance(Bundle data) {
        ProfileCreateAvatarFragment fragment = ProfileCreateAvatarFragment.newInstance();
        fragment.setArguments(data);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageItem = new ImageItem();

        //Get the input BASE64 encoded String and convert into bitmap
        imageItem.setStream((String) getInputParam(ProfileCreateAvatarFragment.FRAGMENT_INPUT_PARAM_USER_AVATAR));
        mAvatar = imageItem.getBitmap();
        //In case we could not parse the phone or it was empty
        if (mAvatar == null) {
            mAvatar = BitmapFactory.decodeResource(getResources(), R.drawable.user_default);
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.profile_create_avatar_fragment, container, false);
        mView = v;

        final ImageView avatar = (ImageView) mView.findViewById(R.id.profile_create_avatar_imageView_avatar);
        avatar.setImageBitmap(mAvatar);

        final CardView cardView = (CardView) v.findViewById(R.id.profile_create_avatar_cardView);
        hiddenPanel = (View) v.findViewById(R.id.profile_create_avatar_hidden_panel);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slideUpDown(hiddenPanel);
                //Here we pop-up the bottom side where we can choose between gallery/picture...
            }
        });



        final Button nextButton = (Button) v.findViewById(R.id.profile_create_avatar_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //putOutputParam(FRAGMENT_OUTPUT_PARAM_USER_PHONE_COUNTRY, mLocale);
                imageItem.setStream(mAvatar);
                putOutputParam(FRAGMENT_OUTPUT_PARAM_USER_AVATAR, imageItem.getStreamString());
                sendResult(Activity.RESULT_OK);
                // Remove our fragment
                removeFragment(ProfileCreateAvatarFragment.this, true);  //This comes from abstract
            }
        });

        //Handle reset Avatar
        final ImageView removeAvatar = (ImageView) v.findViewById(R.id.profile_create_avatar_imageView_delete);
        removeAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView avatar = (ImageView) mView.findViewById(R.id.profile_create_avatar_imageView_avatar);
                mAvatar = BitmapFactory.decodeResource(getResources(), R.drawable.user_default);
                fadeInNewBitmap(mAvatar);
            }
        });

        //Handle open galery
        final ImageView openGallery = (ImageView) v.findViewById(R.id.profile_create_avatar_imageView_gallery);
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

        //Handle take photo
        final ImageView openCamera = (ImageView) v.findViewById(R.id.profile_create_avatar_imageView_photo);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mPhotoFile = getPhotoFile();
        final boolean canTakePhoto = mPhotoFile != null && captureImage.resolveActivity(mActivity.getPackageManager()) != null;
        openCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // check if we can open photo
                if (canTakePhoto) {
                    final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    Uri uri = Uri.fromFile(mPhotoFile);
                    captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    startActivityForResult(captureImage, REQUEST_TAKE_PICTURE);
                }
            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    ImageView avatar = (ImageView) mView.findViewById(R.id.profile_create_avatar_imageView_avatar);
                    Bitmap myBitmap = null;
                    try {
                        Bitmap selectedBitmap = ImageItem.getBitmapFromUri(mActivity, selectedImageUri);
                        Integer dimension = ImageItem.getSquareCropDimensionForBitmap(selectedBitmap);
                        myBitmap = ThumbnailUtils.extractThumbnail(selectedBitmap, dimension, dimension);
                        myBitmap = ImageItem.rotateImage(myBitmap,selectedImageUri, getContext(),mActivity);
                        slideUpDown(hiddenPanel);
                    } catch (IOException e) {
                        Logs.i("Caught exception: " + e);
                    }
                    if (myBitmap != null) {
                        mAvatar = myBitmap;
                        fadeInNewBitmap(mAvatar);
                    }
                }
            } else if (requestCode == REQUEST_TAKE_PICTURE) {
                    Logs.i("We are back from picture !");
                    ImageView avatar = (ImageView) mView.findViewById(R.id.profile_create_avatar_imageView_avatar);
                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    Bitmap myBitmap = BitmapFactory.decodeFile(mPhotoFile.getAbsolutePath(),bmOptions);
                    Integer dimension = ImageItem.getSquareCropDimensionForBitmap(myBitmap);
                    myBitmap = ThumbnailUtils.extractThumbnail(myBitmap,dimension,dimension);
                    myBitmap = ImageItem.rotateImage(myBitmap,mPhotoFile.getAbsolutePath());
                    if (myBitmap != null) {
                        mAvatar = myBitmap;
                        fadeInNewBitmap(mAvatar);
                    }
                    slideUpDown(hiddenPanel);
            }
        }

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

    public File getPhotoFile() {
        File externalFilesDir = mActivity.getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (externalFilesDir == null) {
            return null;
        } else {
            return new File(externalFilesDir, "IMG_profile.jpg");
        }
    }

    private void fadeInNewBitmap(final Bitmap bitmap) {
        final ImageView avatar = (ImageView) mView.findViewById(R.id.profile_create_avatar_imageView_avatar);
        Animation fadeIn;

        fadeIn = AnimationUtils.loadAnimation(getContext(),R.anim.enter_fade);
        fadeIn.setDuration(1000);
        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                avatar.setImageBitmap(bitmap);
            }
            @Override
            public void onAnimationEnd(Animation animation) {}
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        avatar.startAnimation(fadeIn);
    }

}

