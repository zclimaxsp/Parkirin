package com.netra.parkirin.officer.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.netra.parkirin.officer.feature.auth.presentation.LoginScreen
import com.netra.parkirin.officer.feature.dashboard.presentation.DashboardScreen
import com.netra.parkirin.officer.feature.entry.presentation.EntryResultScreen
import com.netra.parkirin.officer.feature.entry.presentation.ParkingEntryScreen
import com.netra.parkirin.officer.feature.exit.presentation.CashPaymentScreen
import com.netra.parkirin.officer.feature.exit.presentation.ParkingExitScreen
import com.netra.parkirin.officer.feature.sessions.presentation.ActiveSessionsScreen

@Composable
fun ParkiRinNavGraph(
    navController: NavHostController,
    isLoggedIn: Boolean,
) {
    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) Destinations.DASHBOARD else Destinations.LOGIN,
    ) {

        composable(Destinations.LOGIN) {
            LoginScreen(
                onLoginClick = {
                    // Launch OAuth2 PKCE flow via AppAuth
                    // On callback → navigate to DASHBOARD
                }
            )
        }

        composable(Destinations.DASHBOARD) {
            DashboardScreen(
                onEntryClick = { navController.navigate(Destinations.PARKING_ENTRY) },
                onExitClick = { navController.navigate(Destinations.PARKING_EXIT) },
            )
        }

        composable(Destinations.PARKING_ENTRY) {
            ParkingEntryScreen(
                onBackClick = { navController.popBackStack() },
                onScanClick = { /* launch CameraX scanner */ },
                onPhotoClick = { /* launch CameraX photo */ },
                onSubmitClick = { _ ->
                    navController.navigate(Destinations.ENTRY_RESULT) {
                        popUpTo(Destinations.PARKING_ENTRY) { inclusive = true }
                    }
                },
            )
        }

        composable(Destinations.ENTRY_RESULT) {
            EntryResultScreen(
                onPrintClick = { /* send to BluetoothPrintService */ },
                onDoneClick = {
                    navController.navigate(Destinations.DASHBOARD) {
                        popUpTo(Destinations.DASHBOARD) { inclusive = true }
                    }
                },
            )
        }

        composable(Destinations.PARKING_EXIT) {
            ParkingExitScreen(
                onBackClick = { navController.popBackStack() },
                onScanClick = { /* launch CameraX scanner */ },
                onSearchClick = { /* call VM */ },
                onCashPaymentClick = {
                    navController.navigate(Destinations.exitPayment("INVOICE_ID_PLACEHOLDER"))
                },
                onQrisPaymentClick = { /* show QR dialog */ },
                onNotifyAbsentClick = { /* call VM */ },
            )
        }

        composable(
            route = Destinations.EXIT_PAYMENT,
            arguments = listOf(navArgument("invoiceId") { type = NavType.StringType }),
        ) { backStackEntry ->
            val invoiceId = backStackEntry.arguments?.getString("invoiceId") ?: ""
            CashPaymentScreen(
                onBackClick = { navController.popBackStack() },
                onConfirmClick = { _ ->
                    navController.navigate(Destinations.EXIT_RESULT) {
                        popUpTo(Destinations.PARKING_EXIT) { inclusive = true }
                    }
                },
            )
        }

        composable(Destinations.SESSIONS) {
            ActiveSessionsScreen()
        }
    }
}
