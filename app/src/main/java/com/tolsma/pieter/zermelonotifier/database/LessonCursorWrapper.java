package com.tolsma.pieter.zermelonotifier.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.tolsma.pieter.zermelonotifier.Lesson;

/**
 * Created by pietertolsma on 1/3/16.
 */
public class LessonCursorWrapper extends CursorWrapper {

    public LessonCursorWrapper(Cursor cursor){
        super(cursor);
    }

    public Lesson getLesson(){

        int startTime = getInt(getColumnIndex(LessonDbSchema.Cols.START_TIME));
        int endTime = getInt(getColumnIndex(LessonDbSchema.Cols.END_TIME));
        int timeSlot = getInt(getColumnIndex(LessonDbSchema.Cols.TIME_SLOT));
        String teachers = getString(getColumnIndex(LessonDbSchema.Cols.TEACHERS));
        String locations = getString(getColumnIndex(LessonDbSchema.Cols.LOCATIONS));
        String changeDescription = getString(getColumnIndex(LessonDbSchema.Cols.CHANGE_DESCRIPTION));
        String groups = getString(getColumnIndex(LessonDbSchema.Cols.GROUPS));
        String subjects = getString(getColumnIndex(LessonDbSchema.Cols.SUBJECTS));
        boolean modified = getInt(getColumnIndex(LessonDbSchema.Cols.MODIFIED)) > 0;
        boolean cancelled = getInt(getColumnIndex(LessonDbSchema.Cols.CANCELLED)) > 0;
        boolean moved = getInt(getColumnIndex(LessonDbSchema.Cols.MOVED)) > 0;


        Lesson lesson = new Lesson(subjects, teachers, groups, locations, changeDescription, startTime, endTime, timeSlot,
                cancelled, moved, modified);

        return lesson;
    }
}
