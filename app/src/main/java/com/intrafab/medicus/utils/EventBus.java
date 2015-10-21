package com.intrafab.medicus.utils;

import com.squareup.otto.Bus;

/**
 * Created by Artemiy Terekhov on 21.10.2015.
 * Copyright (c) 2015 Artemiy Terekhov. All rights reserved.
 */
public class EventBus {
    private static class EventBusHolder {
        private final static Bus instance = new Bus();
    }

    public static Bus getInstance() {
        return EventBusHolder.instance;
    }
}
