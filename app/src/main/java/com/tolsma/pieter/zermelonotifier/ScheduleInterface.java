package com.tolsma.pieter.zermelonotifier;

import android.support.v4.app.DialogFragment;

import org.json.JSONObject;

/**
 * Created by pietertolsma on 1/5/16.
 */
public interface ScheduleInterface {
        public void onPullScheduleFinished(JSONObject object);
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
}
