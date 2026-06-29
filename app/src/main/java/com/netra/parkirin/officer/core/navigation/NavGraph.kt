package com.netra.parkirin.officer.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.remember
import com.netra.parkirin.officer.feature.auth.presentation.LoginScreen
import com.netra.parkirin.officer.feature.dashboard.presentation.DashboardScreen
import com.netra.parkirin.officer.feature.entry.presentation.EntryResultScreen
import com.netra.parkirin.officer.feature.entry.presentation.ParkingEntryScreen
import com.netra.parkirin.officer.feature.entry.data.EntryViewModel
import com.netra.parkirin.officer.feature.exit.presentation.CashPaymentScreen
import com.netra.parkirin.officer.feature.exit.presentation.ParkingExitScreen
import com.netra.parkirin.officer.feature.sessions.presentation.ActiveSessionsScreen
import com.netra.parkirin.officer.feature.exit.presentation.ExitResultScreen
import com.netra.parkirin.officer.feature.exit.data.ExitViewModel
import com.netra.parkirin.officer.feature.camera.CameraCaptureScreen
import com.netra.parkirin.officer.feature.history.presentation.HistoryScreen
import androidx.compose.runtime.LaunchedEffect



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
                    navController.navigate(Destinations.DASHBOARD) {
                        popUpTo(Destinations.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Destinations.DASHBOARD) {
            DashboardScreen(
                onEntryClick = { navController.navigate(Destinations.PARKING_ENTRY) },
                onExitClick = { navController.navigate(Destinations.PARKING_EXIT) },
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        composable(Destinations.PARKING_ENTRY) {
            val viewModel: EntryViewModel = hiltViewModel()

            // Observe camera results
            val savedStateHandle = it.savedStateHandle
            val cameraResult = savedStateHandle.get<String>("camera_result")
            val cameraMode = savedStateHandle.get<String>("camera_mode")

            LaunchedEffect(cameraResult) {
                cameraResult?.let { result ->
                    if (cameraMode == "OCR") {
                        viewModel.onPlateNumberChanged(result)
                    } else if (cameraMode == "PHOTO") {
                        viewModel.onPhotoCaptured(result)
                    }
                    savedStateHandle.remove<String>("camera_result")
                    savedStateHandle.remove<String>("camera_mode")
                }
            }

            ParkingEntryScreen(
                onBackClick = { navController.popBackStack() },
                onScanClick = {
                    navController.navigate(Destinations.camera("OCR"))
                },
                onPhotoClick = {
                    navController.navigate(Destinations.camera("PHOTO"))
                },
                onSubmitSuccess = {
                    navController.navigate(Destinations.ENTRY_RESULT) {
                        popUpTo(Destinations.PARKING_ENTRY) { inclusive = false }
                    }
                },
                viewModel = viewModel
            )
        }

        composable(
            route = Destinations.CAMERA,
            arguments = listOf(navArgument("mode") { type = NavType.StringType })
        ) { backStackEntry ->
            val mode = backStackEntry.arguments?.getString("mode") ?: "PHOTO"
            CameraCaptureScreen(
                mode = mode,
                onResult = { result ->
                    navController.previousBackStackEntry?.savedStateHandle?.set("camera_result", result)
                    navController.previousBackStackEntry?.savedStateHandle?.set("camera_mode", mode)
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Destinations.ENTRY_RESULT) {
            val parentEntry = remember(it) {
                navController.getBackStackEntry(Destinations.PARKING_ENTRY)
            }
            val viewModel: EntryViewModel = hiltViewModel(parentEntry)
            EntryResultScreen(
                onPrintClick = { /* TODO: BluetoothPrintService */ },
                onDoneClick = {
                    navController.navigate(Destinations.DASHBOARD) {
                        popUpTo(Destinations.DASHBOARD) { inclusive = true }
                    }
                },
                viewModel = viewModel
            )
        }

        composable(Destinations.PARKING_EXIT) {
            val viewModel: ExitViewModel = hiltViewModel()

            // Observe camera results for Exit too
            val savedStateHandle = it.savedStateHandle
            val cameraResult = savedStateHandle.get<String>("camera_result")
            val cameraMode = savedStateHandle.get<String>("camera_mode")

            LaunchedEffect(cameraResult) {
                cameraResult?.let { result ->
                    if (cameraMode == "OCR") {
                        viewModel.onPlateNumberChanged(result)
                    }
                    savedStateHandle.remove<String>("camera_result")
                    savedStateHandle.remove<String>("camera_mode")
                }
            }

            ParkingExitScreen(
                onBackClick = { navController.popBackStack() },
                onScanClick = {
                    navController.navigate(Destinations.camera("OCR"))
                },
                onExitSuccess = { invoiceId ->
                    navController.navigate(Destinations.exitPayment(invoiceId)) {
                        popUpTo(Destinations.PARKING_EXIT) { inclusive = false }
                    }
                },
                viewModel = viewModel
            )
        }

        composable(
            route = Destinations.EXIT_PAYMENT,
            arguments = listOf(navArgument("invoiceId") { type = NavType.StringType }),
        ) { backStackEntry ->
            val invoiceId = backStackEntry.arguments?.getString("invoiceId") ?: ""
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Destinations.PARKING_EXIT)
            }
            val viewModel: ExitViewModel = hiltViewModel(parentEntry)
            CashPaymentScreen(
                invoiceId = invoiceId,
                onBackClick = { navController.popBackStack() },
                onPaymentSuccess = {
                    navController.navigate(Destinations.EXIT_RESULT) {
                        // 🚀 Pop EXIT_PAYMENT tapi PARKING_EXIT tetep ada biar ViewModel-nya gak mati meks!
                        popUpTo(Destinations.EXIT_PAYMENT) { inclusive = true }
                    }
                },
                viewModel = viewModel
            )
        }

        composable(Destinations.EXIT_RESULT) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Destinations.PARKING_EXIT)
            }
            val viewModel: ExitViewModel = hiltViewModel(parentEntry)

            ExitResultScreen(
                onPrintClick = { /* TODO: BluetoothPrintService */ },
                onBackClick = {
                    viewModel.clearResult()
                    viewModel.clearPaymentResult()
                    navController.navigate(Destinations.DASHBOARD) {
                        popUpTo(Destinations.DASHBOARD) { inclusive = true }
                    }
                },
                onDoneClick = {
                    viewModel.clearResult()
                    viewModel.clearPaymentResult()
                    navController.navigate(Destinations.DASHBOARD) {
                        popUpTo(Destinations.DASHBOARD) { inclusive = true }
                    }
                },
                viewModel = viewModel
            )
        }

        composable(Destinations.SESSIONS) {
            ActiveSessionsScreen(
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        composable(Destinations.HISTORY) {
            HistoryScreen(
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
