package com.intrafab.medicus.cards;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.intrafab.medicus.R;
import com.intrafab.medicus.adapters.PeriodCycleAdapter;
import com.tyczj.extendedcalendarview.ExtendedCalendarView;


/**
 * Created by Анна on 20.08.2015.
 */
public class CardPeriodCalendar extends RecyclerView.ViewHolder implements View.OnClickListener {

    ExtendedCalendarView calendar;
    TextView tvTitle;
    CardView cv;
    PeriodCycleAdapter.OnClickListener mListener;

    public CardPeriodCalendar(View itemView, PeriodCycleAdapter.OnClickListener listener) {
        super(itemView);

        mListener = listener;

        cv = (CardView)itemView.findViewById(R.id.cv);
        cv.setOnClickListener(this);
        //cv.setRadius(0);
        //cv.setCardBackgroundColor(R.color.material_blue_grey_800);
        //cv.setPadding(30,30,30,30);
        //cv.setCardElevation(50);
        // cv.setShadowPadding(10,10,70,70);
        cv.setUseCompatPadding(true);
        tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
        calendar = (ExtendedCalendarView) itemView.findViewById(R.id.calendar);

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.cv && mListener!=null)
            mListener.onClickItem(PeriodCycleAdapter.CALENDAR_CARD_TYPE, view);
    }
}
