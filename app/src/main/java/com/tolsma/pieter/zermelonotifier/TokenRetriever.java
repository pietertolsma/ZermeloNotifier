package com.tolsma.pieter.zermelonotifier;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pietertolsma on 1/3/16.
 */
public class TokenRetriever extends AsyncTask<String, Integer, String> {

    private static final String LOG_TAG = TokenRetriever.class.getSimpleName();

    private static String mAuthorizationCode;
    private static String mSchoolCode;

    private static String mAccessToken;

    @Override
    protected String doInBackground(String... params) {

        return generateToken(params[0], params[1]);

    }

    public String generateToken(String authorizationCode, String schoolCode){
        mAuthorizationCode = authorizationCode;
        mSchoolCode = schoolCode;

        String JSONString = "";

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("https://" + mSchoolCode + ".zportal.nl/api/v2/oauth/token");
        try{
            List<NameValuePair> nameValuePairs = new ArrayList<>(2);
            httppost.setHeader("Accept", "application/json");
            nameValuePairs.add(new BasicNameValuePair("grant_type", "authorization_code"));
            nameValuePairs.add(new BasicNameValuePair("code", mAuthorizationCode));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));

            HttpParams parameters = httppost.getParams();
            HttpConnectionParams.setConnectionTimeout(parameters, 45000);
            HttpConnectionParams.setSoTimeout(parameters, 45000);

            HttpResponse response = httpClient.execute(httppost);
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuilder builder = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null){
                Log.i(LOG_TAG, line);
                builder.append(line);
            }
            JSONString = builder.toString();
        }catch(ClientProtocolException e){
            Log.e(LOG_TAG, "Error", e);
        }catch(IOException e){
            Log.e(LOG_TAG, "IO Error", e);
        }

        try{
            JSONObject json = new JSONObject(JSONString);
            mAccessToken =  json.get("access_token").toString();
            return mAccessToken;
        }catch(JSONException e){

        }
        return null;
    }
}
