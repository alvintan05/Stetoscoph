package com.project.stetoscoph;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;


/**
 * A simple {@link Fragment} subclass.
 */
public class GraphFragment extends Fragment {

    ToggleButton tbStream;

    //graph init
    static GraphView graphView;
    static LineGraphSeries series;
    private static double graph2LastXValue = 0;
    private static int Xview = 10;

    private final Handler mHandler = new Handler();
    private Runnable mTimer;
    private double graphLastXValue = 5d;
    private LineGraphSeries<DataPoint> mSeries;

    public GraphFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_graph, container, false);

        final GraphView graph = (GraphView) v.findViewById(R.id.graph);
        tbStream = (ToggleButton) v.findViewById(R.id.tb_stream);

        //init(v);

        initGraph(graph);

        tbStream.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTimer = new Runnable() {
                    @Override
                    public void run() {
                        if (graphLastXValue == 40) {
                            graphLastXValue = 0;
                            mSeries.resetData(new DataPoint[] {
                                    new DataPoint(graphLastXValue, getRandom())
                            });
                        }
                        graphLastXValue += 1d;
                        mSeries.appendData(new DataPoint(graphLastXValue, getRandom()), false, 40);
                        mHandler.postDelayed(this, 50);
                    }
                };
                mHandler.postDelayed(mTimer, 700);
            }
        });

        return v;
    }

    private void initGraph(GraphView graph){
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(40);

        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(-100);
        graph.getViewport().setMaxY(100);

        graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.NONE);
        graph.getViewport().setDrawBorder(false);

        // first mSeries is a line
        mSeries = new LineGraphSeries<>();
        graph.addSeries(mSeries);
    }


    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeCallbacks(mTimer);
    }

    double mLastRandom = 2;
    private double getRandom() {
        mLastRandom++;
        return Math.sin(mLastRandom*0.5) * 10 * (Math.random() * 10 + 1);
    }

}
