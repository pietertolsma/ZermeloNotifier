package com.tolsma.pieter.zermelonotifier;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tolsma.pieter.zermelonotifier.utils.DateHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by pietertolsma on 1/2/16.
 */
public class DayFragment extends Fragment {

    private ArrayList<Lesson> mLessons;

    public final static String EXTRA_DATE = "com.tolsma.pieter.date";
    private final static String LOG_TAG=  DayFragment.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private LessonAdapter mAdapter;
    private Date mDate;

    private TextView mDateTextView;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mDate = (Date) getArguments().getSerializable(EXTRA_DATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_day, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.lessons_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mDateTextView = (TextView) view.findViewById(R.id.date_string);
        mDateTextView.setText(DateHelper.getSimpleDate(mDate));

        updateUI();

        return view;
    }

    private void updateUI(){
        LessonLab lessonLab = LessonLab.get(getActivity());
        List<Lesson> lessonDays = lessonLab.getLessons(mDate);

        if(mAdapter == null){
            mAdapter = new LessonAdapter(lessonDays);
            mRecyclerView.setAdapter(mAdapter);
        }else{
            mAdapter.setLessons(lessonDays);
            mAdapter.notifyDataSetChanged();
        }
    }

    private class LessonAdapter extends RecyclerView.Adapter<LessonHolder>{

        private List<Lesson> mLessons;

        public LessonAdapter(List<Lesson> lessons){
            mLessons = lessons;
            addFreeHours();
        }

        public void setLessons(List<Lesson> lessons){
            mLessons = lessons;
            addFreeHours();
        }

        private void addFreeHours(){
            for(int i = 0; i < mLessons.size(); i++){
                if(mLessons.get(i).getTimeSlot() != (i + 1)){
                    mLessons.add(i, new Lesson(i + 1));
                }
            }
        }

        @Override
        public LessonHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.list_item_lesson, parent, false);
            return new LessonHolder(view);
        }

        @Override
        public void onBindViewHolder(LessonHolder holder, int position) {
            Lesson lesson = mLessons.get(position);
            holder.bindLesson(lesson, lesson.isCancelled(), lesson.isFreeHour());
        }

        @Override
        public int getItemCount() {
            return mLessons.size();
        }
    }

    private class LessonHolder extends RecyclerView.ViewHolder{
        private Lesson mLesson;
        private CardView mCardView;

        private TextView mTeacherTextView;
        private TextView mSubjectTextView;
        private TextView mEndDateTimeTextView;
        private TextView mStartDateTimeTextView;
        private TextView mLocationTextView;
        private TextView mUntilTextView;

        public LessonHolder(View itemView){
            super(itemView);

            mCardView = (CardView) itemView.findViewById(R.id.card_view);

            mTeacherTextView = (TextView) itemView.findViewById(R.id.teacher_text_view);
            mSubjectTextView = (TextView) itemView.findViewById(R.id.subject_text_view);
            mEndDateTimeTextView = (TextView) itemView.findViewById(R.id.end_date_time_text_view);
            mStartDateTimeTextView = (TextView) itemView.findViewById(R.id.start_date_time_text_view);
            mUntilTextView = (TextView) itemView.findViewById(R.id.until_text_view);
            mLocationTextView = (TextView) itemView.findViewById(R.id.location_text_view);
        }

        public void bindLesson(Lesson lesson, boolean isCancelled, boolean isFreeHour){
            mLesson = lesson;
            View container = getView();
            if(isCancelled){
                mCardView.setBackgroundColor(getResources().getColor(R.color.red));
                mCardView.getBackground().setAlpha(128);
            }else if(isFreeHour){
                mCardView.setBackgroundColor(getResources().getColor(R.color.light_green));
                mCardView.getBackground().setAlpha(128);
            }
            String teachers = lesson.getTeachers().replaceAll("[^\\p{Alpha}]+", "");
            mTeacherTextView.setText(teachers);
            String subjects = lesson.getSubjects().replaceAll("[^\\p{Alpha}]+", "");
            mSubjectTextView.setText(subjects);
            String locations = lesson.getLocations().replaceAll("\\[|\\]", "").replace("\"", "");
            mLocationTextView.setText(locations);
            if(lesson.getStartTime() != 0 && lesson.getEndTime() != 0) {
                Date startDate = DateHelper.fromTimestamp(lesson.getStartTime());
                mStartDateTimeTextView.setText(DateHelper.getSimpleTime(startDate));
                Date endDate = DateHelper.fromTimestamp(lesson.getEndTime());
                mEndDateTimeTextView.setText(DateHelper.getSimpleTime(endDate));
            }else{
                mUntilTextView.setVisibility(View.INVISIBLE);
            }
        }
    }

}
