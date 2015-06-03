package com.intrafab.medicus.actions;

import android.os.Bundle;

import com.intrafab.medicus.http.HttpRestService;
import com.intrafab.medicus.http.RestApiConfig;
import com.intrafab.medicus.utils.Connectivity;
import com.telly.groundy.GroundyTask;
import com.telly.groundy.TaskResult;

/**
 * Created by Artemiy Terekhov on 03.06.2015.
 */
public class ActionRequestSendFeedbackTask extends GroundyTask {

    public static final String INTERNET_AVAILABLE = "internet_available";

    public static final String ARG_RATING_VALUE = "arg_rating_value";
    public static final String ARG_RATING_HOSPITAL_VALUE = "arg_rating_hospital_value";
    public static final String ARG_FEEDBACK_TEXT = "arg_feedback_text";
    public static final String ARG_FEEDBACK_HOSPITAL_TEXT= "arg_feedback_hospital_text";

    @Override
    protected TaskResult doInBackground() {

        if (!Connectivity.isNetworkConnected()) {
            return failed().add(INTERNET_AVAILABLE, false);
        }

        Bundle inputBundle = getArgs();
        int valueRating = inputBundle.getInt(ARG_RATING_VALUE);
        int valueRatingHospital = inputBundle.getInt(ARG_RATING_HOSPITAL_VALUE);
        String feedback = inputBundle.getString(ARG_FEEDBACK_TEXT);
        String feedbackHospital = inputBundle.getString(ARG_FEEDBACK_HOSPITAL_TEXT);

        try {
            HttpRestService service = RestApiConfig.getRestService("");
            //StateEntry entry = service.changeStateEntry();

            // only test
            Thread.sleep(5000);
        } catch (Exception e) {
            e.printStackTrace();
            return failed().add(INTERNET_AVAILABLE, true);
        }

        return succeeded();
    }
}
