package com.intrafab.medicus;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.intrafab.medicus.actions.ActionRequestLoginTask;
import com.intrafab.medicus.actions.ActionSaveMeTask;
import com.intrafab.medicus.data.Account;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.validation.RegexpValidator;
import com.telly.groundy.CallbacksManager;
import com.telly.groundy.Groundy;
import com.telly.groundy.annotations.OnFailure;
import com.telly.groundy.annotations.OnSuccess;
import com.telly.groundy.annotations.Param;

/**
 * Created by Artemiy Terekhov on 06.06.2015.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    public static final String TAG = LoginActivity.class.getName();
    public static final String EXTRA_OPEN_LOGIN = "openLogin";

    private TextView mButtonLogin;
    private MaterialEditText mEditUserName;
    private MaterialEditText mEditUserPassword;

    private CallbacksManager mCallbacksManager;

    private MaterialDialog mLoginProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().getThemedContext();

        getSupportActionBar().setTitle("Payment");
        hideActionBar();
        //setActionBarIcon(R.mipmap.ic_action_back);

        ViewCompat.setTransitionName(toolbar, EXTRA_OPEN_LOGIN);

        mCallbacksManager = CallbacksManager.init(savedInstanceState);
        mCallbacksManager.linkCallbacks(this);

        mButtonLogin = (TextView) findViewById(R.id.btnLogin);
        mButtonLogin.setOnClickListener(this);

        mEditUserName = (MaterialEditText) findViewById(R.id.etEmail);
        mEditUserPassword = (MaterialEditText) findViewById(R.id.etPassword);

        //mEditUserName.addValidator(new RegexpValidator(getString(R.string.error_email_format), Constants.EMAIL_PATTERN));
        mEditUserPassword.addValidator(new RegexpValidator(getString(R.string.error_password_format), Constants.PASSWORD_PATTERN));
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
    protected int getLayoutResource() {
        return R.layout.activity_login;
    }

    public static void launch(BaseActivity activity, View transitionView) {
        Intent intent = new Intent(activity, LoginActivity.class);

        ActivityOptionsCompat options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                        transitionView,
                        EXTRA_OPEN_LOGIN
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
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.btnLogin:
                String userName = mEditUserName.getText().toString();

                if (TextUtils.isEmpty(userName)) {
                    mEditUserName.setError(getString(R.string.error_empty_login));
                    return;
                }

                if (!TextUtils.isEmpty(userName) && userName.contains("@")) {
                    if (!mEditUserName.validate()) {
                        mEditUserName.setError(getString(R.string.error_email_format));
                        return;
                    } else {
                        mEditUserName.setError(null);
                    }
                }

                if (!mEditUserPassword.validate()) {
                    mEditUserPassword.setError(getString(R.string.error_password_format));
                    return;
                } else {
                    mEditUserPassword.setError(null);
                }

                String userPassword = mEditUserPassword.getText().toString();
                startLogin(userName, userPassword);
                break;
        }
    }

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, LoginActivity.class);

        ActivityOptionsCompat options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(activity);

        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }

    private void startLogin(String userName, String userPassword) {
        mButtonLogin.setEnabled(false);
        hideKeyboard();
        showProgress();
        Groundy.create(ActionRequestLoginTask.class)
                .callback(LoginActivity.this)
                .callbackManager(mCallbacksManager)
                .arg(ActionRequestLoginTask.ARG_USER_NAME, userName)
                .arg(ActionRequestLoginTask.ARG_USER_PASSWORD, userPassword)
                .queueUsing(LoginActivity.this);
    }

    @OnSuccess(ActionRequestLoginTask.class)
    public void onSuccessRequestLogin(
            @Param(Constants.Extras.PARAM_TOKEN) String token,
            @Param(Constants.Extras.PARAM_USER_DATA) Account userAccount) {
        mButtonLogin.setEnabled(true);
        hideProgress();
        AppApplication.setLogin(this, token);
        AppApplication.getApplication(this).setUserAccount(this, mCallbacksManager, userAccount);
    }

    @OnFailure(ActionRequestLoginTask.class)
    public void onFailureRequestLogin(
            @Param(Constants.Extras.PARAM_INTERNET_AVAILABLE) boolean isAvailable) {
        mButtonLogin.setEnabled(true);
        hideProgress();

        if (!isAvailable) {
            showSnackBarError(getResources().getString(R.string.error_internet_not_available));
        } else {
            showSnackBarError(getResources().getString(R.string.error_login));
        }
    }

    @OnSuccess(ActionSaveMeTask.class)
    public void onSuccessSaveMe() {
        finish();
        MainActivity.launch(this);
    }

    @OnFailure(ActionSaveMeTask.class)
    public void onFailureSaveMe() {
        showSnackBarError(getResources().getString(R.string.error_occurred));
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEditUserPassword.getWindowToken(), 0);
    }

    private void showProgress() {
        mLoginProgressDialog = new MaterialDialog.Builder(this)
                .title(R.string.dialog_progress_login)
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
                Snackbar.with(LoginActivity.this) // context
                        .type(SnackbarType.MULTI_LINE)
                        .text(error)
                        .color(getResources().getColor(R.color.colorLightError))
                        .textColor(getResources().getColor(R.color.colorLightEditTextHint))
                        .swipeToDismiss(true)
                        .duration(Snackbar.SnackbarDuration.LENGTH_LONG)
                , LoginActivity.this); // activity where it is displayed
    }
}