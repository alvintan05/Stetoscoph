package com.project.stetoscoph.database;

import com.project.stetoscoph.entity.Data;

import java.util.ArrayList;

public interface LoadDatasCallback {
    void preExecute();

    void postExecute(ArrayList<Data> datas);
}
