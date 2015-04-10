package com.intrafab.medicus;

/**
 * Created by Artemiy Terekhov on 10.04.2015.
 */
public class Constants {
    public static final String TAG = Constants.class.getName();

    public static final boolean RELEASE_MODE = false;

    public static class Server {
        public static final String BASE_HOST_NAME = RELEASE_MODE ? "api.medicus.com" : "test.medicus.com";
        public static final String VERSION_API = "v1.0";
        public static final String BASE_HOST_URL = "http://" + BASE_HOST_NAME + "/" + VERSION_API;
    }

    public static class Api {
        public static final String LOGOUT = "/logout.json"; //DELETE
    }
}
