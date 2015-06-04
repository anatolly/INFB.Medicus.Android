package com.intrafab.medicus;

/**
 * Created by Artemiy Terekhov on 10.04.2015.
 */
public class Constants {
    public static final String TAG = Constants.class.getName();

    public static final boolean RELEASE_MODE = false;

    public static class Prefs {
        public static final String PREF_PARAM_STORAGE_TRIP = "pref_param_store_trip";
        public static final String PREF_PARAM_STORAGE = "pref_param_storage";
        public static final String PREF_PARAM_STATE_ENTRIES = "pref_param_state_entries";
    }

    public static class Extras {
        public static final String PARAM_SUPPORTED_FORMAT = "param_supported_format";
        public static final String PARAM_INTERNET_AVAILABLE = "param_internet_available";
        public static final String PARAM_FILE_NOT_FOUND = "param_file_not_found";
        public static final String PARAM_UNKNOWN_ERROR = "param_unknown_error";
    }
}
