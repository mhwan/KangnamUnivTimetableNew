package com.mhwan.kangnamunivtimetable.CustomUI;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;


public class ChangeColorAdapter extends RecyclerView.Adapter<ChangeColorAdapter.ViewHolder> {
    private int[] colorArray;
    private Context context;
    private int checkid;

    public ChangeColorAdapter(Context context, int[] arr, int checkid) {
        this.colorArray = arr;
        this.context = context;
        this.checkid = checkid;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        CheckedCircleView item = new CheckedCircleView(context);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT,
                RecyclerView.LayoutParams.WRAP_CONTENT);
        item.setLayoutParams(layoutParams);
        item.setPadding(5, 8, 8, 5);

        return new ViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.checkedCircleView.setBackgroundColor(colorArray[i]);
        if (i == checkid) {
            viewHolder.checkedCircleView.setChecked(true);
            Log.d("i", "checked!!!" + i);
        } else {
            viewHolder.checkedCircleView.setChecked(false);
            Log.d("uncheck", i + "");
        }
        //viewHolder.checkedCircleView.setChecked((i==checkid)?true : false);
    }

    @Override
    public int getItemCount() {
        return colorArray.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public CheckedCircleView checkedCircleView;

        ViewHolder(View itemView) {
            super(itemView);
            checkedCircleView = (CheckedCircleView) itemView;
            checkedCircleView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            checkid = getAdapterPosition();
            Log.d("id", checkid + "");
            notifyDataSetChanged();
        }
    }

    public int getCheckid() {
        return this.checkid;
    }

}
