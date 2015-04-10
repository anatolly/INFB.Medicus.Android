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
public class FeedbackActivity extends BaseActivity {

    public static final String TAG = FeedbackActivity.class.getName();
    public static final String EXTRA_OPEN_FEEDBACK = "openFeedback";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().getThemedContext();

        getSupportActionBar().setTitle("Feedback");
        showActionBar();

        ViewCompat.setTransitionName(toolbar, EXTRA_OPEN_FEEDBACK);
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
}