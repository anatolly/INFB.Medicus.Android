package com.intrafab.medicus.medJournal.views;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.intrafab.medicus.R;
import com.intrafab.medicus.medJournal.data.PeriodCalendarEntry;
import com.intrafab.medicus.medJournal.adapters.PeriodCalendarOptionsAdapter;
import com.intrafab.medicus.medJournal.data.PeriodDataKeeper;
import com.intrafab.medicus.utils.Logger;

import java.util.Calendar;

/**
 * Created by 1 on 01.09.2015.
 */
public class ItemPeriodOptionView extends RecyclerView.ViewHolder implements View.OnClickListener {

    CheckBox[] droplets = new CheckBox[5];
    PeriodCalendarOptionsAdapter.OnItemClickListener mListener;

    PeriodCalendarEntry mEntry;


    public ItemPeriodOptionView(View v, final PeriodCalendarOptionsAdapter.OnItemClickListener listener, PeriodCalendarEntry entry) {
        super(v);

        mListener = listener;
        mEntry = entry;
       /* RelativeLayout topLayout = (RelativeLayout)v.findViewById(R.id.rlTop);
        topLayout.setClickable(true);
        topLayout.setFocusable(true);
        topLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ViewGroup viewGroup = (ViewGroup) view;
                for (int i = 0; i < viewGroup.getChildCount(); i++) {

                    View viewChild = viewGroup.getChildAt(i);
                    viewChild.setPressed(true);
                    Logger.d("ItemPeriodOptionView", "OnClick");
                }
            }
        });

        LinearLayout textViewContainer = (LinearLayout)v.findViewById(R.id.textview_container);
        textViewContainer.setClickable(true);
        textViewContainer.setOnClickListener(this);
*/
        TextView tvTitle = (TextView)v.findViewById(R.id.textViewTitle);
        TextView tvDescription = (TextView)v.findViewById(R.id.textViewDescription);

        RelativeLayout bottomLayout = (RelativeLayout)v.findViewById(R.id.rlBottom);
        TextView tvMenstrualFlow = (TextView)v.findViewById(R.id.tvMenstFlow);

        ImageView settingButton = (ImageView)v.findViewById(R.id.settingButton);
        settingButton.setOnClickListener(this);
        settingButton.setOnCreateContextMenuListener(contextMenuListener);

        droplets[0] = (CheckBox)v.findViewById(R.id.droplet1);
        droplets[1] = (CheckBox)v.findViewById(R.id.droplet2);
        droplets[2] = (CheckBox)v.findViewById(R.id.droplet3);
        droplets[3] = (CheckBox)v.findViewById(R.id.droplet4);
        droplets[4] = (CheckBox)v.findViewById(R.id.droplet5);

        tvTitle.setClickable(true);
        tvTitle.setOnClickListener(this);
        tvDescription.setClickable(true);
        tvDescription.setOnClickListener(this);

        if (!mEntry.isPeriod()){
            tvTitle.setText(R.string.option_menses_start);
            tvDescription.setText(R.string.tip_period_start);
            //bottomLayout.setVisibility(View.INVISIBLE);

        } else{
            bottomLayout.setVisibility(View.VISIBLE);

            tvTitle.setText(mEntry.getPeriod() +  " " + itemView.getResources().getString(R.string.day_of_cycle) );
            tvDescription.setText(R.string.tip_period_start);

            tvMenstrualFlow.setText(R.string.option_menstrual_flow);
            setMenstrualFlow(mEntry.getMenstrualFlowAmount());

            for (int i=0; i<droplets.length; i++)
                droplets[i].setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.settingButton){
            view.performLongClick();
        }
        else if (view.getId() == R.id.rlTop){
            Logger.d("ItemPeriodOptionView", "on top layout click!!");
            mListener.onItemClick(ItemPeriodCycleOptionView.PERIOD);
        }
        else if(view.getId() == R.id.textViewTitle || view.getId() == R.id.textViewDescription){
            mListener.onItemClick(ItemPeriodCycleOptionView.PERIOD);
        }
        else {
            int i;
            for (i = 0; i < droplets.length; i++)
                if (view == droplets[i])
                    break;
            // new position of menstrualFlow;
            mEntry.setMenstrualFlowAmount(i);

            for (int y = i - 1; y >= 0; y--) {
                droplets[y].setChecked(true);
            }
            for (++i; i < droplets.length; i++)
                droplets[i].setChecked(false);
        }
    }

    private int setMenstrualFlow (int position){
        if (position == -1)
            return -1;
        droplets[position].setChecked(true);
        if (position != 0)
            return setMenstrualFlow(--position);
        return 1;
    }

    private View.OnCreateContextMenuListener contextMenuListener = new View.OnCreateContextMenuListener() {

        @Override
        public void onCreateContextMenu(ContextMenu arg0, View arg1, ContextMenu.ContextMenuInfo arg2) {
            arg0.add(0, 0, 0, "Delete cycle").setOnMenuItemClickListener(mMenuItemClickListener);
            arg0.add(0, 1, 0, "Edit cycle").setOnMenuItemClickListener(mMenuItemClickListener);
        }

    };

    private MenuItem.OnMenuItemClickListener mMenuItemClickListener = new MenuItem.OnMenuItemClickListener() {

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()){
                case 0:
                    deletePeriod();
                    Logger.d("ItemPeriodOptionView", "delete period");
                    return true;
                case 1:
                    break;
                default:
                    break;
            }
            return false;
        }
    };

    private void deletePeriod(){
        PeriodDataKeeper.getInstance().deletePeriod(mEntry);
    }

}
