package com.mhwan.kangnamunivtimetable.Util;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.mhwan.kangnamunivtimetable.Callback.TimeTableDataListener;
import com.mhwan.kangnamunivtimetable.Items.TimetableSubject;
import com.mhwan.kangnamunivtimetable.R;
import com.mhwan.kangnamunivtimetable.Util.KnuUtil.KnuTimeTable;

import java.util.ArrayList;

public class GetTimeTableTask extends AsyncTask<Void, Void, Object> {
    private String id, cookies;
    private Context context;
    private KnuTimeTable timeTable;
    private TimeTableDataListener listener;

    public GetTimeTableTask(Context context, TimeTableDataListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setIdCookie(String id, String cookies) {
        this.id = id;
        this.cookies = cookies;
    }

    @Override
    protected void onPreExecute() {
        if (timeTable == null)
            timeTable = new KnuTimeTable(id, cookies);
        if (!AppUtility.getAppinstance().isNetworkConnection()){
            cancel(true);
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        Toast.makeText(context, context.getString(R.string.message_cancel_timetable), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPostExecute(Object o) {
        if (o instanceof Integer) {
            Toast.makeText(AppContext.getContext(), AppContext.getContext().getString(R.string.message_error_invalid_ssl_certificate), Toast.LENGTH_SHORT).show();
        } else if (o != null && o instanceof ArrayList) {
            //성공
            if (listener != null) {
                ArrayList<TimetableSubject> subjects = (ArrayList<TimetableSubject>) o;
                listener.onDataSet(subjects);
            }
        } else {
            //오류발생
            if (listener != null) {
                listener.onDataSet(null);
                Toast.makeText(AppContext.getContext(), AppContext.getContext().getString(R.string.message_error_get_timetable_task), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected Object doInBackground(Void... voids) {
        if (!isCancelled()) {
            Object result = timeTable.doGetTimeTable2();
            return result;
        }
        return null;
    }
}