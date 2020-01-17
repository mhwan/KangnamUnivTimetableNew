package com.mhwan.kangnamunivtimetable.Items;

public enum Days {
    SUNDAY("SUN"), MONDAY("MON"), TUESDAY("TUE"), WEDNESDAY("WED"), THURSDAY("THU"), FRIDAY("FRI"), SATURDAY("SAT");
    private String desc;

    Days(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public int getIndex() {
        return ordinal();
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static Days fromString(String text) {
        for (Days b : Days.values()) {
            if (b.desc.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }

    public static Days[] getWeekDays() {
        Days[] temp = Days.values();
        Days[] value = new Days[5];
        for (int i = 1; i < 6; i++) {
            value[i - 1] = temp[i];
        }
        return value;
    }

    public static Days[] getAllDays() {
        return Days.values();
    }
}
