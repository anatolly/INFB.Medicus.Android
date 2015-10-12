package com.intrafab.medicus.medJournal.views;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.intrafab.medicus.R;
import com.intrafab.medicus.medJournal.adapters.PeriodCalendarOptionsAdapter;
import com.intrafab.medicus.utils.Logger;

/**
 * Created by Анна on 25.08.2015.
 */
public class ItemPeriodCycleOptionView extends RecyclerView.ViewHolder implements View.OnClickListener{

    public static final int PERIOD = 0;
    //public static final int MENSTRUAL_FLOW = 1;
    //public static final int OVULATION = 2;
    public static final int INTERCOURSE = 1;
    public static final int NOTE = 2;
    public static final int SYMPTOMS = 3;
    public static final int MOODS = 4;
    public static final int TEMPERATURE = 5;
    public static final int AMOUNT_OF_OPTIONS = 6;

    PeriodCalendarOptionsAdapter.OnItemClickListener mListener;

    private TextView tvName;
    private TextView tvDescription;
    private ImageView ivIcon;
    private ImageView ivRightArrow;
    private LinearLayout checkBoxLayout;
    private CheckBox checkBox;

    public ItemPeriodCycleOptionView(View v, int viewType, PeriodCalendarOptionsAdapter.OnItemClickListener listener, int argument) {
        super(v);
        mListener = listener;

        tvName = (TextView)v.findViewById(R.id.textViewName);
        tvDescription = (TextView)v.findViewById(R.id.textViewDescription);
        ivIcon = (ImageView)v.findViewById(R.id.icOption);
        ivRightArrow = (ImageView)v.findViewById(R.id.ivRightArrow);
        checkBoxLayout = (LinearLayout)v.findViewById(R.id.checkBoxContainer);
        checkBox = (CheckBox)v.findViewById(R.id.check_box);

        checkBox.setChecked(argument == 1);
        Logger.d("checkboxFlag", String.valueOf(argument == 1));
        RelativeLayout layout = (RelativeLayout) itemView.findViewById(R.id.rlItemLayout);
        layout.setOnClickListener(this);
        //ivRightArrow.setOnClickListener(this);
        checkBox.setClickable(false);


        switch (viewType){
            /*case PERIOD:
                tvName.setText(R.string.option_menses_start);
                ivRightArrow.setVisibility(View.INVISIBLE);
                ivIcon.setImageResource(R.mipmap.ic1);
                break;
            case MENSTRUAL_FLOW:
                tvName.setText(R.string.option_menstrual_flow);
                ivRightArrow.setVisibility(View.INVISIBLE);
                ivIcon.setImageResource(R.mipmap.ic2);
                break;*/
            case INTERCOURSE:
                tvName.setText(R.string.option_intercourse);
                ivRightArrow.setVisibility(View.INVISIBLE);
                ivIcon.setImageResource(R.mipmap.ic4);
                break;
            case NOTE:
                tvName.setText(R.string.option_note);
                checkBox.setVisibility(View.INVISIBLE);
                ivIcon.setImageResource(R.mipmap.ic5);
                break;
            case SYMPTOMS:
                tvName.setText(R.string.option_symptoms);
                checkBox.setVisibility(View.INVISIBLE);
                ivIcon.setImageResource(R.mipmap.ic6);
                break;
            case MOODS:
                tvName.setText(R.string.option_moods);
                checkBox.setVisibility(View.INVISIBLE);
                ivIcon.setImageResource(R.mipmap.ic7);
                break;
            case TEMPERATURE:
                tvName.setText(R.string.option_temperature);
                ivRightArrow.setVisibility(View.INVISIBLE);
                checkBox.setVisibility(View.INVISIBLE);
                ivIcon.setImageResource(R.mipmap.ic3);
                break;
        }
    }


    @Override
    public void onClick(View view) {
        if (getItemViewType() == INTERCOURSE)
            checkBox.setChecked(!checkBox.isChecked());
        // getViewItem() returns a view's position in list
        mListener.onItemClick(getItemViewType());
    }

}
