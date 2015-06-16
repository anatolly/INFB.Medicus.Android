package com.intrafab.medicus;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.intrafab.medicus.actions.ActionRequestSOSCallbackTask;
import com.intrafab.medicus.actions.ActionRequestSOSInfoTask;
import com.intrafab.medicus.data.Account;
import com.intrafab.medicus.data.Order;
import com.intrafab.medicus.utils.Logger;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.telly.groundy.CallbacksManager;
import com.telly.groundy.Groundy;
import com.telly.groundy.annotations.OnFailure;
import com.telly.groundy.annotations.OnSuccess;
import com.telly.groundy.annotations.Param;

import java.util.ArrayList;
import java.util.List;

import fr.ganfra.materialspinner.MaterialSpinner;

/**
 * Created by Artemiy Terekhov on 10.04.2015.
 */
public class SosActivity extends BaseActivity implements View.OnClickListener {

    public static final String TAG = SosActivity.class.getName();
    public static final String EXTRA_OPEN_SOS = "openSos";

    private ImageView mIconPhone;
    private ImageView mIconHeadset;
    private LinearLayout mLayoutPhone;
    private LinearLayout mLayoutHeadset;
    private TextView mTextRequestCall;

    private CallbacksManager mCallbacksManager;

    private MaterialDialog mLoginProgressDialog;

    private Order mActiveOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().getThemedContext();

        getSupportActionBar().setTitle(R.string.menu_sos);
        showActionBar();
        setActionBarIcon(R.mipmap.ic_action_back);

        ViewCompat.setTransitionName(toolbar, EXTRA_OPEN_SOS);

        mCallbacksManager = CallbacksManager.init(savedInstanceState);
        mCallbacksManager.linkCallbacks(this);

        mIconPhone = (ImageView) findViewById(R.id.ivPhone);
        mIconHeadset = (ImageView) findViewById(R.id.ivHeadset);

        mLayoutPhone = (LinearLayout) findViewById(R.id.layoutPhone);
        mLayoutHeadset = (LinearLayout) findViewById(R.id.layoutHeadset);

        mTextRequestCall = (TextView) findViewById(R.id.tvRequestCall);

        mLayoutPhone.setOnClickListener(this);
        mLayoutHeadset.setOnClickListener(this);

//        mIconPhone.setColorFilter(getResources().getColor(R.color.colorLightWindowBackground));
        mIconHeadset.setColorFilter(getResources().getColor(R.color.colorLightWindowBackground));

        toolbar.post(new Runnable() {
            @Override
            public void run() {
                startRequest();
            }
        });
    }

    private void startRequest() {
        mLayoutPhone.setEnabled(true);
        mLayoutHeadset.setEnabled(true);
        showProgress();

        Account userAccount = AppApplication.getApplication(this).getUserAccount();

        Groundy.create(ActionRequestSOSInfoTask.class)
                .callback(SosActivity.this)
                .callbackManager(mCallbacksManager)
                .arg(ActionRequestSOSInfoTask.ARG_USER_OWNER_ID, userAccount != null ? userAccount.getUid() : "")
                .queueUsing(SosActivity.this);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_sos;
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
        Intent intent = new Intent(activity, SosActivity.class);

        ActivityOptionsCompat options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                        transitionView,
                        EXTRA_OPEN_SOS
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

    @OnSuccess(ActionRequestSOSInfoTask.class)
    public void onSuccessRequestSOSInfo(
            @Param(Constants.Extras.PARAM_ACTIVE_ORDER) Order activeOrder) {
        mLayoutPhone.setEnabled(true);
        mLayoutHeadset.setEnabled(true);
        hideProgress();

        mActiveOrder = activeOrder;

        updateUi();

//        requestCallback("110", "+79200491000"); //test clean request
    }

    @OnSuccess(ActionRequestSOSCallbackTask.class)
    public void onSuccessRequestSOSCallback(
            @Param(Constants.Extras.PARAM_ACTIVE_ORDER) Order activeOrder) {
        hideProgress();
        mActiveOrder = activeOrder;

        updateUi();
    }

    @OnFailure(ActionRequestSOSCallbackTask.class)
    public void onFailureRequestSOSCallback(
            @Param(Constants.Extras.PARAM_INTERNET_AVAILABLE) boolean isAvailable) {
        hideProgress();

        if (!isAvailable) {
            showSnackBarError(getResources().getString(R.string.error_internet_not_available));
        } else {
            showSnackBarError(getResources().getString(R.string.error_occurred));
        }
    }

    private void updateUi() {
        if (mActiveOrder != null) {
            if (mActiveOrder.isCallbackIsRequested()) {
                mLayoutHeadset.setEnabled(false);
                mTextRequestCall.setText(getString(R.string.sos_callback_requested));
                mLayoutHeadset.setBackgroundResource(R.drawable.circle_request_disable_background);
            } else {
                mLayoutHeadset.setEnabled(true);
                mTextRequestCall.setText(getString(R.string.sos_request_callback));
                mLayoutHeadset.setBackgroundResource(R.drawable.circle_request_background);
            }
        }
    }

    @OnFailure(ActionRequestSOSInfoTask.class)
    public void onFailureRequestSOSInfo(
            @Param(Constants.Extras.PARAM_INTERNET_AVAILABLE) boolean isAvailable) {
        mLayoutPhone.setEnabled(true);
        mLayoutHeadset.setEnabled(true);
        hideProgress();

        if (!isAvailable) {
            showSnackBarError(getResources().getString(R.string.error_internet_not_available));
        } else {
            showSnackBarError(getResources().getString(R.string.error_occurred));
        }
    }

    private void showProgress() {
        mLoginProgressDialog = new MaterialDialog.Builder(this)
                .title(R.string.dialog_progress_sos)
                .titleColor(getResources().getColor(R.color.colorLightTextMain))
                .content(R.string.please_wait)
                .contentColor(getResources().getColor(R.color.colorLightTextMain))
                .backgroundColor(getResources().getColor(R.color.colorLightWindowBackground))
                .progress(true, 0)
                .autoDismiss(false)
                .cancelable(false)
                .build();

        mLoginProgressDialog.show();
    }

    private void hideProgress() {
        if (mLoginProgressDialog != null) {
            mLoginProgressDialog.dismiss();
            mLoginProgressDialog = null;
        }
    }

    private void showSnackBarError(String error) {
        SnackbarManager.show(
                Snackbar.with(SosActivity.this) // context
                        .type(SnackbarType.MULTI_LINE)
                        .text(error)
                        .color(getResources().getColor(R.color.colorLightError))
                        .textColor(getResources().getColor(R.color.colorLightEditTextHint))
                        .swipeToDismiss(true)
                        .duration(Snackbar.SnackbarDuration.LENGTH_LONG)
                , SosActivity.this); // activity where it is displayed
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.layoutPhone:
                if (mActiveOrder != null) {
                    String phone = mActiveOrder.getHotLinePhone();
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + (!TextUtils.isEmpty(phone) ? phone : "")));
                    startActivity(intent);
//                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
//                    startActivity(intent);
                } else {
                    showSnackBarError(getResources().getString(R.string.error_occurred));
                }
                break;
            case R.id.layoutHeadset:
                showHeadsetDialog();
                break;
        }
    }

    private MaterialEditText metPhoneNumber;
    private MaterialSpinner mMaterialSpinner;
    private TextView mTextHelp;
    private String mSelectedCallbackPhone;

    private void showHeadsetDialog() {
        if (mActiveOrder != null) {
            String callbackPhone = mActiveOrder.getCallbackPhoneNumber();
            String myPhone = getPhoneNumber();

            List<String> phones = new ArrayList<>();
            if (!TextUtils.isEmpty(myPhone)) {
                phones.add(myPhone);
            }

            if (!TextUtils.isEmpty(callbackPhone)) {
                phones.add(callbackPhone);
            }

            MaterialDialog dialog = new MaterialDialog.Builder(this)
                    .titleColor(getResources().getColor(R.color.colorLightTextMain))
                    .title(getResources().getString(R.string.sos_request_callback))
                    .customView(R.layout.dialog_sos_request_callback, true)
                    .backgroundColor(getResources().getColor(R.color.colorLightWindowBackground))
                    .positiveText(getResources().getString(R.string.dialog_sos_button_request))
                    .negativeText(getResources().getString(R.string.dialog_sos_button_cancel))
                    .autoDismiss(false)
                    .cancelable(false)
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            if (TextUtils.isEmpty(mSelectedCallbackPhone)) {
                                if (metPhoneNumber != null) {
                                    String phone = metPhoneNumber.getText().toString();
                                    if (TextUtils.isEmpty(phone)) {
                                        metPhoneNumber.setError(getResources().getString(R.string.error_empty_phone));
                                        return;
                                    } else {
                                        metPhoneNumber.setError(null);
                                    }
                                    requestCallback(mActiveOrder.getId(), phone);
                                    dialog.dismiss();
                                }
                            } else {
                                requestCallback(mActiveOrder.getId(), mSelectedCallbackPhone);
                                dialog.dismiss();
                            }
                        }

                        @Override
                        public void onNegative(MaterialDialog dialog) {
                            dialog.dismiss();
                        }
                    })
                    .build();

            metPhoneNumber = (MaterialEditText) dialog.getCustomView().findViewById(R.id.metPhone);
            mMaterialSpinner = (MaterialSpinner) dialog.getCustomView().findViewById(R.id.phoneSpinner);
            mTextHelp = (TextView) dialog.getCustomView().findViewById(R.id.tvHelp);

            String[] ITEMS = new String[phones.size()];
            phones.toArray(ITEMS);
            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ITEMS);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mMaterialSpinner.setAdapter(adapter);
            mMaterialSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Logger.e(TAG, "mMaterialSpinner onItemSelected position: " + position);
                    Logger.e(TAG, "mMaterialSpinner onItemSelected count: " + adapter.getCount());
                    if (position >= 0 && position < adapter.getCount()) {
                        mSelectedCallbackPhone = (String) adapter.getItem(position);
                        Logger.e(TAG, "mMaterialSpinner onItemSelected mSelectedCallbackPhone: " + mSelectedCallbackPhone);
                        mTextHelp.setVisibility(View.GONE);
                        metPhoneNumber.setVisibility(View.GONE);
                    } else {
                        mSelectedCallbackPhone = null;
                        mTextHelp.setVisibility(View.VISIBLE);
                        metPhoneNumber.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    Logger.e(TAG, "mMaterialSpinner onNothingSelected");
                    mTextHelp.setVisibility(View.VISIBLE);
                    metPhoneNumber.setVisibility(View.VISIBLE);
                }
            });

            dialog.show();

        } else {
            showSnackBarError(getResources().getString(R.string.error_occurred));
        }
    }

    private void requestCallback(String id, String phone) {
        showProgress();
        Groundy.create(ActionRequestSOSCallbackTask.class)
                .callback(SosActivity.this)
                .callbackManager(mCallbacksManager)
                .arg(ActionRequestSOSCallbackTask.ARG_PHONE_NUMBER, phone)
                .arg(ActionRequestSOSCallbackTask.ARG_OWNER_ID, id)
                .queueUsing(SosActivity.this);
    }

    private String getPhoneNumber() {
        String phoneNumber = null;

        try {
            TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            phoneNumber = tMgr.getLine1Number();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return phoneNumber;
    }
}
