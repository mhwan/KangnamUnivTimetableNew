package com.mhwan.kangnamunivtimetable.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import com.mhwan.kangnamunivtimetable.Items.Days;
import com.mhwan.kangnamunivtimetable.Items.ScheduleItem;
import com.mhwan.kangnamunivtimetable.Items.TimetableSubject;
import com.mhwan.kangnamunivtimetable.Util.AppUtility;

import java.util.ArrayList;

/*
 * 시간표 저장하기위한 db, 모든 시간표 등록, 겟, 지우기, 색깔수정, 학점(성적) 등록
 * 과제 저장,
 * */
public class AppDatabaseHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "KNU_TIMETABLE.db";
    private static final String SQL_CREATE_TABLE_ENTRIES =
            "CREATE TABLE " + TimeTableEntry.TABLE_NAME + " (" +
                    TimeTableEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    TimeTableEntry.COLUMN_NAME_TITLE + " TEXT," +
                    TimeTableEntry.COLUMN_NAME_DAY + " TEXT," +
                    TimeTableEntry.COLUMN_NAME_TIME + " TEXT," +
                    TimeTableEntry.COLUMN_NAME_TIMECODE + " TEXT," +
                    TimeTableEntry.COLUMN_COLOR + " INTEGER," +
                    TimeTableEntry.COLUMN_CLASSROOM + " TEXT," +
                    TimeTableEntry.COLUMN_SCORE + " TEXT," +
                    TimeTableEntry.COLUMN_UNIT + " TEXT)";

    private static final String SQL_DELETE_TABLE_ENTRIES =
            "DROP TABLE IF EXISTS " + TimeTableEntry.TABLE_NAME;

    private static final String SQL_CREATE_SCHEDULE_ENTRIES =
            "CREATE TABLE " + ScheduleEntry.TABLE_NAME + " (" +
                    ScheduleEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    ScheduleEntry.COLUMN_CONTENT + " TEXT," +
                    ScheduleEntry.COLUMN_TITLE + " TEXT," +
                    ScheduleEntry.COLUMN_WRITETIME + " TEXT," +
                    ScheduleEntry.COLUMN_TIMEMILLIS + " TEXT," +
                    ScheduleEntry.COLUMN_SUBJECT + " TEXT," +
                    ScheduleEntry.COLUMN_PRIORITY + " INTEGER," +
                    ScheduleEntry.COLUMN_ISFINISH + " INTEGER," +
                    ScheduleEntry.COLUMN_SEMESTER + " TEXT," +
                    ScheduleEntry.COLUMN_ALARMTYPE + " INTEGER," +
                    ScheduleEntry.COLUMN_ALARMMILLIS + " TEXT)";
    private static final String SQL_DELETE_SCHEDULE_ENTRIES =
            "DROP TABLE IF EXISTS " + TimeTableEntry.TABLE_NAME;

    public AppDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_ENTRIES);
        db.execSQL(SQL_CREATE_SCHEDULE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_TABLE_ENTRIES);
        db.execSQL(SQL_DELETE_SCHEDULE_ENTRIES);
        onCreate(db);
    }

    /**
     * 시간표 부분
     */
    public void addAllTimeTable(ArrayList<TimetableSubject> subjects) {
        SQLiteDatabase db = this.getWritableDatabase();
        for (TimetableSubject subject : subjects)
            addTimeTableItem(db, subject);

        Log.d("db", "add!!");
    }

    private void addTimeTableItem(SQLiteDatabase db, TimetableSubject subject) {
        ContentValues values = new ContentValues();
        values.put(TimeTableEntry.COLUMN_NAME_TITLE, subject.getName());
        values.put(TimeTableEntry.COLUMN_CLASSROOM, subject.getClassRoom());
        values.put(TimeTableEntry.COLUMN_COLOR, subject.getColor());
        values.put(TimeTableEntry.COLUMN_NAME_DAY, subject.getDay().getDesc());
        values.put(TimeTableEntry.COLUMN_NAME_TIME, subject.getStartTime() + "-" + subject.getEndTime());
        values.put(TimeTableEntry.COLUMN_NAME_TIMECODE, subject.getStartTimeCode() + "-" + subject.getEndTimeCode());
        values.put(TimeTableEntry.COLUMN_SCORE, subject.getScore());
        values.put(TimeTableEntry.COLUMN_UNIT, subject.getUnit());
        db.insert(TimeTableEntry.TABLE_NAME, null, values);
    }

    public String[] getSubjectList() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + TimeTableEntry.COLUMN_NAME_TITLE + " FROM " + TimeTableEntry.TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor == null || cursor.getCount() == 0)
            return null;

        String[] results = new String[cursor.getCount()];
        int i = 0;
        try {
            if (cursor.moveToFirst()) {
                do {
                    results[i] = cursor.getString(cursor.getColumnIndex(TimeTableEntry.COLUMN_NAME_TITLE));
                    i++;
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }

        return results;
    }

    public TimetableSubject getTimeTableItem(String subject) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TimeTableEntry.TABLE_NAME + " WHERE " + TimeTableEntry.COLUMN_NAME_TITLE + " = \'" + subject + "\'";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor == null || cursor.getCount() == 0)
            return null;

        TimetableSubject item = new TimetableSubject();
        try {
            if (cursor.moveToFirst()) {
                item.setId(cursor.getInt(0));
                item.setName(cursor.getString(1));
                item.setDay(Days.fromString(cursor.getString(2)));
                String[] time = cursor.getString(3).split("-");
                item.setStartTime(time[0]);
                item.setEndTime(time[1]);
                String[] timecode = cursor.getString(4).split("-");
                item.setStartTimeCode(timecode[0]);
                item.setEndTimeCode(timecode[1]);
                item.setColor(cursor.getInt(5));
                item.setClassRoom(cursor.getString(6));
                item.setScore(cursor.getString(7));
                item.setUnit(cursor.getString(8));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return item;
    }

    public ArrayList<TimetableSubject> getAllTimeTable() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TimeTableEntry.TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor == null || cursor.getCount() == 0)
            return null;

        Log.d("db", "cursor yes!!");
        ArrayList<TimetableSubject> subjects = new ArrayList<>();
        try {
            if (cursor.moveToFirst()) {
                do {
                    TimetableSubject subject = new TimetableSubject();
                    subject.setId(cursor.getInt(0));
                    subject.setName(cursor.getString(1));
                    subject.setDay(Days.fromString(cursor.getString(2)));
                    String[] time = cursor.getString(3).split("-");
                    subject.setStartTime(time[0]);
                    subject.setEndTime(time[1]);
                    String[] timecode = cursor.getString(4).split("-");
                    subject.setStartTimeCode(timecode[0]);
                    subject.setEndTimeCode(timecode[1]);
                    subject.setColor(cursor.getInt(5));
                    subject.setClassRoom(cursor.getString(6));
                    subject.setScore(cursor.getString(7));
                    subject.setUnit(cursor.getString(8));

                    subjects.add(subject);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }


        return subjects;
    }

    public void removeAllTimetable() {
        SQLiteDatabase db = this.getReadableDatabase();
        db.delete(TimeTableEntry.TABLE_NAME, null, null);
    }

    public void updateColor(int id, int color) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TimeTableEntry.COLUMN_COLOR, color);
        db.update(TimeTableEntry.TABLE_NAME, contentValues, TimeTableEntry._ID + "=" + id, null);

    }

    public int getColor(String subject) {
        SQLiteDatabase db = this.getReadableDatabase();
        int color = -1;
        String query = "SELECT " + TimeTableEntry.COLUMN_COLOR + " FROM " + TimeTableEntry.TABLE_NAME + " WHERE " + TimeTableEntry.COLUMN_NAME_TITLE + " = \'" + subject + "\'";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor == null || cursor.getCount() == 0)
            return color;

        try {
            if (cursor.moveToFirst()) {
                color = cursor.getInt(cursor.getColumnIndex(TimeTableEntry.COLUMN_COLOR));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return color;
    }

    public void updateScoreUnit(String unit, String score, int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TimeTableEntry.COLUMN_UNIT, unit);
        contentValues.put(TimeTableEntry.COLUMN_SCORE, score);
        db.update(TimeTableEntry.TABLE_NAME, contentValues, TimeTableEntry._ID + "=" + id, null);

    }


    /**
     * 스케쥴 부분
     */
    public int addNewScheduleItem(ScheduleItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        long id = db.insert(ScheduleEntry.TABLE_NAME, null, getScheduleItemtoContentValue(item));

        return (int) id;
    }

    public void changeFinishedItem(int id, boolean isFinish) {
        if (id < 0)
            return;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ScheduleEntry.COLUMN_ISFINISH, isFinish ? 1 : 0);
        db.update(ScheduleEntry.TABLE_NAME, contentValues, ScheduleEntry._ID + "=" + id, null);
    }

    public void removeScheduleItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + ScheduleEntry.TABLE_NAME + " where " + ScheduleEntry._ID + "= " + id;
        db.execSQL(query);
    }

    public void editItems(int id, ScheduleItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.update(ScheduleEntry.TABLE_NAME, getScheduleItemtoContentValue(item), ScheduleEntry._ID + "=" + id, null);
    }

    public void removeAllScheduleItems() {
        SQLiteDatabase db = this.getReadableDatabase();
        db.delete(ScheduleEntry.TABLE_NAME, null, null);
    }

    public ScheduleItem getScheduleItem(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + ScheduleEntry.TABLE_NAME + " WHERE " + ScheduleEntry._ID + "=" + id, null);
        if (cursor == null || cursor.getCount() == 0)
            return null;

        ScheduleItem item = new ScheduleItem();
        try {
            if (cursor.moveToFirst()) {
                item.setId(cursor.getInt(cursor.getColumnIndex(ScheduleEntry._ID)));
                item.setContent(cursor.getString(cursor.getColumnIndex(ScheduleEntry.COLUMN_CONTENT)));
                item.setTitle(cursor.getString(cursor.getColumnIndex(ScheduleEntry.COLUMN_TITLE)));
                item.setSemester(cursor.getString(cursor.getColumnIndex(ScheduleEntry.COLUMN_SEMESTER)));
                item.setWriteTime(cursor.getString(cursor.getColumnIndex(ScheduleEntry.COLUMN_WRITETIME)));
                item.setTimemills(cursor.getString(cursor.getColumnIndex(ScheduleEntry.COLUMN_TIMEMILLIS)));
                item.setFinish(cursor.getInt(cursor.getColumnIndex(ScheduleEntry.COLUMN_ISFINISH)) == 1);
                item.setPriority(cursor.getInt(cursor.getColumnIndex(ScheduleEntry.COLUMN_PRIORITY)));
                String subject_name = cursor.getString(cursor.getColumnIndex(ScheduleEntry.COLUMN_SUBJECT));
                item.setSubject_name(subject_name);
                item.setSubject_color_id(getColor(subject_name));
                item.setAlarmMills(cursor.getString(cursor.getColumnIndex(ScheduleEntry.COLUMN_ALARMMILLIS)));
                item.setAlarm_type(cursor.getInt(cursor.getColumnIndex(ScheduleEntry.COLUMN_ALARMTYPE)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return item;
    }

    public ArrayList<ScheduleItem> getPassedScheduleItems(String thisSemester) {
        SQLiteDatabase db = this.getReadableDatabase();
        StringBuffer query = new StringBuffer("SELECT * FROM " + ScheduleEntry.TABLE_NAME);
        query.append(" WHERE " + ScheduleEntry.COLUMN_SEMESTER + " != \'" + thisSemester + "\' ");
        query.append("ORDER BY datetime(\'" + ScheduleEntry.COLUMN_WRITETIME + "\') DESC");


        String q = query.toString();
        Log.d("passed query", q);
        Cursor cursor = db.rawQuery(q, null);
        if (cursor == null || cursor.getCount() == 0)
            return null;

        ArrayList<ScheduleItem> items = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                ScheduleItem item = new ScheduleItem();
                item.setId(cursor.getInt(cursor.getColumnIndex(ScheduleEntry._ID)));
                item.setContent(cursor.getString(cursor.getColumnIndex(ScheduleEntry.COLUMN_CONTENT)));
                item.setTitle(cursor.getString(cursor.getColumnIndex(ScheduleEntry.COLUMN_TITLE)));
                item.setSemester(cursor.getString(cursor.getColumnIndex(ScheduleEntry.COLUMN_SEMESTER)));
                item.setWriteTime(cursor.getString(cursor.getColumnIndex(ScheduleEntry.COLUMN_WRITETIME)));
                //null가능성
                if (!cursor.getString(cursor.getColumnIndex(ScheduleEntry.COLUMN_TIMEMILLIS)).equals("null"))
                    item.setTimemills(cursor.getString(cursor.getColumnIndex(ScheduleEntry.COLUMN_TIMEMILLIS)));
                item.setFinish(true);
                item.setPriority(cursor.getInt(cursor.getColumnIndex(ScheduleEntry.COLUMN_PRIORITY)));
                String subject_name = cursor.getString(cursor.getColumnIndex(ScheduleEntry.COLUMN_SUBJECT));
                item.setSubject_name(subject_name);
                item.setSubject_color_id(getColor(subject_name));

                items.add(item);
            } while (cursor.moveToNext());
        }

        return items;
    }

    public ArrayList<ScheduleItem> getApproachScheduleItems(String subject, int maxCount) {
        SQLiteDatabase db = this.getReadableDatabase();
        StringBuffer stringBuffer = new StringBuffer("SELECT * FROM " + ScheduleEntry.TABLE_NAME);
        stringBuffer.append(" WHERE " + ScheduleEntry.COLUMN_SEMESTER + " = \'" + AppUtility.getAppinstance().getNowSemester() + "\' ");
        stringBuffer.append("AND " + ScheduleEntry.COLUMN_ISFINISH + " = \'0\' ");
        if (subject == null) {
            stringBuffer.append("ORDER BY " + ScheduleEntry.COLUMN_PRIORITY + " DESC, " + ScheduleEntry.COLUMN_TIMEMILLIS + " = \'null\' asc, datetime(" + ScheduleEntry.COLUMN_TIMEMILLIS + ") ASC, datetime(" + ScheduleEntry.COLUMN_WRITETIME + ") ASC");
        } else {
            stringBuffer.append("AND " + ScheduleEntry.COLUMN_SUBJECT + " = \'" + subject + "\' ");
            stringBuffer.append("ORDER BY " + ScheduleEntry.COLUMN_PRIORITY + " DESC, " + ScheduleEntry.COLUMN_TIMEMILLIS + " = \'null\' asc, datetime(" + ScheduleEntry.COLUMN_TIMEMILLIS + ") ASC, datetime(" + ScheduleEntry.COLUMN_WRITETIME + ") ASC");
        }


        String query = stringBuffer.toString();
        Log.d("approach query", query);
        Cursor cursor = db.rawQuery(query, null);

        if (cursor == null || cursor.getCount() == 0)
            return null;

        ArrayList<ScheduleItem> items = new ArrayList<>();
        int i = 0;
        if (cursor.moveToFirst()) {
            do {
                ScheduleItem item = new ScheduleItem();
                item.setId(cursor.getInt(cursor.getColumnIndex(ScheduleEntry._ID)));
                item.setContent(cursor.getString(cursor.getColumnIndex(ScheduleEntry.COLUMN_CONTENT)));
                item.setTitle(cursor.getString(cursor.getColumnIndex(ScheduleEntry.COLUMN_TITLE)));
                item.setSemester(cursor.getString(cursor.getColumnIndex(ScheduleEntry.COLUMN_SEMESTER)));
                item.setWriteTime(cursor.getString(cursor.getColumnIndex(ScheduleEntry.COLUMN_WRITETIME)));
                //null가능성
                if (!cursor.getString(cursor.getColumnIndex(ScheduleEntry.COLUMN_TIMEMILLIS)).equals("null"))
                    item.setTimemills(cursor.getString(cursor.getColumnIndex(ScheduleEntry.COLUMN_TIMEMILLIS)));
                item.setFinish(cursor.getInt(cursor.getColumnIndex(ScheduleEntry.COLUMN_ISFINISH)) == 1);
                item.setPriority(cursor.getInt(cursor.getColumnIndex(ScheduleEntry.COLUMN_PRIORITY)));
                String subject_name = cursor.getString(cursor.getColumnIndex(ScheduleEntry.COLUMN_SUBJECT));
                item.setSubject_name(subject_name);
                item.setSubject_color_id(getColor(subject_name));
                item.setAlarmMills(cursor.getString(cursor.getColumnIndex(ScheduleEntry.COLUMN_ALARMMILLIS)));
                item.setAlarm_type(cursor.getInt(cursor.getColumnIndex(ScheduleEntry.COLUMN_ALARMTYPE)));

                items.add(item);
                i++;
                if (i >= maxCount)
                    break;

            } while (cursor.moveToNext());
        }

        return items;
    }

    /**
     * @param sort        정렬방법, 0 : 다가오는 일정, 1 : 작성순, 2: 과목별
     * @param getfinished 끝난 일정도 불러올것인지 (기본은 true)
     * @return
     */
    public ArrayList<ScheduleItem> getAllNowScheduleItems(int sort, boolean getfinished) {
        Log.d("ordertype", sort + "");
        SQLiteDatabase db = this.getReadableDatabase();
        StringBuffer stringBuffer = new StringBuffer("SELECT * FROM " + ScheduleEntry.TABLE_NAME);
        stringBuffer.append(getQueryForOrdering(sort, getfinished, AppUtility.getAppinstance().getNowSemester()));

        Log.d("AllNow query", stringBuffer.toString());
        Cursor cursor = db.rawQuery(stringBuffer.toString(), null);
        ArrayList<ScheduleItem> items = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                ScheduleItem item = new ScheduleItem();
                item.setId(cursor.getInt(cursor.getColumnIndex(ScheduleEntry._ID)));
                item.setContent(cursor.getString(cursor.getColumnIndex(ScheduleEntry.COLUMN_CONTENT)));
                item.setTitle(cursor.getString(cursor.getColumnIndex(ScheduleEntry.COLUMN_TITLE)));
                item.setSemester(cursor.getString(cursor.getColumnIndex(ScheduleEntry.COLUMN_SEMESTER)));
                item.setWriteTime(cursor.getString(cursor.getColumnIndex(ScheduleEntry.COLUMN_WRITETIME)));
                //null가능성
                if (!cursor.getString(cursor.getColumnIndex(ScheduleEntry.COLUMN_TIMEMILLIS)).equals("null"))
                    item.setTimemills(cursor.getString(cursor.getColumnIndex(ScheduleEntry.COLUMN_TIMEMILLIS)));
                item.setFinish(cursor.getInt(cursor.getColumnIndex(ScheduleEntry.COLUMN_ISFINISH)) == 1);
                item.setPriority(cursor.getInt(cursor.getColumnIndex(ScheduleEntry.COLUMN_PRIORITY)));
                String subject_name = cursor.getString(cursor.getColumnIndex(ScheduleEntry.COLUMN_SUBJECT));
                item.setSubject_name(subject_name);
                item.setSubject_color_id(getColor(subject_name));
                item.setAlarmMills(cursor.getString(cursor.getColumnIndex(ScheduleEntry.COLUMN_ALARMMILLIS)));
                item.setAlarm_type(cursor.getInt(cursor.getColumnIndex(ScheduleEntry.COLUMN_ALARMTYPE)));
                items.add(item);
            } while (cursor.moveToNext());
        }

        return items;
    }

    private String getQueryForOrdering(int sort, boolean finished, String nowSemester) {
        StringBuffer query = new StringBuffer(" WHERE " + ScheduleEntry.COLUMN_SEMESTER + " = \'" + nowSemester + "\' ");
        if (!finished) {
            query.append("AND ");
            query.append(ScheduleEntry.COLUMN_ISFINISH + " = \'0\' ");
        }

        if (sort == 0) {
            if (finished) {
                query.append("ORDER BY " + ScheduleEntry.COLUMN_ISFINISH + " = \'1\' asc, ");
            } else
                query.append("ORDER BY ");
            /*
            query.append("AND ");
            query.append(ScheduleEntry.COLUMN_TIMEMILLIS + " IS NOT NULL ");
            query.append("AND ");
            query.append(ScheduleEntry.COLUMN_TIMEMILLIS+ " != \'null\' ");*/
            query.append(ScheduleEntry.COLUMN_PRIORITY + " DESC, " + ScheduleEntry.COLUMN_TIMEMILLIS + " = \'null\' asc, datetime(" + ScheduleEntry.COLUMN_TIMEMILLIS + ") ASC, datetime(" + ScheduleEntry.COLUMN_WRITETIME + ") ASC");
        } else if (sort == 1) {
            query.append("AND ");
            query.append(ScheduleEntry.COLUMN_TIMEMILLIS + " IS NOT NULL ");
            query.append("AND ");
            query.append(ScheduleEntry.COLUMN_TIMEMILLIS + " != \'null\' ");
            query.append("ORDER BY datetime(" + ScheduleEntry.COLUMN_TIMEMILLIS + ") ASC");
        } else if (sort == 2) {
            query.append("ORDER BY datetime(" + ScheduleEntry.COLUMN_WRITETIME + ") DESC");
        } else if (sort == 3) {
            query.append("AND ");
            query.append(ScheduleEntry.COLUMN_SUBJECT + " IS NOT NULL ");
            query.append("ORDER BY " + ScheduleEntry.COLUMN_SUBJECT + " ASC");
        }

        return query.toString();
    }


    private ContentValues getScheduleItemtoContentValue(ScheduleItem item) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ScheduleEntry.COLUMN_TITLE, item.getTitle());
        contentValues.put(ScheduleEntry.COLUMN_CONTENT, item.getContent());
        contentValues.put(ScheduleEntry.COLUMN_TIMEMILLIS, String.valueOf(item.getTimemills()));
        contentValues.put(ScheduleEntry.COLUMN_ISFINISH, item.isFinish() ? 1 : 0);
        contentValues.put(ScheduleEntry.COLUMN_PRIORITY, item.getPriority());
        contentValues.put(ScheduleEntry.COLUMN_SEMESTER, item.getSemester());
        contentValues.put(ScheduleEntry.COLUMN_SUBJECT, item.getSubject_name());
        contentValues.put(ScheduleEntry.COLUMN_WRITETIME, item.getWriteTime());
        contentValues.put(ScheduleEntry.COLUMN_ALARMMILLIS, item.getAlarmMills());
        contentValues.put(ScheduleEntry.COLUMN_ALARMTYPE, item.getAlarm_type());

        return contentValues;
    }

    public static class TimeTableEntry implements BaseColumns {
        public static final String TABLE_NAME = "timetable";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_DAY = "day";
        public static final String COLUMN_NAME_TIME = "time";
        public static final String COLUMN_NAME_TIMECODE = "timecode";
        public static final String COLUMN_COLOR = "colorcode";
        public static final String COLUMN_CLASSROOM = "classroom";
        public static final String COLUMN_SCORE = "score";
        public static final String COLUMN_UNIT = "unit";
    }

    public static class ScheduleEntry implements BaseColumns {
        public static final String TABLE_NAME = "schedule";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_WRITETIME = "writetime";
        public static final String COLUMN_TIMEMILLIS = "timemillis";
        public static final String COLUMN_PRIORITY = "priority";
        public static final String COLUMN_ISFINISH = "finish";
        public static final String COLUMN_SEMESTER = "semester";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_SUBJECT = "subject";
        public static final String COLUMN_ALARMMILLIS = "alarmmillis";
        public static final String COLUMN_ALARMTYPE = "alarmType";
    }
}
