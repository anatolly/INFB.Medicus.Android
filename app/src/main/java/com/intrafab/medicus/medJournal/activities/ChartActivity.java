package com.intrafab.medicus.medJournal.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.intrafab.medicus.BaseActivity;
import com.intrafab.medicus.R;

import java.util.ArrayList;

/**
 * Created by Анна on 17.09.2015.
 */
public class ChartActivity extends BaseActivity{
    public static final String TAG = ChartActivity.class.getName();
    private static final String EXTRA_CHART_ACTIVITY = "chartActivity";


    LineChart lineChart;
    LineDataSet dataSet;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().getThemedContext();
        getSupportActionBar().setTitle("Scatter Chart");
        showActionBar();
        setActionBarIcon(R.mipmap.ic_action_back);
        ViewCompat.setTransitionName(toolbar, EXTRA_CHART_ACTIVITY);

        lineChart = (LineChart)findViewById(R.id.chart);
        //lineChart.setPinchZoom(true);
        //lineChart.setScaleMinima(10, 1);
        //lineChart.moveViewToY(38, YAxis.AxisDependency.LEFT);

        //lineChart.setAutoScaleMinMaxEnabled(true);



        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(36.6f, 0));
        entries.add(new Entry(36.8f, 1));
        entries.add(new Entry(36.9f, 2));
        entries.add(new Entry(36.9f, 3));
        entries.add(new Entry(37f, 4));
        entries.add(new Entry(38f, 5));

        ArrayList<String> labels = new ArrayList<String>();
        labels.add("1");
        labels.add("2");
        labels.add("3");
        labels.add("4");
        labels.add("5");
        labels.add("6");

        dataSet = new LineDataSet(entries, "label");
        LineData data = new LineData(labels, dataSet);
        lineChart.setData(data);

        YAxis leftAxis = lineChart.getAxis(YAxis.AxisDependency.LEFT);
        leftAxis.setStartAtZero(false);
        leftAxis.setAxisMaxValue(40);
        leftAxis.setAxisMinValue(34);


        YAxis rightAxis = lineChart.getAxis(YAxis.AxisDependency.RIGHT);
        rightAxis.setEnabled(false);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        //lineChart.setBorderColor(Color.WHITE);



    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_chart;
    }


}
