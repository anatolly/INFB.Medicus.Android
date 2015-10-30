package com.intrafab.medicus.actions;

import android.os.Bundle;

import com.intrafab.medicus.Constants;
import com.intrafab.medicus.calendar.pedometer.PedometerCalendar;
import com.intrafab.medicus.db.DBManager;
import com.telly.groundy.GroundyTask;
import com.telly.groundy.TaskResult;

/**
 * Created by Artemiy Terekhov on 28.10.2015.
 * Copyright (c) 2015 Artemiy Terekhov. All rights reserved.
 */
public class ActionSavePedometerTask extends GroundyTask {

    public static final String PEDOMETER_DATA = "arg_pedometer_data";
    @Override
    protected TaskResult doInBackground() {
        Bundle inputBundle = getArgs();
        PedometerCalendar calendar = inputBundle.getParcelable(PEDOMETER_DATA);

        if (calendar != null) {
            DBManager.getInstance().insertObject(getContext(), Constants.Prefs.PREF_PARAM_PEDOMETER_CALENDAR, calendar, PedometerCalendar.class);
        }

        return succeeded();
    }
}
