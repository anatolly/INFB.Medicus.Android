package com.intrafab.medicus.medJournal.fragments;

import android.app.Activity;
import android.content.Loader;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Window;

import com.intrafab.medicus.R;
import com.intrafab.medicus.medJournal.data.ContraceptionInfo;
import com.intrafab.medicus.medJournal.data.PeriodCalendarEntry;
import com.intrafab.medicus.medJournal.data.PeriodCycleEntry;
import com.intrafab.medicus.medJournal.activities.PeriodCalendarActivity;
import com.intrafab.medicus.medJournal.adapters.PeriodCardAdapter;
import com.intrafab.medicus.medJournal.data.PeriodDataKeeper;
import com.intrafab.medicus.medJournal.loaders.ContraceptionInfoLoader;
import com.intrafab.medicus.medJournal.loaders.PeriodCalendarEntryListLoader;
import com.intrafab.medicus.medJournal.loaders.PeriodCycleEntryLoader;
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
    ContraceptionInfo contraceptionInfo;

    private static final int LOADER_P_CALENDAR_ENTRY_ID = 20;
    private static final int LOADER_P_CYCLE_ENTRY_ID = 22;
    private static final int LOADER_CONTRACEPTION_ID = 24;

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
                default:
                    return null;
            }
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


    private android.app.LoaderManager.LoaderCallbacks<ContraceptionInfo> mContraceptionInfoCallback = new android.app.LoaderManager.LoaderCallbacks<ContraceptionInfo>() {

        @Override
        public Loader<ContraceptionInfo> onCreateLoader(int id, Bundle bundle) {
            switch (id) {
                case LOADER_CONTRACEPTION_ID:
                    Logger.d(TAG, "onCreateLoader LOADER_CONTRACEPTION_ID");
                    return new ContraceptionInfoLoader(getActivity().getApplicationContext());
                default:
                    return null;
            }
        }

        @Override
        public void onLoadFinished(Loader<ContraceptionInfo> loader, ContraceptionInfo contraceptionInfo) {
            if (loader.getId() == LOADER_CONTRACEPTION_ID) {
                Logger.d(TAG, "onLoadFinished LOADER_CONTRACEPTION_ID + \n " );
                if (contraceptionInfo != null)
                    Logger.d (TAG, contraceptionInfo.toString());
                PlaceHolderMenstrualCycleFragment.this.contraceptionInfo = contraceptionInfo;
                if (contraceptionInfo != null)
                Logger.d("on load finished", contraceptionInfo.toString());
                mAdapter.setContraceptionInfo(contraceptionInfo);
                mAdapter.notifyItemChanged(PeriodCardAdapter.PILLS_CARD_TYPE);
            }
        }

        @Override
        public void onLoaderReset(Loader<ContraceptionInfo> loader) {
            int id = loader.getId();
            switch (id) {
                case LOADER_P_CALENDAR_ENTRY_ID:
                    //resetStateEntryLoader();
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
    public void onClickItem(int itemType, View view) {
        // if this is a calendar card{
        if (itemType == PeriodCardAdapter.CYCLE_CARD_TYPE && isCalendarDataLoaded && isCycleDataLoaded)
            PeriodCalendarActivity.launch(getActivity(), view/*, mCalendarData, mCycleData*/);
        else if (itemType == PeriodCardAdapter.PILLS_CARD_TYPE){
            PillsSettingFragment pillsSettingFragment = new PillsSettingFragment();
            pillsSettingFragment.setContraceptionInfo(contraceptionInfo);
            int themeId = pillsSettingFragment.getTheme();
            //pillsSettingFragment.setStyle(android.R.style.Theme_Material_Light_Dialog_NoActionBar, android.R.style.Theme_Material_Light_Dialog);
            //pillsSettingFragment.setStyle(android.R.style.Holo_ButtonBar_AlertDialog, R.style.DialogTheme);
            pillsSettingFragment.show(getActivity().getFragmentManager(),"pills_setting");
        }

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
        getActivity().getLoaderManager().initLoader(LOADER_CONTRACEPTION_ID,null,mContraceptionInfoCallback);
    }

    private void fillCard(){
        if (mCycleData == null || mCycleData.isEmpty())
            return;
        Logger.d(TAG, "fill card!!!");
        mAdapter.setPeriod(mCycleData.get(mCycleData.size()-1));
        mAdapter.notifyDataSetChanged();
    }
}



