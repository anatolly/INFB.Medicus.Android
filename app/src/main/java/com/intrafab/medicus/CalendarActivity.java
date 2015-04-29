package com.intrafab.medicus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.intrafab.medicus.actions.ActionRequestStateEntryTask;
import com.intrafab.medicus.adapters.StateEntryAdapter;
import com.intrafab.medicus.data.StateEntry;
import com.intrafab.medicus.db.DBManager;
import com.intrafab.medicus.fragments.PlaceholderStateEntryFragment;
import com.intrafab.medicus.loaders.StateEntryListLoader;
import com.intrafab.medicus.utils.Logger;
import com.telly.groundy.CallbacksManager;
import com.telly.groundy.Groundy;

import java.util.List;

/**
 * Created by Artemiy Terekhov on 10.04.2015.
 */
public class CalendarActivity extends BaseActivity
        implements StateEntryAdapter.OnClickListener,
        View.OnClickListener {

    public static final String TAG = CalendarActivity.class.getName();
    public static final String EXTRA_OPEN_CALENDAR = "openCalendar";

    private static final int LOADER_ENTRY_ID = 12;

    private CallbacksManager mCallbacksManager;

    private TextView mBtnClear;
    private TextView mBtnAcceptAll;

    private android.app.LoaderManager.LoaderCallbacks<List<StateEntry>> mLoaderCallback = new android.app.LoaderManager.LoaderCallbacks<List<StateEntry>>() {
        @Override
        public android.content.Loader<List<StateEntry>> onCreateLoader(int id, Bundle args) {
            switch (id) {
                case LOADER_ENTRY_ID:
                    return createStateEntryLoader();
                default:
                    return null;
            }
        }

        @Override
        public void onLoadFinished(android.content.Loader<List<StateEntry>> loader, List<StateEntry> data) {
            int id = loader.getId();
            switch (id) {
                case LOADER_ENTRY_ID:
                    finishedStateEntryLoader(data);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onLoaderReset(android.content.Loader<List<StateEntry>> loader) {
            int id = loader.getId();
            switch (id) {
                case LOADER_ENTRY_ID:
                    resetStateEntryLoader();
                    break;
                default:
                    break;
            }
        }
    };

    private android.content.Loader<List<StateEntry>> createStateEntryLoader() {
        Logger.d(TAG, "createStateEntryLoader");
        return new StateEntryListLoader(CalendarActivity.this);
    }

    private void finishedStateEntryLoader(List<StateEntry> data) {
        PlaceholderStateEntryFragment fragment = getFragment();
        if (data == null) {
            Logger.d(TAG, "finishedStateEntryLoader start ActionRequestStorageTask");
            if (fragment != null)
                fragment.showProgress();
            Groundy.create(ActionRequestStateEntryTask.class)
                    .callback(CalendarActivity.this)
                    .callbackManager(mCallbacksManager)
                    .queueUsing(CalendarActivity.this);
        } else {
            Logger.d(TAG, "finishedStateEntryLoader setData size = " + data.size());
            if (fragment != null) {
                fragment.hideProgress();
                fragment.setData(data);
            }
        }
    }

    private void resetStateEntryLoader() {
        Logger.d(TAG, "resetStateEntryLoader");

        PlaceholderStateEntryFragment fragment = getFragment();
        if (fragment != null) {
            fragment.hideProgress();
            fragment.setData(null);
        }
    }

    public PlaceholderStateEntryFragment getFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(PlaceholderStateEntryFragment.TAG);
        if (fragment != null && fragment instanceof PlaceholderStateEntryFragment)
            return (PlaceholderStateEntryFragment) fragment;

        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().getThemedContext();

        getSupportActionBar().setTitle("Calendar");
        showActionBar();

        ViewCompat.setTransitionName(toolbar, EXTRA_OPEN_CALENDAR);

        mCallbacksManager = CallbacksManager.init(savedInstanceState);
        mCallbacksManager.linkCallbacks(this);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderStateEntryFragment(), PlaceholderStateEntryFragment.TAG)
                    .commit();
        }

        mBtnClear = (TextView) this.findViewById(R.id.btnClear);
        mBtnAcceptAll = (TextView) this.findViewById(R.id.btnAcceptAll);

        int rippleColor = getResources().getColor(R.color.colorLightPrimary);
        float rippleAlpha = 0.5f;

        mBtnClear.setOnClickListener(this);

        MaterialRippleLayout.on(mBtnClear)
                .rippleColor(rippleColor)
                .rippleAlpha(rippleAlpha)
                .rippleHover(true)
                .create();

        mBtnAcceptAll.setOnClickListener(this);

        MaterialRippleLayout.on(mBtnAcceptAll)
                .rippleColor(rippleColor)
                .rippleAlpha(rippleAlpha)
                .rippleHover(true)
                .create();

        toolbar.post(new Runnable() {
            @Override
            public void run() {
                startStateEntryLoading();
            }
        });

    }

    private void startStateEntryLoading() {
        PlaceholderStateEntryFragment fragment = getFragment();
        if (fragment == null)
            return;

        if (fragment.isProgress())
            return;
        
        fragment.showProgress();
        getLoaderManager().initLoader(LOADER_ENTRY_ID, null, mLoaderCallback);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_calendar;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mCallbacksManager.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCallbacksManager.onDestroy();
    }

    public static void launch(BaseActivity activity, View transitionView) {
        Intent intent = new Intent(activity, CalendarActivity.class);

        ActivityOptionsCompat options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                        transitionView,
                        EXTRA_OPEN_CALENDAR
                );
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }

    @Override
    public void onClickItem(StateEntry itemStateEntry) {
        EventDetailActivity.launch(this, toolbar, itemStateEntry);
    }

    @Override
    public void onClick(View v) {
        PlaceholderStateEntryFragment fragment = getFragment();
        switch (v.getId()) {
            case R.id.btnClear:
                if (fragment != null) {
                    fragment.hideProgress();
                    fragment.setData(null);
                }
                break;
            case R.id.btnAcceptAll:
                if (fragment != null) {
                    fragment.hideProgress();
                    fragment.setData(null);
                }
                DBManager.getInstance().deleteObject(Constants.Prefs.PREF_PARAM_STTATE_ENTRIES, StateEntryListLoader.class);
                break;
        }
    }
}
