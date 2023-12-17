package ru.netology.nmedia.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.messaging.FirebaseMessaging
import ru.netology.nmedia.activity.EditPostFragment.Companion.edit
import ru.netology.nmedia.activity.NewPostFragment.Companion.textArg
import ru.netology.nmedia.databinding.ActivityAppBinding
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import ru.netology.nmedia.R

class AppActivity : AppCompatActivity() {
    lateinit var appBarConfiguration: AppBarConfiguration
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityAppBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestNotificationsPermission()

        intent?.let {
            if (it.action != Intent.ACTION_SEND) {
                return@let
            }

            val text = it.getStringExtra(Intent.EXTRA_TEXT)
            if (text?.isNotBlank() != true) {
                return@let
            }
            intent.removeExtra(Intent.EXTRA_TEXT)
            findNavController(R.id.nav_host_fragment)
                .navigate(R.id.action_feedFragment_to_newPostFragment, Bundle().apply {
                    textArg = text
                }
                )
            findNavController(R.id.nav_host_fragment).navigate(
                R.id.action_feedFragment_to_editPostFragment,
                Bundle().apply {
                    edit = text
                }
            )
        }
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

            lifecycleScope
            checkGoogleApiAvailability()
        }
        private fun requestNotificationsPermission() {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                return
            }

            val permission = Manifest.permission.POST_NOTIFICATIONS

            if (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
                return
            }

            requestPermissions(arrayOf(permission), 1)
        }

        private fun checkGoogleApiAvailability() {
            with(GoogleApiAvailability.getInstance()) {
                val code = isGooglePlayServicesAvailable(this@AppActivity)
                if (code == ConnectionResult.SUCCESS) {
                    return@with
                }
                if (isUserResolvableError(code)) {
                    getErrorDialog(this@AppActivity, code, 9000)?.show()
                    return
                }
                Toast.makeText(
                    this@AppActivity,
                    R.string.google_play_unavailable,
                    Toast.LENGTH_LONG
                )
                    .show()
            }

            FirebaseMessaging.getInstance().token.addOnSuccessListener {
                println(it)
            }
        }

            override fun onSupportNavigateUp(): Boolean {
                val navController = findNavController(R.id.nav_host_fragment)
                return navController.navigateUp(appBarConfiguration)
                        || super.onSupportNavigateUp()

        }
    }
