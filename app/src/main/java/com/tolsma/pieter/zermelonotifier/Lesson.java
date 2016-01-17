package com.tolsma.pieter.zermelonotifier;

/**
 * Created by pietertolsma on 1/2/16.
 */
public class Lesson {

    private String mSubjects;
    private String mTeachers;
    private String mGroups;
    private String mLocations;
    private String mExtraInfo;
    private int mStartTime;
    private int mEndTime;
    private int mTimeSlot;
    private boolean mIsCancelled;
    private boolean mIsMoved;
    private boolean mHasChanged;
    private boolean isFreeHour = false;


    public Lesson(int timeSlot){
        isFreeHour = true;
        mSubjects = "Free";
        mTeachers = "";
        mGroups = "";
        mLocations = "";
        mExtraInfo = "";
        mStartTime = 0;
        mEndTime = 0;
        mTimeSlot = timeSlot;
        mIsCancelled = false;
        mIsMoved = false;
        mHasChanged = false;
    }

    public Lesson(String subjects, String teachers, String groups, String locations, String extraInfo, int startTime, int endTime, int timeSlot,
                  boolean isCancelled, boolean isMoved, boolean hasChanged){
        mSubjects = subjects;
        mTeachers = teachers;
        mGroups = groups;
        mLocations = locations;
        mExtraInfo = extraInfo;
        mStartTime = startTime;
        mEndTime = endTime;
        mIsCancelled = isCancelled;
        mIsMoved = isMoved;
        mHasChanged = hasChanged;
        mTimeSlot = timeSlot;
        isFreeHour = false;

    }

    public boolean isFreeHour() {
        return isFreeHour;
    }

    public void setIsFreeHour(boolean isFreeHour) {
        this.isFreeHour = isFreeHour;
    }

    public int getTimeSlot() {
        return mTimeSlot;
    }

    public void setTimeSlot(int timeSlot) {
        mTimeSlot = timeSlot;
    }

    public String getLocations() {
        return mLocations;
    }

    public void setLocations(String locations) {
        mLocations = locations;
    }

    public String getExtraInfo() {
        return mExtraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        mExtraInfo = extraInfo;
    }

    public String getSubjects() {
        return mSubjects;
    }

    public void setSubjects(String subjects) {
        mSubjects = subjects;
    }

    public int getStartTime() {
        return mStartTime;
    }

    public void setStartTime(int startTime) {
        mStartTime = startTime;
    }

    public int getEndTime() {
        return mEndTime;
    }

    public void setEndTime(int endTime) {
        mEndTime = endTime;
    }

    public String getTeachers() {
        return mTeachers;
    }

    public void setTeachers(String teachers) {
        mTeachers = teachers;
    }

    public boolean isCancelled() {
        return mIsCancelled;
    }

    public void setIsCancelled(boolean isCancelled) {
        mIsCancelled = isCancelled;
    }

    public boolean isHasChanged() {
        return mHasChanged;
    }

    public void setHasChanged(boolean hasChanged) {
        mHasChanged = hasChanged;
    }

    public String getGroups() {
        return mGroups;
    }

    public void setGroups(String groups) {
        mGroups = groups;
    }

    public boolean isMoved() {
        return mIsMoved;
    }

    public void setIsMoved(boolean isMoved) {
        mIsMoved = isMoved;
    }

}
