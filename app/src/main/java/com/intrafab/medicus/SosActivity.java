package com.intrafab.medicus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Artemiy Terekhov on 10.04.2015.
 */
public class SosActivity extends BaseActivity {

    public static final String TAG = SosActivity.class.getName();
    public static final String EXTRA_OPEN_SOS = "openSos";

    private ImageView mIconPhone;
    private ImageView mIconHeadset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().getThemedContext();

        getSupportActionBar().setTitle("SOS");
        showActionBar();
        setActionBarIcon(R.mipmap.ic_action_back);

        ViewCompat.setTransitionName(toolbar, EXTRA_OPEN_SOS);

        mIconPhone = (ImageView) findViewById(R.id.ivPhone);
        mIconHeadset = (ImageView) findViewById(R.id.ivHeadset);

//        mIconPhone.setColorFilter(getResources().getColor(R.color.colorLightWindowBackground));
        mIconHeadset.setColorFilter(getResources().getColor(R.color.colorLightWindowBackground));
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
