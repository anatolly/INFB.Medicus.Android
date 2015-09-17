package com.intrafab.medicus.medJournal.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.intrafab.medicus.R;
import com.intrafab.medicus.medJournal.data.PeriodCycleEntry;
import com.intrafab.medicus.utils.Logger;
import com.intrafab.medicus.medJournal.cards.CardPeriodCalendar;
import com.intrafab.medicus.medJournal.cards.CardOvulation;

/**
 * Created by Anna Anfilova on 18.08.2015.
 */
public class PeriodCardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final String TAG =  PeriodCardAdapter.class.getName();

    public static final int CALENDAR_CARD_TYPE = 0;
    public static final int CYCLE_CARD_TYPE = 1;
    private OnClickListener mListener;

    CardPeriodCalendar caledarCard;
    CardOvulation ovulationCard;
    PeriodCycleEntry period;

    public interface OnClickListener {
        void onClickItem(int itemPosition, View view);
    }


    public PeriodCardAdapter(OnClickListener listener) {
        mListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Logger.d(TAG, String.format("onCreateViewHolder: viewType = %d", viewType) );
        Context context = parent.getContext();

        if (viewType == CALENDAR_CARD_TYPE) {
            View v = LayoutInflater.from(context).inflate(R.layout.card_for_period_calendar, parent, false);
            CardPeriodCalendar holder = new CardPeriodCalendar(v, mListener, period);
            return holder;
        } else {
            View v = LayoutInflater.from(context).inflate(R.layout.card_period_cycle, parent, false);
            CardOvulation holder = new CardOvulation(v, period/*, mListener*/);
            return holder;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Logger.d (TAG, "onBindViewHolder, position: " + position);
        switch (getItemViewType(position)){
            case CALENDAR_CARD_TYPE:
                CardPeriodCalendar holder1 = (CardPeriodCalendar)holder;
                holder1.fillCard(period);
                break;
            case CYCLE_CARD_TYPE:
                CardOvulation holder2 = (CardOvulation)holder;
                holder2.fillCard(period);
                break;

        }
    }

    @Override
    public int getItemCount() {
        // some logic here
        return 2;
    }

    public void setPeriod (PeriodCycleEntry period){
        Logger.d(TAG, "fill card in card adapter");
        this.period = period;
    }
}
