package com.locker.ilockapp.dao;


import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.locker.ilockapp.authentication.Encryption;
import com.locker.ilockapp.toolbox.Logs;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by sredorta on 11/24/2016.
 */


public class CloudFetchr {

    private static final String URI_BASE_GOOGLE = "http://clients3.google.com/generate_204";    // Only required to check if internet is available
    private static final String PHP_CONNECTION_CHECK = "locker.connection.check.php";           // Params required : none
    private static final String PHP_USER_REMOVE = "locker.users.remove.php";                    // Params required : phone,email,user_table
    private static final String PHP_USER_SIGNIN = "locker.users.signin.php";                    // Params required : user,password,user_table and returns token
    private static final String PHP_USER_SIGNUP = "locker.users.signup.php";                    // Params required : user,password,email,user_table and returns token
    private static final String PHP_USER_PASSWORD = "locker.users.setpassword.php";                    // Params required : user,password,email,user_table and returns token
    private static final String PHP_USER_TOKEN = "locker.users.checktoken.php";                 // Params required : email,token and returns if token is valid or not
    private static final String PHP_USER_TEST = "test.php";
    private String SEND_METHOD = "POST";                                                        // POST or GET method

    public static final String URI_BASE_DEBUG = "http://10.0.2.2/example1/api/";                //localhost controlled by prefs
    public static final String URI_BASE_PROD = "http://ibikestation.000webhostapp.com/api/";        //realserver controlled by prefs
    public static String URI_BASE = "http://10.0.2.2/example1/api/";


    //We try to see if we can connect to google for example
    public  Boolean isNetworkConnected() {
        URL url = null;
        Uri ENDPOINT = Uri
                .parse(URI_BASE_GOOGLE)
                .buildUpon()
                .build();
        Uri.Builder uriBuilder = ENDPOINT.buildUpon();
        try {
            url = new URL(uriBuilder.toString());
            Logs.i("Trying to access: " + uriBuilder.toString());
        } catch(MalformedURLException e) {
            Logs.i("Malformed URL !");
        }
        HttpURLConnection connection;

        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "Android");
            connection.setRequestProperty("Connection", "close");
            connection.setConnectTimeout(2000);
            connection.connect();
            if (connection.getResponseCode() == 204 && connection.getContentLength() == 0) {
                Logs.i("Connected !", this.getClass());
                return true;
            }
            Logs.i("Not connected !", this.getClass());
            return false;
        } catch (IOException e) {
            //Network not connected
            Logs.i("Caught IOE : "+ e);
            return false;
        }
    }



    //Build http string besed on method and query
    private URL buildUrl(String Action,HashMap<String, String> params) {
        Uri ENDPOINT = Uri
                .parse(URI_BASE + Action)
                .buildUpon()
                .build();

        URL url = null;
        Uri.Builder uriBuilder = ENDPOINT.buildUpon();
        if (this.SEND_METHOD.equals("GET")) {
            //Add GET query parameters using the HashMap in the URL
            try {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    uriBuilder.appendQueryParameter(URLEncoder.encode(entry.getKey(), "utf-8"), URLEncoder.encode(entry.getValue(), "utf-8"));
                }
            } catch (UnsupportedEncodingException e) {
                // do nothing
            }
        }
        String result = uriBuilder.build().toString();
        try {
            url = new URL(result);
        } catch(MalformedURLException e) {
            //Do nothing
        }
        Logs.i("Final URL :" + url.toString(), this.getClass());
        return url;
    }

    //Gets the data from the server and aditionally sends POST parameters if SEND_METHOD is set to POST
    private byte[] getURLBytes(URL url,HashMap<String,String> parametersPOST) throws IOException {

        HttpURLConnection connection;
        OutputStreamWriter request = null;
        byte[] response = null;
        JsonItem json = new JsonItem();  //json answer in case network not available


        json.setSuccess(false);
        json.setResult(false);
        try {
            connection = (HttpURLConnection) url.openConnection();
            //Required to enable input stream, otherwise we get EOF (When using POST DoOutput is required
            connection.setDoInput(true);
            if (this.SEND_METHOD.equals("POST")) connection.setDoOutput(true);
            connection.setReadTimeout(4000);
            connection.setConnectTimeout(4000);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
            connection.setRequestMethod(this.SEND_METHOD);
            connection.connect();

            //Write the POST parameters
            if (this.SEND_METHOD.equals("POST")) {
                OutputStream os = connection.getOutputStream();
                OutputStreamWriter writer = new OutputStreamWriter(os, "UTF-8");
                String postData = getPostDataString(parametersPOST);

                writer.write(postData);
                writer.flush();
                writer.close();
                os.close();
            }

            switch(connection.getResponseCode())
            {
                case HttpURLConnection.HTTP_OK:
                    Logs.i("Connected !",this.getClass());
                    break;
                case HttpURLConnection.HTTP_GATEWAY_TIMEOUT:
                    Logs.i("Timout !");
                    json.setMessage("ERROR: Server timeout !");
                    break;
                case HttpURLConnection.HTTP_UNAVAILABLE:
                    Logs.i("Server not available");
                    json.setMessage("ERROR: Server not available !");
                    break;
                default:
                    Logs.i("Not connected  !");
                    json.setMessage("ERROR: Not connected to server !");
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in            =  connection.getInputStream();
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer))>0) {
                out.write(buffer,0,bytesRead);
            }
            out.close();

            // Response from server after login process will be stored in response variable.
            response = out.toByteArray();

            // You can perform UI operations here
            //Log.i(TAG, "Message from Server: \n" + response);
        } catch (IOException e) {
            // Error
            Logs.i("Caught exception :" + e);
        }
        // In case that response is null we output the json we have created
        if (response == null) {
            Logs.i("Error during access to server");
            response = json.encodeJSON().getBytes();
        }
        //Log.i(TAG, new String(response));
        return response;
    }

    //Get string data from URL
    public String getURLString(URL url,HashMap<String,String> parametersPOST) throws IOException {
        byte[] test =  getURLBytes(url,parametersPOST);
        if (test == null) {
            return "";
        } else {
            return new String(test);
        }
    }

    // Converts a HashMap of string parameter pairs into a string for POST send
    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException{
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append(";");
/*            result.append(URLEncoder.encode("$_POST['" + entry.getKey() +"']", "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));*/
            result.append("$_POST['" + entry.getKey() +"']");
            result.append("=");
            result.append("\"" + entry.getValue() + "\"");
        }
        if (result.toString().length()==0) {
            Logs.i("POST HEADER :");
            return "";
        } else {
            result.append(";");
            Logs.i("POST HEADER :" + result.toString());
            //Encrypt the parameters
            Encryption cipher = new Encryption();
            String encryptedStr = new String();
            try {
                encryptedStr = Encryption.bytesToHex(cipher.encrypt(result.toString()));
            } catch (Exception e) {
                Logs.i("Caught exception: " + e);
            }
            String resultEncrypted = "mydata=" + encryptedStr;
            Logs.i("POST HEADER encrypted: " + resultEncrypted, this.getClass());
            return resultEncrypted;
        }
    }


    // Sends PHP request and returns JSON object
    private JsonItem getJSON(URL url,HashMap<String,String> parametersPOST){
        JsonItem item = new JsonItem();
        //Get the input from the network
        String network;
        String jsonString;
        try {
            network = getURLString(url, parametersPOST);
        } catch (IOException ioe) {
            item.setSuccess(false);
            item.setResult(false);
            item.setMessage("ERROR: Failed to fetch input !");
            Logs.i("Falied to fetch input ! :" + ioe, this.getClass());
            return item;
        }

        //Decrypt the input
        String netStr = new String(network);
        netStr = Encryption.hexToString(netStr);
        Logs.i("Encrypted JSON: " + netStr, this.getClass());

        Encryption mcrypt = new Encryption();
        try {
            jsonString = new String(mcrypt.decrypt(netStr));
        } catch (Exception e) {
            Logs.i("Caught decryption exception: " + e);
            item.setSuccess(false);
            item.setResult(false);
            item.setMessage("ERROR: Decrypt error !");
            return item;
        }

        //Parse the JSON string
        try {
            JSONObject jsonBody = new JSONObject(jsonString);
            Logs.i ("Final JSON:\n" + jsonBody.toString(1)); // We want to see always this message for now
            item = JsonItem.parseJSON(jsonBody.toString());
        } catch (JSONException je) {
            Logs.i("Failed to parse JSON :" + je);
            item.setSuccess(false);
            item.setResult(false);
            item.setMessage("ERROR: Failed to parse JSON !");
        }
        return item;
    }



/*******************************************************************************************/



    public Boolean isCloudConnected() {
        //Define the POST/GET parameters in a HashMap
        this.SEND_METHOD="POST";
        HashMap<String, String> parameters = new HashMap<>();

        URL url = buildUrl(PHP_CONNECTION_CHECK,parameters);
        JsonItem networkAnswer = getJSON(url,parameters);
        return (networkAnswer.getResult());
    }

    //Checks if the station is registered
    public Boolean userRemove(String account, String table) {
        this.SEND_METHOD="POST";
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("account", account);
        parameters.put("table_users", table);

        URL url = buildUrl(PHP_USER_REMOVE,parameters);
        JsonItem networkAnswer = getJSON(url,parameters);
        return (networkAnswer.getResult());
    }

    //Checks if the user is registered and returns all details of the answer with full JsonItem
    public JsonItem userSignUp(String phone, String email, String password, String firstName, String lastName, String avatar, String type, String auth_type, String table) {
        this.SEND_METHOD="POST";
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("phone", phone);
        parameters.put("email", email);
        parameters.put("first_name", firstName);
        parameters.put("last_name", lastName);
        parameters.put("avatar", avatar);
        parameters.put("password", password);
        parameters.put("account_type", type);
        parameters.put("account_access", auth_type);
        parameters.put("table_users", table);

        URL url = buildUrl(PHP_USER_SIGNUP,parameters);
        return getJSON(url,parameters);
    }




    //Checks if the user is registered and returns all Details
    public JsonItem userSignIn(String user,  String password, String type, String auth_type, String table) {
        this.SEND_METHOD="POST";
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("account", user);
        parameters.put("password", password);
        parameters.put("account_type", type);
        parameters.put("account_access", auth_type);
        parameters.put("table_users", table);
        //Password needs to be sha1

        URL url = buildUrl(PHP_USER_SIGNIN,parameters);
        return getJSON(url,parameters);
    }
/*
    //Checks if the user is registered and returns token only
    public String userSignIn(String user,  String password, String table) {
        JsonItem networkAnswer = userSignInDetails(user,password,table);
        return (networkAnswer.getToken());
    }
*/



    //Checks if the user is registered and returns all details of the answer with full JsonItem
    public Boolean userSetPassword(String account, String password, String table) {
        this.SEND_METHOD="POST";
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("account", account);
        parameters.put("password", password);
        parameters.put("table_users", table);

        URL url = buildUrl(PHP_USER_PASSWORD,parameters);
        JsonItem networkAnswer = getJSON(url,parameters);
        return networkAnswer.getResult();
    }





    //Checks if the user is registered and returns all (non critical) details of the answer with full JsonItem
    public JsonItem isTokenValid(String account, String token, String table) {
        this.SEND_METHOD="POST";
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("account", account);
        parameters.put("token", token);
        parameters.put("table_users", table);

        URL url = buildUrl(PHP_USER_TOKEN,parameters);
        return getJSON(url,parameters);
    }

    public void debugEncryption() {
        Logs.i("DEBUG encrypt:");
        this.SEND_METHOD="POST";
        new AsyncTask<Void,Void,Void>() {
            byte[] network;
            @Override
            protected Void doInBackground(Void... voids) {

                HashMap<String, String> parameters = new HashMap<>();
               // parameters.put("param1", "value1");
               // parameters.put("param2", "value2");
                URL url = buildUrl(PHP_USER_TEST,parameters);
                try {
                    network = getURLBytes(url, parameters);
                } catch (IOException e) {
                    Logs.i("Caught exception: " + e);
                }
                Logs.i("End of on background...");

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                String netStr = new String(network);
                Logs.i(netStr);
                netStr = Encryption.hexToString(netStr);
                Integer l = netStr.length();
                Log.i("TEST", "Length is : " + l);
                Log.i("TEST","Recieved from network : " + netStr);

                try {
                    Logs.i("decripting...");
                    Encryption mcrypt = new Encryption();
                    String decrypted = new String( mcrypt.decrypt( netStr ) );

                    //String res = new String( mcrypt.decrypt(test), "UTF-8" );
                    //res = URLDecoder.decode(res,"UTF-8");
                    Logs.i("Decrypted result: " + decrypted);
                } catch (Exception e) {
                    Logs.i("Caught exception: " + e);
                }

                super.onPostExecute(aVoid);
            }
        }.execute();

    }
    private String myTest(byte[] myresponse) {
      if (myresponse == null) {
        return "";
      } else {
        return new String(myresponse);
      }
    }












}

