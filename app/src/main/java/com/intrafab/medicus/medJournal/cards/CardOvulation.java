package com.intrafab.medicus.medJournal.cards;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.intrafab.medicus.Constants;
import com.intrafab.medicus.R;
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
public class CardOvulation extends RecyclerView.ViewHolder{

    /// this class provides the appearance of Menstrual Cycle Card\
    /// now it contain only one text view which provide some important information about current day of menstrual cycle
    /// may be in future it will contain some grapfical information

    /// Need to implement logic

    CardView cv;
    TextView tvTitle;
    TextView tvDate;
    TextView tvPeriodDay;
    TextView tvPhase;
    TextView tvTip;


    public CardOvulation(View itemView, PeriodCycleEntry period) {
        super(itemView);

        cv = (CardView)itemView.findViewById(R.id.cv);
        cv.setRadius(10);

        tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
        tvDate = (TextView) itemView.findViewById(R.id.tvDate);
        tvPeriodDay = (TextView) itemView.findViewById(R.id.tvPeriodDay);
        tvPhase = (TextView) itemView.findViewById(R.id.tvPhase);
        tvTip = (TextView) itemView.findViewById(R.id.tvTip);

        Logger.d("OVULATIONCARD", "before");
        if (period != null) {
            Logger.d("OVULATIONCARD", "period is not null");
            /// set ovulation Date
            Calendar calendar = new GregorianCalendar();
            calendar.setTimeInMillis(period.getFirstDay());
            calendar.add(Calendar.DATE, period.getOvulationDay());
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM dd", Locale.ENGLISH);
            dateFormat.setTimeZone(calendar.getTimeZone());
            String strDate = dateFormat.format(calendar.getTime()) + "th";
            tvDate.setText(strDate);
            /// set day of the period
            calendar = Calendar.getInstance();
            long diff = calendar.getTimeInMillis() - (period.getFirstDay() + period.getOvulationDay() * Constants.Numeric.dayToMillis);
            String ovulationDate;
            if (diff < 0) {
                ovulationDate = " d before ovulation";
                diff *= (-1);
            } else
                ovulationDate = " d after ovulation";
            long diffInDays = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
            tvPeriodDay.setText(String.valueOf(diffInDays) + ovulationDate);

            Calendar fertileFirstDay = Calendar.getInstance();
            fertileFirstDay.setTimeInMillis(period.getFirstDay() + period.getFertileFirstDay() * Constants.Numeric.dayToMillis);
            Calendar fertileLastDay = Calendar.getInstance();
            fertileLastDay.setTimeInMillis(period.getFirstDay() + (period.getOvulationDay() + 1) * Constants.Numeric.dayToMillis);
            if (calendar.after(fertileFirstDay) && calendar.before(fertileLastDay))
                tvPhase.setText("fertile day");
            else
                tvPhase.setText("infertile day");

            dateFormat = new SimpleDateFormat("MMMM dd", Locale.getDefault());
            String firstFertileDay = dateFormat.format(fertileFirstDay.getTime()) + "th";
            String lastFertileDay = dateFormat.format(fertileLastDay.getTime()) + "th";

            tvTip.setText("Fertile period: " + firstFertileDay + " to " + lastFertileDay);
        }


        }
    public void fillCard (PeriodCycleEntry period){
        if (period != null) {
            Logger.d("OVULATIONCARD", "period is not null");
            /// set ovulation Date
            Calendar calendar = new GregorianCalendar();
            calendar.setTimeInMillis(period.getFirstDay());
            calendar.add(Calendar.DATE, period.getOvulationDay());
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM dd", Locale.getDefault());
            dateFormat.setTimeZone(calendar.getTimeZone());
            String strDate = dateFormat.format(calendar.getTime()) + "th";
            tvDate.setText(strDate);
            /// set day of the period
            calendar = Calendar.getInstance();
            long diff = calendar.getTimeInMillis() - (period.getFirstDay() + period.getOvulationDay() * Constants.Numeric.dayToMillis);
            String ovulationDate;
            if (diff < 0) {
                ovulationDate = " d before ovulation";
                diff *= (-1);
            } else
                ovulationDate = " d after ovulation";
            long diffInDays = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
            tvPeriodDay.setText(String.valueOf(diffInDays) + ovulationDate);

            Calendar fertileFirstDay = Calendar.getInstance();
            fertileFirstDay.setTimeInMillis(period.getFirstDay() + period.getFertileFirstDay() * Constants.Numeric.dayToMillis);
            Calendar fertileLastDay = Calendar.getInstance();
            fertileLastDay.setTimeInMillis(period.getFirstDay() + (period.getOvulationDay() + 1) * Constants.Numeric.dayToMillis);
            if (calendar.after(fertileFirstDay) && calendar.before(fertileLastDay))
                tvPhase.setText("fertile day");
            else
                tvPhase.setText("infertile day");

            dateFormat = new SimpleDateFormat("MMMM dd", Locale.getDefault());
            String firstFertileDay = dateFormat.format(fertileFirstDay.getTime()) + "th";
            String lastFertileDay = dateFormat.format(fertileLastDay.getTime()) + "th";

            tvTip.setText("Fertile period: " + firstFertileDay + " to " + lastFertileDay);
        }

    }
}
