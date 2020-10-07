package com.barebone.app

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class BottomNav : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_bottom_nav)
        var persistantlogin = getSharedPreferences(packageName, Context.MODE_PRIVATE)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_dashboard,
                R.id.map,
                R.id.navigation_notifications,
                R.id.settingsFragment
            )
        )
//        setupActionBarWithNavController(navController, appBarConfiguration)
        var toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        toolbar.setupWithNavController(navController, appBarConfiguration)
        toolbar.setBackgroundColor(resources.getColor(R.color.colorAccent))
        toolbar.setTitleTextColor(resources.getColor(R.color.white))

        navView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { navController: NavController, navDestination: NavDestination, bundle: Bundle? ->
            if (navDestination.label == "Maps") {
                toolbar.visibility = View.GONE
            } else {
                toolbar.visibility = View.VISIBLE
            }
        }
    }
}