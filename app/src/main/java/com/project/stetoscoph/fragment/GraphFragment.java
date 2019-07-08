package com.project.stetoscoph.fragment;


import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.project.stetoscoph.BluetoothManager;
import com.project.stetoscoph.R;
import com.project.stetoscoph.SessionSharedPreference;
import com.project.stetoscoph.database.DMLHelper;
import com.project.stetoscoph.entity.Data;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;


/**
 * A simple {@link Fragment} subclass.
 */
public class GraphFragment extends Fragment {

    Button btnStart, btnStop;
    TextView tvFrekuensi;
    ImageButton imgBtnSave;

    private static final String TAG = "GraphFragment";

    private double graphLastXValue = 0d;
    int graphYValue = 0;
    private LineGraphSeries<DataPoint> mSeries;
    private ArrayList<Integer> dataArray = new ArrayList<>();


    // Bluetooth Stuff
    BluetoothManager btManager;
    BluetoothSocket mBTSocket;
    public ConnectThread connectThread = new ConnectThread();
    public ConnectedThread mConnectedThread;

    DMLHelper dmlHelper;
    Data data;
    SessionSharedPreference session;

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
        imgBtnSave = (ImageButton) v.findViewById(R.id.img_btn_save);
        btManager = BluetoothManager.getInstance();
        dmlHelper = DMLHelper.getInstance(getActivity());
        session = new SessionSharedPreference(getActivity());

        //init(v);

        initGraph(graph);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectThread.run();
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mConnectedThread.cancel();
                    mSeries.resetData(new DataPoint[]{
                            new DataPoint(graphLastXValue, graphYValue)
                    });
                    dataArray.clear();
                } catch (Exception e) {
                    e.getMessage();
                }
            }
        });

        imgBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dmlHelper.open();
                data = new Data();
                String date = getDateId();

                data.setTime(date);
                data.setTitle(session.getUserName());

                String convert = convertArray(dataArray);

                data.setData(convert);

                long result = dmlHelper.insertData(data);

                if (result > 0) {
                    Toast.makeText(getActivity(), "Data berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Data gagal ditambahkan", Toast.LENGTH_SHORT).show();
                }

                dmlHelper.close();
            }
        });

        return v;
    }

    private void initGraph(GraphView graph) {
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(40);

        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(200);

        graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.NONE);
        graph.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        graph.getGridLabelRenderer().setVerticalLabelsVisible(false);

        graph.getViewport().setDrawBorder(false);

        mSeries = new LineGraphSeries<>();
        graph.addSeries(mSeries);
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
                Toast.makeText(getActivity(), "Mulai menerima data", Toast.LENGTH_SHORT).show();
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
                    sleep(30);
                    bytes = mmInStream.read(buffer);
                    final String incomingMessage = new String(buffer, 0, bytes);
                    Log.d(TAG, "InputStream: " + incomingMessage);

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (incomingMessage != null) {
                                graphYValue = Integer.parseInt(incomingMessage);
                            } else {
                                graphYValue = 0;
                            }

                            if (graphLastXValue == 30) {
                                graphLastXValue = 0;
                                mSeries.resetData(new DataPoint[]{
                                        new DataPoint(graphLastXValue, graphYValue)
                                });
                            }
                            graphLastXValue += 0.03;
                            dataArray.add(graphYValue);
                            mSeries.appendData(new DataPoint(graphLastXValue, graphYValue), false, 1000);
                            tvFrekuensi.setText(incomingMessage + " hz");
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                } catch (InterruptedException e) {
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
        } catch (Exception e) {
            e.getMessage();
        }
    }

    @NonNull
    private String getDateId() {
        Date currentDate = Calendar.getInstance().getTime();

        DateFormat format = new SimpleDateFormat("yyy/MM/dd/kk:mm:ss", Locale.US);

        return format.format(currentDate);
    }

    private String convertArray(ArrayList<Integer> arrayList) {
        Gson gson = new Gson();
        return gson.toJson(arrayList);
    }
}
