package com.intrafab.medicus.medJournal.fragments;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.intrafab.medicus.R;
import com.intrafab.medicus.medJournal.data.PeriodDataKeeper;
import com.intrafab.medicus.utils.Logger;

/**
 * Created by Анна on 22.09.2015.
 */
public class AllCyclesFragment extends DialogFragment {
    public static final String TAG = AllCyclesFragment.class.getName();

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Logger.d(TAG, "onCreateView");

        View rootView = inflater.inflate(R.layout.fragment_all_cycles, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String stringWithPeriods =  PeriodDataKeeper.getInstance().getPeriodInformation();
        ((TextView)view.findViewById(R.id.textView)).setText(stringWithPeriods);
    }
}
