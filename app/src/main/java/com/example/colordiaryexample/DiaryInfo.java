package com.example.colordiaryexample;

public class DiaryInfo {
    private String db_date;
    private String title;
    private String date;
    private String content;
    private String picture;
    private String db_time;
    private String pre_emotion;
    private String birthday;

    public DiaryInfo(){}
    public DiaryInfo(String title, String date,  String content, String picture, String db_date, String db_time,String pre_emotion, String birthday) {
        this.title = title;
        this.date = date;
        this.content = content;
        this.picture = picture;
        this.db_date = db_date;
        this.db_time = db_time;
        this.pre_emotion =pre_emotion;
        this.birthday = birthday;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getDb_date() {
        return db_date;
    }

    public void setDb_date(String db_date) { this.db_date = db_date; }

    public String getDb_time() {
        return db_time;
    }

    public void setDb_time(String db_time) { this.db_date = db_time; }

    public String getPre_emotion() {
        return pre_emotion;
    }

    public void setPre_emotion(String pre_emotion) { this.pre_emotion = pre_emotion; }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }


}