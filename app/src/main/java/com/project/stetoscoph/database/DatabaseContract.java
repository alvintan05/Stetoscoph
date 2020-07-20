package com.project.stetoscoph.database;

import android.provider.BaseColumns;

public class DatabaseContract {

    // mendefinisikan nama table agar tinggal dipanggil saja di DatabaseHelper
    static String TABLE_DATA = "table_data";

    // mendefinisikan nama kolom agar tinggal dipanggil saja di DatabaseHelper
    static final class NoteColumns implements BaseColumns {
        static String TITLE = "title";
        static String TIME = "time";
        static String DATA = "data";
    }
}
