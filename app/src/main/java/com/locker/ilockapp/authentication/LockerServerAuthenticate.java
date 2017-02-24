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
    public JsonItem isTokenValid(User user) {
        JsonItem item = new CloudFetchr().isTokenValid(user.getId(),user.getToken(),"users");
        //Here in the case of account details more extensive we need to parse jsonobject with AccountDetails.parseJson with field item.account
        return item;
    }

    @Override
    public Boolean userRemove(User user) {
        Boolean isUserRemoved = new CloudFetchr().userRemove(user.getId(),"users");
        return isUserRemoved;
    }

    @Override
    public JsonItem userSignUp(User user) {
        user.print("Before JsonParse");
        JsonItem item = new CloudFetchr().userSignUp(user.getPhone(),
                user.getEmail(),
                user.getPassword(),
                user.getFirstName(),
                user.getLastName(),
                user.getAvatar(),
                user.getType(),
                user.getAuthType(),
                "users");
        if (item.getAccountDetails() != null) {
            User myUser = User.parseJSON(item.getAccountDetails());
            //Update the fields that we have got from the server
            user.update(myUser);
        }
        user.print("After Json parse");
        return item;
    }

    //Send to the Server username and password and get corresponding token
    @Override
    public JsonItem userSignIn(User user) {
        user.print("Before userSignIn:");
        String userID = null;
        if (user.getId() != null)
            userID = user.getId();
        else {
            if (user.getName() != null)
                userID = user.getName();
            else
                userID = user.getPhone();
        }
        JsonItem item =  new CloudFetchr().userSignIn(userID, user.getPassword(),
                                                    user.getType(),
                                                    user.getAuthType(), "users");
        if (item.getAccountDetails() != null) {
            User myUser = User.parseJSON(item.getAccountDetails());
            //Update the fields that we have got from the server
            user.update(myUser);
        }
        user.print("After Json parse");
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
