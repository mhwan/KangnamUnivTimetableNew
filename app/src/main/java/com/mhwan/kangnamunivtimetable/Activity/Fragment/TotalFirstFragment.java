package com.mhwan.kangnamunivtimetable.Activity.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mhwan.kangnamunivtimetable.Activity.ScoreActivity;
import com.mhwan.kangnamunivtimetable.R;
import com.mhwan.kangnamunivtimetable.Util.KnuUtil.KnuGrade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import az.plainpie.PieView;
import az.plainpie.animation.PieAngleAnimation;

public class TotalFirstFragment extends Fragment implements ScoreActivity.UpdateData {
    private View view;
    private PieView total_pie, average_pie;

    public TotalFirstFragment() {

    }

    public static TotalFirstFragment newInstance() {
        TotalFirstFragment fragment = new TotalFirstFragment();
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
        view = inflater.inflate(R.layout.ui_total_score_first, container, false);
        ((ScoreActivity) getActivity()).setTotal_listener1(this);
        initView();
        return view;
    }

    private void initView() {
        total_pie = view.findViewById(R.id.total_score_pie);
        average_pie = view.findViewById(R.id.total_average_pie);
        total_pie.setTextColor(getResources().getColor(R.color.colorPrimaryBlack));
        average_pie.setTextColor(getResources().getColor(R.color.colorPrimaryBlack));
        total_pie.setInnerBackgroundColor(getResources().getColor(android.R.color.white));
        average_pie.setInnerBackgroundColor(getResources().getColor(android.R.color.white));
        total_pie.setPercentageBackgroundColor(getResources().getColor(R.color.colorPieLightGreen));
        average_pie.setPercentageBackgroundColor(getResources().getColor(R.color.colorPieSkyBlue));
    }

    @Override
    public void updateView(HashMap<KnuGrade.Semester, KnuGrade.Grade> gradelist) {
        if (gradelist != null) {
            ArrayList<KnuGrade.Semester> keylist = KnuGrade.sortSemester(new ArrayList<KnuGrade.Semester>(gradelist.keySet()));
            KnuGrade.Grade list = gradelist.get(keylist.get(0));

            ArrayList<KnuGrade.Grade> allvalues = new ArrayList<>(gradelist.values());

            //specific_major.setText(String.valueOf(getAllMajorUnit(allvalues)));
            //specific_general.setText(String.valueOf(getAllGeneralUnit(allvalues)));
            String total = list.totl_unit;
            String avg = list.totl_avrg;

            total_pie.setPercentage((float) (Double.parseDouble(total) / 130 * 100));
            average_pie.setPercentage((float) (Double.parseDouble(avg) / 4.5 * 100));
            total_pie.setInnerText(total);
            average_pie.setInnerText(avg);
            PieAngleAnimation pieani_1 = new PieAngleAnimation(total_pie);
            PieAngleAnimation pieani_2 = new PieAngleAnimation(average_pie);
            pieani_1.setDuration(1200);
            pieani_2.setDuration(1300);
            total_pie.startAnimation(pieani_1);
            average_pie.startAnimation(pieani_2);
        } else {
            total_pie.setPercentage(0.0f);
            average_pie.setPercentage(0.0f);
            total_pie.setInnerText("0.0");
            average_pie.setInnerText("0.0");
        }

    }

    private int getAllMajorUnit(List<KnuGrade.Grade> values) {
        int sum = 0;
        for (KnuGrade.Grade grade : values) {
            for (int i = 0; i < grade.subjects.length; i++) {
                if (grade.subjects[i].getClassify().matches("전기|전선|전필|복기|복수|부전|기초")) {
                    sum += Integer.parseInt(grade.subjects[i].getUnit());
                }
            }
        }
        return sum;
    }

    private int getAllGeneralUnit(List<KnuGrade.Grade> values) {
        int sum = 0;
        for (KnuGrade.Grade grade : values) {
            for (int i = 0; i < grade.subjects.length; i++) {
                if (grade.subjects[i].getClassify().matches("교필|교선|균형|계열")) {
                    sum += Integer.parseInt(grade.subjects[i].getUnit());
                }
            }
        }
        return sum;
    }
}
