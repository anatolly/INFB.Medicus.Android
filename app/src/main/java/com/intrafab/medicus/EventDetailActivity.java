package com.intrafab.medicus;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;
import com.intrafab.medicus.data.StateEntry;
import com.intrafab.medicus.data.StateEntryType;
import com.intrafab.medicus.data.StorageInfo;

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
    private TextView mViewBtnDismiss;
    private TextView mViewBtnClaim;

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
        mViewBtnDismiss = (TextView) this.findViewById(R.id.btnDismiss);
        mViewBtnClaim = (TextView) this.findViewById(R.id.btnClaim);
    }

    private void setupViews(StateEntryType entryType) {
        mViewThumbnail.setImageResource(entryType.getResIconId());
        mViewCardEntry.setCardBackgroundColor(entryType.getBackgroundColor());

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

        int rippleColor = getResources().getColor(R.color.colorLightPrimary);
        float rippleAlpha = 0.5f;

        mViewBtnAccept.setOnClickListener(this);
        mViewBtnAccept.setTextColor(entryType.getTextColor());

        MaterialRippleLayout.on(mViewBtnAccept)
                .rippleColor(rippleColor)
                .rippleAlpha(rippleAlpha)
                .rippleHover(true)
                .create();

        mViewBtnDismiss.setOnClickListener(this);
        mViewBtnDismiss.setTextColor(entryType.getTextColor());

        MaterialRippleLayout.on(mViewBtnDismiss)
                .rippleColor(rippleColor)
                .rippleAlpha(rippleAlpha)
                .rippleHover(true)
                .create();

        mViewBtnClaim.setOnClickListener(this);
        mViewBtnClaim.setTextColor(entryType.getTextColor());

        MaterialRippleLayout.on(mViewBtnClaim)
                .rippleColor(rippleColor)
                .rippleAlpha(rippleAlpha)
                .rippleHover(true)
                .create();

        ViewCompat.setTransitionName(mViewThumbnail, EXTRA_OPEN_EVENT_DETAIL);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_event_detail;
    }

    public static void launch(BaseActivity activity, View transitionView, StateEntry entry) {
        Intent intent = new Intent(activity, EventDetailActivity.class);
        intent.putExtra(EXTRA_STATE_ENTRY, entry);

        ActivityOptionsCompat options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                        transitionView,
                        EXTRA_OPEN_EVENT_DETAIL
                );
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAccept:
                runAccept();
                break;
            case R.id.btnDismiss:
                runDismiss();
                break;
            case R.id.btnClaim:
                runClaim();
                break;
        }
    }

    private void runAccept() {
        Toast.makeText(this, "run Accept", Toast.LENGTH_SHORT).show();
    }

    private void runDismiss() {
        Toast.makeText(this, "run Dismiss", Toast.LENGTH_SHORT).show();
    }

    private void runClaim() {
        Toast.makeText(this, "run Claim", Toast.LENGTH_SHORT).show();
    }
}