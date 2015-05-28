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
        put("default", new StateEntryType(R.mipmap.ic_launcher, Color.parseColor("#5b2796"), Color.parseColor("#5b2796")));
        put("taxi", new StateEntryType(R.mipmap.ic_car_grey2, Color.parseColor("#273849"), Color.parseColor("#ffd980")));
        put("flight", new StateEntryType(R.mipmap.ic_airplane_grey2, Color.parseColor("#273849"), Color.parseColor("#8ad5f0")));
        put("opera", new StateEntryType(R.mipmap.ic_blogger_grey2, Color.parseColor("#273849"), Color.parseColor("#c5e26d")));
        put("checkup", new StateEntryType(R.mipmap.ic_hospital_event_grey2, Color.parseColor("#273849"), Color.parseColor("#ff9494")));
    }};

    public static StateEntryType getDefault() {
        return TYPES.get("default");
    }

    public static final  List<String> STATUSES = Arrays.asList(
            "Accepted", "Canceled", "Changed", "ChangeRequested"
    );
}
