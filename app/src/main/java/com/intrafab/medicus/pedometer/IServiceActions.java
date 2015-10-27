package com.intrafab.medicus.pedometer;

import android.os.Message;

/**
 * Created by Artemiy Terekhov on 22.10.2015.
 * Copyright (c) 2015 Artemiy Terekhov. All rights reserved.
 */
public interface IServiceActions {
    void onStart(Message msg);
    void onPause(Message msg);
    void onResume(Message msg);
    void onStop(Message msg);
}
