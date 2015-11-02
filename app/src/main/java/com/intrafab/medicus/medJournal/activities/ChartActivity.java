package com.intrafab.medicus.medJournal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.intrafab.medicus.BaseActivity;
import com.intrafab.medicus.R;
import com.intrafab.medicus.medJournal.data.PeriodCalendarEntry;
import com.intrafab.medicus.medJournal.data.PeriodDataKeeper;
import com.intrafab.medicus.utils.Logger;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by Анна on 17.09.2015.
 */
public class ChartActivity extends BaseActivity{
    public static final String TAG = ChartActivity.class.getName();
    private static final String EXTRA_CHART_ACTIVITY = "chartActivity";

    public static final int TEMPERATURE_TYPE = 0;
    public static final int WEIGHT_TYPE = 1;

    private LineChart lineChart;
    private int type;
    private int duration = 15;
    private int year;
    private String [] yearsArray;
    private int[] cumulMonthDur = new int[12];
    private String [] months;
    private int monthIndex;
    private TextView tvMonth;
    private int isSpinerFirstCall = 0b00;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().getThemedContext();
        getSupportActionBar().setTitle(getResources().getString(R.string.chart_title_temperature));
        showActionBar();
        setActionBarIcon(R.mipmap.ic_action_back);
        ViewCompat.setTransitionName(toolbar, EXTRA_CHART_ACTIVITY);

        Intent intent = getIntent();
        /// BY DEFAULT TEMPERATURE
        type = intent.getIntExtra("type", 0);

        lineChart = (LineChart)findViewById(R.id.chart);
        TextView chartDescription = (TextView)findViewById(R.id.tvChartDescription);
        Spinner spScale = (Spinner)findViewById(R.id.spScale);
        Spinner spYears = (Spinner)findViewById(R.id.spYears);
        tvMonth = (TextView) findViewById(R.id.tvMonth);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.chart_period, android.R.layout.simple_spinner_item);
        spScale.setAdapter(adapter);

        setupSpinners(spScale, spYears);
        months = getResources().getStringArray(R.array.months);

        switch (type){
            case TEMPERATURE_TYPE:
                chartDescription.setText(getResources().getString(R.string.chart_scale));
                setupChart();
                break;
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_chart;
    }

    private void setupSpinners(Spinner spScale, Spinner spYears){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.chart_period, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spScale.setAdapter(adapter);

        spScale.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (isSpinerFirstCall == 0b00 || isSpinerFirstCall == 0b01){
                    isSpinerFirstCall += 0b10;
                    return;
                }
                String[] durationArray = getResources().getStringArray(R.array.chart_period);
                String[] splittedStr = durationArray[i].split(" ");
                duration = Integer.valueOf(splittedStr[0]);
                setupChart();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        calendar.set(Calendar.MONTH, 0);
        yearsArray = new String[currentYear-1990];
        for (int i=0; i<currentYear - 1990; i++)
            yearsArray[i]=String.valueOf(currentYear - i);
        year = currentYear;
        for (int i=0; i<12; i++){
            if (i==0)
                cumulMonthDur[i] = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            else
                cumulMonthDur[i] = cumulMonthDur[i-1] + calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            Logger.d(TAG, "cumulMonthDur " + i + " : " + cumulMonthDur[i]);
            calendar.add(Calendar.MONTH, 1);
        }

        spYears.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (isSpinerFirstCall == 0b00 || isSpinerFirstCall == 0b10){
                    isSpinerFirstCall += 0b01;
                    return;
                }
                year = Integer.valueOf(yearsArray[i]);
                setupChart();
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, 0);
                for (int y=0; y<12; y++){
                    if (y==0)
                        cumulMonthDur[y] = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                    else
                        cumulMonthDur[y] = cumulMonthDur[y-1] + calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                    Logger.d(TAG, "cumulMonthDur " + y + " : " + cumulMonthDur[y]);
                    calendar.add(Calendar.MONTH, 1);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, yearsArray);
        spYears.setAdapter(spinnerArrayAdapter);

    }

    private void setupChart() {
        Logger.d(TAG, "setupChart");
        // get data for chart
        HashMap<Long, PeriodCalendarEntry> mCalendarData = PeriodDataKeeper.getInstance().getCalendarData();
        Calendar lastDay = Calendar.getInstance();
        if (year != lastDay.get(Calendar.YEAR)) {
            lastDay.set(Calendar.YEAR, year);
            lastDay.set(Calendar.MONTH, 11);
            lastDay.set(Calendar.DAY_OF_MONTH, 31);
        }
        lastDay.set(Calendar.HOUR, 0);
        lastDay.set(Calendar.MINUTE, 0);
        lastDay.set(Calendar.SECOND, 0);
        lastDay.set(Calendar.MILLISECOND, 0);


        Calendar previousDayOfCurrYear = Calendar.getInstance();
        previousDayOfCurrYear.set(Calendar.YEAR, year);
        previousDayOfCurrYear.set(Calendar.MONTH, 0);
        previousDayOfCurrYear.set(Calendar.DAY_OF_MONTH, 0);
        previousDayOfCurrYear.set(Calendar.HOUR, 0);
        previousDayOfCurrYear.set(Calendar.AM_PM, Calendar.AM);
        previousDayOfCurrYear.set(Calendar.MINUTE, 0);
        previousDayOfCurrYear.set(Calendar.SECOND, 0);
        previousDayOfCurrYear.set(Calendar.MILLISECOND, 0);

        long diffInMillis = lastDay.getTimeInMillis() - previousDayOfCurrYear.getTimeInMillis();
        long diffInDays = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);

        final ArrayList<String> labels = new ArrayList<String>();
        ArrayList<Entry> entries = new ArrayList<>();
        float bodyTemperature;
        float minTemperature = 45;
        float maxTemperature = 34;
        int i=0;
        while (previousDayOfCurrYear.getTimeInMillis() < lastDay.getTimeInMillis()){
            previousDayOfCurrYear.add(Calendar.DATE, 1);
            labels.add(String. valueOf(previousDayOfCurrYear.get(Calendar.DAY_OF_MONTH)));
            // try to find pcentry for this date
            if (mCalendarData.containsKey(previousDayOfCurrYear.getTimeInMillis()/1000)) {
                bodyTemperature = ((float)mCalendarData.get(previousDayOfCurrYear.getTimeInMillis() / 1000).getBodyTemperature())/1000;
                if (bodyTemperature == 0)
                    continue;
                entries.add(new Entry(bodyTemperature, i));
                if (bodyTemperature < minTemperature)
                    minTemperature = bodyTemperature;
                if (bodyTemperature > maxTemperature)
                    maxTemperature = bodyTemperature;
            }
            i++;
        }
        // remove description
        lineChart.setDescription("");

        // setup Axises
        YAxis leftAxis = lineChart.getAxis(YAxis.AxisDependency.LEFT);
        leftAxis.setStartAtZero(false);
        leftAxis.setEnabled(true);
        leftAxis.setAxisMaxValue(maxTemperature + 0.5f);
        leftAxis.setAxisMinValue(minTemperature - 0.5f);

        if (entries.size() == 0) {
            Toast toast = Toast.makeText(ChartActivity.this, String.format(getString(R.string.chart_no_data), year), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }

        YAxis rightAxis = lineChart.getAxis(YAxis.AxisDependency.RIGHT);
        rightAxis.setEnabled(false);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setSpaceBetweenLabels(1);

        // setup dataset
        LineDataSet dataSet = new LineDataSet(entries, getResources().getString(R.string.chart_title_temperature));
        dataSet.setDrawValues(false);
        lineChart.getLegend().setEnabled(false);

        LineData data = new LineData(labels, dataSet);
        lineChart.setData(data);

        dataSet.setDrawFilled(true);
        //dataSet.setDrawCircleHole(true);
        dataSet.setDrawCubic(true);
        //dataSet.setDrawCircles(true);

        //setup scale
        float factorX = ((float)diffInDays)/duration;
        lineChart.setScaleMinima(factorX, 1);
        lineChart.zoom(factorX / 100, 1, diffInDays, maxTemperature);
        lineChart.moveViewToX(labels.size());

        lastDay.add(Calendar.DATE, duration * (-1));
        tvMonth.setText(months[lastDay.get(Calendar.MONTH)]);

        lineChart.setOnChartGestureListener(new OnChartGestureListener() {
            @Override
            public void onChartLongPressed(MotionEvent motionEvent) {
            }

            @Override
            public void onChartDoubleTapped(MotionEvent motionEvent) {
            }

            @Override
            public void onChartSingleTapped(MotionEvent motionEvent) {
            }

            @Override
            public void onChartFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            }

            @Override
            public void onChartScale(MotionEvent motionEvent, float v, float v1) {
            }

            @Override
            public void onChartTranslate(MotionEvent motionEvent, float v, float v1) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, 0, 1);
                calendar.add(Calendar.DATE, lineChart.getLowestVisibleXIndex());
                if (monthIndex != calendar.get(Calendar.MONTH)){
                    monthIndex = calendar.get(Calendar.MONTH);
                    tvMonth.setText(months[calendar.get(Calendar.MONTH)]);
                }

                /*int lowIndex = lineChart.getLowestVisibleXIndex()+1;
                Logger.d (TAG, "LowestVisibleXIndex: " + lowIndex);
                int i=0;
                while (lowIndex >= cumulMonthDur[i])
                    i++;
                tvMonth.setText(months[i]);*/
            }
        });

        lineChart.invalidate();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
