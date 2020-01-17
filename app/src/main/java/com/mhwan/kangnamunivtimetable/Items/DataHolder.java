package com.mhwan.kangnamunivtimetable.Items;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.mhwan.kangnamunivtimetable.R;

public class DataHolder {

    private int score_selected = 0, unit_selected = 0;
    private ArrayAdapter<String> score_adapter, unit_adapter;

    public DataHolder(Context context) {
        //Log.d("aa", "init");
        String[] scores = context.getResources().getStringArray(R.array.Score_value_array);
        String[] units = context.getResources().getStringArray(R.array.Unit_value_array);

        score_adapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item, scores);
        score_adapter.setDropDownViewResource(R.layout.item_spinner);
        unit_adapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item, units);
        unit_adapter.setDropDownViewResource(R.layout.item_spinner);
    }

    public ArrayAdapter<String> getScoreAdapter() {
        return score_adapter;
    }

    public String getScoreText() {
        return score_adapter.getItem(score_selected);
    }

    public int getScoreSelected() {
        return score_selected;
    }

    public void setScoreSelected(int selected) {
        this.score_selected = selected;
    }

    public ArrayAdapter getUnitAdapter() {
        return unit_adapter;
    }

    public String getUnitText() {
        return unit_adapter.getItem(unit_selected);
    }

    public int getUnitSelected() {
        return unit_selected;
    }

    public void setUnitSelected(int selected) {
        this.unit_selected = selected;
    }

}