package com.intrafab.medicus.actions;

import com.telly.groundy.GroundyTask;
import com.telly.groundy.TaskResult;

/**
 * Created by Artemiy Terekhov on 02.05.2015.
 */
public class ActionRequestAcceptAllTask extends GroundyTask {

    @Override
    protected TaskResult doInBackground() {

        // only test
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return succeeded();
    }
}