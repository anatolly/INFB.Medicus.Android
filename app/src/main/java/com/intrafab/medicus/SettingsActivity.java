package com.intrafab.medicus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.intrafab.medicus.utils.Logger;
import com.jenzz.materialpreference.Preference;


/**
 * Created by Artemiy Terekhov on 10.06.2015.
 */
public class SettingsActivity extends BaseActivity {
    public static final String TAG = SettingsActivity.class.getName();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().getThemedContext();

        getSupportActionBar().setTitle(R.string.settings_screen_header);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorLightTextColorPrimary));
        showActionBar();
        setActionBarIcon(R.mipmap.ic_action_back);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.content, new SettingsFragment())
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_settings;
    }

    public static class SettingsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);

            Preference prefLanguage = (Preference) findPreference("prefLanguage");
            prefLanguage.setOnPreferenceClickListener(new android.preference.Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(android.preference.Preference preference) {
                    String key = preference.getKey();
                    if (key.equals("prefLanguage")) {
                        showDialogLanguage(SettingsFragment.this.getActivity());
                        return true;
                    }
                    return false;
                }
            });
        }
    }

    public static void launch(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, SettingsActivity.class);

        ActivityOptionsCompat options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(activity);

        ActivityCompat.startActivityForResult(activity, intent, requestCode, options.toBundle());
    }

    private static void showDialogLanguage(final Activity context) {
        new MaterialDialog.Builder(context)
                .title(R.string.dialog_select_language_header)
                .items(R.array.pref_list_language)
                .itemsCallbackSingleChoice(mCurrentLanguageIndex, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
//                        setLanguage(context, which);
                        Logger.e("SettingsActivity", "onSelection which: " + which);
                        setCurrentLanguage(context, which);
//                        restartActivity(context);
                        return true; // allow selection
                    }
                })
                .positiveText(R.string.dialog_language_button_choose)
                .show();
    }

//    private static void setLanguage(Activity context, int index) {
//        String[] mLangArray = context.getResources().getStringArray(R.array.pref_list_language_values);
//
//        Locale myLocale = new Locale(mLangArray[index]);
//        Resources res = context.getBaseContext().getResources();
//        DisplayMetrics dm = res.getDisplayMetrics();
//        Configuration conf = res.getConfiguration();
//        conf.locale = myLocale;
//
//        res.updateConfiguration(conf, dm);
//        context.getBaseContext().getResources().updateConfiguration(
//                context.getBaseContext().getResources().getConfiguration(),
//                context.getBaseContext().getResources().getDisplayMetrics());
//    }
}
