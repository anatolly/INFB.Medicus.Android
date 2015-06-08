package com.intrafab.medicus.actions;

import android.os.Bundle;

import com.intrafab.medicus.Constants;
import com.intrafab.medicus.data.Account;
import com.intrafab.medicus.db.DBManager;
import com.telly.groundy.GroundyTask;
import com.telly.groundy.TaskResult;

/**
 * Created by Artemiy Terekhov on 08.06.2015.
 */
public class ActionSaveMeTask extends GroundyTask {

    public static final String USER_DATA = "arg_user_data";
    @Override
    protected TaskResult doInBackground() {
        Bundle inputBundle = getArgs();
        Account userAccount = inputBundle.getParcelable(USER_DATA);

        if (userAccount != null) {
            DBManager.getInstance().insertObject(getContext(), Constants.Prefs.PREF_PARAM_ME, userAccount, Account.class);
        }

        return succeeded();
    }
}
