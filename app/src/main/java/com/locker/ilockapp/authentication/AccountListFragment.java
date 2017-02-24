package com.locker.ilockapp.authentication;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.locker.ilockapp.R;
import com.locker.ilockapp.dao.QueryPreferences;
import com.locker.ilockapp.abstracts.FragmentAbstract;
import com.locker.ilockapp.toolbox.ImageItem;
import com.locker.ilockapp.toolbox.Logs;

import java.util.ArrayList;
import java.util.List;

import static com.locker.ilockapp.authentication.User.*;
/**
 * Created by sredorta on 1/24/2017.
 */
public class AccountListFragment extends FragmentAbstract {
        private AccountListAdapter mAdapter;
        private RecyclerView mAccountsRecycleView;
        private List<Account> mAccounts = new ArrayList<>();
        AccountGeneral myAccountGeneral;
        User user;
        private boolean mUpdatePostitions;
        private final int REQ_SIGNIN = 1;
        public static final String FRAGMENT_INPUT_PARAM_USER = "current_user";    //String
        public static final String FRAGMENT_INPUT_PARAM_UPDATE_POSITIONS = "update_positions";
        public static final String FRAGMENT_OUTPUT_PARAM_SELECTED_USER = "selected_user";    //String


        // Constructor
        public static AccountListFragment newInstance() {
            return new AccountListFragment();
        }

        // Constructor with input arguments
        public static AccountListFragment newInstance(Bundle data) {
            AccountListFragment fragment = AccountListFragment.newInstance();
            fragment.setArguments(data);
            return fragment;
        }

        public AccountListAdapter getAdapter() {return mAdapter;}

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            myAccountGeneral = new AccountGeneral(getContext());
            user = new User();
            user.initEmpty(getContext());
            user.setName((String) getInputParam(AccountListFragment.FRAGMENT_INPUT_PARAM_USER));
            mUpdatePostitions = (boolean) getInputParam(AccountListFragment.FRAGMENT_INPUT_PARAM_UPDATE_POSITIONS);
        }


    @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_account_list, container, false);
            mAccountsRecycleView = (RecyclerView) v.findViewById(R.id.account_list_recycle_view);
            mAccountsRecycleView.setLayoutManager(new LinearLayoutManager(mActivity));
            //mAccountsRecycleView.addItemDecoration(new DividerItemDecoration(getActivity(), 1));
            updateUI();
            return v;
        }
    //Updates the recycleview
        private void updateUI() {
            AccountManager mAccountManager;
            mAccountManager = AccountManager.get(mActivity.getApplicationContext());
            for (Account account : myAccountGeneral.getAccounts()) {
                mAccounts.add(account);
            }
            //Do the swap to make sure that we start with last login as first element
            if (mUpdatePostitions) {
                Account myAccount;
                if (user.getName() != null)
                    for (Account account : myAccountGeneral.getAccounts()) {
                        if (user.getName().equals(mAccountManager.getUserData(account, PARAM_USER_ACCOUNT_NAME))) {
                            int index = mAccounts.indexOf(account);
                            if (index != 0) {
                                myAccount = mAccounts.get(0);
                                mAccounts.set(0, account);
                                mAccounts.set(index, myAccount);
                            }
                            break;
                        }
                    }
            }
            mAdapter = new AccountListAdapter(mAccounts);
            mAccountsRecycleView.setAdapter(mAdapter);

        }



    private class AccountListHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Account mAccount;
        private TextView mUserFullNameTextView;
        private TextView mAccountNameTextView;
        private ImageView mAvatarImageView;
        public ImageView buttonViewOption;
        public ImageView buttonActiveView;

        private AccountListHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mUserFullNameTextView = (TextView) itemView.findViewById(R.id.fragment_account_textView_user);
            mAccountNameTextView =  (TextView) itemView.findViewById(R.id.fragment_account_textView_account);
            mAvatarImageView = (ImageView) itemView.findViewById(R.id.fragment_account_imageView_avatar);
            buttonViewOption = (ImageView) itemView.findViewById(R.id.fragment_account_imageView_more);
            buttonActiveView = (ImageView) itemView.findViewById(R.id.fragment_account_imageView_active);

        }


        @Override
        public void onClick(View view) {
            //We need to update the user with the account data that has been selected
            user.getDataFromDeviceAccount(mAccount);
            mAdapter.notifyDataSetChanged();
            //Send result to master fragment
            putOutputParam(FRAGMENT_OUTPUT_PARAM_SELECTED_USER, user.getName());
            sendResult(Activity.RESULT_OK);
        }

        private void deleteItem() {
            mAccounts.remove(getAdapterPosition());
            //After removing one account we select the upper on the list
            if (mAccounts.size()>0) {

                mAccount = mAccounts.get(0);
                user.getDataFromDeviceAccount(mAccount);
                Logs.i("We are here !!!!!!" + user.getName());
                putOutputParam(FRAGMENT_OUTPUT_PARAM_SELECTED_USER,user.getName());
                sendResult(Activity.RESULT_OK);
                mAdapter.notifyDataSetChanged();
            } else {
                //If there are no accounts left we start the sign-in activity
                //Change fragment as we have removed all accounts
                Logs.i("Removed latest account:");
                SignInFragment fragment = SignInFragment.newInstance();
                //Now replace the AuthenticatorFragment with the SignInFragment
                replaceFragment(fragment,"test",true);  //This comes from abstract
            }
        }

        public void bindAccount(Account account, AccountListHolder holder ) {
            AccountManager mAccountManager;
            mAccountManager = AccountManager.get(mActivity.getApplicationContext());

            mAccountManager.getUserData(account, PARAM_USER_EMAIL);
            String fullName = mAccountManager.getUserData(account, PARAM_USER_FIRST_NAME);
            fullName = fullName + " " + mAccountManager.getUserData(account, PARAM_USER_LAST_NAME);
            mAccount = account;
            mUserFullNameTextView.setText(fullName);
            mAccountNameTextView.setText(mAccountManager.getUserData(account, PARAM_USER_EMAIL));
            ImageItem imageItem = new ImageItem();
            imageItem.setStream(mAccountManager.getUserData(account,PARAM_USER_AVATAR));
            Bitmap bmp = imageItem.getBitmap();
            if (bmp == null) {
                mAvatarImageView.setImageResource(R.drawable.user_default);
                Logs.i("Bitmap was null !!!!");
            } else
                mAvatarImageView.setImageBitmap(bmp);
            //Define color for active or not active account (last log-in)
            if (user.getName()!= null) {
                if (user.getName().equals(mAccountManager.getUserData(account, PARAM_USER_ACCOUNT_NAME))) {
                    //itemView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.md_green_A700));
                    itemView.setBackground(getResources().getDrawable(R.drawable.view_rounded_selected, null));
                    buttonViewOption.setEnabled(true);
                } else {
                    buttonViewOption.setEnabled(false);
                    //itemView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.md_green_200));
                    itemView.setBackground(getResources().getDrawable(R.drawable.view_rounded_unselected, null));
                }
            }

            //Handle here the options menu of each account
            buttonViewOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //creating a popup menu
                    PopupMenu popup = new PopupMenu(getContext(), buttonViewOption);
                    //inflating menu from xml resource
                    popup.inflate(R.menu.options_menu_account_item);
                    //adding click listener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.options_menu_account_item_edit:
                                    Intent i = new Intent(mActivity.getBaseContext(), QueryPreferences.class);
                                    startActivity(i);
                                    //handle menu1 click
                                    break;
                                case R.id.options_menu_account_item_remove:
                                    myAccountGeneral.removeAccount(user);
                                    deleteItem();
                                    break;
                            }
                            return false;
                        }
                    });
                    //displaying the popup
                    popup.show();
                }
            });
        }
    }

    private class AccountListAdapter extends RecyclerView.Adapter<AccountListHolder> {

        public AccountListAdapter(List<Account> accounts) {
            mAccounts = accounts;
        }

        @Override
        public AccountListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(mActivity);
            View view = layoutInflater.inflate(R.layout.fragment_account_display, parent,false);
            return new AccountListHolder(view);
        }

        @Override
        public void onBindViewHolder(AccountListHolder holder, int position) {
            Account account = mAccounts.get(position);
            holder.bindAccount(account,holder);
        }

        @Override
        public int getItemCount() {
            return mAccounts.size();
        }

    }

    //When we come back from new account creation we fall here
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // The sign up activity returned that the user has successfully created an account
        if (requestCode == REQ_SIGNIN && resultCode == Activity.RESULT_OK) {
            mActivity.setResult(Activity.RESULT_OK, data);
            mActivity.finish();
        } else
            super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();
        mAccounts = new ArrayList<>();
        updateUI();
    }


}


