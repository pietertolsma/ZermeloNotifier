package com.tolsma.pieter.zermelonotifier;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.tolsma.pieter.zermelonotifier.utils.DateHelper;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by pietertolsma on 1/3/16.
 */
public class ScheduleRetriever extends AsyncTask<String, Integer, JSONObject> {


    private final static String LOG_TAG = ScheduleRetriever.class.getSimpleName();

    private static String mAuthorizationCode;
    private static String mSchoolCode;

    private static String mAccessToken;

    private static String mUser;
    private static String mStartTime;
    private static String mEndTime;

    private ScheduleActivity mActivity;

    public ScheduleRetriever(Context context){
        super();
        mActivity = (ScheduleActivity) context;
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        mUser = params[0];
        mSchoolCode = params[1];
        mAccessToken = params[2];

        String[] times = DateHelper.getTimeStamps(5);
        mStartTime = times[0];
        mEndTime = times[1];

        return pullSchedule();
    }

    private JSONObject pullSchedule(){
        JSONObject object = null;

        HttpClient httpClient = new DefaultHttpClient();
        String url = "https://" + mSchoolCode + ".zportal.nl/api/v2/appointments?user=" + mUser + "&start=" +mStartTime
                +"&end=" + mEndTime + "&access_token=" + mAccessToken;
        HttpGet httpget = new HttpGet(url);

        try{
            HttpResponse response = httpClient.execute(httpget);
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuilder builder = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null) builder.append(line);
            object = new JSONObject(builder.toString());
            Log.e(LOG_TAG, object.toString());
        }catch(ClientProtocolException e){
            Log.e(LOG_TAG, "Error getting response", e);
        }catch(IOException e){
            Log.e(LOG_TAG, "Error handling IO", e);
        }catch(JSONException e){
            Log.e(LOG_TAG, "Error handling json", e);
        }

        final JSONObject finalObject = object;

        Handler mainHandler = new Handler(mActivity.getMainLooper());
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                mActivity.onPullScheduleFinished(finalObject);
            }
        });
        return object;

    }

    public String getToken(){
        return mAccessToken;
    }


    protected void onProgressUpdate(Integer... progress) {
        //setProgressPercent(progress[0]);
    }

    protected void onPostExecute(Long result) {
       // showDialog("Downloaded " + result + " bytes");
    }
}
