package com.intrafab.medicus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.view.View;

/**
 * Created by Artemiy Terekhov on 10.04.2015.
 */
public class CalendarActivity extends BaseActivity {

    public static final String TAG = CalendarActivity.class.getName();
    public static final String EXTRA_OPEN_CALENDAR = "openCalendar";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().getThemedContext();

        getSupportActionBar().setTitle("Calendar");
        showActionBar();

        ViewCompat.setTransitionName(toolbar, EXTRA_OPEN_CALENDAR);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_calendar;
    }

    public static void launch(BaseActivity activity, View transitionView) {
        Intent storageIntent = new Intent(activity, CalendarActivity.class);

        ActivityOptionsCompat options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                        transitionView,
                        EXTRA_OPEN_CALENDAR
                );
        ActivityCompat.startActivity(activity, storageIntent, options.toBundle());
    }
}
