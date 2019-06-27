package com.project.stetoscoph;


import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Random;

import me.aflak.bluetooth.Bluetooth;

/**
 * A simple {@link Fragment} subclass.
 */
public class GraphFragment extends Fragment implements Bluetooth.CommunicationCallback {

    Button btnStartStream, btnStopStream;

    //graph init
    static GraphView graphView;
    TextView tvFrekuensi;
    GraphView graph;
    static LineGraphSeries series;
    private static double graph2LastXValue = 0;
    private static int Xview = 10;

    private final Handler mHandler = new Handler();
    private Runnable mTimer;
    private double graphLastXValue = 0d;
    private LineGraphSeries<DataPoint> mSeries;

    private Bluetooth b;

    public GraphFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        b = new Bluetooth(getActivity());
        b.setCommunicationCallback(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_graph, container, false);

        graph = (GraphView) v.findViewById(R.id.graph);
        btnStartStream = (Button) v.findViewById(R.id.btn_start_stream);
        btnStopStream = (Button) v.findViewById(R.id.btn_stop_stream);
        tvFrekuensi = (TextView) v.findViewById(R.id.tv_frekuensi);

        //init(v);

        initGraph(graph);

        btnStartStream.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mTimer = new Runnable() {
                    @Override
                    public void run() {
                        if (graphLastXValue == 9d) {
                            graphLastXValue = 0d;
                            mSeries.resetData(new DataPoint[]{
                                    new DataPoint(graphLastXValue, getRandom())
                            });
                        }
                        graphLastXValue += 0.1;
                        mSeries.appendData(new DataPoint(graphLastXValue, getRandom()), false, 50);
                        mHandler.postDelayed(this, 50);
                    }
                };
                mHandler.postDelayed(mTimer, 100);
            }
        });

        btnStopStream.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandler.removeCallbacks(mTimer);
//                mSeries.resetData(new DataPoint[]);
            }
        });

        return v;
    }

    private void initGraph(GraphView graph) {
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(10);

        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(-6);
        graph.getViewport().setMaxY(6);

        graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.BOTH);
        graph.getViewport().setDrawBorder(true);

        // first mSeries is a line
        mSeries = new LineGraphSeries<>();
        graph.addSeries(mSeries);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mTimer);
    }

    private double getRandom() {
        double upper = 6.0;
        double lower = -6.0;
        double result = Math.random() * (upper - lower) + lower;
        return result;
    }


    @Override
    public void onConnect(BluetoothDevice device) {

    }

    @Override
    public void onDisconnect(BluetoothDevice device, String message) {

    }

    @Override
    public void onMessage(String message) {
        Double value = Double.parseDouble(message);
        mSeries.appendData(new DataPoint(graphLastXValue, value), false, 50);
        tvFrekuensi.setText(message);
    }

    @Override
    public void onError(String message) {

    }

    @Override
    public void onConnectError(BluetoothDevice device, String message) {

    }
}
