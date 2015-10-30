package com.intrafab.medicus.medJournal.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.intrafab.medicus.R;
import com.intrafab.medicus.medJournal.cards.CardCycle;
import com.intrafab.medicus.medJournal.cards.CardPills;
import com.intrafab.medicus.medJournal.cards.CardTemperature;
import com.intrafab.medicus.medJournal.data.ContraceptionInfo;
import com.intrafab.medicus.medJournal.data.PeriodCycleEntry;
import com.intrafab.medicus.utils.Logger;
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

    CardCycle caledarCard;
    CardOvulation ovulationCard;
    PeriodCycleEntry period;
    ContraceptionInfo contraceptionInfo;

    public interface OnClickListener {
        void onClickItem(int itemType, View view);
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
            View v = LayoutInflater.from(context).inflate(R.layout.card_cycle_ovulation, parent, false);
            CardCycle holder = new CardCycle(v, mListener, period);
            return holder;
        } else if (viewType == OVULATION_CARD_TYPE) {
            View v = LayoutInflater.from(context).inflate(R.layout.card_cycle_ovulation, parent, false);
            CardOvulation holder = new CardOvulation(v, period/*, mListener*/);
            return holder;
        } else if (viewType == TEMPERATURE_CARD_TYPE) {
            View v = LayoutInflater.from(context).inflate(R.layout.card_temperature, parent, false);
            CardTemperature holder = new CardTemperature(v/*, period, mListener*/);
            return holder;
        } else if (viewType == PILLS_CARD_TYPE){
            View v = LayoutInflater.from(context).inflate(R.layout.card_contraception, parent, false);
            CardPills holder = new CardPills(v, contraceptionInfo, mListener);
            return holder;
        } else {
            View v = LayoutInflater.from(context).inflate(R.layout.card_cycle_ovulation, parent, false);
            CardOvulation holder = new CardOvulation(v, period/*, mListener*/);
            return holder;
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Logger.d (TAG, "onBindViewHolder, position: " + position);
        switch (getItemViewType(position)){
            case CYCLE_CARD_TYPE:
                CardCycle holderCycle = (CardCycle)holder;
                holderCycle.fillCard(period);
                break;
            case OVULATION_CARD_TYPE:
                CardOvulation holderOvul = (CardOvulation)holder;
                holderOvul.fillCard(period);
                break;
            case TEMPERATURE_CARD_TYPE:
                CardTemperature holderTemp = (CardTemperature)holder;
                holderTemp.fillCard();
                break;
            case PILLS_CARD_TYPE:
                CardPills holderPills = (CardPills)holder;
                holderPills.setContraceptionInfo(contraceptionInfo );
                holderPills.fillCard();
                break;
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }

    public void setPeriod (PeriodCycleEntry period){
        Logger.d(TAG, "fill card in card adapter");
        this.period = period;
    }

    public void setContraceptionInfo (ContraceptionInfo contraceptionInfo){
        Logger.d(TAG, "fill card in card adapter");
        this.contraceptionInfo = contraceptionInfo;
    }
}
