package com.intrafab.medicus.medJournal.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import com.intrafab.medicus.BaseActivity;
import com.intrafab.medicus.R;
import com.intrafab.medicus.medJournal.data.PeriodCycleEntry;
import com.intrafab.medicus.medJournal.data.PeriodDataKeeper;
import com.intrafab.medicus.medJournal.views.ItemPeriodCycleOptionView;
import com.intrafab.medicus.medJournal.data.PeriodCalendarEntry;
import com.intrafab.medicus.medJournal.fragments.PeriodCalendarNoteFragment;
import com.intrafab.medicus.medJournal.fragments.PeriodCalendarOptionListFragment;
import com.intrafab.medicus.medJournal.adapters.PeriodCalendarOptionsAdapter;
import com.intrafab.medicus.medJournal.fragments.PeriodEndSettingFragment;
import com.intrafab.medicus.utils.Logger;

import java.util.Calendar;

/**
 * Created by Анна on 24.08.2015.
 */
public class PeriodCalendarDayOptionsActivity extends BaseActivity implements PeriodCalendarOptionsAdapter.OnItemClickListener {

    public static final String TAG = PeriodCalendarDayOptionsActivity.class.getName();


    private RecyclerView mRecyclerView;
    private PeriodCalendarOptionsAdapter mAdapter;
    private PeriodCalendarEntry mEntry;
    private boolean isNewPeriod = false;

    @Override
    public void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        long dateInSec = getIntent().getLongExtra(PeriodCalendarActivity.PERIOD_CALENDAR_ENTRY_DATE,0);
        mEntry = PeriodDataKeeper.getInstance().getCalendarData().get(dateInSec);
        Logger. d(TAG, "dateInSec in OnDayCick Listener" + dateInSec);
        PeriodDataKeeper.getInstance().showAllEntries();

        if (dateInSec == 0){
            Logger.e (TAG, "dateInMillis = 0!!!");
        }

        if (mEntry == null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(dateInSec * 1000);
            mEntry = new PeriodCalendarEntry(calendar);
            Logger.d (TAG, "create new entry");
        }

        getSupportActionBar().getThemedContext();

        getSupportActionBar().setTitle(mEntry.getDay() + "." + (mEntry.getMonth()+1) + "." + mEntry.getYear());
        showActionBar();
        setActionBarIcon(R.mipmap.ic_action_back);
        ViewCompat.setTransitionName(toolbar, PeriodCalendarActivity.EXTRA_OPEN_OPTIONS);
        mRecyclerView = (RecyclerView) findViewById(R.id.list_options);
        mAdapter = new PeriodCalendarOptionsAdapter(this, mEntry);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_menst_cal_options;
    }

    public static void launch (Activity activity, View transitionView, long dateInMillis){
        Intent intent = new Intent(activity.getApplicationContext(),PeriodCalendarDayOptionsActivity.class );
        intent.putExtra(PeriodCalendarActivity.PERIOD_CALENDAR_ENTRY_DATE, dateInMillis);
        ActivityCompat.startActivityForResult(activity, intent, 1, null);
    }

    @Override
    public void onItemClick(int optionType) {
        if (optionType == ItemPeriodCycleOptionView.PERIOD) {
            if (mEntry.isPeriod()){
                endPeriod();
            }
            else {
                PeriodEndSettingFragment fragment = new PeriodEndSettingFragment();
                fragment.setSetting(PeriodDataKeeper.menstrualDuration);
                fragment.show(getFragmentManager(), "period_setting");
            }
        }
        else if (optionType == ItemPeriodCycleOptionView.INTERCOURSE)
            mEntry.setIntercourse(!mEntry.isIntercourse());
        else if (optionType == ItemPeriodCycleOptionView.TEXT){
            PeriodCalendarNoteFragment fragment = new PeriodCalendarNoteFragment();
            fragment.setData(mEntry);
            fragment.show(getFragmentManager(), "note_dialog");
        }
        else if (optionType == ItemPeriodCycleOptionView.MOODS){
            PeriodCalendarOptionListFragment moodFragment = new PeriodCalendarOptionListFragment();
            moodFragment.setType(ItemPeriodCycleOptionView.MOODS);
            moodFragment.setData(mEntry);
            moodFragment.show(getFragmentManager(), "mood_options_dialog");
        }
        else if (optionType == ItemPeriodCycleOptionView.SYMPTOMS){
            PeriodCalendarOptionListFragment symptomsFragment = new PeriodCalendarOptionListFragment();
            symptomsFragment.setType(ItemPeriodCycleOptionView.SYMPTOMS);
            symptomsFragment.setData(mEntry);
            symptomsFragment.show(getFragmentManager(), "symptoms_options_dialog");
        }
        else
            Logger.e (TAG, "onItemClick: Unknown optionType");
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {

            if (!mEntry.isEmpty())
                PeriodDataKeeper.getInstance().getCalendarData().put(mEntry.getTimeInSec(), mEntry);
            Intent intent = new Intent();

            if (isNewPeriod){
                Logger.d (TAG, "create new period");
                /// create new PeriodCycleEntry
                PeriodCycleEntry newPeriod = new PeriodCycleEntry(mEntry.getTimeInSec()*1000);
                newPeriod.setMentsrualDuration(PeriodDataKeeper.menstrualDuration);
                newPeriod.setFertileFirstDay(PeriodDataKeeper.periodDuration / 2 - 5);
                newPeriod.setPeriodDuration(PeriodDataKeeper.periodDuration);
                newPeriod.setOvulationDay(PeriodDataKeeper.periodDuration / 2);

                int periodIndex = PeriodDataKeeper.getInstance().addPeriodToList(newPeriod);
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(newPeriod.getFirstDay());
                for (int i = 2; i<= newPeriod.getMentsrualDuration(); i++) {
                    calendar.add(Calendar.DATE, 1);
                    Long timeInSec = calendar.getTimeInMillis()/1000;
                    if (PeriodDataKeeper.getInstance().getCalendarData().containsKey(timeInSec)){
                        PeriodCalendarEntry existEntry = PeriodDataKeeper.getInstance().getCalendarData().get(timeInSec);
                        existEntry.setMenstrualPeriod(i);
                        Logger.d(TAG, "period entry exist");
                    } else {
                        Logger.d(TAG, "new period entry: " + calendar.get(Calendar.DAY_OF_MONTH)+"."+calendar.get(Calendar.MONTH)+"."+calendar.get(Calendar.YEAR));
                        PeriodCalendarEntry newEntry = new PeriodCalendarEntry(calendar);
                        newEntry.setMenstrualPeriod(i);
                        PeriodDataKeeper.getInstance().getCalendarData().put(timeInSec, newEntry);
                        Logger.d (TAG, "mCalendarData.size(): + " + PeriodDataKeeper.getInstance().getCalendarData().size());
                    }
                }
                // put it to intent
                intent.putExtra(PeriodCalendarActivity.NEW_PERIOD_INDEX, periodIndex);
            }
            /// отправим в intent timeInMillis для измененной записи
            intent.putExtra(PeriodCalendarActivity.PERIOD_CALENDAR_ENTRY_DATE, mEntry.getTimeInSec());
            setResult(1, intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setPeriod(){
        ///set first day of the period
        isNewPeriod = true;
        mEntry.setMenstrualPeriod(1);
        mAdapter.notifyItemChanged(ItemPeriodCycleOptionView.PERIOD);
    }

    public void endPeriod() {
        mEntry.setMenstrualPeriod(0);
        mAdapter.notifyItemChanged(ItemPeriodCycleOptionView.PERIOD);
    }

}
