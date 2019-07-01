package com.project.stetoscoph;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;


/**
 * A simple {@link Fragment} subclass.
 */
public class GraphFragment extends Fragment {

    Button btnStart, btnStop;
    TextView tvFrekuensi;

    private static final String TAG = "GraphFragment";

    //graph init
    static GraphView graphView;
    static LineGraphSeries series;
    private static double graph2LastXValue = 0;
    private static int Xview = 10;

    private final Handler mHandler = new Handler();
    private Runnable mTimer;
    private double graphLastXValue = 5d;
    double graphYValue = 0;
    private LineGraphSeries<DataPoint> mSeries;

    // Bluetooth Stuff
    BluetoothManager btManager;
    BluetoothSocket mBTSocket;
    BluetoothAdapter btAdapter;
    public ConnectThread connectThread = new ConnectThread();
    public ConnectedThread mConnectedThread;
    private String address;

    public GraphFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_graph, container, false);

        final GraphView graph = (GraphView) v.findViewById(R.id.graph);
        btnStart = (Button) v.findViewById(R.id.btn_start_stream);
        btnStop = (Button) v.findViewById(R.id.btn_stop_stream);
        tvFrekuensi = (TextView) v.findViewById(R.id.tv_frekuensi);
        btManager = BluetoothManager.getInstance();

        //init(v);

        initGraph(graph);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectThread.run();
                /*mTimer = new Runnable() {
                    @Override
                    public void run() {
                        if (graphLastXValue == 40) {
                            graphLastXValue = 0;
                            mSeries.resetData(new DataPoint[] {
                                    new DataPoint(graphLastXValue, getRandom())
                            });
                        }
                        graphLastXValue += 1d;
                        mSeries.appendData(new DataPoint(graphLastXValue, graphYValue ), false, 40);
                        mHandler.postDelayed(this, 50);
                    }
                };
                mHandler.postDelayed(mTimer, 1000);*/
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mConnectedThread.cancel();
                    mSeries.resetData(new DataPoint[] {
                            new DataPoint(graphLastXValue, graphYValue)
                    });
                } catch (Exception e){
                    e.getMessage();
                }
            }
        });

        return v;
    }

    /*BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("theMessage");
            tvFrekuensi.setText(message);
        }
    };*/

    private void initGraph(GraphView graph) {
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(40);

        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(-15);
        graph.getViewport().setMaxY(15);

        graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.NONE);
        graph.getViewport().setDrawBorder(false);

        // first mSeries is a line
        mSeries = new LineGraphSeries<>();
        graph.addSeries(mSeries);
    }


    @Override
    public void onPause() {
        super.onPause();
//        mHandler.removeCallbacks(mTimer);
    }

    double mLastRandom = 2;

    private double getRandom() {
        mLastRandom++;
        return Math.sin(mLastRandom * 0.5) * 10 * (Math.random() * 10 + 1);
    }

    public class ConnectThread extends Thread {
        public void run() {
            boolean fail = false;

            BluetoothDevice device = btManager.getBtDevice();

            try {
                mBTSocket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
            } catch (IOException e) {
                fail = true;
                Toast.makeText(getActivity(), "Socket Creation Failed", Toast.LENGTH_SHORT).show();
            }
            // Establish the Bluetooth socket connection.
            try {
                mBTSocket.connect();
            } catch (IOException e) {
                try {
                    fail = true;
                    mBTSocket.close();
                } catch (IOException e2) {
                    //insert code to deal with this
                    Toast.makeText(getActivity(), "Socket Creation Failed", Toast.LENGTH_SHORT).show();
                }
            }
            if (!fail) {
                mConnectedThread = new ConnectedThread(mBTSocket);
                mConnectedThread.start();
                /*Toast.makeText(getActivity(), "Connected to " + btDevice.getName(), Toast.LENGTH_SHORT).show();*/
            }
        }

    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
            }

            mmInStream = tmpIn;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()
            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    sleep(1000);
                    bytes = mmInStream.read(buffer);
                    final String incomingMessage = new String(buffer, 0 , bytes);
                    Log.d(TAG, "InputStream: " + incomingMessage);

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            graphYValue = Double.parseDouble(incomingMessage);
                            if (graphLastXValue == 40) {
                                graphLastXValue = 0;
                                mSeries.resetData(new DataPoint[] {
                                        new DataPoint(graphLastXValue, graphYValue)
                                });
                            }
                            graphLastXValue += 1d;
                            mSeries.appendData(new DataPoint(graphLastXValue, graphYValue ), false, 40);
                            tvFrekuensi.setText(incomingMessage + " hz");
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                } catch (InterruptedException e){
                    e.printStackTrace();
                    break;
                }

            }
        }

        //* Call this from the main activity to shutdown the connection *//*
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            mConnectedThread.cancel();
        } catch (Exception e){
            e.getMessage();
        }
    }
}
