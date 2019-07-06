package com.project.stetoscoph;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.project.stetoscoph.database.DMLHelper;
import com.project.stetoscoph.database.LoadDatasCallback;
import com.project.stetoscoph.entity.Data;

import java.lang.ref.WeakReference;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReportFragment extends Fragment implements LoadDatasCallback {

    private RecyclerView recyclerView;
    private AdapterReportData adapterReportData;
    private DMLHelper dmlHelper;
    private ProgressDialog progressDialog;

    public ReportFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_report, container, false);

        // inisialisasi
        recyclerView = (RecyclerView) v.findViewById(R.id.rv_data);
        dmlHelper = DMLHelper.getInstance(getActivity());
        progressDialog = new ProgressDialog(getActivity());

        progressDialog.setMessage("Harap Tunggu");

        dmlHelper.open();

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        adapterReportData = new AdapterReportData(getActivity());
        recyclerView.setAdapter(adapterReportData);

        new LoadDatasAsync(dmlHelper, this).execute();

        return v;
    }

    @Override
    public void preExecute() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog.show();
            }
        });
    }

    @Override
    public void postExecute(ArrayList<Data> datas) {
        progressDialog.dismiss();
        adapterReportData.setListData(datas);
    }

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
            weakCallback.get().preExecute();
        }

        @Override
        protected ArrayList<Data> doInBackground(Void... voids) {
            return weakDataHelper.get().getAllData();
        }

        @Override
        protected void onPostExecute(ArrayList<Data> data) {
            super.onPostExecute(data);
            weakCallback.get().postExecute(data);
        }
    }
}
