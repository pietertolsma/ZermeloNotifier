package com.tolsma.pieter.zermelonotifier;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * Created by pietertolsma on 1/3/16.
 */
public class ConnectDialog extends DialogFragment {

    private static final String LOG_TAG = ConnectDialog.class.getSimpleName();


    private ScheduleInterface mListener;

    private EditText mSchoolCodeEditText;
    private EditText mAuthorizationEditText;
    private EditText mUserCodeEditText;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_connect, null);

        mSchoolCodeEditText = (EditText) view.findViewById(R.id.school_code_edit_text);
        mAuthorizationEditText = (EditText) view.findViewById(R.id.authorization_code_edit_text);
        mUserCodeEditText = (EditText) view.findViewById(R.id.user_code_edit_text);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.prompt_connect)
                .setView(view)
                .setPositiveButton(R.string.connect, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogPositiveClick(ConnectDialog.this);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        mListener.onDialogNegativeClick(ConnectDialog.this);
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    public EditText getSchoolCodeEditText(){
        return mSchoolCodeEditText;
    }

    public EditText getAuthorizationEditText(){
        return mAuthorizationEditText;
    }

    public EditText getUserCodeEditText(){
        return mUserCodeEditText;
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        try{
            mListener = (ScheduleInterface) activity;
        }catch(ClassCastException e){
            Log.e(LOG_TAG, e.getLocalizedMessage());
        }
    }
}
