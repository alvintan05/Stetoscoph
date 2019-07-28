package com.project.stetoscoph.fragment;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.project.stetoscoph.AdapterReportData;
import com.project.stetoscoph.R;
import com.project.stetoscoph.database.DMLHelper;
import com.project.stetoscoph.database.LoadDatasCallback;
import com.project.stetoscoph.entity.Data;

import java.lang.ref.WeakReference;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReportFragment extends Fragment implements LoadDatasCallback {

    // widget
    private RecyclerView recyclerView;
    private ProgressDialog progressDialog;

    // variabel
    private AdapterReportData adapterReportData;
    private DMLHelper dmlHelper;

    public ReportFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_report, container, false);

        // inisialisasi widget dan variabel
        recyclerView = (RecyclerView) v.findViewById(R.id.rv_data);
        progressDialog = new ProgressDialog(getActivity());

        // membuat objek
        dmlHelper = DMLHelper.getInstance(getActivity());
        // menampilkan progress dialog
        progressDialog.setMessage("Harap Tunggu");

        // membuka database agar dapat diakses
        dmlHelper.open();

        // mengatur list
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        adapterReportData = new AdapterReportData(getActivity());
        recyclerView.setAdapter(adapterReportData);

        // Untuk melakukan load data
        new LoadDatasAsync(dmlHelper, this).execute();

        return v;
    }

    @Override
    public void preExecute() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // menampilkan progress dialog
                progressDialog.show();
            }
        });
    }

    @Override
    public void postExecute(ArrayList<Data> datas) {
        // menutup progress dialog
        progressDialog.dismiss();
        // Menaruh data pada list
        adapterReportData.setListData(datas);
    }

    // AsyncTask ini berjalan di background untuk melakukan proses agar tidak mengganggu UI penguna
    private static class LoadDatasAsync extends AsyncTask<Void, Void, ArrayList<Data>> {

        private final WeakReference<DMLHelper> weakDataHelper;
        private final WeakReference<LoadDatasCallback> weakCallback;

        private LoadDatasAsync(DMLHelper dataHelper, LoadDatasCallback callback) {
            weakDataHelper = new WeakReference<>(dataHelper);
            weakCallback = new WeakReference<>(callback);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // sebelum eksekusi
            // memanggil method preExecute() di atas
            weakCallback.get().preExecute();
        }

        @Override
        protected ArrayList<Data> doInBackground(Void... voids) {
            // melakukan eksekusi
            // Mengambil data dari database
            return weakDataHelper.get().getAllData();
        }

        @Override
        protected void onPostExecute(ArrayList<Data> data) {
            super.onPostExecute(data);
            // setelah eksekusi
            // memanggil method postExecute() di atas
            weakCallback.get().postExecute(data);
        }
    }
}
