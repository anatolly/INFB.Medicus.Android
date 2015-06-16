package com.intrafab.medicus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.intrafab.medicus.actions.ActionRequestChangeStatusTask;
import com.intrafab.medicus.data.StateEntry;
import com.intrafab.medicus.data.StateEntryType;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;
import com.telly.groundy.CallbacksManager;
import com.telly.groundy.Groundy;
import com.telly.groundy.annotations.OnFailure;
import com.telly.groundy.annotations.OnSuccess;
import com.telly.groundy.annotations.Param;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Artemiy Terekhov on 29.04.2015.
 */
public class EventDetailActivity extends BaseActivity
        implements View.OnClickListener {

    public static final String TAG = EventDetailActivity.class.getName();
    public static final String EXTRA_OPEN_EVENT_DETAIL = "openEventDetail";
    public static final String EXTRA_STATE_ENTRY = "state_entry";

    private StateEntry mStateEntry;

    private ImageView mViewThumbnail;

    private TextView mViewHeaderStart;
    private TextView mViewValueStart;

    private TextView mViewHeaderEnd;
    private TextView mViewValueEnd;

    private TextView mViewHeaderStatus;
    private TextView mViewValueStatus;

    private CardView mViewCardEntry;

    private TextView mViewBtnAccept;
    private TextView mViewBtnChange;
    private TextView mViewBtnCancel;

    private CallbacksManager mCallbacksManager;

    private MaterialDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().getThemedContext();

        Intent intent = getIntent();
        if (intent != null) {
            mStateEntry = getIntent().getParcelableExtra(EXTRA_STATE_ENTRY);
        }

        getSupportActionBar().setTitle(mStateEntry.getStateDescription());
        showActionBar();
        setActionBarIcon(R.mipmap.ic_action_back);

        mCallbacksManager = CallbacksManager.init(savedInstanceState);
        mCallbacksManager.linkCallbacks(this);

        StateEntryType entryType = null;
        String itemType = mStateEntry.getStateType();
        if (itemType.equals("default")) {
            entryType = StateEntryType.getDefault();
            //showActionBar();
        } else {
            if (!TextUtils.isEmpty(itemType) && StateEntryType.TYPES.containsKey(itemType)) {
                entryType = StateEntryType.TYPES.get(itemType);
//                if (toolbar != null) {
//                    toolbar.setVisibility(View.VISIBLE);
//                    bar.setBackgroundDrawable(new ColorDrawable(entryType.getBackgroundColor()));
//                }
            } else {
                entryType = StateEntryType.getDefault();
                //showActionBar();
            }
        }

        injectViews();
        setupViews(entryType);
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

    private void injectViews() {
        mViewThumbnail = (ImageView) this.findViewById(R.id.ivThumbnail);

        mViewHeaderStart = (TextView) this.findViewById(R.id.tvHeaderStart);
        mViewValueStart = (TextView) this.findViewById(R.id.tvValueStart);

        mViewHeaderEnd = (TextView) this.findViewById(R.id.tvHeaderEnd);
        mViewValueEnd = (TextView) this.findViewById(R.id.tvValueEnd);

        mViewHeaderStatus = (TextView) this.findViewById(R.id.tvHeaderStatus);
        mViewValueStatus = (TextView) this.findViewById(R.id.tvValueStatus);

        mViewCardEntry = (CardView) this.findViewById(R.id.cardEntry);

        mViewBtnAccept = (TextView) this.findViewById(R.id.btnAccept);
        mViewBtnChange = (TextView) this.findViewById(R.id.btnChange);
        mViewBtnCancel = (TextView) this.findViewById(R.id.btnCancel);
    }

    private void setupViews(StateEntryType entryType) {
        mViewThumbnail.setImageResource(entryType.getResIconId());
        mViewThumbnail.setColorFilter(entryType.getBackgroundColor());
        //mViewCardEntry.setCardBackgroundColor(entryType.getBackgroundColor());

        mViewHeaderStart.setTextColor(entryType.getTextColor());
        mViewValueStart.setTextColor(entryType.getTextColor());

        if (mStateEntry.getStateStart() > 0) {
            Calendar calStart = GregorianCalendar.getInstance();
            calStart.setTimeInMillis(mStateEntry.getStateStart());

            String dateTime = DateUtils.formatDateTime(this, calStart.getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_TIME);
            mViewValueStart.setText(dateTime);
        } else {
            mViewValueStart.setText("");
        }

        mViewHeaderEnd.setTextColor(entryType.getTextColor());
        mViewValueEnd.setTextColor(entryType.getTextColor());

        if (mStateEntry.getStateEnd() > 0) {
            Calendar calEnd = GregorianCalendar.getInstance();
            calEnd.setTimeInMillis(mStateEntry.getStateEnd());

            String dateTime = DateUtils.formatDateTime(this, calEnd.getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_TIME);
            mViewValueEnd.setText(dateTime);
        } else {
            mViewValueEnd.setText("");
        }

        mViewHeaderStatus.setTextColor(entryType.getTextColor());
        mViewValueStatus.setTextColor(entryType.getTextColor());

        mViewValueStatus.setText(mStateEntry.getStateStatus());

        if (mStateEntry.getStateStatus().equals(StateEntryType.STATUSES.get(0))) { //Changed
            mViewBtnAccept.setVisibility(View.VISIBLE);
            mViewBtnChange.setVisibility(View.VISIBLE);
            mViewBtnCancel.setVisibility(View.VISIBLE);
        } else if (mStateEntry.getStateStatus().equals(StateEntryType.STATUSES.get(3))) { //Canceled
            mViewBtnAccept.setVisibility(View.GONE);
            mViewBtnCancel.setVisibility(View.GONE);
            mViewBtnChange.setVisibility(View.VISIBLE);
        } else {
            mViewBtnAccept.setVisibility(View.GONE);
            mViewBtnChange.setVisibility(View.VISIBLE);
            mViewBtnCancel.setVisibility(View.VISIBLE);
        }

        mViewBtnAccept.setOnClickListener(this);
        mViewBtnAccept.setTextColor(entryType.getTextColor());

        mViewBtnChange.setOnClickListener(this);
        mViewBtnChange.setTextColor(entryType.getTextColor());

        mViewBtnCancel.setOnClickListener(this);
        mViewBtnCancel.setTextColor(entryType.getTextColor());

        ViewCompat.setTransitionName(mViewThumbnail, EXTRA_OPEN_EVENT_DETAIL);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_event_detail;
    }

    public static void launch(BaseActivity activity, View transitionView, StateEntry entry, int requestCode) {
        Intent intent = new Intent(activity, EventDetailActivity.class);
        intent.putExtra(EXTRA_STATE_ENTRY, entry);

        ActivityOptionsCompat options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                        transitionView,
                        EXTRA_OPEN_EVENT_DETAIL
                );
        ActivityCompat.startActivityForResult(activity, intent, requestCode, options.toBundle());
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAccept:
                runAccept();
                break;
            case R.id.btnChange:
                runChange();
                break;
            case R.id.btnCancel:
                runCancel();
                break;
        }
    }

    private void runAccept() {
        showProgress();
        Groundy.create(ActionRequestChangeStatusTask.class)
                .callback(EventDetailActivity.this)
                .callbackManager(mCallbacksManager)
                .arg(ActionRequestChangeStatusTask.ARG_NEW_STATUS, StateEntryType.STATUSES.get(2)) //Accepted
                .arg(ActionRequestChangeStatusTask.ARG_STATE_ENTRY, mStateEntry)
                .queueUsing(EventDetailActivity.this);
    }

    private void runChange() {
        showProgress();
        Groundy.create(ActionRequestChangeStatusTask.class)
                .callback(EventDetailActivity.this)
                .callbackManager(mCallbacksManager)
                .arg(ActionRequestChangeStatusTask.ARG_NEW_STATUS, StateEntryType.STATUSES.get(1)) //Request Changed
                .arg(ActionRequestChangeStatusTask.ARG_STATE_ENTRY, mStateEntry)
                .queueUsing(EventDetailActivity.this);
    }

    private void runCancel() {
        showProgress();
        Groundy.create(ActionRequestChangeStatusTask.class)
                .callback(EventDetailActivity.this)
                .callbackManager(mCallbacksManager)
                .arg(ActionRequestChangeStatusTask.ARG_NEW_STATUS, StateEntryType.STATUSES.get(3))//Cancelled
                .arg(ActionRequestChangeStatusTask.ARG_STATE_ENTRY, mStateEntry)
                .queueUsing(EventDetailActivity.this);
    }

    @OnSuccess(ActionRequestChangeStatusTask.class)
    public void onSuccessRequestChangeStatus(@Param(ActionRequestChangeStatusTask.ARG_STATE_ENTRY) StateEntry entry) {
        mStateEntry = entry;

        StateEntryType entryType = null;
        String itemType = mStateEntry.getStateType();
        if (itemType.equals("default")) {
            entryType = StateEntryType.getDefault();
        } else {
            if (!TextUtils.isEmpty(itemType) && StateEntryType.TYPES.containsKey(itemType)) {
                entryType = StateEntryType.TYPES.get(itemType);
            } else {
                entryType = StateEntryType.getDefault();
            }
        }
        setupViews(entryType);
        hideProgress();
        showSnackBarSuccess(getResources().getString(R.string.success_update_status));
    }

    @OnFailure(ActionRequestChangeStatusTask.class)
    public void onFailureRequestChangeStatus(@Param(Constants.Extras.PARAM_INTERNET_AVAILABLE) boolean isAvailable) {
        hideProgress();
        if (!isAvailable) {
            showSnackBarError(getResources().getString(R.string.error_internet_not_available));
        } else {
            showSnackBarError(getResources().getString(R.string.error_occurred));
        }
    }

    private void showSnackBarError(String error) {
        SnackbarManager.show(
                Snackbar.with(EventDetailActivity.this) // context
                        .type(SnackbarType.MULTI_LINE)
                        .text(error)
                        .color(getResources().getColor(R.color.colorLightError))
                        .textColor(getResources().getColor(R.color.colorLightEditTextHint))
                        .swipeToDismiss(true)
                        .duration(Snackbar.SnackbarDuration.LENGTH_LONG)
                , EventDetailActivity.this); // activity where it is displayed
    }

    private void showSnackBarSuccess(String text) {
        SnackbarManager.show(
                Snackbar.with(EventDetailActivity.this) // context
                        .type(SnackbarType.MULTI_LINE)
                        .text(text)
                        .color(getResources().getColor(R.color.colorLightSuccess))
                        .textColor(getResources().getColor(R.color.colorLightEditTextHint))
                        .swipeToDismiss(false)
                        .duration(Snackbar.SnackbarDuration.LENGTH_SHORT)
                , EventDetailActivity.this); // activity where it is displayed
    }

    private void showProgress() {
        mProgressDialog = new MaterialDialog.Builder(this)
                .title(R.string.dialog_progress_update_status)
                .titleColor(getResources().getColor(R.color.colorLightTextMain))
                .content(R.string.please_wait)
                .contentColor(getResources().getColor(R.color.colorLightTextMain))
                .backgroundColor(getResources().getColor(R.color.colorLightWindowBackground))
                .progress(true, 0)
                .autoDismiss(false)
                .cancelable(false)
                .build();

        mProgressDialog.show();
    }

    private void hideProgress() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }
}