package com.intrafab.medicus.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.intrafab.medicus.R;
import com.intrafab.medicus.adapters.SectionStateEntryAdapter;
import com.intrafab.medicus.adapters.StateEntryAdapter;
import com.intrafab.medicus.data.SectionStateEntry;
import com.intrafab.medicus.data.StateEntry;
import com.intrafab.medicus.widgets.EmptyRecyclerView;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by Artemiy Terekhov on 29.04.2015.
 */
public class PlaceholderStateEntryFragment extends Fragment {

    public static final String TAG = PlaceholderStateEntryFragment.class.getName();

    private EmptyRecyclerView mRecyclerView;
    private LinearLayout mEmptyLayout;
    private ProgressWheel mProgress;

    private SectionStateEntryAdapter mAdapter;
    private StateEntryAdapter mEntryAdapter;
    private StateEntryAdapter.OnClickListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (StateEntryAdapter.OnClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement StateEntryAdapter.OnClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        setData(null);
        mListener = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mEntryAdapter = new StateEntryAdapter(mListener);
        mAdapter = new SectionStateEntryAdapter(mEntryAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_state_entry, container, false);

        mRecyclerView = (EmptyRecyclerView) rootView.findViewById(R.id.listViews);
        mEmptyLayout = (LinearLayout) rootView.findViewById(R.id.layoutEmptyList);
        mProgress = (ProgressWheel) rootView.findViewById(R.id.progress_wheel);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setEmptyView(mEmptyLayout);
    }

    public List<StateEntry> getItems() {
        if (mEntryAdapter != null) {
            return mEntryAdapter.getItems();
        }

        return null;
    }

    public void setData(List<StateEntry> data) {
        mEntryAdapter.clear();
        mEntryAdapter.addAll(data);

        if (data == null || data.size() <= 0) {
            mAdapter.setSections(null);
            return;
        }

        Calendar calStart = GregorianCalendar.getInstance();

        List<SectionStateEntry> sections = new ArrayList<SectionStateEntry>();
        boolean isSectionFound = false;
        int counter = 0;
        for (StateEntry entry : data) {
            calStart.setTimeInMillis(entry.getStateStart());
            String headerDate = DateUtils.formatDateTime(getActivity(), calStart.getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE);
            for (SectionStateEntry entrySection : sections) {
                if (entrySection.getTitle().toString().equals(headerDate)) {
                    isSectionFound = true;
                    break;
                }
            }
            if (!isSectionFound) {
                SectionStateEntry entrySection = new SectionStateEntry(counter, headerDate);
                sections.add(entrySection);
            }
            isSectionFound = false;
            counter++;
        }

        SectionStateEntry[] sectionsArray = new SectionStateEntry[sections.size()];
        mAdapter.setSections(sections.toArray(sectionsArray));
    }

    private void showEmptyView(boolean isShow) {
        int count = mAdapter.getItemCount();
        if (count <= 0 && isShow)
            mEmptyLayout.setVisibility(View.VISIBLE);
        else
            mEmptyLayout.setVisibility(View.GONE);
    }

    public boolean isProgress() {
        return mProgress.getVisibility() == View.VISIBLE;
    }

    public void showProgress() {
        mProgress.setVisibility(View.VISIBLE);
        showEmptyView(false);
    }

    public void hideProgress() {
        mProgress.setVisibility(View.GONE);
    }
}
