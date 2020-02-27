package com.mhwan.kangnamunivtimetable.CustomUI;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mhwan.kangnamunivtimetable.Activity.EmployeeSearchActivity;
import com.mhwan.kangnamunivtimetable.Activity.MapActivity;
import com.mhwan.kangnamunivtimetable.Items.EmployeeItem;
import com.mhwan.kangnamunivtimetable.R;
import com.mhwan.kangnamunivtimetable.Util.AppUtility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EmployeeSearchAdapter extends RecyclerView.Adapter<EmployeeSearchAdapter.ViewHolder> {
    private ArrayList<EmployeeItem> employeeItems;
    private EmployeeSearchActivity activity;

    public EmployeeSearchAdapter(EmployeeSearchActivity context){
        employeeItems = new ArrayList<>();
        this.activity = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(activity).inflate(R.layout.ui_item_employee, viewGroup, false);
        return new EmployeeSearchAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        EmployeeItem item = employeeItems.get(i);

        if (item.getName() != null && item.getName() != "")
            viewHolder.name.setText(item.getName());

        if (item.getLocation() != null && item.getLocation() != "") {
            if (item.getLocation2() !=null && item.getLocation2() != "") {
                viewHolder.locations.setVisibility(View.VISIBLE);
                viewHolder.locations.setText(item.getLocation()+ " - "+ AppUtility.getAppinstance().getFullLectureRoom(item.getLocation2()));
                viewHolder.locs.setVisibility(View.VISIBLE);
            } else {
                viewHolder.locations.setVisibility(View.VISIBLE);
                viewHolder.locations.setText(item.getLocation());
                viewHolder.locs.setVisibility(View.GONE);
            }

        }else {
            viewHolder.locations.setVisibility(View.GONE);
            viewHolder.locs.setVisibility(View.GONE);
        }

        if (item.getAffiliation() != null && item.getAffiliation() != "") {
            viewHolder.affiliation.setVisibility(View.VISIBLE);
            viewHolder.affiliation.setText(item.getAffiliation());
        } else
            viewHolder.affiliation.setVisibility(View.GONE);

        if (item.getEmail() != null && item.getEmail() != "") {
            viewHolder.email.setVisibility(View.VISIBLE);
        } else
            viewHolder.email.setVisibility(View.GONE);

        if (item.getPhonenumber() != null && item.getPhonenumber() != "")
            viewHolder.call.setVisibility(View.VISIBLE);
        else
            viewHolder.call.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return employeeItems.size();
    }

    public void resetEmployeeList(ArrayList<EmployeeItem> employeeItems) {
        this.employeeItems.clear();
        this.employeeItems.addAll(employeeItems);
        notifyDataSetChanged();
    }

    private int getIndex(String str) {
        if (str.contains(" ")) {
            String loc = str.substring(0, str.indexOf(" "));
            String[] locations = activity.getResources().getStringArray(R.array.Building_name_array);
            List<String> arr = Arrays.asList(locations);

            if (loc.equals("심전산학관"))
                return 11;
            else if (arr.contains(loc))
                return arr.indexOf(loc);
            else
                return -1;
        }
        return -1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        RelativeLayout call, locs, email;
        TextView name, locations, affiliation;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            call = itemView.findViewById(R.id.call);
            locs = itemView.findViewById(R.id.loc);
            email = itemView.findViewById(R.id.email);
            name = itemView.findViewById(R.id.name);
            locations = itemView.findViewById(R.id.locations);
            affiliation = itemView.findViewById(R.id.affiliation);

            call.setOnClickListener(this);
            locs.setOnClickListener(this);
            email.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int id = getAdapterPosition();
            EmployeeItem item = employeeItems.get(id);

            switch (v.getId()){
                case R.id.call :
                    Intent intent1 = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", item.getPhonenumber(), null));
                    activity.startActivity(intent1);
                    break;
                case R.id.loc :
                    int index = getIndex(AppUtility.getAppinstance().getFullLectureRoom(item.getLocation2()));
                    Intent intent = new Intent(activity, MapActivity.class);
                    intent.putExtra("loc_index", index);
                    intent.putExtra("loc_extra_string", item.getName()+" / "+item.getLocation()+"-"+item.getLocation2());
                    activity.startActivity(intent);
                    activity.finish();
                    //위치를 어떻게 넘겨줄것인가
                    break;
                case R.id.email :
                    Intent intent3 = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                            "mailto", item.getEmail(), null));
                    activity.startActivity(Intent.createChooser(intent3, null));
                    break;
            }
        }
    }
}
