package com.mhwan.kangnamunivtimetable.Callback;

import com.mhwan.kangnamunivtimetable.Items.TimetableSubject;

import java.util.ArrayList;

public interface TimeTableDataListener {
    void onDataSet(ArrayList<TimetableSubject> list);
}
