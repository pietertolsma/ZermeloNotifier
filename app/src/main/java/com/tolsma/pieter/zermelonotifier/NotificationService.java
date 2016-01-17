package com.tolsma.pieter.zermelonotifier;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.tolsma.pieter.zermelonotifier.utils.DateHelper;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by pietertolsma on 1/5/16.
 */


public class NotificationService extends IntentService implements OnTaskCompleted{

    public static final String LOG_TAG = NotificationService.class.getSimpleName();

    public NotificationService(){
        super(LOG_TAG);
    }

    public static Intent newIntent(Context context){
        return new Intent(context, NotificationService.class);
    }

    @Override
    public void onHandleIntent(Intent intent){
        handleIntent(intent);
    }


    private void handleIntent(Intent intent) {
        // check the global background data setting
        Log.e(LOG_TAG, "Starting service!");
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (!cm.getBackgroundDataSetting()) {
            stopSelf();
            return;
        }
        // do the actual work, in a separate thread
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(NotificationService.this);
        boolean onlyOnWifi = pref.getBoolean(getString(R.string.key_only_wifi), true);
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if((onlyOnWifi && mWifi.isConnected()) || !onlyOnWifi) {
            new PollTask(this).execute();
        }
    }

    public void launchNotification(){

        Resources resources = getResources();
        Intent i = ScheduleActivity.newIntent(this);
        PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);

        Notification notification = new NotificationCompat.Builder(this)
                .setTicker("Lesson changed")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("A lesson has changed!")
                .setContentIntent(pi)
                .setAutoCancel(true)
                .build();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(0, notification);

    }

    @Override
    public void OnTaskCompleted(JSONObject object) {
        if(object != null) {
            try {
                JSONArray currentLessons = LessonLab.get(this).getLessonsInJSON();
                JSONObject responseObj = object.getJSONObject("response");
                JSONArray newLessons = responseObj.getJSONArray("data");

                if (!LessonLab.get(this).areLessonsSame(currentLessons, newLessons)) {
                    launchNotification();
                }
            } catch (JSONException e) {
                Log.e(LOG_TAG, "jsonerror", e);
            }
            LessonLab.get(this).setLessons(object);
        }
    }

    private class PollTask extends AsyncTask<Void, Void, JSONObject> {
        private final String LOG_TAG = PollTask.class.getSimpleName();

        private String mUser;
        private String mSchoolCode;
        private String mAccessToken;
        private String mEndTime;
        private String mStartTime;

        private JSONObject result;
        private OnTaskCompleted mListener;

        public PollTask(OnTaskCompleted listener){
            mListener = listener;
        }

        @Override protected JSONObject doInBackground(Void... params) {
        // do stuff!
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(NotificationService.this);
            mUser = pref.getString(getString(R.string.key_pref_user_code), null);
            mSchoolCode = pref.getString(getString(R.string.key_pref_school_code), null);
            mAccessToken = pref.getString(getString(R.string.key_pref_token), null);
            String[] times = DateHelper.getTimeStamps(5);
            mStartTime = times[0];
            mEndTime = times[1];

            if(mAccessToken == null || mSchoolCode == null || mUser == null || mStartTime == null || mEndTime == null){
                return null;
            }else{
                pullSchedule();
            }
            return result;
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
            }catch(ClientProtocolException e){
                Log.e(LOG_TAG, "Error getting response", e);
            }catch(IOException e){
                Log.e(LOG_TAG, "Error handling IO", e);
            }catch(JSONException e){
                Log.e(LOG_TAG, "Error handling json", e);
            }

            final JSONObject finalObject = object;
            result = object;
            return object;
        }

        @Override protected void onPostExecute(JSONObject object) {
        // ADD NOTIFICATION
            mListener.OnTaskCompleted(object);
         stopSelf();
        }
    }

}
