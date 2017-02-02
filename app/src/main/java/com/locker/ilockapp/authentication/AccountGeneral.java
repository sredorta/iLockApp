package com.locker.ilockapp.authentication;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.locker.ilockapp.dao.JsonItem;
import com.locker.ilockapp.dao.QueryPreferences;
import com.locker.ilockapp.toolbox.Logs;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sredorta on 1/31/2017.
 */
public class AccountGeneral {
    //////////////////////////////////////////////////////////////////////////////////////////////////
    public static final String ACCOUNT_TYPE = "com.locker.ilockapp.auth_locker";

    //Access types
    public static final String AUTHTOKEN_TYPE_STANDARD = "Standard access";
    public static final String AUTHTOKEN_TYPE_STANDARD_LABEL = "Standard access to a Locker account";

    public static final String AUTHTOKEN_TYPE_FULL = "Full access";
    public static final String AUTHTOKEN_TYPE_FULL_LABEL = "Full access to a Locker account";

    //Parameters for Intent
    public final static String ARG_ACCOUNT_NAME = "ACCOUNT_NAME";
    public final static String ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE";
    public final static String ARG_ACCOUNT_AUTH_TYPE = "AUTH_TYPE";
    public final static String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";

    public static final String KEY_ERROR_MESSAGE = "ERR_MSG";

    public static final ServerAuthenticate sServerAuthenticate = new LockerServerAuthenticate();
    /////////////////////////////////////////////////////////////////////////////////////////////////

    Context mContext;
    AccountManager mAccountManager;
    public User user;

    //Singleton definition
    private static AccountGeneral Instance = new AccountGeneral();

    private AccountGeneral() {}
    public static AccountGeneral getInstance() {
        return Instance;
    }

    //Initialize the singleton
    public void init(Context context) {
        mContext = context;
        mAccountManager = AccountManager.get(context);
        user = new User();

        user.setType(ACCOUNT_TYPE);
        user.setAuthType(AUTHTOKEN_TYPE_STANDARD);
        user.setId(null);

        //Try to restore an account from preferences
        user.setName(QueryPreferences.getPreference(context,QueryPreferences.PREFERENCE_ACCOUNT_NAME));

        //If there is one account in the device, we set all data from the device account
        if (getAccount() != null) {
            user.getDataFromDeviceAccount(mAccountManager, getAccount());
        }

    }

    public void resetUser() {
        User newUser = new User();
        newUser.setType(user.getType());
        newUser.setAuthType(user.getAuthType());
        //Try to restore an account from preferences
        newUser.setName(QueryPreferences.getPreference(mContext,QueryPreferences.PREFERENCE_ACCOUNT_NAME));

        user = newUser;
        user.print("After reset user :");
    }



    public AccountManager getAccountManager() {
        return mAccountManager;
    }


    // Gets the "current account" on the device matching our accountType and accountName
    public Account getAccount() {
        if (user.getName() == null)
            return null;

        //Find if there is an account with the correct accountName and get its token
        for (Account account : mAccountManager.getAccountsByType(user.getType())) {
            if (account.name.equals(user.getName())) {
                return account;
            }
        }
        return null;
    }

    //Returns all accounts of our type
    public Account[] getAccounts() {
        for (Account account : mAccountManager.getAccountsByType(user.getType())) {
            Logs.i("Found account : " + account.name);
        }
        //Find if there is an account with the correct accountName and get its token
        return mAccountManager.getAccountsByType(user.getType());

    }

    //Returns count of accounts of our type
    public Integer getAccountsCount() {
        return getAccounts().length;
    }

    // Creates an account on the Device
    public Boolean createAccount() {
        Account account;
        this.user.print("Creating account with following data:");
        if (getAccount() == null) {
            account = new Account(user.getName(), user.getType());
            mAccountManager.addAccountExplicitly(account, user.getPassword(), null);
        } else {
            Logs.i("Account existing, skip creation...");
            account = getAccount();
        }
        user.setDataToDeviceAccount(mAccountManager,account);
        if (account == null)
            return false;
        else
            return true;
    }

    //Creates the Server and Device account and exits activity if successfull
    public void createServerAndDeviceAccount(final Activity activity) {

        new AsyncTask<Void, Void, Intent>() {
            @Override
            protected Intent doInBackground(Void... params) {
                Bundle data = new Bundle();
                JsonItem item = sServerAuthenticate.userSignUp();
                if(!item.getResult()) {
                    data.putString(KEY_ERROR_MESSAGE, item.getMessage());
                } else {
                    Logs.i("Creating now the account on the device !", this.getClass());
                    if (!createAccount()) {
                        //We could not create the device account so removing the server account
                        sServerAuthenticate.userRemove();
                        Logs.i("Removing server account as we could not create device account !", this.getClass());
                        data.putString(KEY_ERROR_MESSAGE, "Could not create device account !");
                    }
                }

                //Settings for the Account AuthenticatorActivity
                data.putString(AccountManager.KEY_ACCOUNT_NAME, user.getName());
                data.putString(AccountManager.KEY_ACCOUNT_TYPE, user.getType());
                data.putString(AccountManager.KEY_AUTHTOKEN, user.getToken());

                final Intent res = new Intent();
                res.putExtras(data);
                return res;
            }

            @Override
            protected void onPostExecute(Intent intent) {

                if (intent.hasExtra(KEY_ERROR_MESSAGE))
                    Toast.makeText(activity.getBaseContext(), intent.getStringExtra(KEY_ERROR_MESSAGE), Toast.LENGTH_SHORT).show();
                else {
                    //Save the account created in the preferences (all except critical things)
                    //Finish and send to AuthenticatorActivity that we where successfull
                    activity.setResult(Activity.RESULT_OK, intent);
                    activity.finish();
                }
            }
        }.execute();
    }

    //Submits credentials to the server and exits activity if successfull
    public void submitCredentials(final Activity activity) {

        //Update the singleton
        User myUser = new User();
        myUser.setAuthType(user.getAuthType());
        myUser.setType(user.getType());
        myUser.setPassword(user.getPassword());
        //We set whatever is set in the user ID
        if (user.getName() != null) myUser.setName(user.getName());
        if (user.getEmail() != null) myUser.setEmail(user.getEmail());
        if (user.getPhone() != null) myUser.setId(user.getPhone());
        if (user.getId() != null) myUser.setId(user.getId());           //If we have Id we use it with priority
        //Refresh user with the only data we have from inputs
        user = myUser;
        user.print("User details for submit:");

        new AsyncTask<Void, Void, Void>() {
            JsonItem item;
            @Override
            protected Void doInBackground(Void... params) {
                Logs.i("Started authenticating", this.getClass());
                //If we have an account we try to create a new session and get the new token sending ID + password
                //The server function directly updates the singleton token,id fields
                item = sServerAuthenticate.userSignIn();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                // If result is bad or no token display the message of error from the server
                if (!item.getResult() || user.getToken() == null) {
                    Toast.makeText(activity.getBaseContext(), item.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    // If we could sign in
                    user.setName(user.getEmail());

                    if (getAccount()==null) {
                        createAccount();
                    }
                    user.setDataToDeviceAccount(mAccountManager, getAccount());
                    Bundle data = new Bundle();
                    //Settings for the Account AuthenticatorActivity
                    data.putString(AccountManager.KEY_ACCOUNT_NAME, user.getName());
                    data.putString(AccountManager.KEY_ACCOUNT_TYPE, user.getType());
                    data.putString(AccountManager.KEY_AUTHTOKEN, user.getToken());
                    final Intent res = new Intent();
                    res.putExtras(data);
                    activity.setResult(Activity.RESULT_OK, res);
                    activity.finish();
                }
            }
        }.execute();
    }



























    //Remove account on the Device
    public Boolean removeAccount() {
        Logs.i("Removing account name: " + user.getName());

        if (getAccount() == null)
            return false;

        Boolean isDone = false;

        if (Build.VERSION.SDK_INT<22) {
            @SuppressWarnings("deprecation")
            final AccountManagerFuture<Boolean> booleanAccountManagerFuture = mAccountManager.removeAccount(getAccount(), null, null);
            try {
                isDone = booleanAccountManagerFuture.getResult(1, TimeUnit.SECONDS);
            } catch (OperationCanceledException e) {
                Logs.i("Caught exception : " + e);
            } catch (IOException e) {
                Logs.i("Caught exception : " + e);
            } catch (AuthenticatorException e) {
                Logs.i("Caught exception : " + e);
            }
        } else
            isDone = mAccountManager.removeAccountExplicitly(getAccount());
        if (isDone) {
            Logs.i("Successfully removed account ! ", AccountGeneral.class);
        }
        return isDone;
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Checker for inputs
    //Check that password meets the required format
    //  8 chars min
    //  2 numbers min
    //  2 lowercase chars min
    //  2 upercase chars min
    public boolean checkPasswordInput(String password) {
        if (password == null) return false;
        //Check the length
        if (password.length() < 8) {
            return false;
        }

        //Check that contains at least 2 numbers
        Pattern r;
        Matcher m;
        r = Pattern.compile("[0-9]");
        m = r.matcher(password);
        int count = 0;
        while (m.find()) count++;
        if (count < 2) return false;

        //Check that at least 2 lowercase characters
        r = Pattern.compile("[a-z]");
        m = r.matcher(password);
        count = 0;
        while (m.find()) count++;
        if (count < 2) return false;

        //Check that at least 2 uppercase characters
        r = Pattern.compile("[A-Z]");
        m = r.matcher(password);
        count = 0;
        while (m.find()) count++;
        if (count < 2) return false;
        return true;
    }

    //Check that email meets the required format
    //  @ must exist
    //  . must exist
    //  8 chars min
    public boolean checkEmailInput(String email) {
        if (email == null) return false;
        //Check the length
        if (email.length() < 8) {
            return false;
        }

        //Check that contains at least 2 numbers
        Pattern r;
        Matcher m;
        r = Pattern.compile("@");
        m = r.matcher(email);
        int count = 0;
        while (m.find()) count++;
        if (count != 1) return false;

        //Check that at least 2 lowercase characters
        r = Pattern.compile("\\.[a-z]+$");
        m = r.matcher(email);
        if (!m.find()) {
            Logs.i("Could not found .com !", AccountGeneral.class);
            return false;
        }
        return true;
    }

    //Check that phone meets the required format
    //  8 numbers min
    //  only numbers
    public boolean checkPhoneInput(String number) {
        if (number == null) return false;
        //Check the length
        if (number.length() < 8) {
            return false;
        }

        //Check that contains at least 2 numbers
        Pattern r;
        Matcher m;
        r = Pattern.compile("[0-9]");
        m = r.matcher(number);
        int count = 0;
        while (m.find()) count++;
        if (count != number.length()) return false;

        return true;
    }
    public boolean checkEmailOrPhoneInput(String email_or_phone) {
        if (this.checkEmailInput(email_or_phone) || this.checkPhoneInput(email_or_phone)) {
            return true;
        } else {
            return false;
        }
    }



}
