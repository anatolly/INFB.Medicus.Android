package com.intrafab.medicus;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by Artemiy Terekhov on 10.04.2015.
 */
public class MainActivity extends BaseActivity implements View.OnClickListener {

    public static final String TAG = MainActivity.class.getName();

    private LinearLayout mButtonStorage;
    private LinearLayout mButtonSos;
    private LinearLayout mButtonFeedback;
    private LinearLayout mButtonPayment;
    private LinearLayout mButtonCalendar;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().getThemedContext();

        getSupportActionBar().setTitle("");
        showTransparentActionBar();

        mButtonStorage = (LinearLayout) findViewById(R.id.llStorage);
        mButtonSos = (LinearLayout) findViewById(R.id.llSos);
        mButtonFeedback = (LinearLayout) findViewById(R.id.llFeedback);
        mButtonPayment = (LinearLayout) findViewById(R.id.llPayment);
        mButtonCalendar = (LinearLayout) findViewById(R.id.llCalendar);

        mButtonStorage.setOnClickListener(this);
        mButtonSos.setOnClickListener(this);
        mButtonFeedback.setOnClickListener(this);
        mButtonPayment.setOnClickListener(this);
        mButtonCalendar.setOnClickListener(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        StateData data = popBackStack();
        if (data == null) {
            super.onBackPressed();
            return;
        }
    }

    public Fragment findActiveFragment(String tag) {
        Fragment fragment = getFragmentManager().findFragmentByTag(tag);
        if (fragment != null)
            return fragment;

        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.llStorage:
                StorageActivity.launch(MainActivity.this, view);
                break;
            case R.id.llSos:
                SosActivity.launch(MainActivity.this, view);
                break;
            case R.id.llFeedback:
                FeedbackActivity.launch(MainActivity.this, view);
                break;
            case R.id.llPayment:
                //PaymentActivity.launch(MainActivity.this, view);
                break;
            case R.id.llCalendar:
                CalendarActivity.launch(MainActivity.this, view);
                break;
        }
    }
}
