package com.minor.expensetracker.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.border
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.minor.expensetracker.ui.screens.auth.AuthScreen
import com.minor.expensetracker.ui.screens.home.HomeScreen
import com.minor.expensetracker.ui.screens.profile.ProfileScreen
import com.minor.expensetracker.ui.screens.stats.StatsScreen
import com.minor.expensetracker.ui.screens.transaction.AddTransactionSheet
import com.minor.expensetracker.ui.theme.*
import com.minor.expensetracker.ui.viewmodel.ProfileViewModel
import com.minor.expensetracker.ui.viewmodel.TransactionViewModel
import com.minor.expensetracker.ui.components.AnimatedMeshBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    transactionViewModel: TransactionViewModel = viewModel(),
    profileViewModel: ProfileViewModel = viewModel()
) {
    val navController = rememberNavController()
    var showAddTransaction by remember { mutableStateOf(false) }

    // Collect states
    val isLoggedIn by profileViewModel.isLoggedIn.collectAsStateWithLifecycle()
    val userName by profileViewModel.userName.collectAsStateWithLifecycle()
    val userEmail by profileViewModel.userEmail.collectAsStateWithLifecycle()
    val isDarkMode by profileViewModel.isDarkMode.collectAsStateWithLifecycle()
    val creditScore by profileViewModel.creditScore.collectAsStateWithLifecycle()

    val transactions by transactionViewModel.transactions.collectAsStateWithLifecycle()
    val summary by transactionViewModel.monthlySummary.collectAsStateWithLifecycle()
    val timeFilter by transactionViewModel.timeFilter.collectAsStateWithLifecycle()
    val categoryBreakdown by transactionViewModel.categoryBreakdown.collectAsStateWithLifecycle()
    val totalExpenses by transactionViewModel.totalExpensesAllTime.collectAsStateWithLifecycle()
    val totalIncome by transactionViewModel.totalIncomeAllTime.collectAsStateWithLifecycle()
    val monthlyBarData by transactionViewModel.monthlyBarData.collectAsStateWithLifecycle()
    val favoritesCount by transactionViewModel.favoritesCount.collectAsStateWithLifecycle()
    val showFavoritesOnly by transactionViewModel.showFavoritesOnly.collectAsStateWithLifecycle()

    // Auth check
    if (!isLoggedIn) {
        AuthScreen(
            onLogin = { name, email ->
                profileViewModel.login(name, email)
            }
        )
        return
    }
    
    // Bottom sheet for adding transaction
    if (showAddTransaction) {
        val sheetBg = if (isDarkMode) Color(0xFF141416).copy(alpha = 0.95f)
                      else MaterialTheme.colorScheme.surface
        ModalBottomSheet(
            onDismissRequest = { showAddTransaction = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = sheetBg,
            shape = BottomSheetShape,
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .width(40.dp)
                        .height(4.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f))
                )
            }
        ) {
            AddTransactionSheet(
                onDismiss = { showAddTransaction = false },
                onSave = { amount, type, category, note, date ->
                    transactionViewModel.addTransaction(amount, type, category, note, date)
                }
            )
        }
    }

    AnimatedMeshBackground {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                // Theme-aware bottom bar gradient
                val barGradient = if (isDarkMode) {
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xFF0C0C14).copy(alpha = 0.7f),
                            Color(0xFF0C0C14)
                        )
                    )
                } else {
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.background.copy(alpha = 0.85f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                }
                val navBarBg = if (isDarkMode) Color.White.copy(alpha = 0.05f)
                               else MaterialTheme.colorScheme.surface
                val navBarBorder = if (isDarkMode) Color.White.copy(alpha = 0.05f)
                                   else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(barGradient)
                ) {
                    NavigationBar(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .clip(BottomNavShape)
                            .border(1.dp, navBarBorder, BottomNavShape)
                            .shadow(0.dp, BottomNavShape),
                        containerColor = navBarBg,
                    tonalElevation = 0.dp
                ) {
                    BottomNavItem.items.forEach { item ->
                        val selected = currentRoute == item.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label,
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = item.label,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                            )
                        )
                    }
                }
            }
        },
        // FAB removed; add transaction moved to top bar
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier
                .padding(top = innerPadding.calculateTopPadding())
                .imePadding(),
            enterTransition = {
                fadeIn(tween(300)) + slideInHorizontally(tween(300)) { it / 4 }
            },
            exitTransition = {
                fadeOut(tween(200)) + slideOutHorizontally(tween(200)) { -it / 4 }
            },
            popEnterTransition = {
                fadeIn(tween(300)) + slideInHorizontally(tween(300)) { -it / 4 }
            },
            popExitTransition = {
                fadeOut(tween(200)) + slideOutHorizontally(tween(200)) { it / 4 }
            }
        ) {
            composable(BottomNavItem.Home.route) {
                val searchQuery by transactionViewModel.searchQuery.collectAsStateWithLifecycle()
                HomeScreen(
                    userName = userName,
                    transactions = transactions,
                    summary = summary,
                    timeFilter = timeFilter,
                    searchQuery = searchQuery,
                    favoritesCount = favoritesCount,
                    showFavoritesOnly = showFavoritesOnly,
                    onTimeFilterChange = { transactionViewModel.setTimeFilter(it) },
                    onSearchQueryChange = { transactionViewModel.setSearchQuery(it) },
                    onDeleteTransaction = { transactionViewModel.deleteTransaction(it) },
                    onToggleFavorite = { transactionViewModel.toggleFavorite(it) },
                    onToggleFavoritesFilter = { transactionViewModel.toggleFavoritesFilter() },
                    onAddTransaction = { showAddTransaction = true }
                )
            }

            composable(BottomNavItem.Balances.route) {
                StatsScreen(
                    creditScore = creditScore,
                    summary = summary,
                    categoryBreakdown = categoryBreakdown,
                    monthlyBarData = monthlyBarData
                )
            }

            composable(BottomNavItem.Profile.route) {
                ProfileScreen(
                    userName = userName,
                    userEmail = userEmail,
                    totalSpendings = totalExpenses,
                    totalBalance = totalIncome - totalExpenses,
                    isDarkMode = isDarkMode,
                    onToggleDarkMode = { profileViewModel.toggleDarkMode() },
                    onUpdateProfile = { name, email ->
                        profileViewModel.updateUserName(name)
                        profileViewModel.updateUserEmail(email)
                    },
                    onLogout = { profileViewModel.logout() }
                )
            }
        }
    }
}
}
