package com.rsschool.myapplication.loyaltycards

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.rsschool.myapplication.loyaltycards.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var navController: NavController? = null
    private var binding : ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

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

/*    override fun onResume() {
        super.onResume()
        navController = findNavController(R.id.nav_host_fragment_container)
            .apply { setGraph(R.navigation.nav_graph) }
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }*/

    //MENU STUFF
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.bottom_menu, menu)
        return true
    }


    override fun onSupportNavigateUp(): Boolean {
        return navController?.navigateUp() == true || super.onSupportNavigateUp()
    }
}