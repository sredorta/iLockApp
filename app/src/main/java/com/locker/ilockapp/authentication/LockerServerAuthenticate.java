package com.locker.ilockapp.authentication;
import com.locker.ilockapp.dao.CloudFetchr;
import com.locker.ilockapp.toolbox.Logs;
import com.locker.ilockapp.dao.JsonItem;

import org.json.JSONException;
import org.json.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Defines the commands required to request server for authentication
 */
public class LockerServerAuthenticate implements ServerAuthenticate {

    AccountGeneral myAccountGeneral;
    //Send to the Server the user name and the token we have stored in our device and check if the token is valid
    @Override
    public JsonItem isTokenValid() {
        myAccountGeneral = myAccountGeneral.getInstance();
        Logs.i("Validating token : " + myAccountGeneral.user.getToken(),this.getClass());
        Logs.i("For account : " + myAccountGeneral.user.getName() + "  id: " + myAccountGeneral.user.getId(),this.getClass() );
        JsonItem item = new CloudFetchr().isTokenValid(myAccountGeneral.user.getId(), myAccountGeneral.user.getToken(),"users");
        //Here in the case of account details more extensive we need to parse jsonobject with AccountDetails.parseJson with field item.account
        return item;
    }

    @Override
    public Boolean userRemove() {
        myAccountGeneral = myAccountGeneral.getInstance();
        Boolean isUserRemoved = new CloudFetchr().userRemove(myAccountGeneral.user.getId(),"users");
        return isUserRemoved;
    }
    @Override
    public JsonItem userSignUp() {
        myAccountGeneral = myAccountGeneral.getInstance();
        myAccountGeneral.user.print("Before JsonParse");
        JsonItem item = new CloudFetchr().userSignUp(myAccountGeneral.user.getPhone(),
                myAccountGeneral.user.getEmail(),
                myAccountGeneral.user.getPassword(),
                myAccountGeneral.user.getFirstName(),
                myAccountGeneral.user.getLastName(),
                myAccountGeneral.user.getType(),
                myAccountGeneral.user.getAuthType(),
                "users");
        if (item.getAccountDetails() != null) {
            try {
                Logs.i("Received JSON:" + item.getAccountDetails());
                User myUser = new User();
                JSONObject jsonBody = new JSONObject(item.getAccountDetails());
                Gson gson = new GsonBuilder().create();
                myUser = gson.fromJson(jsonBody.toString(), User.class);
                //Update the fields that we have got from the server
                myAccountGeneral.user.getDataFromServer(myUser);
                Logs.i("Got from JSON:" + myAccountGeneral.user.getId());
                Logs.i("Got from JSON:" + myAccountGeneral.user.getFirstName());
            } catch (JSONException je) {
                Logs.i("Failed to parse JSON :" + je);
                item.setSuccess(false);
                item.setResult(false);
                item.setMessage("ERROR: Failed to parse JSON !");
                item.setSuccess(false);
                item.setResult(false);
                Logs.i("Falied to fetch items ! :" + je);
            }
        }
        myAccountGeneral.user.print("After Json parse");
        return item;
    }

    //Send to the Server username and password and get corresponding token
    @Override
    public JsonItem userSignIn() {
        myAccountGeneral = myAccountGeneral.getInstance();
        String user = null;
        if (myAccountGeneral.user.getId() != null)
            user = myAccountGeneral.user.getId();
        else if (myAccountGeneral.user.getEmail() != null)
            user = myAccountGeneral.user.getEmail();
        else
            user = myAccountGeneral.user.getEmail();


        JsonItem item =  new CloudFetchr().userSignIn(user, myAccountGeneral.user.getPassword(),
                                                    myAccountGeneral.user.getType(),
                                                    myAccountGeneral.user.getAuthType(), "users");
        if (item.getAccountDetails() != null) {
            try {
                Logs.i("Received JSON:" + item.getAccountDetails());
                User myUser = new User();
                JSONObject jsonBody = new JSONObject(item.getAccountDetails());
                Gson gson = new GsonBuilder().create();
                myUser = gson.fromJson(jsonBody.toString(), User.class);
                //Update the fields that we have got from the server
                myAccountGeneral.user.getDataFromServer(myUser);
                //We need to set also the account name to the email as in the server we don't have it
                myAccountGeneral.user.setName(myAccountGeneral.user.getEmail());
                //We need to create the device account
                myAccountGeneral.createAccount();
                //Save the new inputs
                myAccountGeneral.user.setDataToDeviceAccount(myAccountGeneral.getAccountManager(),myAccountGeneral.getAccount());
                Logs.i("Got from JSON:" + myAccountGeneral.user.getId());
                Logs.i("Got from JSON:" + myAccountGeneral.user.getFirstName());
            } catch (JSONException je) {
                Logs.i("Failed to parse JSON :" + je);
                item.setSuccess(false);
                item.setResult(false);
                item.setMessage("ERROR: Failed to parse JSON !");
                item.setSuccess(false);
                item.setResult(false);
                Logs.i("Falied to fetch items ! :" + je);
            }
        }
        myAccountGeneral.user.print("After Json parse");
        return item;

    }

    /*
    //Send to the server all fields and create a new user and get the token


    //Send to the server all fields and create a new user and get the token
    @Override
    public Boolean userSetPassword(String account, String password, String authType) {
        Boolean result = new CloudFetchr().userSetPassword(account,password,"users");
        return result;
    }


    //Send to the Server username and password and get corresponding token
    @Override
    public JsonItem userSignIn(String user, String password, String authType) {
        return new CloudFetchr().userSignIn(user,password, "users");

    }



    @Override
    public Boolean userRemove(String account, String authType) {
        Boolean isUserRemoved = new CloudFetchr().userRemove(account,"users");
        return isUserRemoved;
    }
*/
}
