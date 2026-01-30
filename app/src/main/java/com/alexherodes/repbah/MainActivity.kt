package com.alexherodes.repbah

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.alexherodes.repbah.data.Booking
import com.alexherodes.repbah.ui.DashboardScreen
import com.alexherodes.repbah.ui.DetailsScreen
import com.alexherodes.repbah.ui.LoginScreen
import com.alexherodes.repbah.ui.ScheduleScreen
import com.alexherodes.repbah.viewmodel.DashboardViewModel
import com.alexherodes.repbah.viewmodel.DetailsViewModel
import com.alexherodes.repbah.viewmodel.LoginViewModel
import com.alexherodes.repbah.viewmodel.ScheduleViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Request Notification Permissions (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
        }

        // 2. Schedule Background Worker for Notifications (Every 15 minutes)
        val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(15, TimeUnit.MINUTES).build()
        WorkManager.getInstance(this).enqueue(workRequest)

        setContent {
            val navController = rememberNavController()
            val auth = FirebaseAuth.getInstance()

            // Determine start screen
            val startDestination = if (auth.currentUser != null) "dashboard" else "login"

            NavHost(navController = navController, startDestination = startDestination) {

                // 1. Login Screen
                composable("login") {
                    val loginViewModel: LoginViewModel = viewModel()
                    LoginScreen(
                        viewModel = loginViewModel,
                        onLoginSuccess = {
                            navController.navigate("dashboard") { popUpTo("login") { inclusive = true } }
                        }
                    )
                }

                // 2. Dashboard Screen
                composable("dashboard") {
                    val dashboardViewModel: DashboardViewModel = viewModel()
                    DashboardScreen(
                        viewModel = dashboardViewModel,
                        onLogout = {
                            auth.signOut()
                            navController.navigate("login") { popUpTo("dashboard") { inclusive = true } }
                        },
                        // NEW: Link to the Schedule/Calendar Screen
                        onScheduleClick = {
                            navController.navigate("schedule")
                        },
                        onBookingClick = { booking ->
                            // Convert booking to text (JSON) so we can pass it to the next screen
                            val json = URLEncoder.encode(Gson().toJson(booking), StandardCharsets.UTF_8.toString())
                            navController.navigate("details/$json")
                        }
                    )
                }

                // 3. Details Screen
                composable("details/{bookingJson}") { backStackEntry ->
                    val bookingJson = backStackEntry.arguments?.getString("bookingJson")
                    // Convert text back to booking object
                    val booking = Gson().fromJson(
                        URLDecoder.decode(bookingJson, StandardCharsets.UTF_8.toString()),
                        Booking::class.java
                    )

                    val detailsViewModel: DetailsViewModel = viewModel()
                    detailsViewModel.setBooking(booking)

                    DetailsScreen(
                        viewModel = detailsViewModel,
                        onBack = { navController.popBackStack() }
                    )
                }

                // 4. Schedule Management Screen
                composable("schedule") {
                    val scheduleViewModel: ScheduleViewModel = viewModel()
                    ScheduleScreen(
                        viewModel = scheduleViewModel,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}