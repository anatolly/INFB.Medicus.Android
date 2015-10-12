package com.intrafab.medicus.medJournal.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.intrafab.medicus.AppApplication;
import com.intrafab.medicus.BaseActivity;
import com.intrafab.medicus.Constants;
import com.intrafab.medicus.R;
import com.intrafab.medicus.medJournal.data.PeriodCalendarEntry;
import com.intrafab.medicus.medJournal.data.PeriodCycleEntry;
import com.intrafab.medicus.medJournal.data.PeriodDataKeeper;
import com.intrafab.medicus.medJournal.actions.ActionGetPCEntry;
import com.intrafab.medicus.medJournal.actions.ActionSaveCalendarEntry;
import com.intrafab.medicus.medJournal.loaders.PeriodCalendarEntrySaver;
import com.intrafab.medicus.medJournal.loaders.PeriodCycleEntrySaver;
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
 * Created by Анна on 19.08.2015.
 */
public class PeriodCalendarActivity extends BaseActivity implements ExtendedCalendarView.OnDayClickListener {
    public static final String TAG = PeriodCalendarActivity.class.getName();

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

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_period_calendar;
    }

    public static void launch(Activity activity, View view/*, ArrayList<PeriodCalendarEntry> calendarData, ArrayList<PeriodCycleEntry> cycleData*/){
        Intent intent = new Intent(activity.getApplicationContext(),PeriodCalendarActivity.class );
        //intent.putParcelableArrayListExtra(PERIOD_CALENDAR_ENTRY, calendarData);
        //intent.putParcelableArrayListExtra(NEW_PERIOD_INDEX, cycleData);
        ActivityCompat.startActivity(activity, intent, null);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().getThemedContext();
        getSupportActionBar().setTitle(getString(R.string.calendar_screen_header));
        showActionBar();
        setActionBarIcon(R.mipmap.ic_action_back);
        ViewCompat.setTransitionName(toolbar, EXTRA_OPEN_PERIOD_CALENDAR);

        mEnabledSyncMenu = true;

        calendar = (ExtendedCalendarView)findViewById(R.id.calendar);
        calendar.setOnDayClickListener(this);

        mCallbacksManager = CallbacksManager.init(savedInstanceState);
        mCallbacksManager.linkCallbacks(this);

        //getLoaderManager().initLoader(LOADER_P_CALENDAR_ENTRY_ID, null, mCalendarLoaderCallback);
        //getLoaderManager().initLoader(LOADER_P_CYCLE_ENTRY_ID, null, mCycleLoaderCallback);
        //Intent data = getIntent();
        //ArrayList<PeriodCalendarEntry> listPCEntry = data.getParcelableArrayListExtra(PERIOD_CALENDAR_ENTRY);

        //for (PeriodCalendarEntry entry : listPCEntry){
        //    mCalendarData.put(entry.getDateString(), entry);
        //}

        mActionsMenu = (FloatingActionButton) this.findViewById(R.id.famEditPhoto);
        mActionsMenu.setClickable(true);
        mActionsMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                AllCyclesFragment fragment = new AllCyclesFragment();
//                fragment.show(getFragmentManager(), "allCycles");

            Intent intent = new Intent(getApplicationContext(), ChartActivity.class);
            startActivity(intent);
            }
        });

        mCalendarData = PeriodDataKeeper.getInstance().getCalendarData();
        calendar.addEvents(PeriodDataKeeper.getInstance().setEntryEvents());

        mCycleData = PeriodDataKeeper.getInstance().getPeriodData();
        calendar.addEvents(PeriodDataKeeper.getInstance().setPeriods());
        Logger.d (TAG, "get data from intent:    mCalendarData: " +mCalendarData.size() + "   mCycleData: " + mCycleData.size() );

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_storage, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem item = menu.findItem(R.id.action_sync_with_cloud);
        if (item != null)
            item.setEnabled(mEnabledSyncMenu);
        return super.onPrepareOptionsMenu(menu);

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        if (id == R.id.action_sync_with_cloud) {
            if (!Connectivity.isConnected(this)) {
                showSnackBarError(getResources().getString(R.string.error_internet_not_available));
                return true;
            }
            mEnabledSyncMenu = false;
            startRefreshing();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDayClicked(AdapterView<?> adapter, View view, int position, long id, Day day) {
        Calendar cal = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        cal.set(day.getYear(), day.getMonth(), day.getDay(), 12, 0,0);
        if (today.getTimeInMillis() < cal.getTimeInMillis()) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.date_time_error), Toast.LENGTH_LONG).show();
            return;
        }
        // search for entry for this date
        Long dateInSec = cal.getTimeInMillis()/1000;
        PeriodCalendarDayOptionsActivity.launch(this, view, dateInSec);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
    public void onPause() {
        super.onPause();
        if (mCalendarData != null) {
            Logger. d(TAG, "SAVE CALENDAR DATA FROM ACTIVITY");
            PeriodCalendarEntrySaver task1 = new PeriodCalendarEntrySaver(getApplicationContext(), mCalendarData);
            task1.execute();
        }
        if (mCycleData != null) {
            Logger. d(TAG, "SAVE CYCLE DATA FROM ACTIVITY");
            PeriodCycleEntrySaver task2 = new PeriodCycleEntrySaver(getApplicationContext(), mCycleData);
            task2.execute();
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
                Snackbar.with(PeriodCalendarActivity.this) // context
                        .type(SnackbarType.MULTI_LINE)
                        .text(error)
                        .color(getResources().getColor(R.color.colorLightError))
                        .textColor(getResources().getColor(R.color.colorLightEditTextHint))
                        .swipeToDismiss(true)
                        .duration(Snackbar.SnackbarDuration.LENGTH_LONG)
                , PeriodCalendarActivity.this); // activity where it is displayed
    }

    private void showSnackBarSuccess(String text) {
        SnackbarManager.show(
                Snackbar.with(PeriodCalendarActivity.this) // context
                        .type(SnackbarType.MULTI_LINE)
                        .text(text)
                        .color(getResources().getColor(R.color.colorLightSuccess))
                        .textColor(getResources().getColor(R.color.colorLightEditTextHint))
                        .swipeToDismiss(false)
                        .duration(Snackbar.SnackbarDuration.LENGTH_SHORT)
                , PeriodCalendarActivity.this); // activity where it is displayed
    }

    private void startRefreshing (){
        String userUid = AppApplication.getApplication(this).getUserAccount().getUid();
        Groundy.create(ActionSaveCalendarEntry.class)
                .callback(PeriodCalendarActivity.this)
                .callbackManager(mCallbacksManager)
                .arg(ActionGetPCEntry.ARG_USER_OWNER_ID,userUid)
                .queueUsing(PeriodCalendarActivity.this);
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
}
