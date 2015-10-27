package com.intrafab.medicus.pedometer;

/**
 * Created by Artemiy Terekhov on 22.10.2015.
 * Copyright (c) 2015 Artemiy Terekhov. All rights reserved.
 */
public class ServiceEvent {
    private static final String TAG = ServiceEvent.class.getName();

    public static final String NOTIFY_MESSAGE = TAG.concat("notify_message");
    public static final String PACE_VALUE = TAG.concat("pace_value");
    public static final String CALORIES_VALUE = TAG.concat("calories_value");
    public static final String DISTANCE_VALUE = TAG.concat("distance_value");
    public static final String SPEED_VALUE = TAG.concat("speed_value");
    public static final String STEP_VALUE = TAG.concat("step_value");

    public static final int START = 1;
    public static final int STOP = 2;
    public static final int PAUSE = 3;
    public static final int RESUME = 4;

    public static final int PACE_CHANGED = 100;
    public static final int CALORIES_CHANGED = 101;
    public static final int DISTANCE_CHANGED = 102;
    public static final int SPEED_CHANGED = 103;
    public static final int STEP_CHANGED = 104;
    public static final int START_DETECTOR = 105;
    public static final int STOP_DETECTOR = 106;
}
