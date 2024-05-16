package com.euphoiniateam.euphonia

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.euphoiniateam.euphonia.databinding.ActivityMainBinding
import com.euphoiniateam.euphonia.domain.models.VKUser
import com.euphoiniateam.euphonia.ui.settings.VKUsersCommand
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKApiCallback
import com.vk.api.sdk.VKTokenExpiredHandler
import com.vk.api.sdk.auth.VKAuthenticationResult
import com.vk.api.sdk.auth.VKScope

// TODO:  Дополнительно
// 1. Удалить мусор Data/Domain
// 2. roboto regular нужен? в android итак roboto
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        val fingerprints = getCertificateFingerprint(this, this.packageName)
//
//        if (fingerprints != null) {
//            for (str in fingerprints)
//                Log.d("AAA", str.toString())
//        }

        val authLauncher = VK.login(this) { result: VKAuthenticationResult ->
            when (result) {
                is VKAuthenticationResult.Success -> {
                    // Toast.makeText(this, "YESYESYESYES", Toast.LENGTH_SHORT).show()
                    VK.addTokenExpiredHandler(tokenTracker)
                    requestUser()
                }
                is VKAuthenticationResult.Failed -> {
                    Toast.makeText(this, "NONONONO", Toast.LENGTH_SHORT).show()
                }
            }
        }
        authLauncher.launch(arrayListOf(VKScope.WALL, VKScope.PHOTOS))

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

    private val tokenTracker = object : VKTokenExpiredHandler {
        override fun onTokenExpired() {
            // token expired
        }
    }

    private fun requestUser() {
        VK.execute(
            VKUsersCommand(),
            object : VKApiCallback<List<VKUser>> {
                override fun success(result: List<VKUser>) {
                    if (result.isNotEmpty()) {
                        val user = result[0]
                        val username = "${user.firstName} ${user.lastName}"
                        Toast.makeText(
                            applicationContext,
                            "Welcome, $username!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                override fun fail(error: Exception) {
                    Log.e("VKRequest", error.toString())
                }
            }
        )
    }
}
