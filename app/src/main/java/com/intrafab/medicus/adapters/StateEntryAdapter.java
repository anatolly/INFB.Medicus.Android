package com.intrafab.medicus.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.intrafab.medicus.R;
import com.intrafab.medicus.data.StateEntry;
import com.intrafab.medicus.views.ItemStateEntryView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Artemiy Terekhov on 27.04.2015.
 */
public class StateEntryAdapter extends RecyclerView.Adapter<ItemStateEntryView> {

    private OnClickListener mListener;
    private List<StateEntry> mListItems = new ArrayList<StateEntry>();

    public interface OnClickListener {
        public void onClickItem(StateEntry itemStateEntry);
    }

    public StateEntryAdapter(OnClickListener listener) {
        mListener = listener;
    }

    @Override
    public ItemStateEntryView onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_state_entry_list_item, parent, false);

        return new ItemStateEntryView(view, mListener);
    }

    @Override
    public void onBindViewHolder(ItemStateEntryView viewHolder, int i) {
        final StateEntry item = mListItems.get(i);
        viewHolder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return mListItems.size();
    }
    public void add(StateEntry item) {
        mListItems.add(item);
    }
    public void addAll(List<StateEntry> items) {
        if (items != null)
            mListItems.addAll(items);
        //notifyDataSetChanged();
    }

    public int size() { return mListItems.size(); }

    public void clear() {
        mListItems.clear();
    }

}
