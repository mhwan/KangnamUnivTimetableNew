package com.mhwan.kangnamunivtimetable.Activity.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mhwan.kangnamunivtimetable.Activity.ScholarshipActivity;
import com.mhwan.kangnamunivtimetable.CustomUI.SpacesItemDecoration;
import com.mhwan.kangnamunivtimetable.CustomUI.TuitionAdapter;
import com.mhwan.kangnamunivtimetable.R;
import com.mhwan.kangnamunivtimetable.Util.KnuUtil.KnuScholarship;
import com.mhwan.kangnamunivtimetable.Util.KnuUtil.KnuTuition;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class TuitionFragment extends Fragment implements ScholarshipActivity.UpdateAllData {
    private RecyclerView recyclerView;
    private TuitionAdapter tuitionAdapter;
    private TextView emptyTextview;
    public TuitionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tuition, container, false);
        ((ScholarshipActivity) getActivity()).setUpdateListener1(this);
        initView(view);
        return view;
    }

    private void initView(View view){
        emptyTextview = (TextView) view.findViewById(R.id.empty_messages);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        tuitionAdapter = new TuitionAdapter(getActivity());
        recyclerView.addItemDecoration(new SpacesItemDecoration(SpacesItemDecoration.RecyclerViewOrientation.LINEAR_VERTICAL, 10));
        recyclerView.setAdapter(tuitionAdapter);

    }

    @Override
    public void updateScholarshipData(ArrayList<KnuScholarship.ScholarshipItem> scholarshipItems) {
        //do nothing
    }

    @Override
    public void updateTuitionData(ArrayList<KnuTuition.TuitionItem> tuitionItems) {
        if (tuitionItems == null || (tuitionItems!=null && tuitionItems.size() == 0 )) {
            emptyTextview.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyTextview.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            tuitionAdapter.updateList(tuitionItems);
        }
    }
}
