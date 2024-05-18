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
import com.vk.sdk.api.friends.FriendsService
import com.vk.sdk.api.friends.dto.FriendsGetFieldsResponseDto
import com.vk.sdk.api.users.dto.UsersFieldsDto

// TODO:  Дополнительно
// 1. Удалить мусор Data/Domain
// 2. roboto regular нужен? в android итак roboto
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var user: VKUser? = null
    private var friends: List<VKUser>? = null

    val authLauncher = VK.login(this) { result: VKAuthenticationResult ->
        when (result) {
            is VKAuthenticationResult.Success -> {
                VK.addTokenExpiredHandler(tokenTracker)
                requestUser(
                    onSuccess = { user = it!! },
                    onFail = {}
                )
                requestFriends(
                    onSuccess = { friends = it!! },
                    onFail = {}
                )
            }

            is VKAuthenticationResult.Failed -> {
                Toast.makeText(this, "NONONONO", Toast.LENGTH_SHORT).show()
            }
        }
    }

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

    fun requestUser(
        onSuccess: (VKUser?) -> Unit,
        onFail: () -> Unit
    ): VKUser? {
        user?.let {
            return user
        }
        var user: VKUser? = null
        VK.execute(
            VKUsersCommand(),
            object : VKApiCallback<List<VKUser>> {
                override fun success(result: List<VKUser>) {
                    if (!isFinishing && result.isNotEmpty()) {
                        user = result[0]
                        val username = "${user!!.firstName} ${user!!.lastName}"
                        Toast.makeText(
                            this@MainActivity,
                            "Welcome, $username!",
                            Toast.LENGTH_SHORT
                        ).show()
                        onSuccess(user)
                    }
                }
                override fun fail(error: Exception) {
                    onFail()
                    Log.e("userRequest", error.toString())
                }
            }
        )
        return user
    }

    fun requestFriends(
        onSuccess: (List<VKUser>?) -> Unit,
        onFail: () -> Unit
    ): List<VKUser>? {
        friends?.let {
            return friends
        }
        var friends: List<VKUser>? = null
        val fields = listOf(UsersFieldsDto.PHOTO_200, UsersFieldsDto.BDATE)
        VK.execute(
            FriendsService().friendsGet(fields = fields),
            object : VKApiCallback<FriendsGetFieldsResponseDto> {
                override fun success(result: FriendsGetFieldsResponseDto) {
                    val friendsRequest = result.items
                    if (!isFinishing && friendsRequest.isNotEmpty()) {
                        friends = friendsRequest.map { friend ->
                            VKUser(
                                id = friend.id.value,
                                firstName = friend.firstName ?: "",
                                lastName = friend.lastName ?: "",
                                birthDate = friend.bdate ?: "",
                                photo = friend.photo200 ?: "",
                            )
                        }
                        onSuccess(friends)
                    }
                }
                override fun fail(error: Exception) {
                    onFail()
                    Log.e("friendsRequest", error.toString())
                }
            }
        )
        return friends
    }

    private fun showFriends(friends: List<VKUser>) {
        Log.d("friend", friends.size.toString())
        for (friend in friends) {
            Log.d("friend", friend.id.toString())
            Log.d("friend", friend.firstName)
            Log.d("friend", friend.lastName)
            Log.d("friend", friend.birthDate)
            Log.d("friend", friend.photo)
        }
    }

    private fun showUser(user: VKUser) {
        Log.d("user", user.id.toString())
        Log.d("user", user.firstName)
        Log.d("user", user.lastName)
        Log.d("user", user.birthDate)
        Log.d("user", user.photo)
    }
}
