package com.intrafab.medicus.medJournal.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.intrafab.medicus.AppApplication;
import com.intrafab.medicus.Constants;
import com.intrafab.medicus.R;
import com.intrafab.medicus.medJournal.actions.ActionGetPCEntry;
import com.intrafab.medicus.medJournal.actions.ActionSaveCalendarEntry;
import com.intrafab.medicus.medJournal.activities.ChartActivity;
import com.intrafab.medicus.medJournal.activities.MedicalJournalActivity;
import com.intrafab.medicus.medJournal.activities.PeriodCalendarActivity;
import com.intrafab.medicus.medJournal.activities.PeriodCalendarDayOptionsActivity;
import com.intrafab.medicus.medJournal.data.PeriodCalendarEntry;
import com.intrafab.medicus.medJournal.data.PeriodCycleEntry;
import com.intrafab.medicus.medJournal.data.PeriodDataKeeper;
import com.intrafab.medicus.utils.Connectivity;
import com.intrafab.medicus.utils.Logger;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;
import com.telly.groundy.CallbacksManager;
import com.telly.groundy.Groundy;
import com.telly.groundy.annotations.OnFailure;
import com.telly.groundy.annotations.OnSuccess;
import com.telly.groundy.annotations.Param;
import com.tyczj.extendedcalendarview.Day;
import com.tyczj.extendedcalendarview.ExtendedCalendarView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;


/**
 * Created by Anna Anfilova on 18.08.2015.
 */
public class TestFragment extends Fragment implements ExtendedCalendarView.OnDayClickListener {
    public static final String TAG = TestFragment.class.getName();

    /*private static final int LOADER_P_CALENDAR_ENTRY_ID = 20;
    private static final int SAVER_P_CALENDAR_ENTRY_ID = 21;
    private static final int LOADER_P_CYCLE_ENTRY_ID = 22;
    private static final int SAVER_P_CYCLE_ENTRY_ID = 23;*/
    private static final String EXTRA_OPEN_PERIOD_CALENDAR = "openPeriodCalendar";

    public static final String PERIOD_CALENDAR_ENTRY_DATE = "menstrual_calendar_entry_date";
    public static final String NEW_PERIOD_INDEX = "period_cycle_entry";
    public static final String EXTRA_OPEN_OPTIONS = "openOptions";

    HashMap<Long,PeriodCalendarEntry> mCalendarData;
    ArrayList<PeriodCycleEntry> mCycleData;
    private CallbacksManager mCallbacksManager;

    ExtendedCalendarView calendar;

    private boolean mEnabledSyncMenu;

    FloatingActionButton mActionsMenu;

//    private android.app.LoaderManager.LoaderCallbacks<List<PeriodCycleEntry>> mCycleLoaderCallback = new android.app.LoaderManager.LoaderCallbacks<List<PeriodCycleEntry>>() {
//
//        @Override
//        public Loader<List<PeriodCycleEntry>> onCreateLoader(int id, Bundle bundle) {
//            switch (id) {
//                case LOADER_P_CYCLE_ENTRY_ID:
//                    return createPCycleEntryLoader();
//                case SAVER_P_CYCLE_ENTRY_ID:
//                    break;
//                default:
//                    return null;
//            }
//            return null;
//        }
//
//        @Override
//        public void onLoadFinished(Loader<List<PeriodCycleEntry>> loader, List<PeriodCycleEntry> data) {
//            int id = loader.getId();
//            switch (id) {
//                case LOADER_P_CYCLE_ENTRY_ID:
//                    finishedCycleEntryLoader(data);
//                    break;
//                default:
//                    break;
//            }
//        }
//
//        @Override
//        public void onLoaderReset(Loader<List<PeriodCycleEntry>> loader) {
//            int id = loader.getId();
//            switch (id) {
//                case LOADER_P_CALENDAR_ENTRY_ID:
//                    //resetStateEntryLoader();
//                    break;
//                default:
//                    break;
//            }
//        }
//    };
//
//    private android.app.LoaderManager.LoaderCallbacks<List<PeriodCalendarEntry>> mCalendarLoaderCallback = new android.app.LoaderManager.LoaderCallbacks<List<PeriodCalendarEntry>>() {
//
//        @Override
//        public Loader<List<PeriodCalendarEntry>> onCreateLoader(int id, Bundle bundle) {
//            switch (id) {
//                case LOADER_P_CALENDAR_ENTRY_ID:
//                    return createPCAclendarEntryLoader();
//                case SAVER_P_CALENDAR_ENTRY_ID:
//                //return createPCEntrySaver();
//                default:
//                    return null;
//            }
//        }
//
//        @Override
//        public void onLoadFinished(Loader<List<PeriodCalendarEntry>> loader, List<PeriodCalendarEntry> data) {
//            int id = loader.getId();
//            switch (id) {
//                case LOADER_P_CALENDAR_ENTRY_ID:
//                    finishedCalendarEntryLoader(data);
//                    break;
//                default:
//                    break;
//            }
//        }
//
//        @Override
//        public void onLoaderReset(Loader<List<PeriodCalendarEntry>> loader) {
//            int id = loader.getId();
//            switch (id) {
//                case LOADER_P_CALENDAR_ENTRY_ID:
//                    //resetStateEntryLoader();
//                    break;
//                default:
//                    break;
//            }
//        }
//    };
//
//    private android.content.Loader<List<PeriodCalendarEntry>> createPCAclendarEntryLoader (){
//        return new PeriodCalendarEntryListLoader(PeriodCalendarActivity.this);
//    }
//
//    private android.content.Loader<List<PeriodCycleEntry>> createPCycleEntryLoader (){
//        return new PeriodCycleEntryLoader(PeriodCalendarActivity.this);
//    }
//
//    private void finishedCalendarEntryLoader (List<PeriodCalendarEntry> data){
//        // must think about clearing hashMap before adding new items
//        //mCalendarData.clear();
//        Logger.d (TAG, "finishedPCEntryLoader");
//        if(data != null) {
//            for (PeriodCalendarEntry entry : data) {
//                if (!mCalendarData.containsKey(entry.getDateString()))
//                    mCalendarData.put(entry.getDateString(), entry);
//            }
//            addCalendarEvents(data);
//        }
//    }
//
//    private void finishedCycleEntryLoader (List<PeriodCycleEntry> data){
//        // must think about clearing hashMap before adding new items
//        //mCalendarData.clear();
//        Logger.d (TAG, "finishedCycleEntryLoader");
//        if(data != null) {
//            mCycleData.addAll(data);
//            addAllPeriods(data);
//            if (mCalendarData != null);
//            ///// здесь нужно запускать функцию проверки соответствия mCalendarData и mCycleData
//        }
//    }

    public static void launch(Activity activity, View view/*, ArrayList<PeriodCalendarEntry> calendarData, ArrayList<PeriodCycleEntry> cycleData*/){
        Intent intent = new Intent(activity.getApplicationContext(),TestFragment.class );
        //intent.putParcelableArrayListExtra(PERIOD_CALENDAR_ENTRY, calendarData);
        //intent.putParcelableArrayListExtra(NEW_PERIOD_INDEX, cycleData);
        ActivityCompat.startActivity(activity, intent, null);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mEnabledSyncMenu = true;



        mCallbacksManager = CallbacksManager.init(savedInstanceState);
        mCallbacksManager.linkCallbacks(this);

        //getLoaderManager().initLoader(LOADER_P_CALENDAR_ENTRY_ID, null, mCalendarLoaderCallback);
        //getLoaderManager().initLoader(LOADER_P_CYCLE_ENTRY_ID, null, mCycleLoaderCallback);
        //Intent data = getIntent();
        //ArrayList<PeriodCalendarEntry> listPCEntry = data.getParcelableArrayListExtra(PERIOD_CALENDAR_ENTRY);

        //for (PeriodCalendarEntry entry : listPCEntry){
        //    mCalendarData.put(entry.getDateString(), entry);
        //}



        /*mActionsMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ChartActivity.class);
                startActivity(intent);
            }
        });*/



    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_storage, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        MenuItem item = menu.findItem(R.id.action_sync_with_cloud);
        if (item != null)
            item.setEnabled(mEnabledSyncMenu);
        super.onPrepareOptionsMenu(menu);

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_sync_with_cloud) {
            /*if (!Connectivity.isConnected(this)) {
                showSnackBarError(getResources().getString(R.string.error_internet_not_available));
                return true;
            }*/
            mEnabledSyncMenu = false;
            startRefreshing();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDayClicked(AdapterView<?> adapter, View view, int position, long id, Day day) {
        Calendar cal = Calendar.getInstance();
        cal.set(day.getYear(), day.getMonth(), day.getDay(), 12, 0,0);
        // search for entry for this date
        Long dateInSec = cal.getTimeInMillis()/1000;
        PeriodCalendarDayOptionsActivity.launch(getActivity(), view, dateInSec);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && data != null){
            // get new entry dateInSec
            long firstEntryDateInSec = data.getLongExtra(PERIOD_CALENDAR_ENTRY_DATE, 0);
            // if get 0 -> return
            if (firstEntryDateInSec == 0)
                return;
                // if entry is not null, then put it to the calendar
            else {
                //calendar.addEvents(PeriodDataKeeper.getInstance().addCalendarEvent(firstEntryDateInSec));
                addEntryEvent(firstEntryDateInSec);

                int newPeriodIndex = data.getIntExtra(PeriodCalendarActivity.NEW_PERIOD_INDEX, -1);
                // if a new periodCycle was created
                if (newPeriodIndex != -1) {
                    addNewPeriodEvent(newPeriodIndex);
                }
            }
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        mCallbacksManager.onDestroy();
    }

    /*public void addCalendarEvents (List<PeriodCalendarEntry> data){
        ArrayList<Event> calendarEvents = new ArrayList<>(data.size());
        for (PeriodCalendarEntry entry : data){
            Event event = new Event(entry.getDate().getTimeInMillis());
            event.setIsPeriod(entry.isPeriod());
            event.setIsOvulation(entry.isOvulationDay());
            event.setIsIntercourse(entry.isIntercourse());
            event.setIsPain(false);
            event.setIsTextNote(entry.getNote() != null && !TextUtils.isEmpty(entry.getNote()) && !TextUtils.equals(entry.getNote(), ""));
            calendarEvents.add(event);
        }
        calendar.addEvents(calendarEvents);
    }*/

    public void addEntryEvents (HashMap<Long, PeriodCalendarEntry> data){
        calendar.addEvents(PeriodDataKeeper.getInstance().getCalendarEvents());
    }

    public void addEntryEvent (long entryDateInSec){

        calendar.addEvents(PeriodDataKeeper.getInstance().addEntryEvent(entryDateInSec));
    }

    public void addNewPeriodEvent(int index){
        calendar.addEvents(PeriodDataKeeper.getInstance().addPeriod(index));
    }

    public void addAllPeriods(List<PeriodCycleEntry> allPeriod){
        calendar.addEvents(PeriodDataKeeper.getInstance().setPeriods());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mCallbacksManager.onSaveInstanceState(outState);
    }

    @OnSuccess(ActionGetPCEntry.class)
    public void onSuccessGetEntries() {
        showSnackBarSuccess("Entries get succesfully");
    }

    @OnFailure(ActionGetPCEntry.class)
    public void onFailureGetEntries(@Param(Constants.Extras.PARAM_INTERNET_AVAILABLE) boolean isAvailable) {
/*        mAdapter.hideProgress(mPager.getCurrentItem());
        mActionsMenu.collapse();
//        mBtnSyncCloud.setEnabled(true);
        enabledMenu(true);*/

        if (!isAvailable) {
            showSnackBarError(getResources().getString(R.string.error_internet_not_available));
        } else {
            showSnackBarError(getResources().getString(R.string.error_occurred));
        }
    }

    private void showSnackBarError(String error) {
        SnackbarManager.show(
                Snackbar.with(getActivity()) // context
                        .type(SnackbarType.MULTI_LINE)
                        .text(error)
                        .color(getResources().getColor(R.color.colorLightError))
                        .textColor(getResources().getColor(R.color.colorLightEditTextHint))
                        .swipeToDismiss(true)
                        .duration(Snackbar.SnackbarDuration.LENGTH_LONG)
                , getActivity()); // activity where it is displayed
    }

    private void showSnackBarSuccess(String text) {
        SnackbarManager.show(
                Snackbar.with(getActivity()) // context
                        .type(SnackbarType.MULTI_LINE)
                        .text(text)
                        .color(getResources().getColor(R.color.colorLightSuccess))
                        .textColor(getResources().getColor(R.color.colorLightEditTextHint))
                        .swipeToDismiss(false)
                        .duration(Snackbar.SnackbarDuration.LENGTH_SHORT)
                , getActivity()); // activity where it is displayed
    }

    private void startRefreshing (){
        String userUid = AppApplication.getApplication(getActivity()).getUserAccount().getUid();
        Groundy.create(ActionSaveCalendarEntry.class)
                .callback(getActivity())
                .callbackManager(mCallbacksManager)
                .arg(ActionGetPCEntry.ARG_USER_OWNER_ID,userUid)
                .queueUsing(getActivity());
    }

    @OnSuccess(ActionSaveCalendarEntry.class)
    public void onSuccessRefreshServerEntries() {
        mEnabledSyncMenu = true;
        showSnackBarSuccess("Entries updated succesfully");
    }

    @OnFailure(ActionSaveCalendarEntry.class)
    public void onFailureRefreshServerEntries(@Param(Constants.Extras.PARAM_INTERNET_AVAILABLE) boolean isAvailable) {
        mEnabledSyncMenu = true;
        if (!isAvailable) {
            showSnackBarError(getResources().getString(R.string.error_internet_not_available));
        } else {
            showSnackBarError(getResources().getString(R.string.error_occurred));
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_period_calendar, container, false);

        calendar = (ExtendedCalendarView)v.findViewById(R.id.calendar);
        calendar.setOnDayClickListener(this);

        mActionsMenu = (FloatingActionButton) v.findViewById(R.id.famAddEntry);
        mActionsMenu.setClickable(true);

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mCalendarData = PeriodDataKeeper.getInstance().getCalendarData();
        calendar.addEvents(PeriodDataKeeper.getInstance().setEntryEvents());

        mCycleData = PeriodDataKeeper.getInstance().getPeriodData();
        calendar.addEvents(PeriodDataKeeper.getInstance().setPeriods());
        Logger.d(TAG, "get data from intent:    mCalendarData: " + mCalendarData.size() + "   mCycleData: " + mCycleData.size());
    }
}