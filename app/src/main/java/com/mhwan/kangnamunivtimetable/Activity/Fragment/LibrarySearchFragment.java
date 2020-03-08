package com.mhwan.kangnamunivtimetable.Activity.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mhwan.kangnamunivtimetable.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LibrarySearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LibrarySearchFragment extends Fragment {
    private View view;
    public LibrarySearchFragment() {
        // Required empty public constructor
    }
    // TODO: Rename and change types and number of parameters
    public static LibrarySearchFragment newInstance() {
        LibrarySearchFragment fragment = new LibrarySearchFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_library_search, container, false);
        initView();
        return view;
    }

    private void initView(){

    }

}
