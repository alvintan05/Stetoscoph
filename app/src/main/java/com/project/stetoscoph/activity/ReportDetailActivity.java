package com.project.stetoscoph.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.project.stetoscoph.R;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ReportDetailActivity extends AppCompatActivity {

    String getdata;
    GraphView graphView;
    LineGraphSeries<DataPoint> series;
    ArrayList<Integer> dataArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_detail);
        getdata = getIntent().getStringExtra("data");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Detail Report");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // inisialisasi
        graphView = (GraphView) findViewById(R.id.graph_report);

        dataArray = jsonToArrray(getdata);

        initGraph(graphView);
    }

    private void initGraph(GraphView graph) {
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(60);

        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(200);

        graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.NONE);
        graph.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        graph.getGridLabelRenderer().setVerticalLabelsVisible(false);

        graph.getViewport().setScrollable(true);
        graph.getViewport().setScalable(true);

        graph.getViewport().setDrawBorder(false);

        // first mSeries is a line
        series = new LineGraphSeries<>();

        for (int i = 0; i < dataArray.size(); i += 0.03) {
            series.appendData(new DataPoint(i, dataArray.get(i)), true, dataArray.size());
        }

        graph.addSeries(series);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    public ArrayList<Integer> jsonToArrray(String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Integer>>() {
        }.getType();
        ArrayList<Integer> array = gson.fromJson(json, type);
        return array;
    }
}