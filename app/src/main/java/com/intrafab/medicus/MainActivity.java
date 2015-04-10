package com.intrafab.medicus;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

/**
 * Created by Artemiy Terekhov on 10.04.2015.
 */
public class MainActivity extends BaseActivity {

    public static final String TAG = MainActivity.class.getName();

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

}
