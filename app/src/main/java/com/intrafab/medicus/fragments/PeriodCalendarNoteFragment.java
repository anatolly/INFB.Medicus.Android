package com.intrafab.medicus.fragments;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.intrafab.medicus.R;

/**
 * Created by Анна on 26.08.2015.
 */
public class PeriodCalendarNoteFragment extends DialogFragment {

    public static final String TAG = PeriodCalendarNoteFragment.class.getName();
    private EditText etNote;
    private String data;

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
        View rootView = inflater.inflate(R.layout.fragment_note, container, false);
        getDialog().setTitle("Note");
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etNote = (EditText) view.findViewById(R.id.et_note);
        etNote.setText(data);
        Button buttonOk = (Button) view.findViewById(R.id.btn_save_note);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    public void setTextData (String note){
        data = note;
    }
}
