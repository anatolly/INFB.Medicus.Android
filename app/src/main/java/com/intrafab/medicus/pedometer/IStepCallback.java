package com.intrafab.medicus.pedometer;

/**
 * Created by Artemiy Terekhov on 13.10.2015.
 * Copyright (c) 2015 Artemiy Terekhov. All rights reserved.
 */
public interface IStepCallback {
    void onPaceChanged(long value);
    void onCaloriesChanged(double value);
    void onDistanceChanged(float value);
    void onSpeedChanged(float value);
    void onStepChanged(long value);
}
