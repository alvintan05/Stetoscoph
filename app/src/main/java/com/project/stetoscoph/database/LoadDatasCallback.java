package com.project.stetoscoph.database;

import com.project.stetoscoph.entity.Data;

import java.util.ArrayList;

public interface LoadDatasCallback {
    // interface yang berisi method
    // method nantinya akan muncul pada class yang melakukan implements pada interface ini
    // interface ini dipakai di ReportFragment
    void preExecute();
    void postExecute(ArrayList<Data> datas);
}
