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

    // widget
    Button btnStart, btnStop;
    TextView tvFrekuensi;
    ImageButton imgBtnSave;

    // variable string
    private static final String TAG = "GraphFragment";

    // variabel yang akan digunakan pada grafik
    private double graphLastXValue = 0d;
    double graphYValue = 0d;
    private LineGraphSeries<DataPoint> mSeries;
    private ArrayList<Double> dataArray = new ArrayList<>();


    // variabel dari kelas BluetoothManager
    BluetoothManager btManager;
    // variabel dari library Bluetooth
    private Bluetooth b;

    // semua variabel ini nanti dijadikan objek
    // variabel dari class DMLHelper
    DMLHelper dmlHelper;
    // variabel dari class Data
    Data data;
    // variabel dari class SessionSharedPreferences
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

        // pembuatan objek
        btManager = BluetoothManager.getInstance();
        dmlHelper = DMLHelper.getInstance(getActivity());
        session = new SessionSharedPreference(getActivity());

        // memanggil method initGraph()
        initGraph(graph);

        // inisalisasi library bluetooth
        b = new Bluetooth(getActivity());
        // memanggil method untuk melakukan komunikasi data bluetooth
        b.setCommunicationCallback(this);

        //  Menghandle button start di klik
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Melakukan koneksi ke device dan mulai menerima data
                b.connectToDevice(btManager.getBtDevice());
            }
        });

        // Menghandle button stop di klik
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // memutus koneksi bluetooth
                b.disconnect();
                // memberi nilai 0 pada variabel dibawah
                graphLastXValue = 0d;
                // mereset data dalam series sehingga grafik akan kosong dan dapat diisi data baru
                mSeries.resetData(new DataPoint[]{
                        new DataPoint(graphLastXValue, graphYValue)
                });
                // mengosongkan array yang menampung data bluetooth
                // array disini nantinya digunakan untuk menyimpan ke dalam database
                dataArray.clear();
            }
        });

        // Menghandle ketika klik tombol save
        imgBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // memanggil method pembuka database
                dmlHelper.open();
                // pembuatan objek data
                data = new Data();
                // variabel date yang menampung data dari fungsi getDateId()
                String date = getDateId();

                // melakukan set data
                data.setTime(date);
                data.setTitle(session.getUserName());

                // melakukan convert data dari array ke string agar dapat disimpan ke database
                String convert = convertArray(dataArray);

                // melakukan set data
                data.setData(convert);

                // variabel result menampung hasil dari proses penyimpanan data
                long result = dmlHelper.insertData(data);

                // jika angkanya lebih dari 0 maka berhasil
                if (result > 0) {
                    Toast.makeText(getActivity(), "Data berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                } else {
                    // jika tidak maka gagal
                    Toast.makeText(getActivity(), "Data gagal ditambahkan", Toast.LENGTH_SHORT).show();
                }

                // memanggil method penutup database
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
        // ukuran title
        graph.getGridLabelRenderer().setHorizontalAxisTitleTextSize(22);
        // title di y
        graph.getGridLabelRenderer().setVerticalAxisTitle("amplitudo (A)");
        // ukuran title
        graph.getGridLabelRenderer().setVerticalAxisTitleTextSize(22);

        // menghilangkan border di grafik
        graph.getViewport().setDrawBorder(false);

        // mengeset mSeries ke graph
        // mSeries ini berfungsi untuk menampung data dan kemudian digambar menjadi titik di grafik
        mSeries = new LineGraphSeries<>();
        graph.addSeries(mSeries);
    }

    // Saat sudah konek ke device maka akan muncul pop up berikut
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
                // mengecek apakah data yang diterima kosong atau tidak
                if (s.trim().equals("")) {
                    // jika ya maka nilai y diset 0
                    graphYValue = 0.0;
                } else {
                    // jika ada data maka akan diconvert dari string mejadi double
                    graphYValue = Double.parseDouble(s.trim());
                }

                // menambahkan data ke dalam array untuk nanti disimpan ke database
                dataArray.add(graphYValue);

                // bagian ini untuk menambah titik pada grafik
                mSeries.appendData(new DataPoint(graphLastXValue, graphYValue), true, 1000);

                // menampilkan angka yang didapat ke layar
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

    // fungsi ini untuk mengkonversi data dari tipe array ke tipe string
    private String convertArray(ArrayList<Double> arrayList) {
        Gson gson = new Gson();
        return gson.toJson(arrayList);
    }
}