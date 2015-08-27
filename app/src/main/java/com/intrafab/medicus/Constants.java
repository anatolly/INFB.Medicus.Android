package com.intrafab.medicus;

/**
 * Created by Artemiy Terekhov on 10.04.2015.
 */
public class Constants {
    public static final String TAG = Constants.class.getName();

    public static final boolean RELEASE_MODE = false;

    public static final String EMAIL_PATTERN = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Za-z]{2,4}$";
    public static final String PASSWORD_PATTERN = "((?!\\s)\\A)(\\s|(?<!\\s)\\S){6,20}\\Z";

    public static class Prefs {
        public static final String PREF_PARAM_STORAGE_TRIP = "pref_param_store_trip";
        public static final String PREF_PARAM_STORAGE = "pref_param_storage";
        public static final String PREF_PARAM_STATE_ENTRIES = "pref_param_state_entries";
        public static final String PREF_PARAM_ME = "pref_param_me";
        public static final String PREF_PARAM_ORDER = "pref_param_order";
        public static final String PREF_PARAM_MENS_CAL_ENTRIES = "pref_param_mens_cal_entries";
    }

    public static class Extras {
        public static final String PARAM_SUPPORTED_FORMAT = "param_supported_format";
        public static final String PARAM_INTERNET_AVAILABLE = "param_internet_available";
        public static final String PARAM_FILE_NOT_FOUND = "param_file_not_found";
        public static final String PARAM_UNKNOWN_ERROR = "param_unknown_error";
        public static final String PARAM_TOKEN = "param_token";
        public static final String PARAM_USER_DATA = "param_user_data";
        public static final String PARAM_ACTIVE_ORDER = "param_active_order";
    }
}
