package com.mhwan.kangnamunivtimetable.Util;

import android.content.Context;


public class AccountPreference extends BaseSharedPreference {
    private final static String key_token = "accountCookies";
    private final static String key_id = "ids";
    private final static String key_help = "helps";
    private final static String key_semester = "semesters";
    private final static String key_map_open = "maps";
    private final static String key_semester_show = "shows";

    public AccountPreference(Context context) {
        super(context, "AccountData");
    }

    public boolean getOpenMaps(){
        return getValue(key_map_open, false);
    }
    public void setOpenMaps(boolean isShow){
        putValue(key_map_open, isShow);
    }
    public int getSemester() {
        return getValue(key_semester, -1);
    }

    public void setSemester(int semester) {
        putValue(key_semester, semester);
    }

    public boolean getShowSemester() {
        return getValue(key_semester_show, true);
    }

    public void setShowSemester(boolean isShow) {
        putValue(key_semester_show, isShow);
    }

    public String getCookies() {
        return getValue(key_token, "");
    }

    public boolean getShowHelp() {
        return getValue(key_help, false);
    }

    public void setShowHelp(boolean isShow) {
        putValue(key_help, isShow);
    }

    public void setCookies(String cookies) {
        putValue(key_token, cookies);
    }

    public void setId(String id) {
        putValue(key_id, id);
    }

    public String getId() {
        return getValue(key_id, null);
    }

    public String getNameFromCookie() {
        String cookie = getCookies();
        String name = cookie.split(";")[6].replace("mast_name_e=", "");

        return name;
    }

    public void removeUserPreference() {
        removeKey(key_token);
        removeKey(key_id);
    }
}
