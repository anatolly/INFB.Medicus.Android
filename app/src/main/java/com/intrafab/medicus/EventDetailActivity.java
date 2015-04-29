package com.intrafab.medicus;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.view.View;

import com.intrafab.medicus.data.StateEntry;
import com.intrafab.medicus.data.StateEntryType;
import com.intrafab.medicus.data.StorageInfo;

/**
 * Created by Artemiy Terekhov on 29.04.2015.
 */
public class EventDetailActivity extends BaseActivity {

    public static final String TAG = EventDetailActivity.class.getName();
    public static final String EXTRA_OPEN_EVENT_DETAIL = "openEventDetail";
    public static final String EXTRA_STATE_ENTRY = "state_entry";

    private StateEntry mStateEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().getThemedContext();

        Intent intent = getIntent();
        if (intent != null) {
            mStateEntry = getIntent().getParcelableExtra(EXTRA_STATE_ENTRY);
        }

        getSupportActionBar().setTitle(mStateEntry.getStateDescription());
        //showActionBar();

        StateEntryType entryType = null;
        String itemType = mStateEntry.getStateType();
        if (itemType.equals("default")) {
            showActionBar();
        } else {
            if (!TextUtils.isEmpty(itemType) && StateEntryType.TYPES.containsKey(itemType)) {
                entryType = StateEntryType.TYPES.get(itemType);
                if (toolbar != null) {
                    toolbar.setVisibility(View.VISIBLE);
                    bar.setBackgroundDrawable(new ColorDrawable(entryType.getBackgroundColor()));
                }
            } else {
                entryType = StateEntryType.getDefault();
                showActionBar();
            }
        }

        ViewCompat.setTransitionName(toolbar, EXTRA_OPEN_EVENT_DETAIL);
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
}