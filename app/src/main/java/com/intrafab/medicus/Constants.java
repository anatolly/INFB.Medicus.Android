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
        public static final String PREF_PARAM_PERIOD_CAL_ENTRIES = "pref_param_mens_cal_entries";
        public static final String PREF_PARAM_PERIOD_CYCLE_ENTRIES = "pref_param_per_cycle_entries";
        public static final String PREF_PARAM_CONTRACEPTION_INFO = "pref_param_contraception_info";
    }

    public static class Extras {
        public static final String PARAM_SUPPORTED_FORMAT = "param_supported_format";
        public static final String PARAM_INTERNET_AVAILABLE = "param_internet_available";
        public static final String PARAM_FILE_NOT_FOUND = "param_file_not_found";
        public static final String PARAM_UNKNOWN_ERROR = "param_unknown_error";
        public static final String PARAM_TOKEN = "param_token";
        public static final String PARAM_USER_DATA = "param_user_data";
        public static final String PARAM_SESSID = "param_sessid";
        public static final String PARAM_SESSNAME = "param_sessname";
        public static final String PARAM_ACTIVE_ORDER = "param_active_order";
    }

    public static class Numeric {
        public static final int dayToMillis = 86400000;
        public static final int dayToSec = 86400;
        public static final String[] pillsActiveDays = {"21","22","23","24","25","26","28","35","42","63","84"};
        public static final String[] pillsBreakDays = {"1","2","3","4","5","6","7", "8"};
        public static final String[] injectionsActiveWeeks = {"8","9","10","11","12","13","14"};
        public static final String[] intervalNotif = {"5","10","15","20","30","45","60", "90", "120"};
    }
}
