package com.example.imcare.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.imcare.R;
import com.example.imcare.model.HealthRecordItem;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

//https://www.numetriclabz.com/android-line-chart-using-mpandroidchart-tutorial/

public class HealthRecordGraphListAdapter extends
        RecyclerView.Adapter<HealthRecordGraphListAdapter.GraphViewHolder> {

    private final Context mContext;
    private List<HealthRecordItem> mHealthRecordItemList;

    public HealthRecordGraphListAdapter(Context context, List<HealthRecordItem> healthRecordItemList) {
        mContext = context;
        mHealthRecordItemList = healthRecordItemList;
    }

    @NonNull
    @Override
    public GraphViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_graph, parent, false
        );
        return new GraphViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GraphViewHolder holder, int position) {
        final HealthRecordItem healthRecordItem = mHealthRecordItemList.get(position);
        holder.mHeaderTextView.setText(healthRecordItem.title);

        float value = healthRecordItem.getValue();
        float minValue = healthRecordItem.minValue;
        float maxValue = healthRecordItem.maxValue;

        //float minGraph =  value < minValue ? value : minValue;
        //float maxGraph =  value > maxValue ? value : maxValue;

        //https://github.com/PhilJay/MPAndroidChart/blob/master/MPChartExample/src/com/xxmassdeveloper/mpchartexample/LineChartActivity1.java

        // กำหนดข้อความแกน x
        ArrayList<String> labels = new ArrayList<>();
        labels.add("");
        labels.add("");
        labels.add("");
        labels.add("");
        labels.add("");
        labels.add("");

        // กำหนดข้อมูล
        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(0, value));
        entries.add(new Entry(1, value));
        entries.add(new Entry(2, value));
        entries.add(new Entry(3, value));
        entries.add(new Entry(4, value));
        entries.add(new Entry(5, value));
        LineDataSet valueDataSet = new LineDataSet(entries, "ค่าของคุณ");

        holder.mGraphView.setMarker(null);

        LineData valueLineData = new LineData(valueDataSet);
        holder.mGraphView.setData(valueLineData);

    }

    @Override
    public int getItemCount() {
        return mHealthRecordItemList.size();
    }

    class GraphViewHolder extends RecyclerView.ViewHolder {

        private final TextView mHeaderTextView;
        private final LineChart mGraphView;

        GraphViewHolder(View itemView) {
            super(itemView);
            mHeaderTextView = itemView.findViewById(R.id.header_text_view);
            mGraphView = itemView.findViewById(R.id.graph_view);
        }
    }
}
