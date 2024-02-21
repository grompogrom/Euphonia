package com.euphoiniateam.euphonia

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.euphoiniateam.euphonia.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val historyTitle = resources.getString(R.string.title_history)
            val createTitle = resources.getString(R.string.title_home)
            val settingsTitle = resources.getString(R.string.title_settings)

            if (destination.label in listOf(historyTitle, createTitle, settingsTitle))
                binding.navView.visibility = View.VISIBLE
            else
                binding.navView.visibility = View.GONE

        }
    }
}