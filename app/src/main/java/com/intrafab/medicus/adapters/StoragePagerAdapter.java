package com.intrafab.medicus.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.intrafab.medicus.data.StorageInfo;
import com.intrafab.medicus.fragments.PlaceholderStorageFragment;
import com.intrafab.medicus.fragments.PlaceholderStorageTripFragment;
import com.intrafab.medicus.utils.Logger;

import java.util.List;

/**
 * Created by Artemiy Terekhov on 19.04.2015.
 */
public class StoragePagerAdapter extends FragmentStatePagerAdapter {
    public static final String TAG = StoragePagerAdapter.class.getName();

    private SparseArray<Fragment> mRegisteredFragments = new SparseArray<Fragment>();

    public StoragePagerAdapter(FragmentManager fm) {
        super(fm);
    }
    public Fragment getItem(int number) {

//        Fragment current = getCurrentFragment(number);
//        if (current != null)
//            return current;

        if (number == 0) {
            return new PlaceholderStorageFragment();
        } else {
            return new PlaceholderStorageTripFragment();
        }
    }
    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        mRegisteredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        mRegisteredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    private Fragment getCurrentFragment(int fragmentPosition) {

        return mRegisteredFragments.get(fragmentPosition);
    }

    public <T extends Fragment> void setData(List<StorageInfo> data, Class<T> clazz) {

        int size = mRegisteredFragments.size();
        for (int i = 0; i < size; ++i) {
            Fragment current = mRegisteredFragments.get(i);
            if (PlaceholderStorageFragment.class.isAssignableFrom(clazz)) {
                if (current instanceof PlaceholderStorageFragment) {
                    ((PlaceholderStorageFragment)current).setData(data);
                }
            } else if (PlaceholderStorageTripFragment.class.isAssignableFrom(clazz)) {
                if (current instanceof PlaceholderStorageTripFragment) {
                    ((PlaceholderStorageTripFragment)current).setData(data);
                }
            }
        }
    }

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
            if (current instanceof PlaceholderStorageFragment) {
                ((PlaceholderStorageFragment)current).showProgress();
            } else if (current instanceof PlaceholderStorageTripFragment) {
                ((PlaceholderStorageTripFragment)current).showProgress();
            }
        }
    }

    public boolean isProgress(int fragmentPosition) {
        Fragment current = getCurrentFragment(fragmentPosition);
        if (current != null) {
            if (current instanceof PlaceholderStorageFragment) {
                return ((PlaceholderStorageFragment)current).isProgress();
            } else if (current instanceof PlaceholderStorageTripFragment) {
                return ((PlaceholderStorageTripFragment)current).isProgress();
            }
        }

        return false;
    }

    public void hideProgress(int fragmentPosition) {
        Fragment current = getCurrentFragment(fragmentPosition);
        if (current != null) {
            if (current instanceof PlaceholderStorageFragment) {
                ((PlaceholderStorageFragment)current).hideProgress();
            } else if (current instanceof PlaceholderStorageTripFragment) {
                ((PlaceholderStorageTripFragment)current).hideProgress();
            }
        }
    }
}
