package com.project.stetoscoph.activity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.project.stetoscoph.R;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;

public class ReportDetailActivity extends AppCompatActivity {

    String getdata;
    GraphView graphView;
    LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
    private double graphLastXValue = 0d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_detail);
        getdata = getIntent().getStringExtra("data");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Detail Report");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // inisialisasi widget grafik
        graphView = (GraphView) findViewById(R.id.graph_report);

        // memanggil method untuk menyiapkan grafik
        initGraph(graphView);

        // Menjalankan proses di background agar tidak mengganggu UI (User Interface) dan mencegah hang
        new TampilData().execute();

    }

    // untuk menginisialisasi bagian grafik
    private void initGraph(GraphView graph) {
        graph.getViewport().setXAxisBoundsManual(true);
        // minimal x
        graph.getViewport().setMinX(0);
        // maximal x
        graph.getViewport().setMaxX(30);

        graph.getViewport().setYAxisBoundsManual(true);
        // minimal y
        graph.getViewport().setMinY(-200);
        // maximal y
        graph.getViewport().setMaxY(200);

        graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.NONE);
        // title di x
        graph.getGridLabelRenderer().setHorizontalAxisTitle("time (s)");
        // title di y
        graph.getGridLabelRenderer().setVerticalAxisTitle("amplitudo (A)");

        // agar grafik dapat di scroll
        graph.getViewport().setScrollable(true);
        // agar grafik dapat di zoom
        graph.getViewport().setScalable(true);

        graph.getViewport().setDrawBorder(false);

        graph.addSeries(series);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    public ArrayList<Double> jsonToArrray(String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Double>>() {
        }.getType();
        ArrayList<Double> array = gson.fromJson(json, type);
        return array;
    }

    /* Ini adalah proses AsyncTask yang terjadi di background, digunakan untuk mengambil data dan mengolahnya menjadi grafik
    Karena jumlah data yang sangat banyak. Apabila tidak menggunakan ini, maka akan terjadi hang pada aplikasi
    * */
    public class TampilData extends AsyncTask<Void, Void, ArrayList> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // Memunculkan progress dialog
            progressDialog = new ProgressDialog(ReportDetailActivity.this);
            progressDialog.setMessage("Memuat Data...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected ArrayList doInBackground(Void... voids) {
            ArrayList<Double> arrayList = jsonToArrray(getdata);
            // Memanggil method
            setData(arrayList);
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList arrayList) {
            super.onPostExecute(arrayList);
            // Menghilangkan progress dialog
            progressDialog.dismiss();
        }
    }

    // Method untuk menggambarkan setiap titik ke dalam grafik
    public void setData(final ArrayList<Double> arr) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < arr.size(); i++) {
                    series.appendData(new DataPoint(graphLastXValue, arr.get(i)), false, arr.size());
                    graphLastXValue += 0.03;
                }
            }
        });
    }
}
