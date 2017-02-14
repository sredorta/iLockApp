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
}