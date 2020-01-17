package com.mhwan.kangnamunivtimetable.Items;

import android.os.Parcel;
import android.os.Parcelable;

public class ScheduleItem implements Parcelable {
    private int id, priority = 0, subject_color_id = -1, alarm_type = -1;
    private String content, title, semester, writeTime, timemills, subject_name, alarmMills;
    private boolean isFinish = false;

    public ScheduleItem() {
    }

    public ScheduleItem(String content) {
        this.content = content;
    }

    public ScheduleItem(String content, boolean isFinish, String timemills, String subject) {
        this(content);
        this.isFinish = isFinish;
        this.timemills = timemills;
        this.subject_name = subject;
    }

    public String getAlarmMills() {
        return alarmMills;
    }

    public void setAlarmMills(String alarmMills) {
        this.alarmMills = alarmMills;
    }

    public String getSubject_name() {
        return subject_name;
    }

    public void setSubject_name(String subject_name) {
        this.subject_name = subject_name;
    }

    public String getWriteTime() {
        return writeTime;
    }

    public void setWriteTime(String writeTime) {
        this.writeTime = writeTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSubject_color_id() {
        return subject_color_id;
    }

    public void setSubject_color_id(int subject_color_id) {
        this.subject_color_id = subject_color_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public boolean isFinish() {
        return isFinish;
    }

    public void setFinish(boolean finish) {
        isFinish = finish;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getTimemills() {
        return timemills;
    }

    public void setTimemills(String timemills) {
        this.timemills = timemills;
    }

    public void setAlarm_type(int alarm_type) {
        this.alarm_type = alarm_type;
    }

    public int getAlarm_type() {
        return this.alarm_type;
    }

    public ScheduleItem(Parcel parcel) {
        this.id = parcel.readInt();
        this.content = parcel.readString();
        this.title = parcel.readString();
        this.semester = parcel.readString();
        this.writeTime = parcel.readString();
        this.timemills = parcel.readString();
        this.subject_name = parcel.readString();
        this.priority = parcel.readInt();
        this.isFinish = parcel.readByte() != 0;
        this.alarmMills = parcel.readString();
        this.alarm_type = parcel.readInt();
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public ScheduleItem createFromParcel(Parcel in) {
            return new ScheduleItem(in);
        }

        public ScheduleItem[] newArray(int size) {
            return new ScheduleItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.content);
        dest.writeString(this.title);
        dest.writeString(this.semester);
        dest.writeString(this.writeTime);
        dest.writeString(this.timemills);
        dest.writeString(this.subject_name);
        dest.writeInt(this.priority);
        dest.writeByte((byte) (this.isFinish ? 1 : 0));
        dest.writeString(this.alarmMills);
        dest.writeInt(this.alarm_type);
    }
}
