package com.intrafab.medicus;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.intrafab.medicus.data.Account;
import com.intrafab.medicus.loaders.MeLoader;
import com.intrafab.medicus.utils.Logger;

/**
 * Created by Artemiy Terekhov on 10.04.2015.
 */
public class MainActivity extends BaseActivity implements View.OnClickListener {

    public static final String TAG = MainActivity.class.getName();

    private static final int LOADER_ME_ID = 15;

    private LinearLayout mButtonStorage;
    private LinearLayout mButtonSos;
    private LinearLayout mButtonFeedback;
    private LinearLayout mButtonPayment;
    private LinearLayout mButtonCalendar;

    private TextView mViewUserName;

    private android.app.LoaderManager.LoaderCallbacks<Account> mLoaderCallback = new android.app.LoaderManager.LoaderCallbacks<Account>() {
        @Override
        public android.content.Loader<Account> onCreateLoader(int id, Bundle args) {
            switch (id) {
                case LOADER_ME_ID:
                    return createAccountLoader();
                default:
                    return null;
            }
        }

        @Override
        public void onLoadFinished(android.content.Loader<Account> loader, Account data) {
            int id = loader.getId();
            switch (id) {
                case LOADER_ME_ID:
                    finishedAccountLoader(data);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onLoaderReset(android.content.Loader<Account> loader) {
            int id = loader.getId();
            switch (id) {
                case LOADER_ME_ID:
                    resetAccountLoader();
                    break;
                default:
                    break;
            }
        }
    };

    private android.content.Loader<Account> createAccountLoader() {
        Logger.d(TAG, "createAccountLoader");
        return new MeLoader(MainActivity.this);
    }

    private void finishedAccountLoader(Account data) {
        Logger.d(TAG, "finishedAccountLoader data: " + (data == null ? "NULL" : data.getName()));
        AppApplication.getApplication(this).setUserAccount(data);
        showUserName();
    }

    private void resetAccountLoader() {
        Logger.d(TAG, "resetAccountLoader");
    }

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

        mViewUserName = (TextView) findViewById(R.id.tvUserName);

        if (!showUserName())
            startLoadMe();
    }

    private boolean showUserName() {
        Account userAccount = AppApplication.getApplication(this).getUserAccount();
        if (userAccount != null) {
            mViewUserName.setVisibility(View.VISIBLE);
            mViewUserName.setText(userAccount.getName());
            return true;
        } else {
            mViewUserName.setVisibility(View.INVISIBLE);
            return false;
        }
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
                PaymentActivity.launch(MainActivity.this, view);
                break;
            case R.id.llCalendar:
                CalendarActivity.launch(MainActivity.this, view);
                break;
        }
    }

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);

        ActivityOptionsCompat options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(activity);

        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }

    private void startLoadMe() {
        toolbar.post(new Runnable() {
            @Override
            public void run() {
                getLoaderManager().initLoader(LOADER_ME_ID, null, mLoaderCallback);
            }
        });
    }
}
