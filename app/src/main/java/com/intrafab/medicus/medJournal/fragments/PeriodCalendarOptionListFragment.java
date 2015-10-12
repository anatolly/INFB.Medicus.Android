package com.intrafab.medicus.medJournal.fragments;


import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.intrafab.medicus.R;
import com.intrafab.medicus.medJournal.data.PeriodCalendarEntry;
import com.intrafab.medicus.medJournal.views.ItemPeriodCycleOptionView;

import java.util.ArrayList;

/**
 * Created by Анна on 27.08.2015.
 */
public class PeriodCalendarOptionListFragment  extends DialogFragment{

    public static final String TAG = PeriodCalendarOptionListFragment.class.getName();
    private ListView listOption;
    private int[] userData;
    private ArrayList<Pair<String, Integer>> listOptions;
    private int type;
    PeriodCalendarEntry mEntry;

    public void setType (int type){
        this.type = type;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /// create list options
        listOptions = new ArrayList<>();
        switch (type){
            case ItemPeriodCycleOptionView.SYMPTOMS:
                listOptions.add (new Pair<>("Головная боль", R.mipmap.ic1));
                listOptions.add (new Pair<>("Боль в животе", R.mipmap.ic2));
                listOptions.add (new Pair<>("Тошнота", R.mipmap.ic3));
                listOptions.add (new Pair<>("Колики", R.mipmap.ic4));
                listOptions.add (new Pair<>("Боль в суставах", R.mipmap.ic5));
                listOptions.add (new Pair<>("Боль в груди", R.mipmap.ic1));
                listOptions.add (new Pair<>("Акне", R.mipmap.ic2));
                listOptions.add (new Pair<>("Боль в спине", R.mipmap.ic3));
                listOptions.add (new Pair<>("Боль в шее", R.mipmap.ic4));
                listOptions.add(new Pair<>("Боль в мышцах", R.mipmap.ic5));
                break;
            case ItemPeriodCycleOptionView.MOODS:
                listOptions.add (new Pair<>("Сердитая", R.mipmap.ic1));
                listOptions.add (new Pair<>("Раздраженная", R.mipmap.ic2));
                listOptions.add (new Pair<>("Спокойная", R.mipmap.ic3));
                listOptions.add (new Pair<>("Вялая", R.mipmap.ic4));
                listOptions.add (new Pair<>("Смущенная", R.mipmap.ic5));
                listOptions.add (new Pair<>("Веселая", R.mipmap.ic1));
                listOptions.add (new Pair<>("Энергичная", R.mipmap.ic2));
                listOptions.add (new Pair<>("Счасливая", R.mipmap.ic3));
                listOptions.add (new Pair<>("Голодная!", R.mipmap.ic4));
                listOptions.add(new Pair<>("Влюбленная", R.mipmap.ic5));
                break;
        }
        userData = new int[listOptions.size()];
        String data = null;
        if (type == ItemPeriodCycleOptionView.MOODS)
            data = mEntry.getMoods();
        else if (type == ItemPeriodCycleOptionView.SYMPTOMS)
            data = mEntry.getSymptoms();
        if (data != null && !(TextUtils.isEmpty(data))) {
            String[] splittedStr = data.split(",");
            for (int i = 0; i < splittedStr.length; i++)
                userData[i] = Integer.valueOf(splittedStr[i]);
        }
    }

    public String arrayToString (int [] array){
        StringBuilder builder = new StringBuilder();
        for (int i=0; i< array.length; i++) {
            builder.append(array[i]);
            builder.append(',');
        }
        return builder.toString();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_option_list, container, false);
        switch (type){
            case ItemPeriodCycleOptionView.MOODS:
                getDialog().setTitle(getResources().getString(R.string.header_moods));
                break;
            case ItemPeriodCycleOptionView.SYMPTOMS:
                getDialog().setTitle(getResources().getString(R.string.header_symptoms));
                break;
        }
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listOption = (ListView) view.findViewById(R.id.optionList);
        OptionsListAdapter mAdapter = new OptionsListAdapter();
        listOption.setAdapter(mAdapter);
        listOption.setOnItemClickListener(mAdapter);
        Button buttonOk = (Button) view.findViewById(R.id.btn_ok);
        Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type == ItemPeriodCycleOptionView.MOODS)
                    mEntry.setMoods(arrayToString(userData));
                else if (type == ItemPeriodCycleOptionView.SYMPTOMS)
                    mEntry.setSymptoms(arrayToString(userData));
                dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    public void setData (PeriodCalendarEntry entry){
        mEntry = entry;
    }

    public class OptionsListAdapter extends BaseAdapter implements ListView.OnItemClickListener{

        @Override
        public int getCount() {
            return listOptions.size();
        }

        @Override
        public Object getItem(int i) {
            return listOptions.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            if (view == null){
                Context context = parent.getContext();
                view = LayoutInflater.from(context).inflate(R.layout.view_option_item, parent, false);
            }
            ((TextView)view.findViewById(R.id.tvOptionName)).setText(listOptions.get(position).first);
            ((ImageView)view.findViewById(R.id.icOption)).setImageResource(listOptions.get(position).second);
            final CheckBox checkbox = (CheckBox)view.findViewById(R.id.check_box);
            checkbox.setClickable(false);
            if (userData != null && userData.length > position)
                checkbox.setChecked(userData[position] == 1);
            return view;
        }

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                CheckBox checkbox = (CheckBox) view.findViewById(R.id.check_box);
                checkbox.setChecked(!checkbox.isChecked());
                userData[position] =  checkbox.isChecked() ? 1 : 0;
        }
    }
}
