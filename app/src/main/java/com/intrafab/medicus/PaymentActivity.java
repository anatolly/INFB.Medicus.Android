package com.intrafab.medicus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.intrafab.medicus.data.Account;
import com.intrafab.medicus.loaders.MeLoader;
import com.intrafab.medicus.utils.Logger;

/**
 * Created by Artemiy Terekhov on 10.04.2015.
 */
public class PaymentActivity extends BaseActivity {

    public static final String TAG = PaymentActivity.class.getName();
    public static final String EXTRA_OPEN_PAYMENT = "openPayment";

    private static final int LOADER_ME_ID = 16;

    private TextView mViewUserName;
    private TextView mViewUserEmail;

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
        return new MeLoader(PaymentActivity.this);
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().getThemedContext();

        getSupportActionBar().setTitle("Account");
        showActionBar();
        setActionBarIcon(R.mipmap.ic_action_back);

        ViewCompat.setTransitionName(toolbar, EXTRA_OPEN_PAYMENT);

        mViewUserName = (TextView) findViewById(R.id.tvUserName);
        mViewUserEmail = (TextView) findViewById(R.id.tvUserEmail);

        if (!showUserName())
            startLoadMe();
    }

    private boolean showUserName() {
        Account userAccount = AppApplication.getApplication(this).getUserAccount();
        if (userAccount != null) {
            String userName = userAccount.getName();

            if (!TextUtils.isEmpty(userName))
                mViewUserName.setText(userName);
            else
                mViewUserName.setText(getString(R.string.unknown_name));

            String userEmail = userAccount.getMail();
            if (!TextUtils.isEmpty(userEmail))
                mViewUserEmail.setText(userEmail);
            else
                mViewUserEmail.setText("");
            return true;
        } else {
            mViewUserName.setText(getString(R.string.unknown_name));
            mViewUserEmail.setText("");
            return false;
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_payment;
    }

    public static void launch(BaseActivity activity, View transitionView) {
        Intent intent = new Intent(activity, PaymentActivity.class);

        ActivityOptionsCompat options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                        transitionView,
                        EXTRA_OPEN_PAYMENT
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

    private void startLoadMe() {
        toolbar.post(new Runnable() {
            @Override
            public void run() {
                getLoaderManager().initLoader(LOADER_ME_ID, null, mLoaderCallback);
            }
        });
    }
}
