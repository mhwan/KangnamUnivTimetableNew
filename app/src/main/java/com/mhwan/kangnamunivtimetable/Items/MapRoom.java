package com.mhwan.kangnamunivtimetable.Items;

public class MapRoom {
    private String floor, name;

    public MapRoom(String floor, String name) {
        this.floor = floor;
        this.name = name;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
