package com.intrafab.medicus.data;

import android.graphics.Color;

import com.intrafab.medicus.R;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Artemiy Terekhov on 28.04.2015.
 */
public class StateEntryType {

    private int resIconId;
    private int textColor;
    private int backgroundColor;

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public int getResIconId() {
        return resIconId;
    }

    public void setResIconId(int resIconId) {
        this.resIconId = resIconId;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public StateEntryType() {
        this.resIconId = -1;
        this.textColor = Color.parseColor("#273849");
        this.backgroundColor = Color.parseColor("#f4f4f4");
    }

    public StateEntryType(int resIconId, int textColor, int backgroundColor) {
        this.resIconId = resIconId;
        this.textColor = textColor;
        this.backgroundColor = backgroundColor;
    }

    public static final HashMap<String, StateEntryType> TYPES = new HashMap<String, StateEntryType>() {{
        put("default", new StateEntryType(R.drawable.ic_logo, Color.parseColor("#5b2796"), Color.parseColor("#5b2796")));
        put("taxi", new StateEntryType(R.mipmap.ic_taxi, Color.parseColor("#273849"), Color.parseColor("#ffd980")));
        put("flight", new StateEntryType(R.mipmap.ic_airplane, Color.parseColor("#273849"), Color.parseColor("#8ad5f0")));
        put("entertainment", new StateEntryType(R.mipmap.ic_entertainment, Color.parseColor("#273849"), Color.parseColor("#5b2796")));
        put("checkup", new StateEntryType(R.mipmap.ic_doctor, Color.parseColor("#273849"), Color.parseColor("#4CAF50")));
        put("hotel", new StateEntryType(R.mipmap.ic_hotel, Color.parseColor("#273849"), Color.parseColor("#db6c35")));
        put("food", new StateEntryType(R.mipmap.ic_food, Color.parseColor("#273849"), Color.parseColor("#55cfbc")));
    }};

    public static StateEntryType getDefault() {
        return TYPES.get("default");
    }

    public static final  List<String> STATUSES = Arrays.asList(
            "Changed", "ChangeRequested", "Accepted", "Canceled"
            );
}
