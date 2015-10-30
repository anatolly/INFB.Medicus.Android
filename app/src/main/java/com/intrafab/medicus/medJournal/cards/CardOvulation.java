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
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by Анна on 20.08.2015.
 */
public class CardOvulation extends RecyclerView.ViewHolder{

    CardView cv;
    TextView tvTitle;
    //TextView tvDate;
    TextView tvOvulationDay;
    TextView tvFertilePeriod;
    View rootView;


    public CardOvulation(View itemView, PeriodCycleEntry period) {
        super(itemView);

        rootView = itemView;
        cv = (CardView)itemView.findViewById(R.id.cv);
        cv.setRadius(10);
        cv.setUseCompatPadding(true);

        tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
        //tvDate = (TextView) itemView.findViewById(R.id.tvDate);
        tvOvulationDay = (TextView) itemView.findViewById(R.id.tvHeader);
        tvFertilePeriod = (TextView) itemView.findViewById(R.id.tvSubHeader);

        fillCard(period);
    }

    public void fillCard (PeriodCycleEntry period){
        tvTitle.setText(rootView.getResources().getString(R.string.option_ovulation));
        if (period != null) {
//            /// set ovulation Date
//            Calendar calendar = new GregorianCalendar();
//            calendar.setTimeInMillis(period.getFirstDay());
//            calendar.add(Calendar.DATE, period.getOvulationDay());
//            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM dd", Locale.getDefault());
//            dateFormat.setTimeZone(calendar.getTimeZone());
//            String strDate = dateFormat.format(calendar.getTime());
            //tvDate.setText(strDate);
            /// set day of the period
            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd MMMM hh:mm:ss a", Locale.getDefault());
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.AM_PM, Calendar.AM);
            calendar.set(Calendar.HOUR, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            Logger.d ("CARD OVULATION", " today Date: "  + simpleDateFormat1.format(calendar.getTime()));
            long diff = calendar.getTimeInMillis() - (period.getFirstDay() + (period.getOvulationDay()) * Constants.Numeric.dayToMillis);
            Logger.d ("CArd ovulation", "diff: " + diff);
            String ovulationDate = "";
            if (diff < 0) {
                ovulationDate = rootView.getResources().getString(R.string.day_before_ovulation);
                diff *= (-1);
            } else if (diff > 0)
                ovulationDate = rootView.getResources().getString(R.string.day_after_ovulation);
            else
                tvOvulationDay.setText(rootView.getResources().getString(R.string.day_ovulation));
            long diffInDays = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
            if (diffInDays != 0)
                tvOvulationDay.setText(String.valueOf(diffInDays) + ovulationDate);

            Calendar fertileFirstDay = Calendar.getInstance();
            fertileFirstDay.setTimeInMillis(period.getFirstDay() + period.getFertileFirstDay() * Constants.Numeric.dayToMillis);
            Calendar fertileLastDay = Calendar.getInstance();
            fertileLastDay.setTimeInMillis(period.getFirstDay() + (period.getOvulationDay() + 1) * Constants.Numeric.dayToMillis);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM hh:mm:ss a", Locale.getDefault());
            Logger.d("Card Ovulation", "fertile last day:" + simpleDateFormat.format(fertileLastDay.getTime()));

            if (calendar.after(fertileFirstDay) && calendar.before(fertileLastDay))
                tvFertilePeriod.setText(rootView.getResources().getString(R.string.fertile_days));
            else
                tvFertilePeriod.setText(rootView.getResources().getString(R.string.infertile_days));

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM", Locale.getDefault());
            String firstFertileDay = dateFormat.format(fertileFirstDay.getTime());
            String lastFertileDay = dateFormat.format(fertileLastDay.getTime());

            tvFertilePeriod.setText(tvFertilePeriod.getText() + "\n" + rootView.getResources().getString(R.string.fertile_period) + ":\n" + firstFertileDay + " - " + lastFertileDay);
        } else {
            tvOvulationDay.setText(rootView.getResources().getString(R.string.no_cycle));
            tvFertilePeriod.setText("");
        }
    }
}
