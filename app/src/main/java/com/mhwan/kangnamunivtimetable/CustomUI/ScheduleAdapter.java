package com.mhwan.kangnamunivtimetable.CustomUI;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.mhwan.kangnamunivtimetable.Activity.CreateScheduleActivity;
import com.mhwan.kangnamunivtimetable.Database.AppDatabaseHelper;
import com.mhwan.kangnamunivtimetable.Items.ScheduleItem;
import com.mhwan.kangnamunivtimetable.R;
import com.mhwan.kangnamunivtimetable.Util.AppUtility;

import java.util.ArrayList;
import java.util.Calendar;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> implements Filterable {
    private Context context;
    private ArrayList<ScheduleItem> scheduleList, filterlist;
    private int[] colorArray;
    private AppDatabaseHelper helper;
    private int order;
    private boolean showFinished;
    private Calendar calendar;

    public ScheduleAdapter(Context context, ArrayList list, int[] colorArray, boolean showFinished) {
        this.context = context;
        this.scheduleList = list;
        this.filterlist = list;
        this.showFinished = showFinished;
        this.colorArray = colorArray;
        this.helper = new AppDatabaseHelper(context);
        calendar = Calendar.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.ui_item_schedule, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        final ScheduleItem item = scheduleList.get(i);
        viewHolder.swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        viewHolder.circleView.setCircleBackgroundColor((item.getSubject_color_id() == -1) ? colorArray[25] : colorArray[item.getSubject_color_id()]);
        viewHolder.text_title.setText(item.getTitle());
        viewHolder.text_subject.setText(getSubjectName(item.getSubject_name(), item.getSubject_color_id()));

        String date;

        if (order == 2 || item.getTimemills() == null) {
            date = AppUtility.getAppinstance().changeDateTimeFormat(item.getWriteTime(), AppUtility.AppBase.DATE_FORMAT_DB_WRITE, AppUtility.AppBase.DATE_FORMAT_LIST_WRITE);
        } else {
            date = AppUtility.getAppinstance().changeDateTimeFormat(item.getTimemills(), AppUtility.AppBase.DATE_FORMAT_DB_SCHEDULED, AppUtility.AppBase.DATE_FORMAT_LIST_SCHEDULED);
        }
        viewHolder.text_endDate.setText(date);

        if (item.getTimemills() != null) {
            Calendar calendar = Calendar.getInstance();
            String text = AppUtility.getAppinstance().getRemainTime(AppUtility.getAppinstance().getTimeToCalendar(item.getTimemills(), AppUtility.AppBase.DATE_FORMAT_DB_SCHEDULED).getTimeInMillis(), calendar.getTimeInMillis());
            viewHolder.text_remain.setVisibility(View.VISIBLE);
            if (!item.isFinish()) {
                if (text != null)
                    viewHolder.text_remain.setText(text);
                else
                    viewHolder.text_remain.setText(context.getString(R.string.passed_schedule));
            }
        } else
            viewHolder.text_remain.setVisibility(View.GONE);

        int priority = item.getPriority();
        if (priority > 0) {
            viewHolder.view_priority.setVisibility(View.VISIBLE);
            viewHolder.text_priority.setText(String.valueOf(priority));
        } else
            viewHolder.view_priority.setVisibility(View.GONE);


        if (item.isFinish()) {
            //Log.d("finish - position", i + "");
            viewHolder.background.setClickable(false);
            viewHolder.background.setBackgroundColor(context.getResources().getColor(R.color.colorFinishBackground));
            viewHolder.text_title.setPaintFlags(viewHolder.text_title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            viewHolder.text_endDate.setPaintFlags(viewHolder.text_title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            viewHolder.text_remain.setText(context.getString(R.string.already_finish_schedule));
            viewHolder.button_finish.setText(context.getString(R.string.cancel));
        } else {
            //Log.d("nonfinish - position", i + "");
            viewHolder.background.setBackgroundColor(context.getResources().getColor(android.R.color.white));
            viewHolder.background.setClickable(true);
            viewHolder.button_finish.setText(context.getString(R.string.finish));
            if ((viewHolder.text_title.getPaintFlags() & Paint.STRIKE_THRU_TEXT_FLAG) > 0) {
                viewHolder.text_title.setPaintFlags(0);
                viewHolder.text_endDate.setPaintFlags(0);
            }
        }

    }

    private String getSubjectName(String subjectName, int color) {
        if (subjectName == null)
            return context.getString(R.string.no_subject);
        else {
            if (color == -1)
                return subjectName + " " + context.getString(R.string.no_timetable);
            else
                return subjectName;
        }
    }

    public ArrayList getList() {
        return scheduleList;
    }

    public void refreshAll(int sort, ArrayList<ScheduleItem> list, int type) {
        this.order = sort;
        scheduleList.clear();
        scheduleList.addAll(list);
        filterlist.clear();
        filterlist.addAll(list);
        notifyDataSetChanged();

        if (type >= 0) {
            String message;
            switch (type) {
                case 0:
                    message = context.getString(R.string.message_create_new_schedule);
                    break;
                case 1:
                    message = context.getString(R.string.message_edit_schedule);
                    break;
                default:
                    message = context.getString(R.string.message_changed_schedule_order);
            }


            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        if (scheduleList == null)
            return 0;
        return scheduleList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults return_filter = new FilterResults();
                final ArrayList<ScheduleItem> results = new ArrayList<>();
                if (constraint != null) {
                    if (filterlist != null && filterlist.size() > 0) {
                        for (final ScheduleItem m : filterlist) {

                            if (m.getTitle().toLowerCase().contains(constraint.toString()))
                                results.add(m);
                            else if (m.getContent().toLowerCase().contains(constraint.toString()))
                                results.add(m);
                            else if (m.getSubject_name() != null && m.getSubject_name().toLowerCase().contains(constraint.toString()))
                                results.add(m);
                        }
                    }
                    return_filter.values = results;
                }
                return return_filter;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                scheduleList = (ArrayList<ScheduleItem>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        SwipeLayout swipeLayout;
        TextView button_finish, button_delete, text_endDate, text_title, text_subject, text_priority, text_remain;
        RelativeLayout background, view_priority;
        CircleView circleView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            swipeLayout = itemView.findViewById(R.id.swipe_layout);
            button_finish = itemView.findViewById(R.id.button_swipe_finish);
            button_delete = itemView.findViewById(R.id.button_swipe_delete);
            circleView = itemView.findViewById(R.id.item_subject_circle);
            background = itemView.findViewById(R.id.surface);
            text_endDate = itemView.findViewById(R.id.item_end_date);
            text_subject = itemView.findViewById(R.id.item_subject_name);
            text_title = itemView.findViewById(R.id.item_schedule_title);
            text_priority = itemView.findViewById(R.id.text_priority);
            text_remain = itemView.findViewById(R.id.item_remain_time);
            view_priority = itemView.findViewById(R.id.item_priority_sign);

            button_finish.setOnClickListener(this);
            button_delete.setOnClickListener(this);
            swipeLayout.getSurfaceView().setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int id = getAdapterPosition();
            switch (v.getId()) {
                case R.id.button_swipe_finish:
                    ScheduleItem item = scheduleList.get(id);
                    helper.changeFinishedItem(item.getId(), !item.isFinish());
                    item.setFinish(!item.isFinish());
                    boolean Finish = item.isFinish();
                    StringBuffer buffer = new StringBuffer("선택한 일정이");
                    if (Finish) {
                        buffer.append(" 완료되었습니다.");
                        if (item.getAlarmMills() != null && System.currentTimeMillis() <= AppUtility.getAppinstance().getTimeMillisFromStringDate(item.getAlarmMills(), AppUtility.AppBase.DATE_FORMAT_DB_SCHEDULED)) {
                            buffer.append(" (설정된 알람은 취소됩니다)");
                            AppUtility.getAppinstance().cancelRegisterAlarm(item.getId());
                        }
                    } else {
                        buffer.append(" 취소되었습니다.");
                        if (item.getAlarmMills() != null && System.currentTimeMillis() <= AppUtility.getAppinstance().getTimeMillisFromStringDate(item.getAlarmMills(), AppUtility.AppBase.DATE_FORMAT_DB_SCHEDULED)) {
                            boolean result = AppUtility.getAppinstance().reRegisterAlarm(item);
                            if (!result)
                                buffer.append(" (알람 재설정에 문제가 생겼습니다)");
                            else
                                buffer.append(" (알람이 다시 설정됩니다)");

                        } else
                            buffer.append(" (알람이 다시 설정되지 않습니다)");
                    }
                    scheduleList.set(id, item);
                    swipeLayout.close();
                    if (!showFinished) {
                        scheduleList.remove(id);
                        notifyItemRemoved(id);
                        notifyItemRangeChanged(id, scheduleList.size());
                    } else {
                        notifyDataSetChanged();
                    }

                    Toast.makeText(context, buffer.toString(), Toast.LENGTH_SHORT).show();
                    break;
                case R.id.button_swipe_delete:
                    StringBuffer buffer1 = new StringBuffer("선택한 일정을 삭제했습니다.");
                    if (scheduleList.get(id).getAlarmMills() != null) {
                        buffer1.append(" (설정된 알람은 취소됩니다.)");
                        AppUtility.getAppinstance().cancelRegisterAlarm(scheduleList.get(id).getId());
                    }
                    helper.removeScheduleItem(scheduleList.get(id).getId());
                    scheduleList.remove(id);
                    swipeLayout.close();
                    notifyItemRemoved(id);
                    notifyItemRangeChanged(id, scheduleList.size());

                    Toast.makeText(context, buffer1.toString(), Toast.LENGTH_SHORT).show();
                    break;
                case R.id.surface:
                    Intent intent = new Intent(context, CreateScheduleActivity.class);
                    intent.putExtra("key_schedule_edit", scheduleList.get(id));

                    ((Activity) context).startActivityForResult(intent, AppUtility.AppBase.CREATE_REQUEST_CODE);
                    break;
            }
        }
    }
}
