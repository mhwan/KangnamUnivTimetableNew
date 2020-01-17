package com.mhwan.kangnamunivtimetable.Items;

/*
name : 과목이름
unit : 몇학점짜리인지
score : 학점
classify : 분류
 */
public class Subject {
    private String name;
    private String unit;
    private String score;
    private String classify;

    public Subject() {
    }

    public Subject(String name, String unit, String score, String classify) {
        this.name = name;
        this.unit = unit;
        this.score = score;
        this.classify = classify;
    }

    public Subject(String name, String score) {
        this(name, null, score, null);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getClassify() {
        return classify;
    }

    public void setClassify(String classify) {
        this.classify = classify;
    }
}

enum Type {
    PNF, NONE
}