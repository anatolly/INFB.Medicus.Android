package com.intrafab.medicus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.intrafab.medicus.actions.ActionRequestSendFeedbackTask;
import com.intrafab.medicus.widgets.FloatingActionButton;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.telly.groundy.CallbacksManager;
import com.telly.groundy.Groundy;
import com.telly.groundy.annotations.OnFailure;
import com.telly.groundy.annotations.OnSuccess;
import com.telly.groundy.annotations.Param;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

/**
 * Created by Artemiy Terekhov on 10.04.2015.
 */
public class FeedbackActivity extends BaseActivity implements View.OnClickListener {

    public static final String TAG = FeedbackActivity.class.getName();
    public static final String EXTRA_OPEN_FEEDBACK = "openFeedback";

    private ImageView mIconSad;
    private ImageView mIconSpeechless;
    private ImageView mIconHappy;

    private ImageView mIconSadHospital;
    private ImageView mIconSpeechlessHospital;
    private ImageView mIconHappyHospital;

    private ScrollView mScrollView;
    private FloatingActionButton mButtonSend;

    private ImageView mIconSuccess;
    private LinearLayout mLayoutSuccess;

    private DiscreteSeekBar mViewRating;
    private DiscreteSeekBar mViewRatingHospital;

    private MaterialEditText mEditFeedback;
    private MaterialEditText mEditFeedbackHospital;

    private ProgressWheel mProgressView;

    private CallbacksManager mCallbacksManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().getThemedContext();

        getSupportActionBar().setTitle("Feedback");
        showActionBar();
        setActionBarIcon(R.mipmap.ic_action_back);

        ViewCompat.setTransitionName(toolbar, EXTRA_OPEN_FEEDBACK);

        mCallbacksManager = CallbacksManager.init(savedInstanceState);
        mCallbacksManager.linkCallbacks(this);

        mIconSad = (ImageView) findViewById(R.id.ivSad);
        mIconHappy = (ImageView) findViewById(R.id.ivHappy);
        mIconSpeechless = (ImageView) findViewById(R.id.ivSpeechless);

        mIconSadHospital = (ImageView) findViewById(R.id.ivSad2);
        mIconSpeechlessHospital = (ImageView) findViewById(R.id.ivSpeechless2);
        mIconHappyHospital = (ImageView) findViewById(R.id.ivHappy2);

        mIconSuccess = (ImageView) findViewById(R.id.ivSuccess);

        mViewRating = (DiscreteSeekBar) findViewById(R.id.sbRating);
        mViewRatingHospital = (DiscreteSeekBar) findViewById(R.id.sbRatingHospital);

        mEditFeedback = (MaterialEditText) findViewById(R.id.etMedService);
        mEditFeedbackHospital = (MaterialEditText) findViewById(R.id.etHospital);

        mScrollView = (ScrollView) findViewById(R.id.scroll);
        mButtonSend = (FloatingActionButton) findViewById(R.id.btnSend);

        mLayoutSuccess = (LinearLayout) findViewById(R.id.layoutSuccess);

        mProgressView = (ProgressWheel) findViewById(R.id.progress_wheel);

        mScrollView.setVisibility(View.VISIBLE);
        mButtonSend.setVisibility(View.VISIBLE);
        mLayoutSuccess.setVisibility(View.GONE);

        mIconSad.setColorFilter(getResources().getColor(R.color.colorLightError));
        mIconSpeechless.setColorFilter(getResources().getColor(R.color.colorLightYellow));
        mIconHappy.setColorFilter(getResources().getColor(R.color.colorLightSuccess));

        mIconSadHospital.setColorFilter(getResources().getColor(R.color.colorLightError));
        mIconSpeechlessHospital.setColorFilter(getResources().getColor(R.color.colorLightYellow));
        mIconHappyHospital.setColorFilter(getResources().getColor(R.color.colorLightSuccess));

        mIconSuccess.setColorFilter(getResources().getColor(R.color.colorLightSuccess));

        mButtonSend.setOnClickListener(this);
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
        return R.layout.activity_feedback;
    }

    public static void launch(BaseActivity activity, View transitionView) {
        Intent intent = new Intent(activity, FeedbackActivity.class);

        ActivityOptionsCompat options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                        transitionView,
                        EXTRA_OPEN_FEEDBACK
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
        switch (v.getId()) {
            case R.id.btnSend:
                sendFeedback();
                break;
        }
    }

    private void sendFeedback() {
        int valueRating = mViewRating.getProgress();
        int valueRatingHospital = mViewRatingHospital.getProgress();

        String feedback = mEditFeedback.getText().toString();
        String feedbackHospital = mEditFeedbackHospital.getText().toString();

        Groundy.create(ActionRequestSendFeedbackTask.class)
                .callback(FeedbackActivity.this)
                .callbackManager(mCallbacksManager)
                .arg(ActionRequestSendFeedbackTask.ARG_RATING_VALUE, valueRating)
                .arg(ActionRequestSendFeedbackTask.ARG_RATING_HOSPITAL_VALUE, valueRatingHospital)
                .arg(ActionRequestSendFeedbackTask.ARG_FEEDBACK_TEXT, TextUtils.isEmpty(feedback) ? "" : feedback)
                .arg(ActionRequestSendFeedbackTask.ARG_FEEDBACK_HOSPITAL_TEXT, TextUtils.isEmpty(feedbackHospital) ? "" : feedbackHospital)
                .queueUsing(FeedbackActivity.this);

        showProgress();
    }

    private void showProgress() {
        mScrollView.setVisibility(View.GONE);
        mButtonSend.setVisibility(View.GONE);
        mLayoutSuccess.setVisibility(View.GONE);
        mProgressView.setVisibility(View.VISIBLE);
    }

    private void showRequestSuccess() {
        mScrollView.setVisibility(View.GONE);
        mButtonSend.setVisibility(View.GONE);
        mLayoutSuccess.setVisibility(View.VISIBLE);
        mProgressView.setVisibility(View.GONE);
    }

    private void showRequestFailure() {
        mScrollView.setVisibility(View.VISIBLE);
        mButtonSend.setVisibility(View.VISIBLE);
        mLayoutSuccess.setVisibility(View.GONE);
        mProgressView.setVisibility(View.GONE);
    }

    @OnSuccess(ActionRequestSendFeedbackTask.class)
    public void onSuccessRequestSendFeedback() {
        showRequestSuccess();
    }

    @OnFailure(ActionRequestSendFeedbackTask.class)
    public void onFailureRequestSendFeedback(@Param(ActionRequestSendFeedbackTask.INTERNET_AVAILABLE) boolean isAvailable) {
        showRequestFailure();

        if (!isAvailable) {
            showSnackBarError(getResources().getString(R.string.error_internet_not_available));
        } else {
            showSnackBarError(getResources().getString(R.string.error_occurred));
        }
    }

    private void showSnackBarError(String error) {
        SnackbarManager.show(
                Snackbar.with(FeedbackActivity.this) // context
                        .type(SnackbarType.MULTI_LINE)
                        .text(error)
                        .color(getResources().getColor(R.color.colorLightError))
                        .textColor(getResources().getColor(R.color.colorLightEditTextHint))
                        .swipeToDismiss(true)
                        .duration(Snackbar.SnackbarDuration.LENGTH_LONG)
                , FeedbackActivity.this); // activity where it is displayed
    }
}
