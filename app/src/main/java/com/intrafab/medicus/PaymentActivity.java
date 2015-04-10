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
public class PaymentActivity extends BaseActivity {

    public static final String TAG = PaymentActivity.class.getName();
    public static final String EXTRA_OPEN_PAYMENT = "openPayment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().getThemedContext();

        getSupportActionBar().setTitle("Payment");
        showActionBar();

        ViewCompat.setTransitionName(toolbar, EXTRA_OPEN_PAYMENT);
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
}
