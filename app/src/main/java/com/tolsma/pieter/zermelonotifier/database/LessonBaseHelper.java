package com.tolsma.pieter.zermelonotifier.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by pietertolsma on 1/3/16.
 */
public class LessonBaseHelper extends SQLiteOpenHelper{
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "lessonBase.db";

    public LessonBaseHelper(Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("create table " + LessonDbSchema.NAME + "("
                + "_id integer primary key autoincrement, " +
                LessonDbSchema.Cols.START_TIME + ", " +
                LessonDbSchema.Cols.END_TIME + ", " +
                LessonDbSchema.Cols.TEACHERS + ", " +
                LessonDbSchema.Cols.LOCATIONS + ", " +
                LessonDbSchema.Cols.GROUPS + ", " +
                LessonDbSchema.Cols.MODIFIED + ", " +
                LessonDbSchema.Cols.CANCELLED + ", " +
                LessonDbSchema.Cols.MOVED + ", " +
                        LessonDbSchema.Cols.TIME_SLOT + ", " +
                        LessonDbSchema.Cols.SUBJECTS + ", " +
                LessonDbSchema.Cols.CHANGE_DESCRIPTION + ")");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

    }
}
