package com.locker.ilockapp.authentication;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.locker.ilockapp.dao.JsonItem;
import com.locker.ilockapp.dao.QueryPreferences;
import com.locker.ilockapp.toolbox.Logs;
import com.locker.ilockapp.toolbox.Toolbox;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
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


    public AccountGeneral(Context context) {
        mContext = context;
        mAccountManager = AccountManager.get(context);
    }


    //Returns the Account manager
    public AccountManager getAccountManager() {
        return mAccountManager;
    }


    // Gets the "current account" on the device matching our accountType and accountName
    public Account getAccount(User user) {
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

    //Gets the first account found wmattching accountType
    public Account getAccount() {
        //Find if there is an account with the correct accountName and get its token
        for (Account account : mAccountManager.getAccountsByType(ACCOUNT_TYPE)) {
                return account;
        }
        return null;
    }



    //Returns all accounts of our type
    public Account[] getAccounts() {
        //Find if there is an account with the correct accountName and get its token
        return mAccountManager.getAccountsByType(ACCOUNT_TYPE);
    }

    //Returns count of accounts of our type
    public Integer getAccountsCount() {
        return getAccounts().length;
    }

    // Creates an account on the Device
    public Boolean createAccount(User user) {
        Account account;
        user.print("Creating account with following data:");
        if (getAccount(user) == null) {
            account = new Account(user.getName(), user.getType());
            mAccountManager.addAccountExplicitly(account, user.getPassword(), null);
        } else {
            Logs.i("Account existing, skip creation...");
            account = getAccount(user);
        }
        user.setDataToDeviceAccount(account);
        if (account == null)
            return false;
        else
            return true;
    }


    //Creates the Server and Device account and exits activity if successfull
    public void createServerAndDeviceAccount(final Activity activity, final User user) {

        new AsyncTask<Void, Void, Intent>() {
            @Override
            protected Intent doInBackground(Void... params) {
                Bundle data = new Bundle();
                JsonItem item = sServerAuthenticate.userSignUp(user);
                if(!item.getResult()) {
                    data.putString(KEY_ERROR_MESSAGE, item.getMessage());
                } else {
                    Logs.i("Creating now the account on the device !", this.getClass());
                    if (!createAccount(user)) {
                        //We could not create the device account so removing the server account
                        sServerAuthenticate.userRemove(user);
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
                    activity.getIntent().putExtras(intent.getExtras());
                    Logs.i("We have the following intent we try to give to AuthenticatorActivity:");
                    //Save the account created in the preferences (all except critical things)
                    //Finish and send to AuthenticatorActivity that we where successfull
                    activity.setResult(Activity.RESULT_OK, intent);
                    activity.finish();
                }
            }
        }.execute();
    }

    //Submits credentials to the server and exits activity if successfull
    public void submitCredentials(final Activity activity,final View v, final User user) {
        new AsyncTask<Void, Void, Void>() {
            JsonItem item;
            @Override
            protected Void doInBackground(Void... params) {
                Logs.i("Started authenticating", this.getClass());
                //If we have an account we try to create a new session and get the new token sending ID + password
                //The server function directly updates the singleton token,id fields
                item = sServerAuthenticate.userSignIn(user);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                // If result is bad or no token display the message of error from the server
                if (!item.getResult() || user.getToken() == null) {
                    Snackbar.make(v, item.getMessage(), Snackbar.LENGTH_LONG).show();
                } else {
                    if (getAccount(user)==null) {
                        createAccount(user);
                    }
                    user.setDataToDeviceAccount(getAccount(user));
                    Bundle data = new Bundle();
                    //Settings for the Account AuthenticatorActivity
                    data.putString(AccountManager.KEY_ACCOUNT_NAME, user.getName());
                    data.putString(AccountManager.KEY_ACCOUNT_TYPE, user.getType());
                    data.putString(AccountManager.KEY_AUTHTOKEN, user.getToken());
 //                   final Intent res = new Intent();
                    activity.getIntent().putExtras(data);
                    Logs.i("We have the following intent we try to give to AuthenticatorActivity:");
 //                   Toolbox.dumpIntent(res);
                    activity.setResult(Activity.RESULT_OK);
                    activity.finish();
                }
            }
        }.execute();
    }



    //Remove account on the Device
    public Boolean removeAccount(User user) {
        Logs.i("Removing account name: " + user.getName(), this.getClass());

        if (getAccount(user) == null)
            return false;
        Boolean isDone = false;
        if (Build.VERSION.SDK_INT<22) {
            @SuppressWarnings("deprecation")
            final AccountManagerFuture<Boolean> booleanAccountManagerFuture = mAccountManager.removeAccount(getAccount(user), null, null);
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
            isDone = mAccountManager.removeAccountExplicitly(getAccount(user));
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
    public boolean checkPasswordInput(String password, final View v, final Activity activity) {
        boolean result = checkPasswordInput(password);
        if (!result) {
            Snackbar snackbar = Snackbar.make(v, "Invalid password", Snackbar.LENGTH_LONG).setAction("DETAILS", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Logs.i("clicked");
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setMessage("Must be 8 characters long minimum \n Must contain 2 uppercase\n Must contain 2 lower case\n Must contain 2 numbers")
                            .setTitle("Password requirements:");
                    builder.setCancelable(true);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    builder.create();
                    builder.show();
                    //builder.setPositiveButton("OK")

                }
            });
            snackbar.show();
        }
        return result;
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
