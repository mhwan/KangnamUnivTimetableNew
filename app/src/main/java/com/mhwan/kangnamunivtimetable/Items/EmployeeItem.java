package com.mhwan.kangnamunivtimetable.Items;

/**
 * location은 실제 위치 이전까지
 * location2가 실제 연구실 위
 */
public class EmployeeItem {
    private String name;
    private String phonenumber;
    private String email;
    private String location;
    private String affiliation;
    private String location2;

    public EmployeeItem(){}
    public EmployeeItem(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAffiliation() {
        return affiliation;
    }

    public String getLocation2() {
        return location2;
    }

    public void setLocation2(String location2) {
        this.location2 = location2;
    }

    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }
}
