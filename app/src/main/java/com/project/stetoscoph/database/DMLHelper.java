package com.project.stetoscoph.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.project.stetoscoph.entity.Data;

import java.util.ArrayList;

import static android.provider.BaseColumns._ID;
import static com.project.stetoscoph.database.DatabaseContract.NoteColumns.DATA;
import static com.project.stetoscoph.database.DatabaseContract.NoteColumns.TIME;
import static com.project.stetoscoph.database.DatabaseContract.NoteColumns.TITLE;
import static com.project.stetoscoph.database.DatabaseContract.TABLE_DATA;

public class DMLHelper {

    private static final String DATABASE_TABLE = TABLE_DATA;

    private static DatabaseHelper dataBaseHelper;
    private static DMLHelper INSTANCE;
    private static SQLiteDatabase database;

    private DMLHelper(Context context) {
        dataBaseHelper = new DatabaseHelper(context);
    }

    // untuk memastikan bahwa hanya ada satu objek yang terpanggil
    public static DMLHelper getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (SQLiteOpenHelper.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DMLHelper(context);
                }
            }
        }
        return INSTANCE;
    }

    // untuk membuka dan menutup koneksi database
    public void open() throws SQLException {
        database = dataBaseHelper.getWritableDatabase();
    }

    public void close() {
        dataBaseHelper.close();
        if (database.isOpen())
            database.close();
    }

    // fungsi untuk mengambil data dari database
    public ArrayList<Data> getAllData() {
        ArrayList<Data> arrayList = new ArrayList<>();
        Cursor cursor = database.query(DATABASE_TABLE, null,
                null,
                null,
                null,
                null,
                _ID + " DESC",
                null);
        cursor.moveToFirst();
        Data data;
        if (cursor.getCount() > 0) {
            do {
                data = new Data();
                data.setId(cursor.getInt(cursor.getColumnIndexOrThrow(_ID)));
                data.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(TITLE)));
                data.setTime(cursor.getString(cursor.getColumnIndexOrThrow(TIME)));
                data.setData(cursor.getString(cursor.getColumnIndexOrThrow(DATA)));
                arrayList.add(data);
                cursor.moveToNext();
            } while (!cursor.isAfterLast());
        }
        cursor.close();
        return arrayList;
    }

    // fungsi untuk memasukkan data ke database
    public long insertData(Data data) {
        ContentValues args = new ContentValues();
        args.put(TITLE, data.getTitle());
        args.put(TIME, data.getTime());
        args.put(DATA, data.getData());
        return database.insert(DATABASE_TABLE, null, args);
    }

    // fungsi untuk memperbarui data
    public int updateData(Data data) {
        ContentValues args = new ContentValues();
        args.put(TITLE, data.getTitle());
        args.put(TIME, data.getTime());
        return database.update(DATABASE_TABLE, args, _ID + "= '" + data.getId() + "'", null);
    }

    // fungsi untuk menghapus data
    public int deleteData(int id) {
        return database.delete(TABLE_DATA, _ID + " = '" + id + "'", null);
    }
}