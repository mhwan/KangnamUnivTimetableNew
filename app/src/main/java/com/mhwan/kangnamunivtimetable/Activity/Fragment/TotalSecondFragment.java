package com.mhwan.kangnamunivtimetable.Activity.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.ListPopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import com.mhwan.kangnamunivtimetable.Activity.ScoreActivity;
import com.mhwan.kangnamunivtimetable.CustomUI.CheckableTextView;
import com.mhwan.kangnamunivtimetable.R;
import com.mhwan.kangnamunivtimetable.Util.KnuUtil.KnuGrade;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TotalSecondFragment extends Fragment implements ScoreActivity.UpdateData {
    private View view;
    private static String[] smallClassifyList = {"전기", "전필", "전선", "타전", "자선", "교직", "교필", "교선",  "균형", "계열"};
    private static String[] bigClassifyList = {"전공기초", "전공필수", "전공선택", "타전공","자유선택", "교직", "교양필수", "교양선택", "균형", "교양계열" };
    private HashMap<String, Integer> myScoreList;
    private HashMap<String, String> classifyMap;
    private GridLayout general_list, major_list;
    private TextView general_sum_view, major_sum_view;
    public TotalSecondFragment() {

    }


    public static TotalSecondFragment newInstance() {
        TotalSecondFragment fragment = new TotalSecondFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.ui_total_score_second, container, false);
        ((ScoreActivity)getActivity()).setTotal_listener2(this);
        initClassifyMap();
        initView();
        return view;
    }

    private void initClassifyMap(){
        classifyMap = new HashMap<>();
        for (int i =0; i<smallClassifyList.length; i++){
            classifyMap.put(smallClassifyList[i], bigClassifyList[i]);
        }
    }

    private void initView() {
        general_sum_view = (TextView) view.findViewById(R.id.general_sum);
        major_sum_view = (TextView) view.findViewById(R.id.major_sum);
        general_list = (GridLayout) view.findViewById(R.id.general_list);
        major_list = (GridLayout) view.findViewById(R.id.major_list);
    }

    private void addScoreView(){
        general_sum_view.setText("교양전체 : "+myScoreList.get("generals"));
        major_sum_view.setText("전공전체 : "+myScoreList.get("majors"));
        Set set = myScoreList.entrySet();
        Iterator iterator = set.iterator();

        int m_index = 0;
        int g_index = 0;

        if (set.size() > 0) {
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                String key = (String) entry.getKey();
                int value = (int) entry.getValue();

                if (!key.matches("generals|majors")) {
                    GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
                    layoutParams.width = GridLayout.LayoutParams.WRAP_CONTENT;
                    layoutParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
                    String str = "";
                    //교
                    if (key.contains("_")) {
                        TextView textView = new TextView(getActivity());
                        if (classifyMap.containsKey(key.replace("_", "")))
                            str = classifyMap.get(key.replace("_", ""));
                        else
                            str = key;
                        textView.setText(str + " : " + value);
                        textView.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorLightBlack));
                        textView.setTextSize(12);
                        layoutParams.rowSpec = GridLayout.spec((int) g_index / 2);
                        layoutParams.columnSpec = GridLayout.spec(g_index % 2);
                        if (g_index % 2 != 0)
                            layoutParams.leftMargin = 14;
                        layoutParams.bottomMargin = 2;
                        textView.setLayoutParams(layoutParams);

                        general_list.addView(textView);
                        g_index++;
                    } else {
                        TextView textView = new TextView(getActivity());
                        if (classifyMap.containsKey(key))
                            str = classifyMap.get(key);
                        else
                            str = key;
                        textView.setText(str + " : " + value);
                        textView.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorLightBlack));
                        textView.setTextSize(12);
                        layoutParams.rowSpec = GridLayout.spec((int) m_index / 2);
                        layoutParams.columnSpec = GridLayout.spec(m_index % 2);
                        if (m_index % 2 != 0)
                            layoutParams.leftMargin = 14;
                        layoutParams.bottomMargin = 2;
                        textView.setLayoutParams(layoutParams);

                        major_list.addView(textView);
                        m_index++;
                    }
                }
            }
        } else {
            TextView textView = new TextView(getActivity());
            textView.setText(getResources().getText(R.string.message_no_score_list));
            textView.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorLightBlack));
            textView.setTextSize(12);
            textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            general_list.addView(textView);
            major_list.addView(textView);
        }
    }

    @Override
    public void updateView(HashMap<KnuGrade.Semester, KnuGrade.Grade> gradelist) {
        if (gradelist != null) {
            getSumOfUnit(new ArrayList<KnuGrade.Grade>(gradelist.values()));
            addScoreView();
        } else {
            general_sum_view.setText("교양전체 : 0");
            major_sum_view.setText("전공전체 : 0");

            TextView[] tv = new TextView[2];
            for (int i = 0; i<2; i++) {
                tv[i] = new TextView(getActivity());
                tv[i].setText(getResources().getText(R.string.message_no_score_list));
                tv[i].setTextColor(ContextCompat.getColor(getActivity(), R.color.colorLightBlack));
                tv[i].setTextSize(12);
                tv[i].setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }

            general_list.addView(tv[0]);
            major_list.addView(tv[1]);
        }
    }

    private void initScoreList(){
        myScoreList = new HashMap<>();
        myScoreList.put("generals", 0);
        myScoreList.put("majors", 0);
    }
    private void getSumOfUnit(List<KnuGrade.Grade> values) {
        initScoreList();
        for (KnuGrade.Grade grade : values) {
            for (int i = 0; i < grade.subjects.length; i++) {
                String classify = grade.subjects[i].getClassify();
                if (classify.matches("교필|교선|균형|계열"))
                    classify+="_";

                int unit = Integer.parseInt(grade.subjects[i].getUnit());
                int newscore = unit;
                if (myScoreList.containsKey(classify)) {
                    newscore += myScoreList.get(classify).intValue();
                }
                myScoreList.put(classify, newscore);

                if (classify.matches("교필_|교선_|균형_|계열_")) {
                    myScoreList.put("generals", ((int)myScoreList.get("generals"))+unit);
                } else {
                    myScoreList.put("majors", myScoreList.get("majors")+unit);
                }
            }
        }
    }
}
