package com.rsschool.myapplication.loyaltycards

import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.rsschool.myapplication.loyaltycards.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
    private var navController: NavController? = null
    private var binding: ActivityMainBinding? = null

    private val themePreferenceKey by lazy { getString(R.string.theme_preference_key) }

    @Inject
    lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadUserPrefs()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        initNavigation()
    }

    private fun initNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container)

        val bottomNavView = binding?.bottomNavView
        navController = navHostFragment?.findNavController()
        navController?.let {
            val appBarConfiguration = AppBarConfiguration(it.graph)
            setupActionBarWithNavController(it, appBarConfiguration)
            bottomNavView?.setupWithNavController(it)
        }
    }

    private fun loadUserPrefs() {
        preferences.getString(themePreferenceKey, "")
            ?.let {
                updateTheme(it)
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController?.navigateUp() == true || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        navController?.addOnDestinationChangedListener { _, destination, _ ->
            binding?.bottomNavView?.isVisible = destination.id != R.id.addCardFragment
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key == themePreferenceKey) {
            sharedPreferences.getString(
                themePreferenceKey,
                getString(R.string.follow_system_value)
            )?.let {
                updateTheme(it)
            }
        }
    }

    private fun updateTheme(key: String) {
        when (key) {
            getString(R.string.light_theme_value) -> setNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            getString(R.string.dark_theme_value) -> setNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            getString(R.string.follow_system_value) -> setNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

    private fun setNightMode(mode: Int) {
        AppCompatDelegate.setDefaultNightMode(mode)
    }
}
