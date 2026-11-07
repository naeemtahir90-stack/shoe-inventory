package com.shoestore.inventory.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.shoestore.inventory.ui.screens.AddEditShoeScreen
import com.shoestore.inventory.ui.screens.DailyReportScreen
import com.shoestore.inventory.ui.screens.DetailScreen
import com.shoestore.inventory.ui.screens.HomeScreen
import com.shoestore.inventory.ui.viewmodel.InventoryViewModel

@Composable
fun InventoryNavGraph(viewModel: InventoryViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                viewModel = viewModel,
                onNavigateToAddShoe = { navController.navigate("add_shoe") },
                onNavigateToDetail = { shoeId -> navController.navigate("detail/$shoeId") },
                onNavigateToDailyReport = { navController.navigate("daily_report") }
            )
        }

        composable(
            route = "detail/{shoeId}",
            arguments = listOf(navArgument("shoeId") { type = NavType.LongType })
        ) { backStackEntry ->
            val shoeId = backStackEntry.arguments?.getLong("shoeId") ?: 0L
            DetailScreen(
                shoeId = shoeId,
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { id -> navController.navigate("edit_shoe/$id") }
            )
        }

        composable("add_shoe") {
            AddEditShoeScreen(
                shoeIdToEdit = null,
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "edit_shoe/{shoeId}",
            arguments = listOf(navArgument("shoeId") { type = NavType.LongType })
        ) { backStackEntry ->
            val shoeId = backStackEntry.arguments?.getLong("shoeId")
            AddEditShoeScreen(
                shoeIdToEdit = shoeId,
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("daily_report") {
            DailyReportScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
