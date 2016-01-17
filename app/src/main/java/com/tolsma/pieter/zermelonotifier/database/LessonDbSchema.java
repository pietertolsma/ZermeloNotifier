package com.tolsma.pieter.zermelonotifier.database;

/**
 * Created by pietertolsma on 1/3/16.
 */
public class LessonDbSchema {
    public static final String NAME = "lessons";
    public final static class Cols{
        public static final String START_TIME = "start_time";
        public static final String END_TIME = "end_time";
        public static final String TEACHERS = "teachers";
        public static final String LOCATIONS = "locations";
        public static final String GROUPS = "groups";
        public static final String CANCELLED = "cancelled";
        public static final String MODIFIED = "modified";
        public static final String MOVED = "moved";
        public static final String CHANGE_DESCRIPTION = "change_description";
        public static final String SUBJECTS = "subjects";
        public static final String TIME_SLOT = "time_slot";
    }
}
