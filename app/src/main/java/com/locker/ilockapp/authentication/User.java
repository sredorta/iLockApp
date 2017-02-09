package com.locker.ilockapp.authentication;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.locker.ilockapp.dao.JsonItem;
import com.locker.ilockapp.dao.QueryPreferences;
import com.locker.ilockapp.toolbox.Logs;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by sredorta on 2/1/2017.
 */
public class User extends JsonItem {
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
  @SerializedName("id")
  @Expose(serialize = true, deserialize = true)
  private String mUserId = null;

  @SerializedName("email")
  @Expose(serialize = true, deserialize = true)
  private String mUserEmail = null;

  @SerializedName("phone")
  @Expose(serialize = true, deserialize = true)
  private String mUserPhone = null;;

  @SerializedName("firstName")
  @Expose(serialize = true, deserialize = true)
  private String mUserFirstName = null;;

  @SerializedName("lastName")
  @Expose(serialize = true, deserialize = true)
  private String mUserLastName = null;;



  @SerializedName("account_type")
  @Expose(serialize = true, deserialize = true)
  private String mUserType = null;

  @SerializedName("account_access")
  @Expose(serialize = true, deserialize = true)
  private String mUserAuthType = null;;

  @SerializedName("validated")
  @Expose(serialize = true, deserialize = true)
  private String mUserIsValidated;

  @SerializedName("token")
  @Expose(serialize = true, deserialize = true)
  private String mUserToken = null;;

  @SerializedName("creation_timestamp")
  @Expose(serialize = true, deserialize = true)
  private Integer mCreationTimeStamp;

  @SerializedName("login_timestamp")
  private Integer mLastLoginTimeStamp;

  @SerializedName("password")
  @Expose(serialize = true, deserialize = true)
  private String mUserPassword = null;;

  @SerializedName("latitude")
  @Expose(serialize = true, deserialize = true)
  private String mUserLatitude = null;;

  @SerializedName("longitude")
  @Expose(serialize = true, deserialize = true)
  private String mUserLongitude = null;;

  @SerializedName("avatar")
  @Expose(serialize = true, deserialize = true)
  private String mUserAvatar;

  private String mUserName;


  private AccountGeneral myAccountGeneral;

  //Constructor
  public User() {}

  //Getter/Setters
    public void   setType(String type) { mUserType = type; }
    public String getType() {return mUserType;}

    public void   setAuthType(String authType) { mUserAuthType = authType; }
    public String getAuthType() {return mUserAuthType;}

    public void   setToken(String token) { mUserToken = token; }
    public String getToken() {return mUserToken;}

    public void   setId(String id) { mUserId = id; }
    public String getId() {return mUserId;}

    public void   setName(String name) { mUserName = name; }
    public String getName() {
      if (mUserName == null && mUserEmail != null)
        return mUserEmail;
      else
       return mUserName;
    }

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


  //Prints status of user
  public void print(String s) {
    Logs.i(s);
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

  //Initializes the user account details from preferences or with defaults
  public void init(Context context) {
    myAccountGeneral = new AccountGeneral(context);
    this.setType(AccountGeneral.ACCOUNT_TYPE);
    this.setAuthType(AccountGeneral.AUTHTOKEN_TYPE_STANDARD);
    this.setId(null);
//    mContext = context;
    //Try to restore an account from preferences
    this.setName(QueryPreferences.getPreference(context,QueryPreferences.PREFERENCE_ACCOUNT_NAME));

    //If there is one account matching preferences in the device, we set all data from the device account
    if (myAccountGeneral.getAccount(this) != null) {
      this.getDataFromDeviceAccount(myAccountGeneral.getAccount(this));
    } else
       this.setName(null);
    this.print("Values for User after init:");
  }

  //Initializes the user account details with empty user
  public void initEmpty(Context context) {
    myAccountGeneral = new AccountGeneral(context);
    this.setType(AccountGeneral.ACCOUNT_TYPE);
    this.setAuthType(AccountGeneral.AUTHTOKEN_TYPE_STANDARD);
    this.setId(null);
  }



  //Parses a JSON with the fields and returns a User object
  public static User parseJSON(String jsonString) {
    User myUser = new User();
    try {
      JSONObject jsonBody = new JSONObject(jsonString);
      Gson gson = new GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().create();
      myUser = gson.fromJson(jsonBody.toString(), User.class);
      Logs.i("User details from JSON: " + jsonBody.toString(1));
      myUser.print("User fields we got from JSON:");
    }  catch (JSONException je) {
      Logs.i("Caught exception: " + je);
    }
    //We set the account name equal to the email
    myUser.setName(myUser.getEmail());
    return myUser;
  }


//  public Context getContext() { return mContext;}

  //Queries all the data from the device account and sets the singleton
  public void getDataFromDeviceAccount(Account myAccount) {
    AccountManager mAccountManager = myAccountGeneral.getAccountManager();
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






  public void setDataToDeviceAccount(Account account) {
      AccountManager mAccountManager = myAccountGeneral.getAccountManager();

      if (this.getPassword()!= null)  mAccountManager.setPassword(account, Encryption.getSHA1(this.getPassword()));
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
  public void update(User data){
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
