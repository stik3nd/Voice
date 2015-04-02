package de.paul_woitaschek.audiobook.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

import com.afollestad.materialdialogs.prefs.MaterialListPreference;

import de.paul_woitaschek.audiobook.R;
import de.paul_woitaschek.audiobook.activity.BaseActivity;
import de.paul_woitaschek.audiobook.activity.FolderOverviewActivity;
import de.paul_woitaschek.audiobook.dialog.SeekPreferenceDialog;
import de.paul_woitaschek.audiobook.dialog.SleepPreferenceDialog;
import de.paul_woitaschek.audiobook.utils.PrefsManager;

public class PreferencesFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private PrefsManager prefs;
    private SharedPreferences sp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        prefs = new PrefsManager(getActivity());
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        initValues();
        sp.registerOnSharedPreferenceChangeListener(this);
    }

    private void initValues() {
        // seek pref
        int seekAmount = prefs.getSeekTime();
        String seekSummary = String.valueOf(seekAmount) + " " + getString(R.string.seconds);
        SeekPreferenceDialog seekPreferenceDialog = (SeekPreferenceDialog) findPreference(getString(R.string.pref_key_seek_time));
        seekPreferenceDialog.setSummary(seekSummary);

        // sleep pref
        int sleepAmount = prefs.getSleepTime();
        String sleepSummary = String.valueOf(sleepAmount) + " " + getString(R.string.minutes);
        SleepPreferenceDialog sleepPreferenceDialog = (SleepPreferenceDialog) findPreference(getString(R.string.pref_key_sleep_time));
        sleepPreferenceDialog.setSummary(sleepSummary);

        // folder pref
        Preference folderPreference = findPreference(getString(R.string.pref_key_audiobook_folders));
        folderPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivity(new Intent(getActivity(), FolderOverviewActivity.class));
                return true;
            }
        });

        // theme pref
        MaterialListPreference themePreference = (MaterialListPreference) findPreference(getString(R.string.pref_key_theme));
        String theme = sp.getString(getString(R.string.pref_key_theme), "light");
        String themeSummary;
        switch (theme) {
            case "light":
                themeSummary = getString(R.string.pref_theme_light);
                break;
            case "dark":
                themeSummary = getString(R.string.pref_theme_dark);
                break;
            default:
                throw new AssertionError("This should not have happened. There is no theme for key=" + theme);
        }
        themePreference.setSummary(themeSummary);
        BaseActivity baseActivity = (BaseActivity) getActivity();
        baseActivity.recreateIfThemeChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        sp.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        initValues();
    }
}