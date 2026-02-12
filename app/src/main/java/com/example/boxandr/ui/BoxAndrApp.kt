package com.bbox.boxlife.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bbox.boxlife.BoxAndrApplication
import com.bbox.boxlife.ui.theme.BoxAndrTheme

@Composable
fun BoxAndrApp() {
    BoxAndrTheme {
        val navController = rememberNavController()
        val context = LocalContext.current
        val app = context.applicationContext as BoxAndrApplication
        val viewModel: ExpensesViewModel = viewModel(
            factory = ExpensesViewModelFactory(app.repository)
        )

        NavHost(navController = navController, startDestination = "home") {
            composable("home") {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToAddExpense = { navController.navigate("add_expense") },
                    onNavigateToStatistics = { navController.navigate("statistics") }
                )
            }
            composable("add_expense") {
                AddExpenseScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable("statistics") {
                StatisticsScreen(viewModel = viewModel)
            }
        }
    }
}
