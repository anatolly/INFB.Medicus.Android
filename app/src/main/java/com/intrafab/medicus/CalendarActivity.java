package com.intrafab.medicus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.intrafab.medicus.actions.ActionRequestAcceptAllTask;
import com.intrafab.medicus.actions.ActionRequestStateEntryTask;
import com.intrafab.medicus.adapters.StateEntryAdapter;
import com.intrafab.medicus.data.StateEntry;
import com.intrafab.medicus.data.StateEntryType;
import com.intrafab.medicus.fragments.PlaceholderStateEntryFragment;
import com.intrafab.medicus.loaders.StateEntryListLoader;
import com.intrafab.medicus.utils.Connectivity;
import com.intrafab.medicus.utils.Logger;
import com.intrafab.medicus.views.ItemStateEntryView;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;
import com.telly.groundy.CallbacksManager;
import com.telly.groundy.Groundy;
import com.telly.groundy.annotations.OnFailure;
import com.telly.groundy.annotations.OnSuccess;
import com.telly.groundy.annotations.Param;

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

    private boolean mIsNeedAcceptAll = false;

    private FloatingActionsMenu mActionsMenu;
    private FloatingActionButton mFabAcceptAll;
    private FloatingActionButton mFabSyncCloud;

    //private TextView mBtnClear;
    //private TextView mBtnAcceptAll;

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
            String userUid = AppApplication.getApplication(this).getUserAccount().getUid();
            Groundy.create(ActionRequestStateEntryTask.class)
                    .callback(CalendarActivity.this)
                    .callbackManager(mCallbacksManager)
                    .arg(ActionRequestStateEntryTask.ARG_USER_OWNER_ID, userUid)
                    .queueUsing(CalendarActivity.this);
        } else {
            if (mFabAcceptAll != null)
                mFabAcceptAll.setEnabled(true);
            if (mFabSyncCloud != null)
                mFabSyncCloud.setEnabled(true);
            Logger.d(TAG, "finishedStateEntryLoader setData size = " + data.size());
            if (fragment != null) {
                fragment.hideProgress();
                fragment.setData(data);

                for (StateEntry entry : data) {
                    String status = entry.getStateStatus();
                    if (!TextUtils.isEmpty(status) && status.equals(StateEntryType.STATUSES.get(2))) { //Changed
                        mIsNeedAcceptAll = true;
                        showAcceptAll();
                        break;
                    }
                }

                if (!mIsNeedAcceptAll) {
                    hideAcceptAll();
                }
            }
        }
    }

    private void resetStateEntryLoader() {
        Logger.d(TAG, "resetStateEntryLoader");

        PlaceholderStateEntryFragment fragment = getFragment();
        if (fragment != null) {
            fragment.hideProgress();
            fragment.setData(null);
            mIsNeedAcceptAll = false;
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
        setActionBarIcon(R.mipmap.ic_action_back);

        ViewCompat.setTransitionName(toolbar, EXTRA_OPEN_CALENDAR);

        mCallbacksManager = CallbacksManager.init(savedInstanceState);
        mCallbacksManager.linkCallbacks(this);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderStateEntryFragment(), PlaceholderStateEntryFragment.TAG)
                    .commit();
        }

        mActionsMenu = (FloatingActionsMenu) this.findViewById(R.id.famCalendar);
        mFabAcceptAll = (FloatingActionButton) this.findViewById(R.id.fabAcceptAll);
        mFabSyncCloud = (FloatingActionButton) this.findViewById(R.id.fabSyncCloud);

        mFabAcceptAll.setOnClickListener(this);
        mFabSyncCloud.setOnClickListener(this);

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

    @Override
    protected void onResume() {
        super.onResume();

        if (mIsNeedAcceptAll)
            showAcceptAll();
        else
            hideAcceptAll();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClickItem(StateEntry itemStateEntry, ItemStateEntryView entryView) {
        EventDetailActivity.launch(this, entryView.getThumbnail(), itemStateEntry);
    }

    @Override
    public void onClick(View v) {
        PlaceholderStateEntryFragment fragment = getFragment();
        switch (v.getId()) {
            case R.id.fabSyncCloud:
                mActionsMenu.collapse();
                mFabAcceptAll.setEnabled(false);
                mFabSyncCloud.setEnabled(false);
                if (!Connectivity.isConnected(this)) {
                    showSnackBarError(getResources().getString(R.string.error_internet_not_available));
                    mFabAcceptAll.setEnabled(true);
                    mFabSyncCloud.setEnabled(true);
                    return;
                }

                if (fragment != null) {
                    fragment.hideProgress();
                    fragment.setData(null);
                }

                if (fragment != null)
                    fragment.showProgress();
                String userUid = AppApplication.getApplication(this).getUserAccount().getUid();
                Groundy.create(ActionRequestStateEntryTask.class)
                        .callback(CalendarActivity.this)
                        .callbackManager(mCallbacksManager)
                        .arg(ActionRequestStateEntryTask.ARG_USER_OWNER_ID, userUid)
                        .queueUsing(CalendarActivity.this);
                break;
            case R.id.fabAcceptAll:
                mActionsMenu.collapse();
                mFabAcceptAll.setEnabled(false);
                mFabSyncCloud.setEnabled(false);
                if (fragment != null)
                    fragment.showProgress();

                Groundy.create(ActionRequestAcceptAllTask.class)
                        .callback(CalendarActivity.this)
                        .callbackManager(mCallbacksManager)
                        .queueUsing(CalendarActivity.this);
                break;
        }
    }

    private void showAcceptAll() {
        if (mFabAcceptAll != null) {
            mFabAcceptAll.setVisibility(View.VISIBLE);
        }
    }

    private void hideAcceptAll() {
        if (mFabAcceptAll != null) {
            mFabAcceptAll.setVisibility(View.GONE);
        }
    }

    private void showSnackBarError(String error) {
        SnackbarManager.show(
                Snackbar.with(CalendarActivity.this) // context
                        .type(SnackbarType.MULTI_LINE)
                        .text(error)
                        .color(getResources().getColor(R.color.colorLightError))
                        .textColor(getResources().getColor(R.color.colorLightEditTextHint))
                        .swipeToDismiss(true)
                        .duration(Snackbar.SnackbarDuration.LENGTH_LONG)
                , CalendarActivity.this); // activity where it is displayed
    }

    @OnSuccess(ActionRequestAcceptAllTask.class)
    public void onSuccessRequestAcceptAll() {
        mFabAcceptAll.setEnabled(true);
        mFabSyncCloud.setEnabled(true);
        PlaceholderStateEntryFragment fragment = getFragment();
        if (fragment != null) {
            fragment.hideProgress();
        }

        mIsNeedAcceptAll = false;
        hideAcceptAll();
    }

    @OnFailure(ActionRequestAcceptAllTask.class)
    public void onFailureRequestAcceptAll(
            @Param(Constants.Extras.PARAM_INTERNET_AVAILABLE) boolean isAvailable) {
        mFabAcceptAll.setEnabled(true);
        mFabSyncCloud.setEnabled(true);
        if (!isAvailable) {
            showSnackBarError(getResources().getString(R.string.error_internet_not_available));
        } else {
            showSnackBarError(getResources().getString(R.string.error_occurred));
        }
    }

    @OnSuccess(ActionRequestStateEntryTask.class)
    public void onSuccessRequestStateEntry() {
        PlaceholderStateEntryFragment fragment = getFragment();
        if (fragment != null) {
            fragment.hideProgress();
            fragment.setData(null);
        }

        mFabAcceptAll.setEnabled(true);
        mFabSyncCloud.setEnabled(true);
    }

    @OnFailure(ActionRequestStateEntryTask.class)
    public void onFailureRequestStateEntry(
            @Param(Constants.Extras.PARAM_INTERNET_AVAILABLE) boolean isAvailable) {
        PlaceholderStateEntryFragment fragment = getFragment();
        if (fragment != null) {
            fragment.hideProgress();
            fragment.setData(null);
        }
        if (!isAvailable) {
            showSnackBarError(getResources().getString(R.string.error_internet_not_available));
        } else {
            showSnackBarError(getResources().getString(R.string.error_occurred));
        }

        mFabAcceptAll.setEnabled(true);
        mFabSyncCloud.setEnabled(true);
    }
}
