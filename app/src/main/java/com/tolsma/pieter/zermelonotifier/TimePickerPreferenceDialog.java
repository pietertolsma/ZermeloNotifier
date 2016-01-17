package com.tolsma.pieter.zermelonotifier;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

/**
 * Created by pietertolsma on 1/6/16.
 */
public class TimePickerPreferenceDialog extends DialogPreference {

    private TimePicker picker = null;

    public TimePickerPreferenceDialog(Context context, AttributeSet attrs){
        super(context, attrs);

        setPositiveButtonText("Set");
        setNegativeButtonText("Cancel");
    }

    @Override
    protected View onCreateDialogView() {
        picker = new TimePicker(getContext());
        SharedPreferences pref = getSharedPreferences();
        int currentTime = pref.getInt(getContext().getString(R.string.key_pick_sync_time), 0);
        int currentHour = 0;
        int currentMinutes = 30;
        if(currentTime != 0){
            int hourInMillis = 60 * 60 * 1000;
            while((currentTime / (hourInMillis)) >= 1){
                currentTime -= hourInMillis;
                currentHour++;
            }
            currentMinutes = Math.round(currentTime / (60 * 1000));
        }
        picker.setIs24HourView(true);
        picker.setCurrentHour(currentHour);
        picker.setCurrentMinute(currentMinutes);
        return(picker);
    }

    @Override
    public void onClick(DialogInterface dialog, int which){

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());

        if(which == DialogInterface.BUTTON_POSITIVE) {
            SharedPreferences.Editor editor = pref.edit();
            int hourMilis = picker.getCurrentHour() *  60 * 60 * 1000;
            int minuteMilis = picker.getCurrentMinute() * 60 * 1000;
            editor.putInt(getContext().getString(R.string.key_pick_sync_time), hourMilis + minuteMilis);
            editor.apply();
        }
    }
}
