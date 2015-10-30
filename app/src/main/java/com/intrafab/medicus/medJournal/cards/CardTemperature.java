package com.intrafab.medicus.medJournal.cards;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.intrafab.medicus.R;
import com.intrafab.medicus.medJournal.activities.ChartActivity;

import uk.co.senab.photoview.Compat;

/**
 * Created by Анна on 09.10.2015.
 */
public class CardTemperature extends RecyclerView.ViewHolder implements View.OnClickListener  {

    EditText etTemperature;
    TextView tvDegree;
    ImageView ivGraph;
    CardView cv;



    public CardTemperature(View view) {
        super(view);
        cv = (CardView)itemView.findViewById(R.id.cv);
        cv.setUseCompatPadding(true);
        etTemperature = (EditText)view.findViewById(R.id.etTemperature);
        tvDegree = (TextView)view.findViewById(R.id.tvDegree);
        ivGraph = (ImageView)view.findViewById(R.id.tvGraph);

        etTemperature.getBackground().setColorFilter(view.getContext().getResources().getColor(R.color.calendar_background_period), PorterDuff.Mode.SRC_ATOP);
        etTemperature.setTextColor(Color.BLACK);
        etTemperature.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return false;
            }
        });

        ivGraph.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tvGraph:
                Intent intent = new Intent(view.getContext(), ChartActivity.class);
                view.getContext().startActivity(intent);
                break;
        }
    }

    public void fillCard(){

    }
}
