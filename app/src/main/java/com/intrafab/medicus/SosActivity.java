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
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.intrafab.medicus.actions.ActionRequestSOSInfoTask;
import com.intrafab.medicus.data.Account;
import com.intrafab.medicus.data.Order;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;
import com.telly.groundy.CallbacksManager;
import com.telly.groundy.Groundy;
import com.telly.groundy.annotations.OnFailure;
import com.telly.groundy.annotations.OnSuccess;
import com.telly.groundy.annotations.Param;

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

    private CallbacksManager mCallbacksManager;

    private MaterialDialog mLoginProgressDialog;

    private Order mActiveOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().getThemedContext();

        getSupportActionBar().setTitle("SOS");
        showActionBar();
        setActionBarIcon(R.mipmap.ic_action_back);

        ViewCompat.setTransitionName(toolbar, EXTRA_OPEN_SOS);

        mCallbacksManager = CallbacksManager.init(savedInstanceState);
        mCallbacksManager.linkCallbacks(this);

        mIconPhone = (ImageView) findViewById(R.id.ivPhone);
        mIconHeadset = (ImageView) findViewById(R.id.ivHeadset);

        mLayoutPhone = (LinearLayout) findViewById(R.id.layoutPhone);
        mLayoutHeadset = (LinearLayout) findViewById(R.id.layoutHeadset);

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
                break;
        }
    }

    private void showHeadsetDialog() {

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
