package com.intrafab.medicus.medJournal.fragments;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.intrafab.medicus.R;
import com.intrafab.medicus.medJournal.data.PeriodCalendarEntry;

/**
 * Created by Анна on 26.08.2015.
 */
public class PeriodCalendarNoteFragment extends DialogFragment {

    public static final String TAG = PeriodCalendarNoteFragment.class.getName();
    private EditText etNote;
    private PeriodCalendarEntry mEntry;

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
        etNote.setText(mEntry.getNote());
        Button buttonOk = (Button) view.findViewById(R.id.btn_save_note);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.equals(etNote.getText().toString(), ""))
                    mEntry.setNote(etNote.getText().toString());
                    //((PeriodCalendarDayOptionsActivity) getActivity()).changeUserData(etNote.getText().toString());
                dismiss();
            }
        });

    }

    public void setData (PeriodCalendarEntry entry){
        mEntry = entry;
    }
}
