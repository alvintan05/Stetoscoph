package com.project.stetoscoph;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.project.stetoscoph.activity.PasswordLockScreenActivity;
import com.project.stetoscoph.activity.ReportDetailActivity;
import com.project.stetoscoph.entity.Data;

import java.util.ArrayList;

// class ini untuk mengatur list pada report fragment

public class AdapterReportData extends RecyclerView.Adapter<AdapterReportData.ViewHolder> {

    private ArrayList<Data> listData = new ArrayList<>();
    private Context context;

    public AdapterReportData(Context context) {
        this.context = context;
    }

    public void setListData(ArrayList<Data> listData) {
        if (listData.size() > 0) {
            this.listData.clear();
        }
        this.listData.addAll(listData);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_rv_data, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        viewHolder.tvJudul.setText(listData.get(i).getTitle());
        viewHolder.tvTanggal.setText(listData.get(i).getTime());
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PasswordLockScreenActivity.class);
                intent.putExtra("data", listData.get(i).getData());
                intent.putExtra("nama", listData.get(i).getTitle());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        TextView tvJudul, tvTanggal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvJudul = (TextView) itemView.findViewById(R.id.tv_judul);
            tvTanggal = (TextView) itemView.findViewById(R.id.tv_tanggal);
            cardView = (CardView) itemView.findViewById(R.id.card_view);

        }
    }

}
