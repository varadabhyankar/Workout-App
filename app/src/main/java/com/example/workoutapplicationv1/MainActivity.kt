package com.example.workoutapplicationv1

import CSVReader
import android.content.Context
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.workoutapplicationv1.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val PREF_CSV_IMPORTED = "csv_imported"
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getPreferences(Context.MODE_PRIVATE)
        val csvImported = sharedPreferences.getBoolean(PREF_CSV_IMPORTED, false)

        if (!csvImported) {
            // CSV data not imported yet, so import it
            val csvReader = CSVReader(this)
            csvReader.readAndInsertCSV()

            // Mark CSV data as imported in shared preferences
            sharedPreferences.edit().putBoolean(PREF_CSV_IMPORTED, true).apply()
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
}