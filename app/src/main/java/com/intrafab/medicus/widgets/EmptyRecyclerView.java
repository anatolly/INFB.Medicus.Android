package com.intrafab.medicus.widgets;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Artemiy Terekhov on 19.04.2015.
 */
public class EmptyRecyclerView extends RecyclerView {
    private View mEmptyView;

    public EmptyRecyclerView(Context context) { super(context); }

    public EmptyRecyclerView(Context context, AttributeSet attrs) { super(context, attrs); }

    public EmptyRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    void checkIfEmpty() {
        if (mEmptyView != null) {
            mEmptyView.setVisibility((getAdapter() != null && getAdapter().getItemCount() > 0) ? GONE : VISIBLE);
        }
    }

    final AdapterDataObserver observer = new AdapterDataObserver() {
        @Override public void onChanged() {
            super.onChanged();
            checkIfEmpty();
        }
    };

    @Override public void setAdapter(Adapter adapter) {
        final Adapter oldAdapter = getAdapter();
        if (oldAdapter != null) {
            oldAdapter.unregisterAdapterDataObserver(observer);
        }
        super.setAdapter(adapter);
        if (adapter != null) {
            adapter.registerAdapterDataObserver(observer);
        }
    }

    public void setEmptyView(View emptyView) {
        this.mEmptyView = emptyView;
        checkIfEmpty();
    }
}
