package com.mhwan.kangnamunivtimetable.Activity.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;


import com.mhwan.kangnamunivtimetable.CustomUI.CalculateScoreAdapter;
import com.mhwan.kangnamunivtimetable.Database.AppDatabaseHelper;
import com.mhwan.kangnamunivtimetable.Items.DataHolder;
import com.mhwan.kangnamunivtimetable.Items.TimetableSubject;
import com.mhwan.kangnamunivtimetable.R;
import com.mhwan.kangnamunivtimetable.Util.AppUtility;

import java.util.ArrayList;

public class CalculateScoreFragment extends Fragment implements CalculateScoreAdapter.completeAllData {
    private View view;
    private TextView result_score;
    private ArrayList<TimetableSubject> subjectArrayList;
    private ArrayList<Integer> indexList;
    private AppDatabaseHelper dbHelper;

    public CalculateScoreFragment() {
        // Required empty public constructor
    }

    public static CalculateScoreFragment newInstance() {
        CalculateScoreFragment fragment = new CalculateScoreFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_calculate_score, container, false);
        dbHelper = new AppDatabaseHelper(getContext());
        initView();
        return view;
    }

    private DataHolder[] initDataholder(int size) {
        DataHolder[] holders = new DataHolder[size];
        for (int i = 0; i < holders.length; i++) {
            holders[i] = new DataHolder(getContext());
        }
        return holders;
    }

    private void initView() {
        result_score = view.findViewById(R.id.get_grade);
        ListView listView = view.findViewById(R.id.calculate_listview);
        subjectArrayList = dbHelper.getAllTimeTable();
        if (subjectArrayList == null || subjectArrayList.isEmpty()) {
            view.findViewById(R.id.score_frame).setVisibility(View.GONE);
            view.findViewById(R.id.no_score_data_frame).setVisibility(View.VISIBLE);
        } else {
            view.findViewById(R.id.score_frame).setVisibility(View.VISIBLE);
            view.findViewById(R.id.no_score_data_frame).setVisibility(View.GONE);
            CalculateScoreAdapter adapter = new CalculateScoreAdapter(subjectArrayList, getActivity(), initDataholder(subjectArrayList.size()), this);
            View footerView = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer_calculate_score_listview, null, false);
            listView.addFooterView(footerView);
            listView.setAdapter(adapter);
        }

    }

    @Override
    public void complete(DataHolder holders, int index) {
        String unit = holders.getUnitText().replace(getString(R.string.Unit_hint), "");
        String score = holders.getScoreText();

        subjectArrayList.get(index).setUnit(unit);
        subjectArrayList.get(index).setScore(score);

        if (indexList == null)
            indexList = new ArrayList<>();
        indexList.add(index);

        calculateGrade();
    }

    @Override
    public void onDestroy() {
        if (indexList != null && indexList.size() > 0) {
            dbHelper = new AppDatabaseHelper(getContext());
            for (int i = 0; i < indexList.size(); i++) {
                TimetableSubject subject = subjectArrayList.get(indexList.get(i));
                dbHelper.updateScoreUnit(subject.getUnit(), subject.getScore(), subject.getId());
            }
        }
        super.onDestroy();
    }

    private void calculateGrade() {
        double result = 0;
        double sum_score = 0;
        double sum_unit = 0;
        boolean flag = false;
        for (TimetableSubject s : subjectArrayList) {
            System.out.print("");
            if (Integer.parseInt(s.getUnit()) > 0) {
                sum_score += (Double.parseDouble(s.getUnit()) * Double.parseDouble(AppUtility.GradeValueList.get(s.getScore())));
                sum_unit += Double.parseDouble(s.getUnit());
                flag = true;
            }
        }

        if (flag) {
            result = sum_score / sum_unit;
            if (Double.isNaN(result))
                result = 0;

            result_score.setText(String.format("%.2f", result));
        }
    }
}
