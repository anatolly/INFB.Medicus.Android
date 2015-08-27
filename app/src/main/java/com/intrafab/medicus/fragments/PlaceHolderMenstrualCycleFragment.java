package com.intrafab.medicus.fragments;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.LinearLayoutManager;

import com.intrafab.medicus.R;
import com.intrafab.medicus.adapters.PeriodCycleAdapter;
import com.intrafab.medicus.utils.Logger;


/**
 * Created by Anna Anfilova on 18.08.2015.
 */
public class PlaceHolderMenstrualCycleFragment extends Fragment{

    public static final String TAG = PlaceHolderMenstrualCycleFragment.class.getName();
    private RecyclerView mRecyclerView;
    private PeriodCycleAdapter.OnClickListener mListener;
    private PeriodCycleAdapter mAdapter;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Logger.d(TAG, "onAttach");

        try {
            mListener = (PeriodCycleAdapter.OnClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement MenstrualCycleAdapter.OnClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mListener = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.d(TAG, "onCreate");

        mAdapter = new PeriodCycleAdapter(mListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Logger.d(TAG, "onCreateView");

        View rootView = inflater.inflate(R.layout.fragment_period_calendar, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.list_cards);
        //mEmptyLayout = (LinearLayout) rootView.findViewById(R.id.layoutEmptyList);
        //mProgress = (ProgressWheel) rootView.findViewById(R.id.progress_wheel);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);
    }
}
