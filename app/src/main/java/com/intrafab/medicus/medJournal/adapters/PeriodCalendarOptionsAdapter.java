package com.intrafab.medicus.medJournal.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.intrafab.medicus.R;
import com.intrafab.medicus.medJournal.data.PeriodCalendarEntry;
import com.intrafab.medicus.medJournal.views.ItemPeriodCycleOptionView;
import com.intrafab.medicus.medJournal.views.ItemPeriodOptionView;

/**
 * Created by Анна on 25.08.2015.
 */
public class PeriodCalendarOptionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    public static final String TAG = PeriodCalendarOptionsAdapter.class.getName();

    OnItemClickListener mListener;
    PeriodCalendarEntry mEntry;

    public interface OnItemClickListener {
        void onItemClick(int optionType);
    }

    public PeriodCalendarOptionsAdapter(OnItemClickListener listener, PeriodCalendarEntry entry) {
        mListener = listener;
        mEntry = entry;
    }

    @Override
    public int getItemViewType(int position) {
            return position;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        if (viewType == ItemPeriodCycleOptionView.PERIOD){
            View v = LayoutInflater.from(context).inflate(R.layout.view_period_start_option, parent, false);
            ItemPeriodOptionView view = new ItemPeriodOptionView(v, mListener,mEntry);
            return view;
        }
        else {
            View v = LayoutInflater.from(context).inflate(R.layout.view_period_calend_option_item, parent, false);
            ItemPeriodCycleOptionView view;
            // the 4th parameter is argument (chechbox flag for intercourse option / body temperatute for temperature option / 0 for others)
            if (viewType == ItemPeriodCycleOptionView.INTERCOURSE)
                view = new ItemPeriodCycleOptionView(v, viewType, mListener, mEntry.isIntercourse() ? 1 : 0);
            else if (viewType == ItemPeriodCycleOptionView.TEMPERATURE)
                view = new ItemPeriodCycleOptionView(v, viewType, mListener, mEntry.getBodyTemperature());
            else
                view = new ItemPeriodCycleOptionView(v, viewType, mListener, 0);
            return view;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return ItemPeriodCycleOptionView.AMOUNT_OF_OPTIONS;
    }
}
