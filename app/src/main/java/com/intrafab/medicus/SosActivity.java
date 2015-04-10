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
public class SosActivity extends BaseActivity {

    public static final String TAG = SosActivity.class.getName();
    public static final String EXTRA_OPEN_SOS = "openSos";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().getThemedContext();

        getSupportActionBar().setTitle("SOS");
        showActionBar();

        ViewCompat.setTransitionName(toolbar, EXTRA_OPEN_SOS);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_sos;
    }

    public static void launch(BaseActivity activity, View transitionView) {
        Intent intent = new Intent(activity, SosActivity.class);

        ActivityOptionsCompat options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                        transitionView,
                        EXTRA_OPEN_SOS
                );
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }
}
