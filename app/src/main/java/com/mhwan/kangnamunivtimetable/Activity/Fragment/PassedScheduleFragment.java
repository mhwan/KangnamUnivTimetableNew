package com.mhwan.kangnamunivtimetable.Activity.Fragment;

import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.mhwan.kangnamunivtimetable.CustomUI.CircleView;
import com.mhwan.kangnamunivtimetable.CustomUI.DividerItemDecoration;
import com.mhwan.kangnamunivtimetable.Database.AppDatabaseHelper;
import com.mhwan.kangnamunivtimetable.Items.ScheduleItem;
import com.mhwan.kangnamunivtimetable.R;
import com.mhwan.kangnamunivtimetable.Util.AppUtility;

import java.util.ArrayList;

public class PassedScheduleFragment extends Fragment {
    private View view;
    private TextView empty_textview;
    private RecyclerView recyclerView;
    private int[] color_array;
    private ArrayList<ScheduleItem> passedscheduleList;
    private AppDatabaseHelper databaseHelper;

    public PassedScheduleFragment() {
    }

    public static PassedScheduleFragment newInstance() {
        PassedScheduleFragment fragment = new PassedScheduleFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        color_array = getResources().getIntArray(R.array.Color_array);
        databaseHelper = new AppDatabaseHelper(getContext());
        passedscheduleList = getData();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_passed_schedule, container, false);
        initView();
        return view;
    }

    private void initView() {
        empty_textview = view.findViewById(R.id.passed_empty_messages);
        recyclerView = view.findViewById(R.id.passed_schedule_recyclerview);

        if (showEmptyMessage()) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
            recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST, R.drawable.line_divider, getDividerCallback(passedscheduleList)));
            recyclerView.setAdapter(new PassedScheduleAdapter(getContext()));
        }
    }

    private boolean showEmptyMessage() {
        if (passedscheduleList == null || passedscheduleList.size() == 0) {
            recyclerView.setVisibility(View.GONE);
            empty_textview.setVisibility(View.VISIBLE);
            return false;
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            empty_textview.setVisibility(View.GONE);
            return true;
        }
    }

    private ArrayList<ScheduleItem> getData() {
        return databaseHelper.getPassedScheduleItems(AppUtility.getAppinstance().getNowSemester());
    }

    private DividerItemDecoration.DividerCallback getDividerCallback(final ArrayList<ScheduleItem> list) {
        return new DividerItemDecoration.DividerCallback() {
            //true 일때 안보이고 false일때 보임
            @Override
            public boolean isSection(int position) {
                return list.size() - 1 == position;
            }
        };
    }

    class PassedScheduleAdapter extends RecyclerView.Adapter<PassedScheduleAdapter.ViewHolder> {
        private Context context;

        public PassedScheduleAdapter(Context context) {
            this.context = context;
        }

        @NonNull
        @Override
        public PassedScheduleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(context).inflate(R.layout.ui_passed_schedule_item, viewGroup, false);
            return new PassedScheduleAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PassedScheduleAdapter.ViewHolder viewHolder, int i) {
            ScheduleItem item = passedscheduleList.get(i);
            viewHolder.swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
            viewHolder.circleView.setCircleBackgroundColor(color_array[25]);
            viewHolder.text_title.setText(item.getTitle());
            viewHolder.text_subject.setText(getSubjectName(item.getSubject_name(), -1));
            viewHolder.text_endDate.setText(AppUtility.getAppinstance().changeDateTimeFormat(item.getWriteTime(), AppUtility.AppBase.DATE_FORMAT_DB_WRITE, AppUtility.AppBase.DATE_FORMAT_LIST_WRITE));
            viewHolder.text_semester.setText(getSemester(item.getSemester()));

            viewHolder.text_title.setPaintFlags(viewHolder.text_title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            viewHolder.text_endDate.setPaintFlags(viewHolder.text_title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }


        private String getSemester(String semester) {
            return "/ " + semester;
        }

        private String getSubjectName(String subjectName, int color) {
            if (subjectName == null)
                return getString(R.string.no_subject);
            else {
                if (color == -1)
                    return subjectName + " " + getString(R.string.no_timetable);
                else
                    return subjectName;
            }
        }

        @Override
        public int getItemCount() {
            return passedscheduleList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView button_delete, text_endDate, text_title, text_subject, text_semester;
            CircleView circleView;
            SwipeLayout swipeLayout;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                swipeLayout = itemView.findViewById(R.id.swipe_layout);
                button_delete = itemView.findViewById(R.id.button_passed_swipe_delete);
                text_endDate = itemView.findViewById(R.id.item_passed_end_date);
                text_title = itemView.findViewById(R.id.item_passed_schedule_title);
                text_subject = itemView.findViewById(R.id.item_passed_subject_name);
                text_semester = itemView.findViewById(R.id.item_passed_semester);
                circleView = itemView.findViewById(R.id.item_passed_subject_circle);

                button_delete.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                int id = getAdapterPosition();

                switch (v.getId()) {
                    case R.id.button_passed_swipe_delete:
                        databaseHelper.removeScheduleItem(passedscheduleList.get(id).getId());
                        passedscheduleList.remove(id);
                        swipeLayout.close();
                        notifyItemRemoved(id);
                        notifyItemRangeChanged(id, passedscheduleList.size());
                        Toast.makeText(context, getString(R.string.message_delete_schedule), Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }
    }
}
