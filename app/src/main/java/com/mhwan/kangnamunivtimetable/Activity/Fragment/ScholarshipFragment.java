package com.mhwan.kangnamunivtimetable.Activity.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mhwan.kangnamunivtimetable.Activity.ScholarshipActivity;
import com.mhwan.kangnamunivtimetable.CustomUI.ScholarshipAdapter;
import com.mhwan.kangnamunivtimetable.CustomUI.SpacesItemDecoration;
import com.mhwan.kangnamunivtimetable.R;
import com.mhwan.kangnamunivtimetable.Util.KnuUtil.KnuScholarship;
import com.mhwan.kangnamunivtimetable.Util.KnuUtil.KnuTuition;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ScholarshipFragment extends Fragment implements ScholarshipActivity.UpdateAllData {
    private RecyclerView recyclerView;
    private ScholarshipAdapter scholarshipAdapter;
    private TextView emptyTextview;
    public ScholarshipFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_scholarship, container, false);
        ((ScholarshipActivity)getActivity()).setUpdateListener2(this);
        initView(view);
        return view;
    }

    private void initView(View view){
        emptyTextview = view.findViewById(R.id.sholar_empty_messages);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new SpacesItemDecoration(SpacesItemDecoration.RecyclerViewOrientation.LINEAR_VERTICAL, 10));
        scholarshipAdapter = new ScholarshipAdapter(getActivity());
        recyclerView.setAdapter(scholarshipAdapter);
    }

    @Override
    public void updateScholarshipData(ArrayList<KnuScholarship.ScholarshipItem> scholarshipItems) {
        if (scholarshipItems == null || (scholarshipItems !=null && scholarshipItems.size() == 0)) {
            emptyTextview.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyTextview.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            scholarshipAdapter.updateData(scholarshipItems);
        }
    }

    @Override
    public void updateTuitionData(ArrayList<KnuTuition.TuitionItem> tuitionItems) {
        //do nothing
    }
}
