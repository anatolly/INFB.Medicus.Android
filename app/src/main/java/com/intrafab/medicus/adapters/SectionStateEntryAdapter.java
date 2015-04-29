package com.intrafab.medicus.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.intrafab.medicus.R;
import com.intrafab.medicus.data.SectionStateEntry;
import com.intrafab.medicus.data.StateEntry;
import com.intrafab.medicus.views.ItemStateEntrySectionView;
import com.intrafab.medicus.views.ItemStateEntryView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Artemiy Terekhov on 27.04.2015.
 */
public class SectionStateEntryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int SECTION_TYPE = 0;

    //private OnClickListener mListener;
    //private List<StateEntry> mListItems = new ArrayList<StateEntry>();
    private StateEntryAdapter mAdapter;
    private boolean mValid = true;
    private SparseArray<SectionStateEntry> mSections = new SparseArray<SectionStateEntry>();

    public SectionStateEntryAdapter(StateEntryAdapter adapter) {
        mAdapter = adapter;

        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                mValid = mAdapter.getItemCount() > 0;
                notifyDataSetChanged();
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                mValid = mAdapter.getItemCount() > 0;
                notifyItemRangeChanged(positionStart, itemCount);
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                mValid = mAdapter.getItemCount() > 0;
                notifyItemRangeInserted(positionStart, itemCount);
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                mValid = mAdapter.getItemCount() > 0;
                notifyItemRangeRemoved(positionStart, itemCount);
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == SECTION_TYPE) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.view_state_entry_list_section, parent, false);
            return new ItemStateEntrySectionView(view);
        } else {
            return mAdapter.onCreateViewHolder(parent, viewType - 1);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final SectionStateEntry item = mSections.get(position);

        if (isSectionHeaderPosition(position)) {
            ((ItemStateEntrySectionView) viewHolder).setItem(item);
        } else {
            mAdapter.onBindViewHolder((ItemStateEntryView) viewHolder, sectionedPositionToPosition(position));
        }
    }

    @Override
    public int getItemCount() {
        return (mValid ? mAdapter.getItemCount() + mSections.size() : 0);
    }

    @Override
    public long getItemId(int position) {
        return isSectionHeaderPosition(position)
                ? Integer.MAX_VALUE - mSections.indexOfKey(position)
                : mAdapter.getItemId(sectionedPositionToPosition(position));
    }

    @Override
    public int getItemViewType(int position) {
        return isSectionHeaderPosition(position)
                ? SECTION_TYPE
                : mAdapter.getItemViewType(sectionedPositionToPosition(position)) +1 ;
    }

    public boolean isSectionHeaderPosition(int position) {
        return mSections.get(position) != null;
    }

    public int sectionedPositionToPosition(int sectionedPosition) {
        if (isSectionHeaderPosition(sectionedPosition)) {
            return RecyclerView.NO_POSITION;
        }

        int offset = 0;
        for (int i = 0; i < mSections.size(); i++) {
            if (mSections.valueAt(i).getSectionedPosition() > sectionedPosition) {
                break;
            }
            --offset;
        }
        return sectionedPosition + offset;
    }

    public int positionToSectionedPosition(int position) {
        int offset = 0;
        for (int i = 0; i < mSections.size(); i++) {
            if (mSections.valueAt(i).getFirstPosition() > position) {
                break;
            }
            ++offset;
        }
        return position + offset;
    }

    public void setSections(SectionStateEntry[] sections) {
        mSections.clear();

        if (sections == null || sections.length <= 0) {
            notifyDataSetChanged();
            return;
        }

        Arrays.sort(sections, new Comparator<SectionStateEntry>() {
            @Override
            public int compare(SectionStateEntry o, SectionStateEntry o1) {
                return (o.getFirstPosition() == o1.getFirstPosition())
                        ? 0
                        : ((o.getFirstPosition() < o1.getFirstPosition()) ? -1 : 1);
            }
        });

        int offset = 0; // offset positions for the headers we're adding
        for (SectionStateEntry section : sections) {
            section.setSectionedPosition(section.getFirstPosition() + offset);
            mSections.append(section.getSectionedPosition(), section);
            ++offset;
        }

        notifyDataSetChanged();
    }
}
