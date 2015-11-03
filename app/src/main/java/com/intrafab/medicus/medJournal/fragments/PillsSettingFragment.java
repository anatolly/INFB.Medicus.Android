package com.intrafab.medicus.medJournal.fragments;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.intrafab.medicus.Constants;
import com.intrafab.medicus.R;
import com.intrafab.medicus.medJournal.data.ContraceptionInfo;
import com.intrafab.medicus.medJournal.data.PeriodDataKeeper;
import com.intrafab.medicus.medJournal.loaders.ContraceptionInfoSaver;
import com.intrafab.medicus.medJournal.loaders.PeriodCycleEntrySaver;
import com.intrafab.medicus.service.NotificationIntentService;
import com.intrafab.medicus.utils.Logger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Анна on 13.10.2015.
 */
public class  PillsSettingFragment extends DialogFragment implements View.OnClickListener{
    public static final String TAG = PillsSettingFragment.class.getName();

    private int pageNumber;
    private FrameLayout containerView;
    private TextView tvMainHeader;
    private TextView tvDescription;
    private Button btnNext;
    private Button btnBack;
    ContraceptionInfo contraceptionInfo;

    public void setContraceptionInfo(ContraceptionInfo contraceptionInfo){
        this.contraceptionInfo = contraceptionInfo;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pills_settings, container, false);

        pageNumber = 1;
        if (contraceptionInfo == null)
            contraceptionInfo = new ContraceptionInfo();

        containerView = (FrameLayout)rootView.findViewById(R.id.container);

        tvMainHeader = (TextView) rootView.findViewById(R.id.tvMainHeader);
        tvDescription = (TextView) rootView.findViewById(R.id.tvDescription);

        btnNext = (Button) rootView.findViewById(R.id.btnNext);
        btnBack = (Button) rootView.findViewById(R.id.btnBack);

        btnNext.setOnClickListener(this);
        btnBack.setOnClickListener(this);

        getFirstPage();

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    }

    // return page with a list of different types of contraception
    private View getFirstPage (){

        tvMainHeader.setText(getResources().getString(R.string.contraception_main_header));
        tvDescription.setText(getResources().getString(R.string.contraception_subheader));
        btnNext.setEnabled(false);

        final String NAME = "name";
        final String ICON = "icon";

        // setup data
        String[] titles = getResources().getStringArray(R.array.contraception);
        int[] icons = {R.drawable.ic_launcher, R.drawable.ic_launcher,R.drawable.ic_launcher, R.drawable.ic_launcher, R.drawable.ic_launcher};

        ArrayList<Map<String, Object>> data = new ArrayList<>(titles.length);
        Map<String, Object> m;
        for (int i = 0; i < titles.length; i++) {
            m = new HashMap<>();
            m.put(NAME, titles[i]);
            m.put(ICON, icons[i]);
            data.add(m);
        }

        // array of attributes' names
        String[] from = { NAME, ICON};
        // array of view components' ids
        int[] to = {R.id.tvName,  R.id.ivIcon };

        // create adapter
        SimpleAdapter sAdapter = new SimpleAdapter(getActivity().getApplicationContext(), data, R.layout.view_contraception_list_item, from, to);

        // create listView and set adapter
        final ListView listView = new ListView(getActivity().getApplicationContext());
        listView.setAdapter(sAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (contraceptionInfo.getContraceptionTypeId() != -1)
                    listView.getChildAt(contraceptionInfo.getContraceptionTypeId()).findViewById(R.id.rlContainer).setBackgroundColor(View.INVISIBLE);
                view.findViewById(R.id.rlContainer).setBackgroundColor(getResources().getColor(R.color.background_floating_material_light));
                contraceptionInfo.setContraceptionTypeId(i);
                btnNext.setEnabled(true);

            }
        });
        containerView.removeAllViews();
        containerView.addView(listView);
        return listView;
    }

    private View getSecondPage() {

        //create view
        LayoutInflater inflater = LayoutInflater.from(getActivity().getApplicationContext());
        containerView.removeAllViews();
        View view = inflater.inflate(R.layout.pills_settings, containerView);

        // setup header and description
        tvMainHeader.setText(getResources().getStringArray(R.array.contraception_settings_header)[contraceptionInfo.getContraceptionTypeId()]);
        tvDescription.setText(getResources().getStringArray(R.array.contraception_settings_subheader)[contraceptionInfo.getContraceptionTypeId()]);

        //hide placebo setting
        if (contraceptionInfo.getContraceptionTypeId() != ContraceptionInfo.TYPE_PILLS)
            view.findViewById(R.id.llPlacebo).setVisibility(View.GONE);

        //find view components
        final TextView tvDate = (TextView) view.findViewById(R.id.tvDateStart);
        ImageView ivEditDate = (ImageView) view.findViewById(R.id.ivEditDate);
        Spinner spActiveDays = (Spinner)view.findViewById(R.id.spActiveDays);
        Spinner spBreakDays = (Spinner)view.findViewById(R.id.spBreakDays);
        Switch switchPlacebo = (Switch)view.findViewById(R.id.switchPlacebo);
        LinearLayout llActiveDays = (LinearLayout) view.findViewById(R.id.llActiveDays);
        LinearLayout llBreakDays = (LinearLayout) view.findViewById(R.id.llBreakDays);
        TextView tvDay = (TextView) view.findViewById(R.id.tvDay);

        // setDate and setup datePickerDialog
        int cycleListSize = PeriodDataKeeper.getInstance().getPeriodData().size();
        Calendar date;
        // set today date or first day of last cycle(is any cycle exists)
        if (contraceptionInfo.getStartDate() != 0) {
            date = Calendar.getInstance();
            date.setTimeInMillis(contraceptionInfo.getStartDate());
        } else if (cycleListSize > 0)
            date = PeriodDataKeeper.getInstance().getPeriodData().get(cycleListSize-1).getFirstDayCalendar();
        else
            date = Calendar.getInstance();
        // perform date from Calendar to String
        String dateStr = date.get(Calendar.DAY_OF_MONTH) + " " + getResources().getStringArray(R.array.months)[date.get(Calendar.MONTH)] + " " + date.get(Calendar.YEAR);
        tvDate.setText(dateStr);

        View.OnClickListener calendarListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();
                DatePickerDialog.OnDateSetListener calendarListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        Calendar date = Calendar.getInstance();
                        date.set(year, monthOfYear, dayOfMonth);
                        contraceptionInfo.setStartDate(date.getTimeInMillis());
                        String dateStr = date.get(Calendar.DAY_OF_MONTH) + " " + getResources().getStringArray(R.array.months)[date.get(Calendar.MONTH)] + " " + date.get(Calendar.YEAR);
                        tvDate.setText(dateStr);
                    }
                };
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), calendarListener, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        };

        tvDate.setOnClickListener(calendarListener);
        ivEditDate.setOnClickListener(calendarListener);
        contraceptionInfo.setStartDate(date.getTimeInMillis());

        // setup active and break days
        String [] activeDaysTemp = {""};
        String [] breakDaysTemp = {""};

        switch (contraceptionInfo.getContraceptionTypeId()){
            case ContraceptionInfo.TYPE_PILLS:
                activeDaysTemp = Constants.Numeric.pillsActiveDays;
                breakDaysTemp = Constants.Numeric.pillsBreakDays;
                break;
            case ContraceptionInfo.TYPE_PLASTER:
            case ContraceptionInfo.TYPE_RING:
                activeDaysTemp = new String[]{"21"};
                breakDaysTemp = new String[]{"7"};
                break;
            case ContraceptionInfo.TYPE_INJECTION:
                tvDay.setText(getResources().getString(R.string.week));
                activeDaysTemp = Constants.Numeric.injectionsActiveWeeks;
                contraceptionInfo.setBreakDays(4);
        }

        final String[] activeDays = activeDaysTemp;
        final String[] breakDays = breakDaysTemp;
        activeDaysTemp = null;
        breakDaysTemp = null;

        if (contraceptionInfo.getContraceptionTypeId() == ContraceptionInfo.TYPE_EMERGENCY) {
            llActiveDays.setVisibility(View.GONE);
        }
        else{
            ArrayAdapter<String> spActiveDaysAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_item, activeDays);
            spActiveDays.setAdapter(spActiveDaysAdapter);
        }
        if (contraceptionInfo.getContraceptionTypeId() == ContraceptionInfo.TYPE_INJECTION || contraceptionInfo.getContraceptionTypeId() == ContraceptionInfo.TYPE_EMERGENCY){
            llBreakDays.setVisibility(View.GONE);
        } else{
            ArrayAdapter<String> spBreakDaysAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_item, breakDays);
            spBreakDays.setAdapter(spBreakDaysAdapter);
        }

        spActiveDays.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (contraceptionInfo.getContraceptionTypeId() != ContraceptionInfo.TYPE_INJECTION)
                    contraceptionInfo.setActiveDays(Integer.valueOf(activeDays[i]));
                else
                    contraceptionInfo.setActiveDays(Integer.valueOf(activeDays[i]) * 7);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spBreakDays.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (contraceptionInfo.getContraceptionTypeId() != ContraceptionInfo.TYPE_INJECTION)
                    contraceptionInfo.setBreakDays(Integer.valueOf(breakDays[i]));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // set 7 selected in break days (it is the most frequent)
        if (contraceptionInfo.getContraceptionTypeId() == ContraceptionInfo.TYPE_PILLS)
            spBreakDays.setSelection(6);

        switchPlacebo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                contraceptionInfo.setPlacebo(b ? 1 : 0);
            }
        });

        return view;
    }

    private View getThirdPage() {

        tvMainHeader.setText(getResources().getString(R.string.notification_page_header));
        tvDescription.setText(getResources().getString(R.string.notification_subheader));

        LayoutInflater inflater = LayoutInflater.from(getActivity().getApplicationContext());
        containerView.removeAllViews();
        View view = inflater.inflate(R.layout.pills_notifications, containerView);

        // fina ViewElements
        TextView tvNotificationTitle = (TextView)view.findViewById(R.id.tvNotification);
        Switch swNotification = (Switch)view.findViewById(R.id.swNotification);
        final TextView tvStartTime = (TextView)view.findViewById(R.id.tvTimeStart);
        final EditText etTitleOfNotif = (EditText)view.findViewById(R.id.etNotificationName);
        final EditText etTextOfNotif = (EditText)view.findViewById(R.id.etNotificationText);
        final ImageView ivClock = (ImageView)view.findViewById(R.id.ivClock);
        final Spinner spRepeatInterval = (Spinner)view.findViewById(R.id.spRepeatInterval);
        final LinearLayout llNotifSettings = (LinearLayout) view.findViewById(R.id.llNotificationSettingBottom);

        View.OnClickListener clockListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.tvTimeStart || view.getId() == R.id.ivClock){
                    TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int hours, int minutes) {
                            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(1,1,1,hours,minutes,0);
                            tvStartTime.setText(sdf.format(calendar.getTime()));
                            Calendar startDayTime = Calendar.getInstance();
                            startDayTime.setTimeInMillis(contraceptionInfo.getStartDate());
                            startDayTime.set(Calendar.HOUR_OF_DAY, hours);
                            startDayTime.set(Calendar.MINUTE, minutes);
                            startDayTime.set(Calendar.SECOND, 0);
                            startDayTime.set(Calendar.MILLISECOND, 0);
                            contraceptionInfo.setStartDate(startDayTime.getTimeInMillis());
                        }
                    };
                    TimePickerDialog timeDialog  = new TimePickerDialog(getActivity(),timePickerListener, 12, 0 , true);
                    timeDialog.show();
                }
            }
        };

        // set notif time 12:00
        Calendar startDayTime = Calendar.getInstance();
        startDayTime.setTimeInMillis(contraceptionInfo.getStartDate());
        startDayTime.set(Calendar.HOUR_OF_DAY, 12);
        startDayTime.set(Calendar.MINUTE, 0);
        startDayTime.set(Calendar.SECOND, 0);
        startDayTime.set(Calendar.MILLISECOND, 0);
        contraceptionInfo.setStartDate(startDayTime.getTimeInMillis());

        tvStartTime.setOnClickListener(clockListener);
        ivClock.setOnClickListener(clockListener);
        etTextOfNotif.setOnClickListener(this);
        etTitleOfNotif.setOnClickListener(this);
        swNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    contraceptionInfo.setNotificationEnabled(1);
                    tvStartTime.setEnabled(true);
                    ivClock.setEnabled(true);
                    etTextOfNotif.setEnabled(true);
                    etTitleOfNotif.setEnabled(true);
                    spRepeatInterval.setEnabled(true);
                    llNotifSettings.setAlpha(1);
                } else {
                    contraceptionInfo.setNotificationEnabled(0);
                    tvStartTime.setEnabled(false);
                    ivClock.setEnabled(false);
                    etTextOfNotif.setEnabled(false);
                    etTitleOfNotif.setEnabled(false);
                    spRepeatInterval.setEnabled(false);
                    llNotifSettings.setAlpha((float) 0.5);
                }
            }
        });

        ArrayAdapter<String> spRepeatIntervalsAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_item, Constants.Numeric.intervalNotif);
        spRepeatInterval.setAdapter(spRepeatIntervalsAdapter);

        spRepeatInterval.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                contraceptionInfo.setNotificationInterval(Integer.valueOf(Constants.Numeric.intervalNotif[i]));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        etTextOfNotif.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                Logger.d (TAG, "onEditorAction");
                return false;
            }
        });

        String notifText = getResources().getStringArray(R.array.contraception_notification_text)[contraceptionInfo.getContraceptionTypeId()];
        String notifTitle = getResources().getString(R.string.notification_title_contraception);
        etTextOfNotif.setText(notifText);
        etTitleOfNotif.setText(notifTitle);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contraceptionInfo.setNotificationText(etTextOfNotif.getText().toString());
                contraceptionInfo.setNotificationTitle(etTitleOfNotif.getText().toString());
                planningAlarm();
                dismiss();
            }
        });

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnNext:
                nextPage();
                Animation enteredAnimation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(),R.anim.view_entered);
                containerView.startAnimation(enteredAnimation);
                break;
            case R.id.btnBack:
                Animation exitedAnimation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(),R.anim.view_exited);
                containerView.startAnimation(exitedAnimation);
                previousPage();
                break;
        }
    }

    private void nextPage(){
        switch (pageNumber){
            case 1:
                btnBack.setText(getResources().getString(R.string.btn_back));
                getSecondPage();
                pageNumber++;
                break;
            case 2:
                btnNext.setText(getResources().getString(R.string.ok));
                getThirdPage();
                pageNumber++;
                break;
            case 3:
                planningAlarm();
                dismiss();
                break;
        }
    }

    private void previousPage(){
        switch (pageNumber){
            case 1:
                dismiss();
                break;
            case 2:
                btnBack.setText(getResources().getString(R.string.cancel));
                getFirstPage();
                pageNumber--;
                break;
            case 3:
                btnNext.setText(getResources().getString(R.string.btn_next));
                getSecondPage();
                pageNumber--;
                break;
        }
    }

    @Override
    public void onResume() {
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

        super.onResume();
    }

    private void planningAlarm(){

        Logger.d("planningAlarm", contraceptionInfo.toString() );

        switch (contraceptionInfo.getContraceptionTypeId()){
            case ContraceptionInfo.TYPE_PILLS:
                contraceptionInfo.setNotificationActiveDayFrequency(1);
                contraceptionInfo.setNotificationBreakDayFrequency(contraceptionInfo.getPlacebo());
                break;
            case ContraceptionInfo.TYPE_RING:
                contraceptionInfo.setNotificationActiveDayFrequency(21);
                contraceptionInfo.setNotificationBreakDayFrequency(7);
                break;
            case ContraceptionInfo.TYPE_PLASTER:
                contraceptionInfo.setNotificationActiveDayFrequency(7);
                contraceptionInfo.setNotificationBreakDayFrequency(21);
                break;
            case ContraceptionInfo.TYPE_INJECTION:
                contraceptionInfo.setNotificationActiveDayFrequency(contraceptionInfo.getActiveDays());
                contraceptionInfo.setNotificationBreakDayFrequency(4);
                break;
            case ContraceptionInfo.TYPE_EMERGENCY:
                break;
        }

        //AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);

        // save contraception info
        ContraceptionInfoSaver taskSave = new ContraceptionInfoSaver(contraceptionInfo, getActivity().getApplicationContext());
        taskSave.execute();

/*
        // create intent for intentservice
        Intent intent = new Intent(getActivity(), NotificationIntentService.class);
        // set action
        intent.setAction("contraceptionControl");

        // put contraceptionInfo into intent
        intent.putExtra("contInfo", contraceptionInfo);

        PendingIntent pendingIntent = PendingIntent.getService(getActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, contraceptionInfo.getStartDate(), contraceptionInfo.getNotificationActiveDayFrequency() * AlarmManager.INTERVAL_DAY, pendingIntent);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, contraceptionInfo.getStartDate(), contraceptionInfo.getNotificationInterval(), pendingIntent);


        // send result to activity (which activity)
/*        Intent intent = new Intent();
        intent.putExtra("contraceptionInfo", contraceptionInfo);
        getActivity().setResult(1, intent);  */

    }


}