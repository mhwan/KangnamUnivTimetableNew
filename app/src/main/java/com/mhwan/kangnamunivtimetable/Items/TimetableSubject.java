package com.mhwan.kangnamunivtimetable.Items;

public class TimetableSubject extends Subject {
    private String startTime;
    private String endTime;
    private String classRoom;
    private Days day;
    private int id;
    private String s_timeCode;
    private String e_timeCode;
    private int color;

    public TimetableSubject() {
    }

    public TimetableSubject(String name, String classRoom, Days day) {
        super(name, null);
        this.classRoom = classRoom;
        this.day = day;
    }

    public TimetableSubject(String name, String startTime, String endTime, String classRoom, Days day) {
        this(name, classRoom, day);
        this.endTime = endTime;
        this.startTime = startTime;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getStartTimeCode() {
        return s_timeCode;
    }

    public void setStartTimeCode(String code) {
        this.s_timeCode = code;
    }

    public String getEndTimeCode() {
        return e_timeCode;
    }

    public void setEndTimeCode(String code) {
        this.e_timeCode = code;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getClassRoom() {
        return classRoom;
    }

    public Days getDay() {
        return day;
    }

    public void setDay(Days day) {
        this.day = day;
    }

    public void setClassRoom(String classRoom) {
        this.classRoom = classRoom;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TimetableSubject)) return false;
        TimetableSubject that = (TimetableSubject) o;
        return getClassRoom().equals(that.getClassRoom()) &&
                day == that.day && getName().equals(that.getName());
    }

    @Override
    public int hashCode() {
        return getHashes(new String[]{getClassRoom(), day.getDesc(), getName()});
    }

    private int getHashes(Object[] a) {
        if (a == null)
            return 0;
        int result = 1;
        for (Object element : a)
            result = 31 * result + (element == null ? 0 : element.hashCode());
        return result;
    }
}
/*
enum TimeCode{
    T_1A("09:00-09:25"), T_1B("09:25-09:50"), T_2A("09:50-10:15"), T_2B("10:25-10:50"), T_3A("10:50-11:15"),  T_3B("11:15-11:40"), T_4A(), T_4B(), T_5A(),T_5B(), T_6A(), T_6B(), T_7A(), T_7B(),T_8A(),T_8B(),T_9A(),T_9B();
    private String time;
    TimeCode(String s){
        this.time = s;
    }
}*/