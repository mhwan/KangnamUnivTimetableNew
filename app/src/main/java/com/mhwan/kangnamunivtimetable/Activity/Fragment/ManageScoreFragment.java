package com.mhwan.kangnamunivtimetable.Activity.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mhwan.kangnamunivtimetable.Activity.ScoreActivity;
import com.mhwan.kangnamunivtimetable.CustomUI.CheckableTextView;
import com.mhwan.kangnamunivtimetable.Items.Subject;
import com.mhwan.kangnamunivtimetable.R;
import com.mhwan.kangnamunivtimetable.Util.KnuUtil.KnuGrade;

import java.util.ArrayList;
import java.util.HashMap;

public class ManageScoreFragment extends Fragment implements View.OnClickListener, ScoreActivity.UpdateData {

    private View view, semester_frame;
    private ViewGroup score_view, title_frame;
    private HashMap<KnuGrade.Semester, KnuGrade.Grade> data;
    private ArrayList<KnuGrade.Semester> semesterList;
    private TextView semester_avg, semester_unit, no_data_text;
    private CheckableTextView[] semesterView;

    public ManageScoreFragment() {
        // Required empty public constructor

    }

    public static ManageScoreFragment newInstance() {
        ManageScoreFragment fragment = new ManageScoreFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_manage_score, container, false);
        ((ScoreActivity) getActivity()).setManage_listener(this);
        initView();
        return view;
    }

    private void initView() {
        semester_frame = view.findViewById(R.id.previous_semester_frame);
        score_view = view.findViewById(R.id.previous_score_frame);
        semester_avg = view.findViewById(R.id.semester_average_score);
        semester_unit = view.findViewById(R.id.semester_sum_unit);
        no_data_text = view.findViewById(R.id.no_data_text);
        title_frame = view.findViewById(R.id.manage_title_frame);
    }

    private void addSemesterView(ArrayList<KnuGrade.Semester> semesters) {
        if (semesters.size() > 0) {
            semesterView = new CheckableTextView[semesters.size()];
            for (int i = 0; i < semesters.size(); i++) {
                KnuGrade.Semester s = semesters.get(i);
                View list = LayoutInflater.from(getActivity()).inflate(R.layout.ui_semester_label, (ViewGroup) semester_frame, false);
                String str = s.schl_year + "-" + s.schl_smst;
                CheckableTextView textView = list.findViewById(R.id.semester);
                textView.setText(str);
                textView.setTag(i);
                textView.setOnClickListener(this);

                semesterView[i] = textView;

                ((ViewGroup) semester_frame).addView(list);
            }
        }
    }

    private void inflateScoreView(int index) {
        changeSemesterLabel(index);
        KnuGrade.Semester semester = semesterList.get(index);
        if (!semester.equals(null)) {
            Subject[] subjects = data.get(semester).subjects;
            semester_avg.setText(data.get(semester).smst_avrg);
            semester_unit.setText(data.get(semester).smst_unit);
            if (subjects != null && subjects.length > 0) {
                removeInflatedView();
                for (int i = 0; i < subjects.length; i++) {
                    View list = LayoutInflater.from(getActivity()).inflate(R.layout.ui_item_manage_score, score_view, false);
                    Subject subject = subjects[i];

                    ((TextView) list.findViewById(R.id.content_manage_classify)).setText(subject.getClassify());
                    ((TextView) list.findViewById(R.id.content_manage_name)).setText(subject.getName());
                    ((TextView) list.findViewById(R.id.content_manage_unit)).setText(subject.getUnit());
                    ((TextView) list.findViewById(R.id.content_manage_score)).setText(subject.getScore());

                    list.findViewById(R.id.scoredivider).setVisibility((i == subjects.length - 1) ? View.GONE : View.VISIBLE);

                    score_view.addView(list);
                }
            }
        }
    }

    private void changeSemesterLabel(int index) {
        for (int i = 0; i < semesterView.length; i++) {
            boolean check = false;
            if (i == index)
                check = true;
            semesterView[i].setChecked(check);

        }
    }

    private void removeInflatedView() {
        if (score_view.getChildCount() > 0)
            score_view.removeAllViews();
    }

    @Override
    public void onClick(View v) {
        int i = (int) v.getTag();
        inflateScoreView(i);
    }

    @Override
    public void updateView(HashMap<KnuGrade.Semester, KnuGrade.Grade> gradelist) {
        this.data = gradelist;

        if (data != null && data.size() != 0) {
            title_frame.setVisibility(View.VISIBLE);
            no_data_text.setVisibility(View.GONE);
            semesterList = KnuGrade.sortSemester(new ArrayList<KnuGrade.Semester>(gradelist.keySet()));
            addSemesterView(semesterList);
            inflateScoreView(0);
        } else {
            title_frame.setVisibility(View.GONE);
            no_data_text.setVisibility(View.VISIBLE);
        }
    }
}
