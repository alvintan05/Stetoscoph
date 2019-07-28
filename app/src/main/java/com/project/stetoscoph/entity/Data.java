package com.project.stetoscoph.entity;

public class Data {
    // ini adalah entitas atau biasa disebut model class yang variabel nya disesuaikan dengan kolom pada tabel

    // variabel
    private int id;
    private String title;
    private String time;
    private String data;

    // fungsi
    public String getData() {
        return data;
    }
    // method
    public void setData(String data) {
        this.data = data;
    }
    // fungsi
    public int getId() {
        return id;
    }
    // method
    public void setId(int id) {
        this.id = id;
    }
    // fungsi
    public String getTitle() {
        return title;
    }
    // method
    public void setTitle(String title) {
        this.title = title;
    }
    // fungsi
    public String getTime() {
        return time;
    }
    // method
    public void setTime(String time) {
        this.time = time;
    }
}
