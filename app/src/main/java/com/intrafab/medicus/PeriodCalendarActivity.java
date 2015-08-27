package com.intrafab.medicus;

import android.app.Activity;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.intrafab.medicus.data.PeriodCalendarEntry;
import com.intrafab.medicus.loaders.MenstrualCalendarEntryListLoader;
import com.intrafab.medicus.utils.Logger;
import com.telly.groundy.CallbacksManager;
import com.tyczj.extendedcalendarview.Day;
import com.tyczj.extendedcalendarview.ExtendedCalendarView;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Анна on 19.08.2015.
 */
public class PeriodCalendarActivity extends BaseActivity implements ExtendedCalendarView.OnDayClickListener {
    public static final String TAG = PeriodCalendarActivity.class.getName();

    private static final int LOADER_MC_ENTRY_ID = 20;
    private static final String EXTRA_OPEN_PERIOD_CALENDAR = "openPeriodCalendar";

    HashMap<Long,PeriodCalendarEntry> mCalendarData = new HashMap<>();
    private CallbacksManager mCallbacksManager;

    ExtendedCalendarView calendar;


    private android.app.LoaderManager.LoaderCallbacks<List<PeriodCalendarEntry>> mLoaderCallback = new android.app.LoaderManager.LoaderCallbacks<List<PeriodCalendarEntry>>() {

        @Override
        public Loader<List<PeriodCalendarEntry>> onCreateLoader(int id, Bundle bundle) {
            switch (id) {
                case LOADER_MC_ENTRY_ID:
                    return createMCEntryLoader();
                default:
                    return null;
            }
        }

        @Override
        public void onLoadFinished(Loader<List<PeriodCalendarEntry>> loader, List<PeriodCalendarEntry> data) {
            int id = loader.getId();
            switch (id) {
                case LOADER_MC_ENTRY_ID:
                    finishedStateEntryLoader(data);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onLoaderReset(Loader<List<PeriodCalendarEntry>> loader) {
            int id = loader.getId();
            switch (id) {
                case LOADER_MC_ENTRY_ID:
                    //resetStateEntryLoader();
                    break;
                default:
                    break;
            }
        }
    };

    private android.content.Loader<List<PeriodCalendarEntry>> createMCEntryLoader (){
        Logger.d(TAG, "createPCEntryLoader");
        return new MenstrualCalendarEntryListLoader(PeriodCalendarActivity.this);
    }

    private void finishedStateEntryLoader (List<PeriodCalendarEntry> data){
        // must think about clearing hashMap before adding new items
        for (PeriodCalendarEntry entry : data)
            mCalendarData.put(entry.getTimeInMillis(), entry);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_menstrual_calendar;
    }

    public static void launch(Activity activity, View view){
        Intent intent = new Intent(activity.getApplicationContext(),PeriodCalendarActivity.class );
        ActivityOptionsCompat options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                        view,
                        EXTRA_OPEN_PERIOD_CALENDAR
                );
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        getSupportActionBar().getThemedContext();
        getSupportActionBar().setTitle(getString(R.string.calendar_screen_header));
        showActionBar();
        setActionBarIcon(R.mipmap.ic_action_back);
        ViewCompat.setTransitionName(toolbar, EXTRA_OPEN_PERIOD_CALENDAR);

        calendar = (ExtendedCalendarView)findViewById(R.id.calendar);
        calendar.setOnDayClickListener(this);
        Calendar cal = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
        Calendar _cal = Calendar.getInstance();
        _cal.set(2015, 8, 13);
        calendar.addEvent(_cal, 101);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDayClicked(AdapterView<?> adapter, View view, int position, long id, Day day) {
        Toast.makeText(getApplicationContext(), String.valueOf(day.getDay()), Toast.LENGTH_SHORT).show();
        Calendar cal = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
        cal.set(day.getYear(), day.getMonth(), day.getDay());
        cal.getTimeInMillis();
        // search for entry for this date
        PeriodCalendarEntry item = mCalendarData.get(cal.getTimeInMillis());
        // if we don't have entry, we create new
        if (item == null) {
            Logger.d (TAG, "create new entry");
            item = new PeriodCalendarEntry(day.getYear(), day.getMonth(), day.getDay());
            item.setOvulationDay(true);
            item.setIntercourse(true);
            item.setNote("HELLO!!!!!");
            SparseArray sparseArray = new SparseArray();
            sparseArray.put(1, 1);
            sparseArray.put(3, 1);
            sparseArray.put(5, 1);
            sparseArray.put(25, 1);
            item.setMoods(sparseArray);
        }
        PeriodCalendarDayOptionsActivity.launch(this, view, item);
    }

}
