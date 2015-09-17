package com.intrafab.medicus.medJournal.fragments;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;

import com.intrafab.medicus.R;
import com.intrafab.medicus.medJournal.activities.PeriodCalendarActivity;
import com.intrafab.medicus.medJournal.activities.PeriodCalendarDayOptionsActivity;
import com.intrafab.medicus.medJournal.data.PeriodDataKeeper;

/**
 * Created by 1 on 01.09.2015.
 */
public class PeriodEndSettingFragment extends DialogFragment implements View.OnClickListener{

    private Integer expected_dutation;
    private NumberPicker duration;
    private NumberPicker ovulation;

    public PeriodEndSettingFragment() {

    }

    public void setSetting (Integer duration) {
        //save reference
        expected_dutation = duration;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.period_end_setting, container, false);

        getDialog().setTitle(getResources().getString(R.string.period_setting));

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        duration = (NumberPicker)view.findViewById(R.id.duration);
        //ovulation = (NumberPicker)view.findViewById(R.id.ovulation);


        duration.setMinValue(1);
        duration.setMaxValue(PeriodDataKeeper.periodDuration);
        duration.setValue(expected_dutation);

        Button okButton = (Button)view.findViewById(R.id.ok_button);
        Button cancelButton = (Button)view.findViewById(R.id.cancel_button);

        okButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ok_button:
                PeriodDataKeeper.menstrualDuration = duration.getValue();
                ((PeriodCalendarDayOptionsActivity)getActivity()).setPeriod();
                dismiss();
                break;
            case R.id.cancel_button:
                dismiss();
                break;
        }
    }
}
