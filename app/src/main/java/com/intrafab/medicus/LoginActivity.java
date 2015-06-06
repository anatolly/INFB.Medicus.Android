package com.intrafab.medicus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Artemiy Terekhov on 06.06.2015.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    public static final String TAG = LoginActivity.class.getName();
    public static final String EXTRA_OPEN_LOGIN = "openLogin";

    private TextView mButtonLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().getThemedContext();

        getSupportActionBar().setTitle("Payment");
        hideActionBar();
        //setActionBarIcon(R.mipmap.ic_action_back);

        ViewCompat.setTransitionName(toolbar, EXTRA_OPEN_LOGIN);

        mButtonLogin = (TextView) findViewById(R.id.btnLogin);
        mButtonLogin.setOnClickListener(this);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_login;
    }

    public static void launch(BaseActivity activity, View transitionView) {
        Intent intent = new Intent(activity, PaymentActivity.class);

        ActivityOptionsCompat options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                        transitionView,
                        EXTRA_OPEN_LOGIN
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

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.btnLogin:
                AppApplication.setLogin(this, true);
                finish();
                MainActivity.launch(this);
                break;
        }
    }

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, LoginActivity.class);

        ActivityOptionsCompat options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(activity);

        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }
}