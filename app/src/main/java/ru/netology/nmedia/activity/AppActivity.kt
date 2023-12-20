package ru.netology.nmedia.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
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
import androidx.core.view.MenuProvider
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.viewmodel.AuthViewModel

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

        val authViewModel by viewModels<AuthViewModel>()
        var currentMenuProvider: MenuProvider? = null
        authViewModel.data.observe(this) {
            currentMenuProvider?.let (::removeMenuProvider)
            addMenuProvider(object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.menu_auth, menu)
                    val authenticated = authViewModel.isAuthenticated
                    menu.setGroupVisible(R.id.authorized, authenticated)
                    menu.setGroupVisible(R.id.unauthorized, !authenticated)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                    when (menuItem.itemId) {
                        R.id.signUp -> {
                            AppAuth.getInstance().setAuth(5, "x-token")
                            true
                        }

                        R.id.signIn -> {

                            //вместо 5 надо будет вписывать то что пришлет сервер
                            // и делать это во вьюмодели или репозитории
                            AppAuth.getInstance().setAuth(5, "x-token")
                            true
                        }

                        R.id.logout -> {
                            AppAuth.getInstance().clearAuth()
                            true
                        }

                        else -> false
                    }
            }.also {
                   currentMenuProvider = it
            } ,this)

        }
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
