package com.locker.ilockapp.authentication;

import android.accounts.Account;
import android.accounts.AccountManager;

import com.google.gson.annotations.SerializedName;
import com.locker.ilockapp.toolbox.Logs;

/**
 * Created by sredorta on 2/1/2017.
 */
public class User {
  public final static String PARAM_USER_ACCOUNT_TYPE = "USER_ACCOUNT_TYPE";
  public final static String PARAM_USER_ACCOUNT_AUTH_TYPE = "USER_ACCOUNT_AUTH_TYPE";
  public final static String PARAM_USER_ACCOUNT_ID = "USER_ACCOUNT_ID";
  public final static String PARAM_USER_ACCOUNT_NAME = "USER_ACCOUNT_NAME";
  public final static String PARAM_USER_FIRST_NAME = "USER_FIRST_NAME";
  public final static String PARAM_USER_LAST_NAME = "USER_LAST_NAME";
  public final static String PARAM_USER_EMAIL = "USER_EMAIL";
  public final static String PARAM_USER_PHONE = "USER_PHONE";
  public final static String PARAM_USER_AVATAR = "USER_AVATAR";


    //Account variables that are serializable

    @SerializedName("account_type")
    private String mUserType;

    @SerializedName("account_access")
    private String mUserAuthType;

    @SerializedName("token")
    private String mUserToken;

    @SerializedName("id")
    private String mUserId;

  //  @SerializedName("email")
    private String mUserName;

    @SerializedName("firstName")
    private String mUserFirstName;

    @SerializedName("lastName")
    private String mUserLastName;

    @SerializedName("email")
    private String mUserEmail;

    @SerializedName("phone")
    private String mUserPhone;

    @SerializedName("accountAvatar")
    private String mUserAvatar;


    private String mUserPassword;

    public void   setType(String type) { mUserType = type; }
    public String getType() {return mUserType;}

    public void   setAuthType(String authType) { mUserAuthType = authType; }
    public String getAuthType() {return mUserAuthType;}

    public void   setToken(String token) { mUserToken = token; }
    public String getToken() {return mUserToken;}

    public void   setId(String id) { mUserId = id; }
    public String getId() {return mUserId;}

    public void   setName(String name) { mUserName = name; }
    public String getName() {return mUserName;}

    public void   setFirstName(String firstname) { mUserFirstName = firstname; }
    public String getFirstName() {return mUserFirstName;}

    public void   setLastName(String lastname) { mUserLastName = lastname; }
    public String getLastName() {return mUserLastName;}

    public void   setEmail(String email) { mUserEmail = email; }
    public String getEmail() {return mUserEmail;}

    public void   setPhone(String phone) { mUserPhone = phone; }
    public String getPhone() {return mUserPhone;}

    public void   setAvatar(String avatar) { mUserAvatar = avatar; }
    public String getAvatar() {return mUserAvatar;}

    public void   setPassword(String password) { mUserPassword = password; }
    public String getPassword() {return mUserPassword;}


  public void print(String s) {
    Logs.i(s);
    Logs.i("Values for AccountDetails singleton:", this.getClass());
    Logs.i("mUserId =            " + mUserId);
    Logs.i("mUserName =          " + mUserName);
    Logs.i("mUserFirstName =     " + mUserFirstName);
    Logs.i("mUserLastName =      " + mUserLastName);
    Logs.i("mUserEmail       =   " + mUserEmail);
    Logs.i("mUserPhone       =   " + mUserPhone);
    Logs.i("mUserAvatar      =   " + mUserAvatar);
    Logs.i("mUserPassword    =   " + mUserPassword);
    Logs.i("mUserType        =   " + mUserType);
    Logs.i("mUserAuthType    =   " + mUserAuthType);
    Logs.i("mUserToken       =   " + mUserToken);
  }

  //Queries all the data from the device account and sets the singleton
  public void getDataFromDeviceAccount(AccountManager mAccountManager, Account myAccount) {
    Logs.i("Populating AccountDetails from device account !", this.getClass());
    mUserId          = mAccountManager.getUserData(myAccount, PARAM_USER_ACCOUNT_ID);
    mUserName        = mAccountManager.getUserData(myAccount, PARAM_USER_ACCOUNT_NAME);
    mUserFirstName   = mAccountManager.getUserData(myAccount, PARAM_USER_FIRST_NAME);
    mUserLastName    = mAccountManager.getUserData(myAccount, PARAM_USER_LAST_NAME);
    mUserEmail       = mAccountManager.getUserData(myAccount, PARAM_USER_EMAIL);
    mUserPhone       = mAccountManager.getUserData(myAccount, PARAM_USER_PHONE);
    mUserAvatar      = mAccountManager.getUserData(myAccount, PARAM_USER_AVATAR);

    //System parameters
    mUserPassword    = mAccountManager.getPassword(myAccount);
    mUserType        = mAccountManager.getUserData(myAccount, PARAM_USER_ACCOUNT_TYPE);
    mUserAuthType    = mAccountManager.getUserData(myAccount, PARAM_USER_ACCOUNT_AUTH_TYPE);
    if (mUserAuthType != null){
        mUserToken       = mAccountManager.peekAuthToken(myAccount,mUserAuthType);
        Logs.i("We restored the token:" + mUserToken);
    }

    this.print("User values after populate:");
  }


  public void setDataToDeviceAccount(AccountManager mAccountManager, Account account) {
      if (this.getPassword()!= null)  mAccountManager.setPassword(account, this.getPassword());
      if (this.getAuthType()!=null)   mAccountManager.setUserData(account, PARAM_USER_ACCOUNT_AUTH_TYPE, this.getAuthType());
      if (this.getToken()!= null)     mAccountManager.setAuthToken(account, this.getAuthType(), this.getToken());
      if (this.getType() != null)     mAccountManager.setUserData(account, PARAM_USER_ACCOUNT_TYPE, this.getType());
      if (this.getName() != null)     mAccountManager.setUserData(account, PARAM_USER_ACCOUNT_NAME, this.getName());
      if (this.getId() != null)       mAccountManager.setUserData(account, PARAM_USER_ACCOUNT_ID, this.getId());
      if (this.getEmail() != null)    mAccountManager.setUserData(account, PARAM_USER_EMAIL, this.getEmail());
      if (this.getPhone() != null)    mAccountManager.setUserData(account, PARAM_USER_PHONE, this.getPhone());
      if (this.getFirstName() != null)mAccountManager.setUserData(account, PARAM_USER_FIRST_NAME, this.getFirstName());
      if (this.getLastName() != null) mAccountManager.setUserData(account, PARAM_USER_LAST_NAME, this.getLastName());
  }

  //Update from a user object all non-null variables
  public void getDataFromServer(User data){
    if (data.getId() != null) this.setId(data.getId());
    if (data.getName() != null) this.setName(data.getName());
    if (data.getFirstName() != null) this.setFirstName(data.getFirstName());
    if (data.getLastName() != null) this.setLastName(data.getLastName());
    if (data.getEmail() != null) this.setEmail(data.getEmail());
    if (data.getPhone() != null) this.setPhone(data.getPhone());
    if (data.getAvatar() != null) this.setAvatar(data.getAvatar());

    //
    if (data.getPassword() != null) this.setPassword(data.getPassword());
    if (data.getToken() != null) this.setToken(data.getToken());
    if (data.getType() != null) this.setType(data.getType());
    if (data.getAuthType() != null) this.setAuthType(data.getAuthType());
  }



}
