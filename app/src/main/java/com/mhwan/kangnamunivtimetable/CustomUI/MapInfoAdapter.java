package com.mhwan.kangnamunivtimetable.CustomUI;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.mhwan.kangnamunivtimetable.Items.MapRoom;
import com.mhwan.kangnamunivtimetable.R;

import java.util.ArrayList;

public class MapInfoAdapter extends BaseAdapter implements Filterable {
    private Context context;
    private ArrayList<MapRoom> mapRoomslist, filterlist;

    public MapInfoAdapter(Context context) {
        this.context = context;
    }

    public void setArraylist(ArrayList list) {
        if (mapRoomslist != null && filterlist != null) {
            mapRoomslist.clear();
            filterlist.clear();
        }
        this.mapRoomslist = list;
        this.filterlist = list;
    }

    @Override
    public int getCount() {
        return mapRoomslist.size();
    }

    @Override
    public MapRoom getItem(int position) {
        return mapRoomslist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Viewholder viewholder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.ui_item_map_info, parent, false);
            viewholder = new Viewholder();
            viewholder.text_floor = convertView.findViewById(R.id.item_map_info_floor);
            viewholder.text_name = convertView.findViewById(R.id.item_map_info_name);

            convertView.setTag(viewholder);
        } else
            viewholder = (Viewholder) convertView.getTag();
        MapRoom info = mapRoomslist.get(position);
        viewholder.text_floor.setText(info.getFloor());
        viewholder.text_name.setText(info.getName());

        return convertView;
    }

    class Viewholder {
        TextView text_name, text_floor;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults return_filter = new FilterResults();
                final ArrayList<MapRoom> results = new ArrayList<>();
                if (constraint != null) {
                    if (filterlist != null && filterlist.size() > 0) {
                        for (final MapRoom m : filterlist) {
                            if (m.getName().toLowerCase().contains(constraint.toString()))
                                results.add(m);
                            else if (m.getFloor().toUpperCase().contains(constraint.toString()))
                                results.add(m);
                        }
                    }
                    return_filter.values = results;
                }
                return return_filter;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mapRoomslist = (ArrayList<MapRoom>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
