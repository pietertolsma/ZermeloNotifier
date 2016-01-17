package com.tolsma.pieter.zermelonotifier;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.tolsma.pieter.zermelonotifier.utils.DateHelper;

import org.json.JSONObject;

import java.util.Calendar;

public class ScheduleActivity extends AppCompatActivity implements ScheduleInterface{


    private final static String LOG_TAG = ScheduleActivity.class.getSimpleName();

    private final static int NUM_PAGES = 5;

    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;

    public static Intent newIntent(Context context){
        return new Intent(context, ScheduleActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new PagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        //check if user is already verified with server, if not launch dialog
        checkVerification(sharedPref);
        //Pull schedule from the servers
        pullSchedule(new ScheduleRetriever(this));
        //Start the notification service
        startNotificationService(sharedPref);


    }

    @Override
    public void onResume(){
        super.onResume();
        startNotificationService(PreferenceManager.getDefaultSharedPreferences(this));
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_schedule, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.action_search:
                DialogFragment dialogFragment = new SearchDialog();
                dialogFragment.show(getSupportFragmentManager(), "search_dialog");
            case R.id.action_refresh:
                pullSchedule(new ScheduleRetriever(this));
                return true;
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void startNotificationService(SharedPreferences sharedPref){
        //stopService(new Intent(getBaseContext(), NotificationService.class));
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, 10);

        Intent intent = NotificationService.newIntent(this);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, 0);

        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int refreshTime = sharedPref.getInt(getString(R.string.key_pick_sync_time), 60 * 60 * 1000);
        boolean notificationsEnabled = sharedPref.getBoolean(getString(R.string.key_enable_syncing), true);
        if(notificationsEnabled) {
                alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), refreshTime, pendingIntent);
        }else{
            alarm.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }

    public void cancelNotificationService(){
        Intent intent = NotificationService.newIntent(this);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, 0);
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        pendingIntent.cancel();
        alarm.cancel(pendingIntent);
    }

    public boolean isAlarmOn(){
        Intent i = NotificationService.newIntent(this);
        PendingIntent pi = PendingIntent.getService(this, 0, i, PendingIntent.FLAG_NO_CREATE);
        return pi != null;
    }

    public void checkVerification(SharedPreferences sharedPref){
        String token = sharedPref.getString(getString(R.string.key_pref_token), null);
        String schoolCode = sharedPref.getString(getString(R.string.key_pref_school_code), null);
        if(token == null || schoolCode == null){
            launchSettingsDialog();
        }
    }

    public boolean pullSchedule(ScheduleRetriever retriever){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String userCode = sharedPref.getString(getString(R.string.key_pref_user_code), null);
        String token = sharedPref.getString(getString(R.string.key_pref_token), null);
        String schoolCode = sharedPref.getString(getString(R.string.key_pref_school_code), null);
        retriever.execute(userCode, schoolCode, token);
        return true;
    }
    private void launchSettingsDialog(){
        DialogFragment dialogFragment = new ConnectDialog();
        dialogFragment.show(getSupportFragmentManager(), "connect_dialog");
    }

    @Override
    public void onPullScheduleFinished(JSONObject object) {
        LessonLab.get(this).setLessons(object);
        mPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();

        if(dialog instanceof ConnectDialog) {

            ConnectDialog connDialog = (ConnectDialog) dialog;
            TokenRetriever tokenRetriever = new TokenRetriever();

            String schoolCode = connDialog.getSchoolCodeEditText().getText().toString();
            editor.putString(getString(R.string.key_pref_school_code), schoolCode);
            String authorizationCode = connDialog.getAuthorizationEditText().getText().toString();
            String userCode = connDialog.getUserCodeEditText().getText().toString();
            try {
                String token = tokenRetriever.execute(authorizationCode, schoolCode).get();
                editor.putString(getString(R.string.key_pref_token), token);
                editor.putString(getString(R.string.key_pref_user_code), userCode);
            } catch (Exception e) {
                Log.e(LOG_TAG, "Error fetching the token..", e);
            }
            editor.apply();
            pullSchedule(new ScheduleRetriever(this));
        }else if(dialog instanceof SearchDialog){
            SearchDialog searchDialog = (SearchDialog) dialog;
            String userCode = searchDialog.getUserCodeEditText().getText().toString();
            editor.putString(getString(R.string.key_pref_user_code), userCode);
            editor.apply();
            pullSchedule(new ScheduleRetriever(this));
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }

    private class PagerAdapter extends FragmentStatePagerAdapter{

        public PagerAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(DayFragment.EXTRA_DATE, DateHelper.getDateAhead(position));
            DayFragment fragment = new DayFragment();
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public int getItemPosition(Object object){
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
