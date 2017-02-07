package com.locker.ilockapp.authentication;

import com.locker.ilockapp.dao.JsonItem;

/**
 * Defines the interface with the server for authentication
 */
public interface ServerAuthenticate {
    public JsonItem isTokenValid(User user);
    public Boolean userRemove(User user);
    public JsonItem userSignUp(User user);
    public JsonItem userSignIn(User user);

    /*
    public Boolean userSetPassword(final String user, final String password, String authType);
    public String userSignUp(final String phone, final String email, final String firstName, final String lastName, String authType);
    public JsonItem userSignIn(final String user, final String pass, String authType);

    public Boolean userRemove(final String user, String authType);
    */
}