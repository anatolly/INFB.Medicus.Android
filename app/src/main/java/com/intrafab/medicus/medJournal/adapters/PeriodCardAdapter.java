package com.intrafab.medicus.medJournal.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.intrafab.medicus.R;
import com.intrafab.medicus.medJournal.cards.CardPills;
import com.intrafab.medicus.medJournal.cards.CardTemperature;
import com.intrafab.medicus.medJournal.data.PeriodCycleEntry;
import com.intrafab.medicus.utils.Logger;
import com.intrafab.medicus.medJournal.cards.CardPeriodCalendar;
import com.intrafab.medicus.medJournal.cards.CardOvulation;

/**
 * Created by Anna Anfilova on 18.08.2015.
 */
public class PeriodCardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final String TAG =  PeriodCardAdapter.class.getName();

    public static final int CYCLE_CARD_TYPE = 0;
    public static final int OVULATION_CARD_TYPE = 1;
    public static final int TEMPERATURE_CARD_TYPE = 2;
    public static final int PILLS_CARD_TYPE = 3;
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

        if (viewType == CYCLE_CARD_TYPE) {
            View v = LayoutInflater.from(context).inflate(R.layout.card_for_period_calendar, parent, false);
            CardPeriodCalendar holder = new CardPeriodCalendar(v, mListener, period);
            return holder;
        } else if (viewType == OVULATION_CARD_TYPE) {
            View v = LayoutInflater.from(context).inflate(R.layout.card_period_cycle, parent, false);
            CardOvulation holder = new CardOvulation(v, period/*, mListener*/);
            return holder;
        } else if (viewType == TEMPERATURE_CARD_TYPE) {
            View v = LayoutInflater.from(context).inflate(R.layout.card_period_cycle, parent, false);
            CardTemperature holder = new CardTemperature(v/*, period, mListener*/);
            return holder;
        } else if (viewType == PILLS_CARD_TYPE){
            View v = LayoutInflater.from(context).inflate(R.layout.card_period_cycle, parent, false);
            CardPills holder = new CardPills(v/*, period, mListener*/);
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
            case CYCLE_CARD_TYPE:
                CardPeriodCalendar holderCycle = (CardPeriodCalendar)holder;
                holderCycle.fillCard(period);
                break;
            case OVULATION_CARD_TYPE:
                CardOvulation holderOvul = (CardOvulation)holder;
                holderOvul.fillCard(period);
                break;
            case TEMPERATURE_CARD_TYPE:
                CardTemperature holderTemp = (CardTemperature)holder;
                holderTemp.fillCard();

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
