package com.mhwan.kangnamunivtimetable.CustomUI;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mhwan.kangnamunivtimetable.R;
import com.mhwan.kangnamunivtimetable.Util.KnuUtil.KnuScholarship;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ScholarshipAdapter extends RecyclerView.Adapter<ScholarshipAdapter.ViewHolder> {
    private Context context;
    private ArrayList<KnuScholarship.ScholarshipItem> scholarshipItemArrayList;
    public ScholarshipAdapter(Context context){
        this.context = context;
        this.scholarshipItemArrayList = new ArrayList<>();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.ui_item_scholarship, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        KnuScholarship.ScholarshipItem item = scholarshipItemArrayList.get(i);
        viewHolder.title.setText(item.name);
        viewHolder.amount.setText(new DecimalFormat("###,###").format(Integer.parseInt(item.amount))+"원");
        viewHolder.department.setText(item.dept);
        viewHolder.grade.setText(item.semester+" / "+item.grade+"학년");
    }

    public void updateData(ArrayList<KnuScholarship.ScholarshipItem> scholarshipItems) {
        scholarshipItemArrayList.clear();
        scholarshipItemArrayList.addAll(scholarshipItems);
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return scholarshipItemArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, amount, grade, department;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.scholar_title);
            amount = itemView.findViewById(R.id.scholar_amount);
            grade = itemView.findViewById(R.id.scholar_grade);
            department = itemView.findViewById(R.id.scholar_department);
        }
    }
}
