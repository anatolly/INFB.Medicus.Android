package com.intrafab.medicus.medJournal.fragments;

import android.app.Activity;
import android.content.Loader;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.LinearLayoutManager;

import com.intrafab.medicus.R;
import com.intrafab.medicus.medJournal.data.PeriodCalendarEntry;
import com.intrafab.medicus.medJournal.data.PeriodCycleEntry;
import com.intrafab.medicus.medJournal.activities.PeriodCalendarActivity;
import com.intrafab.medicus.medJournal.adapters.PeriodCardAdapter;
import com.intrafab.medicus.medJournal.data.PeriodDataKeeper;
import com.intrafab.medicus.medJournal.loaders.PeriodCalendarEntryListLoader;
import com.intrafab.medicus.medJournal.loaders.PeriodCalendarEntrySaver;
import com.intrafab.medicus.medJournal.loaders.PeriodCycleEntryLoader;
import com.intrafab.medicus.medJournal.loaders.PeriodCycleEntrySaver;
import com.intrafab.medicus.utils.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by Anna Anfilova on 18.08.2015.
 */
public class PlaceHolderMenstrualCycleFragment extends Fragment implements PeriodCardAdapter.OnClickListener {

    public static final String TAG = PlaceHolderMenstrualCycleFragment.class.getName();
    private RecyclerView mRecyclerView;
    private PeriodCardAdapter mAdapter;

    private static final int LOADER_P_CALENDAR_ENTRY_ID = 20;
    private static final int SAVER_P_CALENDAR_ENTRY_ID = 21;
    private static final int LOADER_P_CYCLE_ENTRY_ID = 22;
    private static final int SAVER_P_CYCLE_ENTRY_ID = 23;

    HashMap<Long, PeriodCalendarEntry> mCalendarData;
    ArrayList<PeriodCycleEntry> mCycleData;

    private boolean isCalendarDataLoaded;
    private boolean isCycleDataLoaded;


    private android.app.LoaderManager.LoaderCallbacks<List<PeriodCycleEntry>> mCycleLoaderCallback = new android.app.LoaderManager.LoaderCallbacks<List<PeriodCycleEntry>>() {

        @Override
        public Loader<List<PeriodCycleEntry>> onCreateLoader(int id, Bundle bundle) {
            switch (id) {
                case LOADER_P_CYCLE_ENTRY_ID:
                    return createPCycleEntryLoader();
                case SAVER_P_CYCLE_ENTRY_ID:
                    break;
                default:
                    return null;
            }
            return null;
        }

        @Override
        public void onLoadFinished(Loader<List<PeriodCycleEntry>> loader, List<PeriodCycleEntry> data) {
            int id = loader.getId();
            switch (id) {
                case LOADER_P_CYCLE_ENTRY_ID:
                    finishedCycleEntryLoader(data);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onLoaderReset(Loader<List<PeriodCycleEntry>> loader) {
            int id = loader.getId();
            switch (id) {
                case LOADER_P_CALENDAR_ENTRY_ID:
                    //resetStateEntryLoader();
                    break;
                default:
                    break;
            }
        }
    };

    private android.app.LoaderManager.LoaderCallbacks<List<PeriodCalendarEntry>> mCalendarLoaderCallback = new android.app.LoaderManager.LoaderCallbacks<List<PeriodCalendarEntry>>() {

        @Override
        public Loader<List<PeriodCalendarEntry>> onCreateLoader(int id, Bundle bundle) {
            switch (id) {
                case LOADER_P_CALENDAR_ENTRY_ID:
                    return createPCAclendarEntryLoader();
                case SAVER_P_CALENDAR_ENTRY_ID:
                    //return createPCEntrySaver();
                default:
                    return null;
            }
        }

        @Override
        public void onLoadFinished(Loader<List<PeriodCalendarEntry>> loader, List<PeriodCalendarEntry> data) {
            int id = loader.getId();
            switch (id) {
                case LOADER_P_CALENDAR_ENTRY_ID:
                    finishedCalendarEntryLoader(data);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onLoaderReset(Loader<List<PeriodCalendarEntry>> loader) {
            int id = loader.getId();
            switch (id) {
                case LOADER_P_CALENDAR_ENTRY_ID:
                    //resetStateEntryLoader();
                    break;
                default:
                    break;
            }
        }
    };

    private android.content.Loader<List<PeriodCalendarEntry>> createPCAclendarEntryLoader (){
        return new PeriodCalendarEntryListLoader(getActivity().getApplicationContext());
    }

    private android.content.Loader<List<PeriodCycleEntry>> createPCycleEntryLoader (){
        return new PeriodCycleEntryLoader(getActivity().getApplicationContext());
    }

    private void finishedCalendarEntryLoader (List<PeriodCalendarEntry> data){

        isCalendarDataLoaded = true;
        mCalendarData = PeriodDataKeeper.getInstance().getCalendarData();
        mCalendarData.clear();
        Logger.d (TAG, "finishedCalendarEntryLoaderfinishedCalendarEntryLoaderfinishedCalendarEntryLoader");
        if(data != null) {
            for (PeriodCalendarEntry entry : data)
                mCalendarData.put(entry.getTimeInSec(), entry);
        }
    }

    private void finishedCycleEntryLoader (List<PeriodCycleEntry> data){

        // must think about clearing hashMap before adding new items
        isCycleDataLoaded = true;
        mCycleData = PeriodDataKeeper.getInstance().getPeriodData();
        mCycleData.clear();
        Logger.d (TAG, "finishedCycleEntryLoaderfinishedCycleEntryLoaderfinishedCycleEntryLoader");
        if(data != null) {
            mCycleData.addAll(data);
            //addAllPeriods(data);
            fillCard();
        }
    }

    @Override
    public void onClickItem(int itemPosition, View view) {
        // if this is a calendar card{
        Logger.d (TAG, "onClickItem");
        Logger.d (TAG, "isCalendarDataLoaded" + String.valueOf(isCalendarDataLoaded));
        Logger.d (TAG, "isCycleDataLoaded" + String.valueOf(isCycleDataLoaded));
        //if (isCalendarDataLoaded && isCycleDataLoaded)
            PeriodCalendarActivity.launch(getActivity(), view/*, mCalendarData, mCycleData*/);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Logger.d(TAG, "onAttach");
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.d(TAG, "onCreate");

        mAdapter = new PeriodCardAdapter(this);
    }

    @Override
    public void onDestroy () {
        super.onDestroy();
        if (mCalendarData != null) {
            Logger. d(TAG, "SAVE CALENDAR DATA");
            PeriodCalendarEntrySaver task1 = new PeriodCalendarEntrySaver(getActivity().getApplicationContext(), mCalendarData);
            task1.execute();
        }
        if (mCycleData != null) {
            Logger. d(TAG, "SAVE CYCLE DATA");
            PeriodCycleEntrySaver task2 = new PeriodCycleEntrySaver(getActivity().getApplicationContext(), mCycleData);
            task2.execute();
        }

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

        getActivity().getLoaderManager().initLoader(LOADER_P_CALENDAR_ENTRY_ID, null, mCalendarLoaderCallback);
        getActivity().getLoaderManager().initLoader(LOADER_P_CYCLE_ENTRY_ID, null, mCycleLoaderCallback);

    }

    private void fillCard(){
        if (mCycleData == null || mCycleData.isEmpty())
            return;
        Logger.d(TAG, "fill card!!!");
        mAdapter.setPeriod(mCycleData.get(mCycleData.size()-1));
        mAdapter.notifyDataSetChanged();
    }
}



