package com.project.stetoscoph.fragment;


import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import me.aflak.bluetooth.Bluetooth;


/**
 * A simple {@link Fragment} subclass.
 */
public class GraphFragment extends Fragment implements Bluetooth.CommunicationCallback {

    Button btnStart, btnStop;
    TextView tvFrekuensi;
    ImageButton imgBtnSave;

    private static final String TAG = "GraphFragment";

    private double graphLastXValue = 0d;
    double graphYValue = 0d;
    private LineGraphSeries<DataPoint> mSeries;
    private ArrayList<Double> dataArray = new ArrayList<>();


    // Bluetooth Stuff
    BluetoothManager btManager;
    private Bluetooth b;

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

        // inisialisasi widget dan variabel
        final GraphView graph = (GraphView) v.findViewById(R.id.graph);
        btnStart = (Button) v.findViewById(R.id.btn_start_stream);
        btnStop = (Button) v.findViewById(R.id.btn_stop_stream);
        tvFrekuensi = (TextView) v.findViewById(R.id.tv_frekuensi);
        imgBtnSave = (ImageButton) v.findViewById(R.id.img_btn_save);
        btManager = BluetoothManager.getInstance();
        dmlHelper = DMLHelper.getInstance(getActivity());
        session = new SessionSharedPreference(getActivity());

        initGraph(graph);

        // inisalisasi library bluetooth
        b = new Bluetooth(getActivity());
        b.setCommunicationCallback(this);

        // Button start di klik
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Melakukan koneksi ke device dan mulai menerima data
                b.connectToDevice(btManager.getBtDevice());
            }
        });

        // Button stop di klik
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b.disconnect();
                graphLastXValue = 0d;
                mSeries.resetData(new DataPoint[]{
                        new DataPoint(graphLastXValue, graphYValue)
                });
                dataArray.clear();
            }
        });

        // Ketika klik save
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
        graph.getGridLabelRenderer().setHorizontalAxisTitleTextSize(22);
        // title di y
        graph.getGridLabelRenderer().setVerticalAxisTitle("amplitudo (A)");
        graph.getGridLabelRenderer().setVerticalAxisTitleTextSize(22);

        graph.getViewport().setDrawBorder(false);

        mSeries = new LineGraphSeries<>();
        graph.addSeries(mSeries);
    }

    // Saat sudah konek ke device maka akan muncul toast berikut
    @Override
    public void onConnect(BluetoothDevice device) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), "Mulai menerima data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDisconnect(BluetoothDevice device, String message) {

    }

    // Saat menerima data
    @Override
    public void onMessage(final String message) {
        // memanggil method Display untuk mengirim data yang diterima untuk kemudian digambar ke grafik
        Display(message.trim());
        Log.d(TAG, "onMessage: " + message.trim());
    }

    // Method ini untuk menggambar titik pada grafik saat menerima data
    public void Display(final String s) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (s.trim().equals("")) {
                    graphYValue = 0.0;
                } else {
                    graphYValue = Double.parseDouble(s.trim());
                }

                dataArray.add(graphYValue);

                // bagian ini untuk menambah titik pada grafik
                mSeries.appendData(new DataPoint(graphLastXValue, graphYValue), true, 1000);

                tvFrekuensi.setText(s + " hz");
                // bagian ini untuk mengatur penambahan nilai x
                graphLastXValue += 0.03;
            }
        });
    }

    @Override
    public void onError(String message) {

    }

    @Override
    public void onConnectError(BluetoothDevice device, String message) {

    }

    // Mendapatkan tanggal dan jam saat ini, ini digunakan saat tombol save ditekan
    @NonNull
    private String getDateId() {
        Date currentDate = Calendar.getInstance().getTime();

        DateFormat format = new SimpleDateFormat("yyy/MM/dd/kk:mm:ss", Locale.US);

        return format.format(currentDate);
    }

    private String convertArray(ArrayList<Double> arrayList) {
        Gson gson = new Gson();
        return gson.toJson(arrayList);
    }
}