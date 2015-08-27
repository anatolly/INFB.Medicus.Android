package com.intrafab.medicus.fragments;


import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.intrafab.medicus.PeriodCalendarDayOptionsActivity;
import com.intrafab.medicus.R;
import com.intrafab.medicus.views.ItemPeriodCycleOptionView;

import java.util.ArrayList;

/**
 * Created by Анна on 27.08.2015.
 */
public class PeriodCalendarOptionListFragment  extends DialogFragment implements View.OnClickListener{

    public static final String TAG = PeriodCalendarNoteFragment.class.getName();
    private ListView listOption;
    private SparseArray userData;
    private ArrayList<Pair<String, Integer>> listOptions;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDismiss(DialogInterface dialog){
        ((PeriodCalendarDayOptionsActivity)getActivity()).changeUserData(ItemPeriodCycleOptionView.MOODS, userData);
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

        listOptions.add (new Pair<>("Pain", R.mipmap.ic1));
        listOptions.add (new Pair<>("Pain1", R.mipmap.ic2));
        listOptions.add (new Pair<>("Pain2", R.mipmap.ic3));
        listOptions.add (new Pair<>("Pain3", R.mipmap.ic4));
        listOptions.add (new Pair<>("Pain4", R.mipmap.ic5));
        listOptions.add (new Pair<>("Pain", R.mipmap.ic1));
        listOptions.add (new Pair<>("Pain1", R.mipmap.ic2));
        listOptions.add (new Pair<>("Pain2", R.mipmap.ic3));
        listOptions.add (new Pair<>("Pain3", R.mipmap.ic4));
        listOptions.add (new Pair<>("Pain4", R.mipmap.ic5));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_option_list, container, false);
        getDialog().setTitle("Moods");
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listOption = (ListView) view.findViewById(R.id.optionList);
        OptionsListAdapter mAdapter = new OptionsListAdapter();
        listOption.setAdapter(mAdapter);
        Button buttonOk = (Button) view.findViewById(R.id.btn_ok);
        Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);

        buttonOk.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

    }

    public void setUserData (SparseArray data){
        userData = data;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_ok:
                break;
            case R.id.btn_cancel:
                break;
        }
    }


    public class OptionsListAdapter extends BaseAdapter{

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
            CheckBox checkbox = (CheckBox)view.findViewById(R.id.check_box);
            if (userData != null)
                checkbox.setChecked(userData.get(position) == 1);
            checkbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    userData.put(position, 1);
                }
            });
            return view;
        }
    }
}
