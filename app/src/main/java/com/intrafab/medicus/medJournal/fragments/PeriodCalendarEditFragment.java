package com.intrafab.medicus.medJournal.fragments;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.intrafab.medicus.PaymentActivity;
import com.intrafab.medicus.R;
import com.intrafab.medicus.medJournal.data.PeriodCalendarEntry;
import com.intrafab.medicus.medJournal.views.ItemPeriodCycleOptionView;
import com.intrafab.medicus.utils.Logger;

import java.math.BigDecimal;

/**
 * Created by Анна on 26.08.2015.
 */
public class PeriodCalendarEditFragment extends DialogFragment {

    public static final String TAG = PeriodCalendarEditFragment.class.getName();
    private EditText editText;
    private PeriodCalendarEntry mEntry;
    private int type;
    private String aboutMe = "";

    public void setType (int type){
        this.type = type;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = null;
        switch (type){
            case PaymentActivity.ABOUT_ME:
                rootView = inflater.inflate(R.layout.fragment_note, container, false);
                getDialog().setTitle(getResources().getString(R.string.header_aboutme));
                break;
            case ItemPeriodCycleOptionView.NOTE:
                rootView = inflater.inflate(R.layout.fragment_note, container, false);
                getDialog().setTitle(getResources().getString(R.string.header_note));
                break;
            case ItemPeriodCycleOptionView.TEMPERATURE:
                rootView = inflater.inflate(R.layout.fragment_temperature, container, false);
                getDialog().setTitle(getResources().getString(R.string.header_temperature));
                break;
        }
        return rootView;

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editText = (EditText) view.findViewById(R.id.editText);
        Button buttonOk  = (Button) view.findViewById(R.id.btn_save_note);

        switch (type){
            case ItemPeriodCycleOptionView.NOTE:
                editText.setText(mEntry.getNote());
                buttonOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!TextUtils.equals(editText.getText().toString(), ""))
                            mEntry.setNote(editText.getText().toString());
                        dismiss();
                    }
                });
                break;
            case ItemPeriodCycleOptionView.TEMPERATURE:
                editText.setText("" + toBigDecimalTemperature(mEntry.getBodyTemperature()));
                editText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (TextUtils.equals(editText.getText().toString(), "0.0"))
                            editText.setText("");
                    }
                });
                buttonOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!TextUtils.equals(editText.getText().toString(), "") && isTemperatureCorrect(editText.getText().toString())) {
                            mEntry.setBodyTemperature(toIntTemperature(editText.getText().toString()));
                            dismiss();
                        } else {
                            Toast toast = Toast.makeText(getActivity(), "Incorrect value for basal body temperature", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 150);
                            toast.show();
                        }
                    }
                });
                break;
            case PaymentActivity.ABOUT_ME:
                if (editText == null)
                    Logger.d(TAG, "editText null");
                if (aboutMe == null)
                    Logger.d(TAG, "aboutMe null");
                editText.setText(aboutMe);
                buttonOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!TextUtils.equals(editText.getText().toString(), "")) {
                            ((PaymentActivity)getActivity()).setAboutMe(editText.getText().toString());
                        }
                        dismiss();
                    }
                });
                break;
        }
    }

    private boolean isTemperatureCorrect (String temperatureStr){
        Float temperature = Float.valueOf(temperatureStr);
        return (temperature > 34 && temperature < 43);
    }


    private int toIntTemperature (String temperatureStr){
        Float temperature = Float.valueOf(temperatureStr);
        // save 5 digits after the decimal point
        temperature*=1000;
        return temperature.intValue();
    }

    private BigDecimal toBigDecimalTemperature (int temperatureInt){
       if (temperatureInt == 0)
           return BigDecimal.valueOf(0.0);

        BigDecimal temperature = BigDecimal.valueOf(temperatureInt);
        temperature = temperature.divide(BigDecimal.valueOf(1000), 3, BigDecimal.ROUND_UNNECESSARY);
        return temperature.stripTrailingZeros();
    }

    public void setData (PeriodCalendarEntry entry){
        mEntry = entry;
    }

    public void setData (String text){
        aboutMe = text;
    }
}
