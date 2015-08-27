package com.intrafab.medicus.cards;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.intrafab.medicus.R;

/**
 * Created by Анна on 20.08.2015.
 */
public class CardPeriodCycle extends RecyclerView.ViewHolder{

    /// this class provides the appearance of Menstrual Cycle Card\
    /// now it contain only one text view which provide some important information about current day of menstrual cycle
    /// may be in future it will contain some grapfical information

    /// Need to implement logic

    CardView cv;
    TextView tvTitle;
    TextView tvInformation;


    public CardPeriodCycle(View itemView) {
        super(itemView);

        cv = (CardView)itemView.findViewById(R.id.cv);
        cv.setRadius(10);
        tvTitle = (TextView)itemView.findViewById(R.id.tvTitle);
        tvInformation = (TextView)itemView.findViewById(R.id.tvInformation);
        }
}
