package com.mhwan.kangnamunivtimetable.Activity.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mhwan.kangnamunivtimetable.Activity.ScoreActivity;
import com.mhwan.kangnamunivtimetable.R;
import com.mhwan.kangnamunivtimetable.Util.KnuUtil.KnuGrade;

import java.util.HashMap;

public class TotalThirdFragment extends Fragment implements ScoreActivity.UpdateData {
    private View view;

    public TotalThirdFragment() {
        Log.d("total3", "constructure!!!");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.ui_total_score_third, container, false);
        //((ScoreActivity)getActivity()).setTotal_listener3(this);
        initView();
        return view;
    }

    private void initView() {

    }

    @Override
    public void updateView(HashMap<KnuGrade.Semester, KnuGrade.Grade> gradelist) {

    }
}
