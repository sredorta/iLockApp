package com.locker.ilockapp.authentication;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.locker.ilockapp.R;
import com.locker.ilockapp.dao.QueryPreferences;
import com.locker.ilockapp.toolbox.Logs;

import java.util.ArrayList;
import java.util.List;

import static com.locker.ilockapp.authentication.AccountGeneral.*;
import static com.locker.ilockapp.authentication.User.*;
/**
 * Created by sredorta on 1/24/2017.
 */
public class AccountListFragment extends Fragment {
        private AccountListAdapter mAdapter;
        private RecyclerView mAccountsRecycleView;
        private List<Account> mAccounts = new ArrayList<>();
        AccountGeneral myAccountGeneral;
        User user;
        public static User myUserSelected = new User();

        private final int REQ_SIGNIN = 1;

        // Constructor
        public static AccountListFragment newInstance() {
            return new AccountListFragment();
        }

        public AccountListAdapter getAdapter() {return mAdapter;}

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            myAccountGeneral = new AccountGeneral(getContext());
            user = new User();
            user.init(getContext());
        }


    @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_account_list, container, false);
            mAccountsRecycleView = (RecyclerView) v.findViewById(R.id.account_list_recycle_view);
            mAccountsRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));

            updateUI();
            return v;
        }
        private void updateUI() {
            AccountManager mAccountManager;
            mAccountManager = AccountManager.get(getActivity().getApplicationContext());
            for (Account account : myAccountGeneral.getAccounts()) {
                mAccounts.add(account);
            }
            //Do the swap to make sure that we start with last login as first element
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
            myUserSelected = user;
        }

        private void deleteItem() {
            mAccounts.remove(getAdapterPosition());
            mAdapter.notifyItemRemoved(getAdapterPosition());
            mAdapter.notifyItemRangeChanged(getAdapterPosition(), mAccounts.size());
            //If there are no accounts left we start the sign-in activity
            if (mAccounts.size() == 0) {
                Logs.i("Removed latest account:");
                Intent signin = new Intent(getActivity().getBaseContext(), SignInActivity.class);
                //Forward extras if necessary
                if (getActivity().getIntent().getExtras() != null) {
                    signin.putExtras(getActivity().getIntent().getExtras());
                    Logs.i("When starting signup extras where found !", this.getClass());
                } else {
                    Logs.i("When starting signup no extras found !", this.getClass());
                }
                startActivityForResult(signin, REQ_SIGNIN);
                //getActivity().finish();
            }
        }

        public void bindAccount(Account account, AccountListHolder holder ) {
            AccountManager mAccountManager;
            mAccountManager = AccountManager.get(getActivity().getApplicationContext());

            mAccountManager.getUserData(account, PARAM_USER_EMAIL);
            String fullName = mAccountManager.getUserData(account, PARAM_USER_FIRST_NAME);
            fullName = fullName + " " + mAccountManager.getUserData(account, PARAM_USER_LAST_NAME);
            mAccount = account;
            mUserFullNameTextView.setText(fullName);
            mAccountNameTextView.setText(mAccountManager.getUserData(account, PARAM_USER_EMAIL));
            mAvatarImageView.setImageResource(R.drawable.user_default);
            //Define color for active or not active account (last log-in)
            user.print("Before crash :");
            if (user.getName()!= null) {
                if (user.getName().equals(mAccountManager.getUserData(account, PARAM_USER_ACCOUNT_NAME))) {
                    itemView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                    buttonViewOption.setEnabled(true);
                } else {
                    buttonViewOption.setEnabled(false);
                    itemView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
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
                                    Intent i = new Intent(getActivity().getBaseContext(), QueryPreferences.class);
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
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
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
        Logs.i("onActivityResult", this.getClass());
        // The sign up activity returned that the user has successfully created an account
        if (requestCode == REQ_SIGNIN && resultCode == Activity.RESULT_OK) {
            getActivity().setResult(Activity.RESULT_OK, data);
            getActivity().finish();
        } else
            super.onActivityResult(requestCode, resultCode, data);
    }


}


