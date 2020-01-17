package com.mhwan.kangnamunivtimetable.CustomUI;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.mhwan.kangnamunivtimetable.Items.DataHolder;
import com.mhwan.kangnamunivtimetable.Items.TimetableSubject;
import com.mhwan.kangnamunivtimetable.R;
import com.mhwan.kangnamunivtimetable.Util.AppUtility;

import java.util.ArrayList;

public class CalculateScoreAdapter extends BaseAdapter {
    private ArrayList<TimetableSubject> list;
    private DataHolder[] holderlist;
    private Context context;
    private completeAllData listener;

    public CalculateScoreAdapter(ArrayList list, Context context, DataHolder[] holderlist, completeAllData listener) {
        this.list = list;
        this.holderlist = holderlist;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public TimetableSubject getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.ui_item_calculate_score, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.dataHolder = holderlist[position];

            viewHolder.text_name = convertView.findViewById(R.id.content_name);
            viewHolder.text_score = convertView.findViewById(R.id.content_score);
            viewHolder.text_unit = convertView.findViewById(R.id.content_unit);
            viewHolder.text_score.setAdapter(viewHolder.dataHolder.getScoreAdapter());
            viewHolder.text_unit.setAdapter(viewHolder.dataHolder.getUnitAdapter());
            viewHolder.text_unit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int p, long id) {
                    ((TextView) parent.getChildAt(0)).setTextSize(13);
                    Log.d("unit", "select");
                    viewHolder.dataHolder.setUnitSelected(p);
                    listener.complete(holderlist[position], position);

                    /*
                    if (flag>(holderlist.length*2)-1) {
                        viewHolder.dataHolder.setUnitSelected(p);
                        listener.complete(holderlist[position], position);
                    }
                    flag++;*/
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            viewHolder.text_score.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int p, long id) {
                    ((TextView) parent.getChildAt(0)).setTextSize(13);
                    viewHolder.dataHolder.setScoreSelected(p);
                    listener.complete(holderlist[position], position);

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.text_name.setText(list.get(position).getName());
        String score = list.get(position).getScore();
        String unit = list.get(position).getUnit();

        if (score != null) {
            viewHolder.text_score.setSelection(AppUtility.getAppinstance().getScoreOrder(score));
        } else
            viewHolder.text_score.setSelection(0);
        if (unit != null) {
            viewHolder.text_unit.setSelection(Integer.parseInt(unit));
        } else {
            viewHolder.text_unit.setSelection(0);
        }


        return convertView;
    }

    public interface completeAllData {
        void complete(DataHolder holders, int position);
    }

    class ViewHolder {
        DataHolder dataHolder;
        TextView text_name;
        Spinner text_score, text_unit;
    }

}
