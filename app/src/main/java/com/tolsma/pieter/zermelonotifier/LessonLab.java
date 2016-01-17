package com.tolsma.pieter.zermelonotifier;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.tolsma.pieter.zermelonotifier.database.LessonBaseHelper;
import com.tolsma.pieter.zermelonotifier.database.LessonCursorWrapper;
import com.tolsma.pieter.zermelonotifier.database.LessonDbSchema;
import com.tolsma.pieter.zermelonotifier.utils.DateHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by pietertolsma on 1/3/16.
 */
public class LessonLab {

    private static LessonLab sLessonLab;

    private final static String LOG_TAG = LessonLab.class.getSimpleName();

    private SQLiteDatabase mDatabase;

    private Context mContext;

    public static LessonLab get(Context context){
        if(sLessonLab == null){
            sLessonLab = new LessonLab(context);
        }
        return sLessonLab;
    }

    private LessonLab(Context context){
        mContext = context;
        mDatabase = new LessonBaseHelper(mContext).getWritableDatabase();
    }

    public void orderDb(){

    }

    public List<Lesson> getLessons(Date date){
        List<Lesson> lessons = new ArrayList<>();
        String[] ultTimes = DateHelper.getMaxTimeStamps(date);
        int minTime = Integer.parseInt(ultTimes[0]);
        int maxTime = Integer.parseInt(ultTimes[1]);
        String whereClause = "(" + LessonDbSchema.Cols.START_TIME + " > " + minTime + ") "+ " AND " + "(" + maxTime+ " > " + LessonDbSchema.Cols.END_TIME + ") ";
        String orderBy = LessonDbSchema.Cols.START_TIME + " ASC";
        LessonCursorWrapper cursor = queryLessons(whereClause, null, orderBy);
        try{
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                Lesson lesson = cursor.getLesson();
                lessons.add(lesson);
                cursor.moveToNext();
            }
        }finally{
            cursor.close();
        }

        return lessons;
    }

    public JSONArray getLessonsInJSON(){
        JSONArray data = new JSONArray();

        LessonCursorWrapper cursor = queryLessons(null, null, null);
        try{
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                Lesson lesson = cursor.getLesson();
                JSONObject lessonObject = new JSONObject();
                lessonObject.put("startTimeSlot", lesson.getTimeSlot());
                lessonObject.put("start", lesson.getStartTime());
                lessonObject.put("cancelled", lesson.isCancelled());
                lessonObject.put("moved", lesson.isMoved());
                lessonObject.put("modified", lesson.isHasChanged());
                data.put(lessonObject);
                cursor.moveToNext();
            }
        }catch(JSONException e){
            Log.e(LOG_TAG, "Error parsing JSON from database", e);
        }finally{
            cursor.close();
        }
        return data;
    }

    public boolean areLessonsSame(JSONArray array1, JSONArray array2){
        try {
            for (int i = 0; i < array1.length(); i++) {
                JSONObject lesson1 = array1.getJSONObject(i);
                JSONObject lesson2 = array2.getJSONObject(i);
                int startTime1 = (int) lesson1.get("start");
                int startTime2 = (int) lesson2.get("start");
                boolean isCancelled1 = lesson1.getBoolean("cancelled");
                boolean isCancelled2 = lesson2.getBoolean("cancelled");
                boolean isModified1 = lesson1.getBoolean("modified");
                boolean isModified2 = lesson2.getBoolean("modified");
                if(startTime1 == startTime2){
                    if(isCancelled1 != isCancelled2){
                        return false;
                    }
                    if(isModified1 != isModified2){
                        return false;
                    }
                }
            }
        }catch(JSONException e){
            Log.e(LOG_TAG, "jsonfail", e);
        }
        return true;
    }

    public void setLessons(JSONObject object){
        flushDatabase();
        try {
            JSONObject responseObj = object.getJSONObject("response");
            JSONArray dataArray = responseObj.getJSONArray("data");
            for(int i = 0; i < dataArray.length(); i++){
                JSONObject lessonObject = dataArray.getJSONObject(i);
                String subjects = lessonObject.getJSONArray("subjects").toString();
                String teachers = lessonObject.getJSONArray("teachers").toString();
                String groups = lessonObject.getJSONArray("groups").toString();
                String locations = lessonObject.getJSONArray("locations").toString();
                String extraInfo = lessonObject.get("changeDescription").toString();
                int startTime = (int) lessonObject.get("start");
                int endTime = (int) lessonObject.get("end");
                int timeSlot = (int) lessonObject.get("startTimeSlot");
                boolean isCancelled = lessonObject.getBoolean("cancelled");
                boolean isMoved = lessonObject.getBoolean("moved");
                boolean hasChanged = lessonObject.getBoolean("modified");
                Lesson lesson = new Lesson(subjects, teachers, groups, locations, extraInfo, startTime, endTime, timeSlot, isCancelled, isMoved, hasChanged);
                ContentValues values = getContentValues(lesson);
                checkDuplicate(startTime);
                mDatabase.insert(LessonDbSchema.NAME, null,  values);
            }
        }catch(JSONException e){
            e.printStackTrace();
        }
        orderDb();
    }

    private void checkDuplicate(int start){
        String whereClause = start + " == " + LessonDbSchema.Cols.START_TIME;
        LessonCursorWrapper cursor = queryLessons(whereClause, null, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            cursor.getLesson();
            mDatabase.delete(LessonDbSchema.NAME, whereClause, null);
            cursor.moveToNext();
        }
    }

    private static ContentValues getContentValues(Lesson lesson){
        ContentValues values = new ContentValues();
        values.put(LessonDbSchema.Cols.SUBJECTS, lesson.getSubjects());
        values.put(LessonDbSchema.Cols.TEACHERS, lesson.getTeachers());
        values.put(LessonDbSchema.Cols.GROUPS, lesson.getGroups());
        values.put(LessonDbSchema.Cols.LOCATIONS, lesson.getLocations());
        values.put(LessonDbSchema.Cols.CHANGE_DESCRIPTION, lesson.getExtraInfo());
        values.put(LessonDbSchema.Cols.START_TIME, lesson.getStartTime());
        values.put(LessonDbSchema.Cols.END_TIME, lesson.getEndTime());
        values.put(LessonDbSchema.Cols.CANCELLED, lesson.isCancelled());
        values.put(LessonDbSchema.Cols.MOVED, lesson.isMoved());
        values.put(LessonDbSchema.Cols.MODIFIED, lesson.isHasChanged());
        values.put(LessonDbSchema.Cols.TIME_SLOT, lesson.getTimeSlot());

        return values;
    }

    public void flushDatabase(){
        mDatabase.delete(LessonDbSchema.NAME, null, null);
    }

    private LessonCursorWrapper queryLessons(String whereClause, String[] whereArgs, String orderBy){
        Cursor cursor = mDatabase.query(
          LessonDbSchema.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                orderBy
        );

        return new LessonCursorWrapper(cursor);
    }
}
