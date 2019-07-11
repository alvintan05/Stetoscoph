package com.project.stetoscoph.entity;

public class Data {
    // ini adalah entitas atau biasa disebut model class yang variabel nya disesuaikan dengan kolom pada tabel

    private int id;
    private String title;
    private String time;
    private String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
