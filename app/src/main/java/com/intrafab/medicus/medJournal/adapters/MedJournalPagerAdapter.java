package com.intrafab.medicus.medJournal.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.intrafab.medicus.medJournal.fragments.PlaceHolderMenstrualCycleFragment;
import com.intrafab.medicus.medJournal.fragments.TestFragment;
import com.intrafab.medicus.utils.Logger;

/**
 * Created by Anna Anfilova on 18.08.2015.
 */
public class MedJournalPagerAdapter extends FragmentStatePagerAdapter {

    public static final String TAG = MedJournalPagerAdapter.class.getName();

    private SparseArray<Fragment> mRegisteredFragments = new SparseArray<Fragment>();

    public MedJournalPagerAdapter(FragmentManager fm) {
        super(fm);
        Logger.d(TAG, "constructor of MedJournalPagerAdapter");
    }

    private static final int MENSTRUAL_CYCLE_TAB = 0;
    private static final int BODY_INDEX_TAB = 1;

    public Fragment getItem(int position) {
        Logger.d(TAG, "getItem position: " + String.valueOf(position));
        switch (position){
            case MENSTRUAL_CYCLE_TAB:
                return new PlaceHolderMenstrualCycleFragment();
            case BODY_INDEX_TAB:
                return new TestFragment();
            default:
                return new TestFragment();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Logger.d(TAG, "instantiateItem");
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        mRegisteredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        Logger.d(TAG, "destroyItem");
        mRegisteredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    private Fragment getCurrentFragment(int fragmentPosition) {

        return mRegisteredFragments.get(fragmentPosition);
    }

}
