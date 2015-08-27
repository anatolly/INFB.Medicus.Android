package com.intrafab.medicus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;

import com.intrafab.medicus.adapters.PeriodCalendarOptionsAdapter;
import com.intrafab.medicus.data.PeriodCalendarEntry;
import com.intrafab.medicus.fragments.PeriodCalendarNoteFragment;
import com.intrafab.medicus.fragments.PeriodCalendarOptionListFragment;
import com.intrafab.medicus.utils.Logger;
import com.intrafab.medicus.views.ItemPeriodCycleOptionView;

/**
 * Created by Анна on 24.08.2015.
 */
public class PeriodCalendarDayOptionsActivity extends BaseActivity implements PeriodCalendarOptionsAdapter.OnItemClickListener{

    public static final String TAG = PeriodCalendarDayOptionsActivity.class.getName();
    public static final String MENSTRUAL_CALENDAR_ENTRY = "menstrual_calendar_entry";
    public static final String EXTRA_OPEN_OPTIONS = "openOptions";

    private RecyclerView mRecyclerView;
    private PeriodCalendarOptionsAdapter mAdapter;
    private PeriodCalendarEntry mEntry;

    @Override
    public void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        mEntry = getIntent().getParcelableExtra(MENSTRUAL_CALENDAR_ENTRY);

        getSupportActionBar().getThemedContext();

        getSupportActionBar().setTitle("DAY MONTH YEAR");
        showActionBar();
        setActionBarIcon(R.mipmap.ic_action_back);
        ViewCompat.setTransitionName(toolbar, EXTRA_OPEN_OPTIONS);
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

    public static void launch (Activity activity, View transitionView, PeriodCalendarEntry entry){
        Intent intent = new Intent(activity.getApplicationContext(),PeriodCalendarDayOptionsActivity.class );
        ActivityOptionsCompat options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                        transitionView,
                        EXTRA_OPEN_OPTIONS
                );
        intent.putExtra(MENSTRUAL_CALENDAR_ENTRY, entry);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }

    @Override
    public void onItemClick(int optionType) {
        if (optionType == ItemPeriodCycleOptionView.MEANSES_START)
            mEntry.setMenstrualPeriod(true);
        else if (optionType == ItemPeriodCycleOptionView.MENSTRUAL_FLOW);
        else if (optionType == ItemPeriodCycleOptionView.OVULATION)
            mEntry.setOvulationDay(true);
        else if (optionType == ItemPeriodCycleOptionView.INTERCOURSE)
            mEntry.setIntercourse(true);
        else if (optionType == ItemPeriodCycleOptionView.TEXT){
            PeriodCalendarNoteFragment fragment = new PeriodCalendarNoteFragment();
            fragment.setTextData(mEntry.getNote());
            fragment.show(getFragmentManager(), "note_dialog");
        }
        else if (optionType == ItemPeriodCycleOptionView.MOODS){
            PeriodCalendarOptionListFragment fragment = new PeriodCalendarOptionListFragment();

            fragment.setUserData(mEntry.getMoods());
            fragment.show(getFragmentManager(), "mood_options_dialog");
        }
        else if (optionType == ItemPeriodCycleOptionView.SYMPTOMS){

        }
        else
            Logger.e (TAG, "onItemClick: Unknown optionType");
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void changeUserData(int dataType, SparseArray userData){
        switch (dataType){
            case ItemPeriodCycleOptionView.MOODS:
                mEntry.setMoods(userData);
                for ()
                break;
            case ItemPeriodCycleOptionView.SYMPTOMS:
                break;
        }
    }
}
