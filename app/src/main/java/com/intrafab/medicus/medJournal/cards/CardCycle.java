package com.intrafab.medicus.medJournal.cards;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
public class CardCycle extends RecyclerView.ViewHolder implements View.OnClickListener {

    TextView tvTitle;
    //TextView tvDate;
    TextView tvPeriodDay;
    TextView tvPhase;
    CardView cv;
    PeriodCardAdapter.OnClickListener mListener;
    View rootView;

    public CardCycle(View itemView, PeriodCardAdapter.OnClickListener listener, PeriodCycleEntry period) {
        super(itemView);

        mListener = listener;
        rootView = itemView;
        cv = (CardView)itemView.findViewById(R.id.cv);
        cv.setOnClickListener(this);
        cv.setUseCompatPadding(true);
        tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
        //tvDate = (TextView) itemView.findViewById(R.id.tvDate);
        tvPeriodDay = (TextView) itemView.findViewById(R.id.tvHeader);
        tvPhase = (TextView) itemView.findViewById(R.id.tvSubHeader);

        RelativeLayout rlTop = (RelativeLayout) itemView.findViewById(R.id.rlTop);
        rlTop.setBackgroundColor(itemView.getResources().getColor(R.color.calendar_background_period));

        fillCard(period);
        //cv.setRadius(0);
        //cv.setCardBackgroundColor(R.color.material_blue_grey_800);
        //cv.setPadding(30,30,30,30);
        //cv.setCardElevation(50);
        // cv.setShadowPadding(10,10,70,70);

        ImageView settingButton = (ImageView) itemView.findViewById(R.id.ivSettingButton);
        settingButton.setClickable(true);
        settingButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.cv && mListener!=null)
            mListener.onClickItem(PeriodCardAdapter.CYCLE_CARD_TYPE, view);
        if (view.getId() == R.id.ivSettingButton)
            Logger.d("CardCycle", "setting button clicked");
    }

    public void fillCard(PeriodCycleEntry period){
        tvTitle.setText(rootView.getResources().getString(R.string.menstrual_cycle_title));
        if (period != null) {
            /// set today Date
              Calendar cal = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM hh:mm:ss a", Locale.getDefault());
//            dateFormat.setTimeZone(cal.getTimeZone());
//            String strDate = dateFormat.format(cal.getTime()) + "th";
//            tvDate.setText(strDate);

            Logger.d ("CardCycle:" ,"firstDat of cycle: " +  dateFormat.format(period.getFirstDay()));
            /// set day of the period
            long diff = cal.getTimeInMillis() - period.getFirstDay();
            long diffInDays = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) + 1;
            tvPeriodDay.setText(String.valueOf(diffInDays) + rootView.getResources().getString(R.string.day_of_cycle));
            //
            tvPhase.setText("Фоликулярная фаза");
            //tvPhase.setText("folicular phase");
        }
        else {
            tvPeriodDay.setText(rootView.getResources().getString(R.string.no_cycle));
            tvPhase.setText("");
        }
    }

}
