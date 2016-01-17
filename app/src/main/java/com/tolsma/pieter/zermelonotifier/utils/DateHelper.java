package com.tolsma.pieter.zermelonotifier.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by pietertolsma on 1/3/16.
 */
public class DateHelper {

    private final static String LOG_TAG = DateHelper.class.getSimpleName();

    public static String[] getTimeStamps(int numDaysAhead){

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        Date startDate = calendar.getTime();
        calendar.setTime(startDate);
        calendar.add(Calendar.DATE, numDaysAhead);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        Date endDate = calendar.getTime();
        String mStartTime = String.valueOf(startDate.getTime() / 1000);
        String mEndTime = String.valueOf(endDate.getTime() / 1000);
        return new String[]{mStartTime, mEndTime};
    }

    public static Date getDateAhead(int numDaysAhead){
        Calendar calendar = Calendar.getInstance();
        Date startDate = new Date();
        calendar.setTime(startDate);
        calendar.add(Calendar.DATE, numDaysAhead);
        return calendar.getTime();
    }


    public static Date fromTimestamp(int timestamp){
        long longTime = (long) timestamp;
        long mseconds = longTime * 1000;
        Date date = new Date();
        date.setTime(mseconds);
        return date;
    }

    public static String[] getMaxTimeStamps(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        String minTime = Long.toString(cal.getTimeInMillis() / 1000);
        cal.add(Calendar.DATE, 1);
        String maxTime = Long.toString(cal.getTimeInMillis() / 1000);
        return new String[]{minTime, maxTime};
    }

    public static String getSimpleTime(Date date){
        SimpleDateFormat localDateFormat = new SimpleDateFormat("HH:mm");
        String time = localDateFormat.format(date);
        return time;
    }

    public static String getSimpleDate(Date date){
        SimpleDateFormat format = new SimpleDateFormat("cccc, d LLLL yyyy");
        return format.format(date);
    }
}
