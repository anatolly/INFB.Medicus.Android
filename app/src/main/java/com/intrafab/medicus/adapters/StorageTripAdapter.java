package com.intrafab.medicus.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.intrafab.medicus.R;
import com.intrafab.medicus.data.StorageInfo;
import com.intrafab.medicus.views.ItemStorageTripInfoView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Artemiy Terekhov on 19.04.2015.
 */
public class StorageTripAdapter extends RecyclerView.Adapter<ItemStorageTripInfoView> {

    private OnClickListener mListener;
    private List<StorageInfo> mListItems = new ArrayList<StorageInfo>();

    public interface OnClickListener {
        public void onStorageTripClickItem(StorageInfo itemStorageInfo);
    }

    public StorageTripAdapter(OnClickListener listener) {
        mListener = listener;
    }

    @Override
    public ItemStorageTripInfoView onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_storage_info_list_item, parent, false);

        return new ItemStorageTripInfoView(view, mListener);
    }

    @Override
    public void onBindViewHolder(ItemStorageTripInfoView viewHolder, int i) {
        final StorageInfo item = mListItems.get(i);
        viewHolder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return mListItems.size();
    }
    public void add(StorageInfo item) {
        mListItems.add(item);
    }
    public void addAll(List<StorageInfo> items) {
        if (items != null)
            mListItems.addAll(items);
        notifyDataSetChanged();
    }

    public int size() { return mListItems.size(); }

    public void clear() {
        mListItems.clear();
    }

}
