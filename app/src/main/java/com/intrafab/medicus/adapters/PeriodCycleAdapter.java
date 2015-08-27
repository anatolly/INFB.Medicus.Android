package com.intrafab.medicus.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.intrafab.medicus.R;
import com.intrafab.medicus.utils.Logger;
import com.intrafab.medicus.cards.CardPeriodCalendar;
import com.intrafab.medicus.cards.CardPeriodCycle;

/**
 * Created by Anna Anfilova on 18.08.2015.
 */
public class PeriodCycleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final String TAG =  PeriodCycleAdapter.class.getName();

    public static final int CALENDAR_CARD_TYPE = 1;
    public static final int CYCLE_CARD_TYPE = 2;
    private OnClickListener mListener;

    public interface OnClickListener {
        void onClickItem(int itemPosition, View view);
    }


    public PeriodCycleAdapter(OnClickListener listener) {
        mListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return CALENDAR_CARD_TYPE;
        else
            return CYCLE_CARD_TYPE;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Logger.d(TAG, String.format("onCreateViewHolder: viewType = %d", viewType) );

        RecyclerView.ViewHolder holder;
        View v;
        Context context = parent.getContext();

        if (viewType == CALENDAR_CARD_TYPE) {
            v = LayoutInflater.from(context)
                    .inflate(R.layout.card_for_period_calendar, parent, false);

            holder = new CardPeriodCalendar(v, mListener);
        } else {
            v = LayoutInflater.from(context)
                    .inflate(R.layout.card_period_cycle, parent, false);
            holder = new CardPeriodCycle(v/*, mListener*/);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)){
            case CALENDAR_CARD_TYPE:
                CardPeriodCalendar holder1 = (CardPeriodCalendar)holder;
                break;
            case CYCLE_CARD_TYPE:
                CardPeriodCycle holder2 = (CardPeriodCycle)holder;
                break;
        }
    }

    @Override
    public int getItemCount() {
        // some logic here
        return 2;
    }
}
