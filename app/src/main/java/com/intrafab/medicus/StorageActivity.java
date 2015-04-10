package com.intrafab.medicus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.transition.Explode;
import android.transition.Transition;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by Artemiy Terekhov on 10.04.2015.
 */
public class StorageActivity extends BaseActivity {

    public static final String TAG = StorageActivity.class.getName();
    public static final String EXTRA_OPEN_STORAGE = "openStorage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().getThemedContext();

        getSupportActionBar().setTitle("Personal Storage");
        showActionBar();

        ViewCompat.setTransitionName(toolbar, EXTRA_OPEN_STORAGE);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_storage;
    }

    public static void launch(BaseActivity activity, View transitionView) {
        Intent storageIntent = new Intent(activity, StorageActivity.class);

        ActivityOptionsCompat options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                        transitionView,
                        EXTRA_OPEN_STORAGE
                );
        ActivityCompat.startActivity(activity, storageIntent, options.toBundle());
    }
}
