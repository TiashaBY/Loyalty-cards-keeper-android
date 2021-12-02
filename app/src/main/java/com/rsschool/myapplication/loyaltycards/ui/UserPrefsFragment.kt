package com.rsschool.myapplication.loyaltycards.ui

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.rsschool.myapplication.loyaltycards.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class UserPrefsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    @Inject
    lateinit var preferences: SharedPreferences

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        val themePreferenceKey = getString(R.string.theme_preference_key)
        if (key == themePreferenceKey) {
            when (sharedPreferences.getString(themePreferenceKey, "")) {
                getString(R.string.light_theme_value) -> setTheme(AppCompatDelegate.MODE_NIGHT_NO)
                getString(R.string.dark_theme_value) -> setTheme(AppCompatDelegate.MODE_NIGHT_YES)
                getString(R.string.follow_system_value) -> setTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
    }

    private fun setTheme(mode: Int) {
        AppCompatDelegate.setDefaultNightMode(mode)
    }
}
