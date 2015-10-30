package com.intrafab.medicus.medJournal.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.intrafab.medicus.BaseActivity;
import com.intrafab.medicus.R;
import com.intrafab.medicus.medJournal.adapters.MedJournalPagerAdapter;
import com.intrafab.medicus.utils.Logger;

import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;

/**
 * Created by Anna Anfilova on 18.08.2015.
 */
public class MedicalJournalActivity extends BaseActivity
implements View.OnClickListener {

    public static final String TAG = MedicalJournalActivity.class.getName();
    public static final String EXTRA_MEDICAL_JOURNAL = "openMedicalJournal";

    private MaterialTabHost mTabHost;
    private ViewPager mPager;
    private MedJournalPagerAdapter mAdapter;

    private final int MAX_TAB_NUMBER = 2;
    /// в этом числе всегда MAX_TAB_NUMBER знаков - 1 - таб есть, 0 - таба нет
    //private short mActiveTabs = 0b111;



    private MaterialTabListener mTabListener = new MaterialTabListener() {
        @Override
        public void onTabSelected(MaterialTab materialTab) {
            int tabPosition = materialTab.getPosition();
            mPager.setCurrentItem(tabPosition);

            switch (tabPosition) {
                case 0:
                    Logger.d(TAG, "startMenstrualCycleLoading()");
                    break;
                case 1:
                    Logger.d(TAG, "startBodyIndexLoading()");
                    break;
            }
        }

        @Override
        public void onTabReselected(MaterialTab materialTab) {

        }

        @Override
        public void onTabUnselected(MaterialTab materialTab) {

        }
    };

    @Override
    public void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Logger.d(TAG, "onCreate()");
        // setup actionbar and toolbar

        getSupportActionBar().getThemedContext();
        getSupportActionBar().setTitle(getString(R.string.medical_journal_header));
        showActionBar();
        setActionBarIcon(R.mipmap.returnblack);
        ViewCompat.setTransitionName(toolbar, EXTRA_MEDICAL_JOURNAL);
        toolbar.setTitleTextColor(Color.BLACK);


        // setup callbacks
          //mCallbacksManager = CallbacksManager.init(savedInstanceState);
          //mCallbacksManager.linkCallbacks(this);
        // setup viewPager
        mTabHost = (MaterialTabHost) this.findViewById(R.id.tabHost);
        mTabHost.setTextColor(Color.BLACK);
        mPager = (ViewPager) this.findViewById(R.id.tabPager);
        //setup Actions menu

        // init view pager
        mAdapter = new MedJournalPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mAdapter);
        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                // when user do a swipe the selected tab change
                mTabHost.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);

                if (state == ViewPager.SCROLL_STATE_SETTLING) {
                    int position = mPager.getCurrentItem();
                    mTabHost.setSelectedNavigationItem(position);
                    mPager.setCurrentItem(position);

                    switch (position) {
                        case 0:
                            Logger.d(TAG, "SCROLL VIEW PAGER ON POSITION 0");
                            //startStorageLoading();
                            break;
                        case 1:
                            Logger.d(TAG, "SCROLL VIEW PAGER ON POSITION 1");
                            //startStorageTripLoading();
                            break;
                        case 2:
                            Logger.d(TAG, "SCROLL VIEW PAGER ON POSITION 2");
                            break;
                    }
                }
            }
        });
        /// add necessary tabs
        String[] headerString  = getResources().getStringArray(R.array.tabs_headers);
        for (int i=0; i<MAX_TAB_NUMBER; i++){
            //if(isSuchTabActive(i))
                //Logger.d(TAG, "add tab!");
            mTabHost.addTab(
                    mTabHost.newTab()
                            .setText(headerString[i])
                            .setTabListener(mTabListener)
            );
        }

        /*mTabHost.post(new Runnable() {
            @Override
            public void run() {
                if (mPager.getCurrentItem() == 0) {
                    mTabHost.setSelectedNavigationItem(0);
                } else {
                    mTabHost.setSelectedNavigationItem(1);
                }
            }
        });*/
    }


    public static void launch(BaseActivity activity, View transitionView) {
        Intent medicalJournalIntent = new Intent(activity, MedicalJournalActivity.class);
        if (transitionView == null){
            ActivityCompat.startActivity(activity, medicalJournalIntent, null);
            return;
        }
        ActivityOptionsCompat options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                        transitionView,
                        EXTRA_MEDICAL_JOURNAL
                );
        ActivityCompat.startActivity(activity, medicalJournalIntent, options.toBundle());
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_medical_journal;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){

        }
    }

//    private int getTabNumber (int position) {
//
//        for (int i=MAX_TAB_NUMBER; i>0; i--){
//            if ((mActiveTabs & (short)Math.pow(2,i)) == 1)
//                position--;
//            if (position == -1)
//                return MAX_TAB_NUMBER - i;
//        }
//        return 0;
//    }
//
//    private boolean isSuchTabActive(int tabNumber){
//        short aShort =  (short)Math.pow(2,MAX_TAB_NUMBER-tabNumber-1);
//        return ((mActiveTabs & aShort) == aShort);
//    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
