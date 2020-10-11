package com.adeleke.samad.plaboscope.settings

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.adeleke.samad.plaboscope.R
import com.adeleke.samad.plaboscope.services.AlarmHelper


class SettingsFragment: PreferenceFragmentCompat() {

    private lateinit var mPreferences: SharedPreferences
    private val sharedPrefFile = "com.adeleke.samad.plaboscope"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPreferences = requireActivity().getSharedPreferences(sharedPrefFile, MODE_PRIVATE)
        val preference: Preference? = findPreference("night_switch")
        preference!!.setSummary(mPreferences.getString("summary",
                getString(R.string.option_on)));
    }


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        val alarmHelper = context?.let { AlarmHelper(it) }

        mPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val nightPref = findPreference<Preference>("night_switch")
        nightPref!!.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { preference, newValue ->
                    if (newValue as Boolean) {
                        val preferencesEditor = mPreferences.edit()
                        preferencesEditor.putString("summary",
                                getString(R.string.option_on)).apply()
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                        preference.setSummary(R.string.option_off)
                        val preferencesEditor = mPreferences.edit()
                        preferencesEditor.putString("summary",
                                getString(R.string.option_off)).apply()

                    }
                    true
                }

        val notifPref = findPreference<Preference>("notif_list")
        notifPref!!.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { preference, newValue ->
                    when (newValue as String) {
                        "disabled" -> alarmHelper!!.cancelAlarms()
                        else -> alarmHelper!!.setAlarm(newValue)
                    }
                    true
                }

    }


}