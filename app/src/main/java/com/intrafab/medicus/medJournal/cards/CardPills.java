package com.intrafab.medicus.medJournal.cards;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.intrafab.medicus.R;
import com.intrafab.medicus.medJournal.adapters.PeriodCardAdapter;
import com.intrafab.medicus.medJournal.data.ContraceptionInfo;
import com.intrafab.medicus.utils.Logger;

import java.util.Calendar;
import java.util.Formatter;
import java.util.concurrent.TimeUnit;


/**
 * Created by Анечка on 07.10.2015.
 */
public class CardPills extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView tvContraceptionType;
    private TextView tvContrInfo;
    private CardView cv;

    private ContraceptionInfo contraceptionInfo;
    private PeriodCardAdapter.OnClickListener mListener;


    public CardPills(View itemView, ContraceptionInfo contraceptionInfo, PeriodCardAdapter.OnClickListener listener) {
        super(itemView);

        mListener = listener;

        this.contraceptionInfo = contraceptionInfo;

        cv = (CardView)itemView.findViewById(R.id.cv);
        cv.setOnClickListener(this);
        cv.setUseCompatPadding(true);

        tvContraceptionType = (TextView) itemView.findViewById(R.id.tvContraceptionType);
        tvContrInfo = (TextView) itemView.findViewById(R.id.tvContrInfo);
        ImageView settingButton = (ImageView) itemView.findViewById(R.id.ivSettingButton);
        settingButton.setClickable(true);
        settingButton.setOnClickListener(this);

        fillCard();

        //fillCard(period);
        //cv.setRadius(0);
        //cv.setCardBackgroundColor(R.color.material_blue_grey_800);
        //cv.setPadding(30,30,30,30);
        //cv.setCardElevation(50);
        // cv.setShadowPadding(10,10,70,70);
    }

    @Override
    public void onClick(View view) {
        if ((view.getId() == R.id.cv) && mListener!=null)
            mListener.onClickItem(PeriodCardAdapter.PILLS_CARD_TYPE, view);
        else if (view.getId() == R.id.ivSettingButton)
            Logger.d("CardCycle", "setting button clicked");
    }

    public void fillCard(){
        if (contraceptionInfo == null) {
            Logger.d("cardPills", "contraceptionInfo = null");
            cv.findViewById(R.id.rlBottom).setVisibility(View.GONE);
            cv.findViewById(R.id.rlEmpty).setVisibility(View.VISIBLE);
            TextView tvStartBirthControl = (TextView)cv.findViewById(R.id.tvStartBC);
            tvStartBirthControl.setClickable(true);
            tvStartBirthControl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onClickItem(PeriodCardAdapter.PILLS_CARD_TYPE, view);
                }
            });

        }
        else {
            cv.findViewById(R.id.rlEmpty).setVisibility(View.GONE);
            cv.findViewById(R.id.rlBottom).setVisibility(View.VISIBLE);

            //set title
            String [] contraceptionName = cv.getResources().getStringArray(R.array.contraception);
            tvContraceptionType.setText(contraceptionName[contraceptionInfo.getContraceptionTypeId()]);

            long startDay  = contraceptionInfo.getStartDate();
            long today = Calendar.getInstance().getTimeInMillis();

            long timeDiff = today - startDay;
            int diffInDay =(int) TimeUnit.DAYS.convert(timeDiff, TimeUnit.MILLISECONDS);
            // is user set start day in the previous cycle
            int contraceptionDay;
            if (diffInDay >= contraceptionInfo.getActiveDays() + contraceptionInfo.getBreakDays()) {
                contraceptionDay = diffInDay % (contraceptionInfo.getActiveDays() + contraceptionInfo.getBreakDays());
                Calendar startDayCalendar = Calendar.getInstance();
                startDayCalendar.setTimeInMillis(startDay);
                startDayCalendar.add(Calendar.DATE, diffInDay - contraceptionDay);
                contraceptionInfo.setStartDate(startDayCalendar.getTimeInMillis());
            }
            else
                contraceptionDay = diffInDay;
            contraceptionDay++;

            String contrInfoStr;
            int dayRemainder;
            int activeDays = contraceptionInfo.getActiveDays();
            int breakDays = contraceptionInfo.getBreakDays();

            switch (contraceptionInfo.getContraceptionTypeId()) {
                case ContraceptionInfo.TYPE_RING:
                    if (contraceptionDay < activeDays) {
                        contrInfoStr = String.format(cv.getResources().getString(R.string.cont_ring_day), contraceptionDay, activeDays - contraceptionDay);
                    } else if (activeDays == contraceptionDay) {
                        contrInfoStr = String.format(cv.getResources().getString(R.string.cont_ring_end_day), contraceptionDay);
                    } else {
                        dayRemainder = activeDays + breakDays - contraceptionDay;
                        contrInfoStr = String.format(cv.getResources().getString(R.string.cont_ring_day_break), dayRemainder);
                    }
                    break;
                case ContraceptionInfo.TYPE_PLASTER:
                    // plaster settings
                    if (contraceptionDay < activeDays) {
                        //if the ACTIVE DAYS CONTINUE
                        if (contraceptionDay % contraceptionInfo.getNotificationActiveDayFrequency() != 0){
                            //if the period for ONE PLASTER doesN'T  end
                            int plasterNumber = activeDays / contraceptionInfo.getNotificationActiveDayFrequency();
                            dayRemainder = activeDays - plasterNumber * contraceptionInfo.getNotificationActiveDayFrequency();
                            plasterNumber++;
                            contrInfoStr = String.format(cv.getResources().getString(R.string.cont_plaster_day), plasterNumber, dayRemainder);
                        } else{
                            //if the period for ONE PLASTER END
                            int plasterNumber = activeDays / contraceptionInfo.getNotificationActiveDayFrequency() + 1;
                            contrInfoStr = String.format(cv.getResources().getString(R.string.cont_plaster_end_week), plasterNumber);
                        }
                    } else if (contraceptionDay == activeDays) {
                        // if the ACTIVE DAYS ENDS
                        int plasterNumber = activeDays / contraceptionInfo.getNotificationActiveDayFrequency() + 1;
                        contrInfoStr = String.format(cv.getResources().getString(R.string.cont_plaster_end_period), plasterNumber);
                    } else {
                        // if the BREAK DAYS
                        dayRemainder = activeDays + breakDays - contraceptionDay;
                        contrInfoStr = String.format(cv.getResources().getString(R.string.cont_plaster_day_break), dayRemainder);
                    }
                    break;
                case ContraceptionInfo.TYPE_PILLS:
                    if (contraceptionDay <= activeDays) {
                        // if the ACTYIVE DAYS continue
                        int pillsCount = activeDays + contraceptionInfo.getPlacebo() * breakDays;
                        contrInfoStr = String.format(cv.getResources().getString(R.string.cont_pills_day), contraceptionDay, pillsCount);
                    } else {
                        // if the BREAK DAYS continue
                        dayRemainder = activeDays + breakDays - contraceptionDay;
                        contrInfoStr = String.format(cv.getResources().getString(R.string.cont_pills_day_break), dayRemainder);
                    }
                    break;
                // condition contraceptionDay <= activeDays + 4 will always false !
                case ContraceptionInfo.TYPE_INJECTION:
                    int contraceptionWeek = (int) contraceptionDay/7 + 1;
                    if (activeDays <= contraceptionDay || contraceptionDay <= activeDays + 4 ){
                        // it is time to MAKE A NEW INJECTION
                        dayRemainder = activeDays + 4 - contraceptionDay;
                        contrInfoStr = String.format(cv.getResources().getString(R.string.cont_injection_end_day), contraceptionDay, contraceptionWeek, dayRemainder);
                    } else {
                        //
                        dayRemainder = activeDays - (int)contraceptionDay;
                        int weekRemainder = dayRemainder / 7 + 1;
                        contrInfoStr = String.format(cv.getResources().getString(R.string.cont_injection_day), contraceptionDay, contraceptionWeek, dayRemainder, weekRemainder);
                    }
                    break;
                default:
                    contrInfoStr = "";
            }

            tvContrInfo.setText(contrInfoStr);
            /// set today Date
/*
            Calendar cal = Calendar.getInstance();
            Calendar startDate = Calendar.getInstance();
            startDate.setTimeInMillis(contraceptionInfo.getStartDate());


            Calendar march8h12 = Calendar.getInstance();
            march8h12.set(2015, 2, 8, 12, 0, 0);

            Calendar march10h10 = Calendar.getInstance();
            march10h10.set(2015, 2, 10, 12, 0, 0);

            long timeDiff = march10h10.getTimeInMillis() - march8h12.getTimeInMillis();
            long diffInDays = TimeUnit.DAYS.convert(timeDiff, TimeUnit.MILLISECONDS);
            tvHeader.setText("diff in days: " + diffInDays);
            //
            tvSubHeader.setText("InfoAboutPills");

            */
        }
    }

    public  void setContraceptionInfo (ContraceptionInfo contraceptionInfo){
        this.contraceptionInfo = contraceptionInfo;
    }
}
