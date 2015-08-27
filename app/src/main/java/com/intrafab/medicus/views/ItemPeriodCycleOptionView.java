package com.intrafab.medicus.views;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.intrafab.medicus.R;
import com.intrafab.medicus.adapters.PeriodCalendarOptionsAdapter;
import com.intrafab.medicus.utils.Logger;

/**
 * Created by Анна on 25.08.2015.
 */
public class ItemPeriodCycleOptionView extends RecyclerView.ViewHolder implements View.OnClickListener{

    public static final int MEANSES_START = 0;
    public static final int MENSTRUAL_FLOW = 1;
    public static final int OVULATION = 2;
    public static final int INTERCOURSE = 3;
    public static final int TEXT = 4;
    public static final int SYMPTOMS = 5;
    public static final int MOODS = 6;
    public static final int AMOUNT_OF_OPTIONS = 7;

    PeriodCalendarOptionsAdapter.OnItemClickListener mListener;

    private TextView tvName;
    private TextView tvDescription;
    private ImageView ivIcon;
    private ImageView ivRightArrow;
    private LinearLayout checkBoxLayout;
    private CheckBox checkBox;

    public ItemPeriodCycleOptionView(View v, int viewType, PeriodCalendarOptionsAdapter.OnItemClickListener listener, boolean checkBoxFlag) {
        super(v);
        mListener = listener;

        tvName = (TextView)v.findViewById(R.id.textViewName);
        tvDescription = (TextView)v.findViewById(R.id.textViewDescription);
        ivIcon = (ImageView)v.findViewById(R.id.icOption);
        ivRightArrow = (ImageView)v.findViewById(R.id.ivRightArrow);
        checkBoxLayout = (LinearLayout)v.findViewById(R.id.checkBoxContainer);
        checkBox = (CheckBox)v.findViewById(R.id.check_box);

        checkBox.setChecked(checkBoxFlag);
        Logger.d ("checkboxFlag", String.valueOf(checkBoxFlag));
        ivRightArrow.setOnClickListener(this);
        checkBox.setOnClickListener(this);


        switch (viewType){
            case MEANSES_START:
                tvName.setText(R.string.option_menses_start);
                ivRightArrow.setVisibility(View.INVISIBLE);
                ivIcon.setImageResource(R.mipmap.ic1);
                break;
            case MENSTRUAL_FLOW:
                tvName.setText(R.string.option_menstrual_flow);
                ivRightArrow.setVisibility(View.INVISIBLE);
                ivIcon.setImageResource(R.mipmap.ic2);
                break;
            case OVULATION:
                tvName.setText(R.string.option_ovulation);
                ivRightArrow.setVisibility(View.INVISIBLE);
                ivIcon.setImageResource(R.mipmap.ic3);
                break;
            case INTERCOURSE:
                tvName.setText(R.string.option_intercourse);
                ivRightArrow.setVisibility(View.INVISIBLE);
                ivIcon.setImageResource(R.mipmap.ic4);
                break;
            case TEXT:
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
        }
    }


    @Override
    public void onClick(View view) {
        /// function getViewItem returns a view's position in list
        mListener.onItemClick(getItemViewType());
    }

}
