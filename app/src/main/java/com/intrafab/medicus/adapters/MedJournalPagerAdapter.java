package com.intrafab.medicus.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.intrafab.medicus.fragments.PlaceHolderMenstrualCycleFragment;
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
    private static final int BLOOD_PRESSURE_TAB = 2;

/// !!! ПОКА ЧТО ВСЕГДА ВОЗВРАЩАЕТСЯ ОДИН И ТОТ ЖЕ ФРАГМЕНТ
    public Fragment getItem(int position) {
        Logger.d(TAG, "getItem position: " + String.valueOf(position));
        switch (position){
            case MENSTRUAL_CYCLE_TAB:
                return new PlaceHolderMenstrualCycleFragment();
            case BODY_INDEX_TAB:
                return new PlaceHolderMenstrualCycleFragment();
            case BLOOD_PRESSURE_TAB:
            default:
                return new PlaceHolderMenstrualCycleFragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
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

    //// !!! каким образом помещать данные в эти фрагменты? все фрагменты разные -> нужно придумать единый формат данных ?? биг трабл

    /*

    public void setData(List<StorageInfo> data, int fragmentPosition) {
        Fragment current = getCurrentFragment(fragmentPosition);
        if (current != null) {
            if (current instanceof PlaceholderStorageFragment) {
                ((PlaceholderStorageFragment)current).setData(data);
            } else if (current instanceof PlaceholderStorageTripFragment) {
                ((PlaceholderStorageTripFragment)current).setData(data);
            }
        }
    }



    public void showProgress(int fragmentPosition) {
        Fragment current = getCurrentFragment(fragmentPosition);
        if (current != null) {
            if (current instanceof PlaceHolderMenstrualCycleFragment) {
                ((PlaceHolderMenstrualCycleFragment)current).showProgress();
            } //else if (current instanceof PlaceholderStorageTripFragment) {
               // ((PlaceholderStorageTripFragment)current).showProgress();
           //}
        }
    }

    public boolean isProgress(int fragmentPosition) {
        Fragment current = getCurrentFragment(fragmentPosition);
        if (current != null) {
            if (current instanceof PlaceHolderMenstrualCycleFragment) {
                return ((PlaceHolderMenstrualCycleFragment)current).isProgress();
            } //else if (current instanceof PlaceholderStorageTripFragment) {
              //  return ((PlaceholderStorageTripFragment)current).isProgress();
            //}
        }

        return false;
    }

    public void hideProgress(int fragmentPosition) {
        Fragment current = getCurrentFragment(fragmentPosition);
        if (current != null) {
            if (current instanceof PlaceHolderMenstrualCycleFragment) {
                ((PlaceHolderMenstrualCycleFragment)current).hideProgress();
            } //else if (current instanceof PlaceholderStorageTripFragment) {
              //((PlaceholderStorageTripFragment)current).hideProgress();
            //}
        }
    }

    */
}
