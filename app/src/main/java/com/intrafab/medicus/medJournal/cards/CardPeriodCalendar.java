package com.intrafab.medicus.medJournal.cards;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.intrafab.medicus.R;
import com.intrafab.medicus.medJournal.adapters.PeriodCardAdapter;
import com.intrafab.medicus.medJournal.data.PeriodCycleEntry;
import com.intrafab.medicus.utils.Logger;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


/**
 * Created by Анна on 20.08.2015.
 */
public class CardPeriodCalendar extends RecyclerView.ViewHolder implements View.OnClickListener {

    TextView tvTitle;
    TextView tvDate;
    TextView tvPeriodDay;
    TextView tvPhase;
    TextView tvTip;
    CardView cv;
    PeriodCardAdapter.OnClickListener mListener;

    public CardPeriodCalendar(View itemView, PeriodCardAdapter.OnClickListener listener, PeriodCycleEntry period) {
        super(itemView);

        mListener = listener;

        cv = (CardView)itemView.findViewById(R.id.cv);
        cv.setOnClickListener(this);
        cv.setUseCompatPadding(true);
        tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
        tvDate = (TextView) itemView.findViewById(R.id.tvDate);
        tvPeriodDay = (TextView) itemView.findViewById(R.id.tvPeriodDay);
        tvPhase = (TextView) itemView.findViewById(R.id.tvPhase);
        tvTip = (TextView) itemView.findViewById(R.id.tvTip);

        fillCard(period);
        //cv.setRadius(0);
        //cv.setCardBackgroundColor(R.color.material_blue_grey_800);
        //cv.setPadding(30,30,30,30);
        //cv.setCardElevation(50);
        // cv.setShadowPadding(10,10,70,70);

        ImageView settingButton = (ImageView) itemView.findViewById(R.id.iv_settingButton);
        settingButton.setClickable(true);
        settingButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.cv && mListener!=null)
            mListener.onClickItem(PeriodCardAdapter.CYCLE_CARD_TYPE, view);
        if (view.getId() == R.id.iv_settingButton)
            Logger.d("CardPeriodCalendar", "setting button clicked");
    }

    public void fillCard(PeriodCycleEntry period){
        if (period != null) {
            /// set today Date
            Calendar cal = new GregorianCalendar();
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM dd", Locale.getDefault());
            dateFormat.setTimeZone(cal.getTimeZone());
            String strDate = dateFormat.format(cal.getTime()) + "th";

            tvDate.setText(strDate);
            /// set day of the period
            long diff = cal.getTimeInMillis() - period.getFirstDay();
            long diffInDays = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
            tvPeriodDay.setText(String.valueOf(diffInDays) + "th day of cycle");
            tvPhase.setText("folicular phase");
            tvTip.setText("Tap to go to the calendar");
        }
        else {
            Logger.d("PERIODCARD", "period is null");
        }
    }

}
