package com.minor.expensetracker.ui.screens.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.TrendingDown
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.minor.expensetracker.ui.components.EmptyState
import com.minor.expensetracker.ui.screens.home.components.BalanceCard
import com.minor.expensetracker.ui.screens.home.components.SegmentedToggle
import com.minor.expensetracker.ui.screens.home.components.TransactionItem
import com.minor.expensetracker.ui.theme.*
import com.minor.expensetracker.ui.viewmodel.MonthlySummary
import com.minor.expensetracker.ui.viewmodel.TimeFilter
import com.minor.expensetracker.data.model.Transaction
import com.minor.expensetracker.ui.components.glassEffect

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Star

@Composable
fun HomeScreen(
    userName: String,
    transactions: List<Transaction>,
    summary: MonthlySummary,
    timeFilter: TimeFilter,
    searchQuery: String,
    favoritesCount: Int = 0,
    showFavoritesOnly: Boolean = false,
    onTimeFilterChange: (TimeFilter) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onDeleteTransaction: (Transaction) -> Unit,
    onToggleFavorite: (Transaction) -> Unit,
    onToggleFavoritesFilter: () -> Unit = {},
    onAddTransaction: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isSearchActive by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent),
        contentPadding = PaddingValues(bottom = 140.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 16.dp)
            ) {
                // Top bar — logo + Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(36.dp),
                            shape = MaterialTheme.shapes.small,
                            color = MaterialTheme.colorScheme.primary
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "₹",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "ExpenseTracker",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    // Action Icons
                    Row {
                        // Favorites filter toggle
                        IconButton(onClick = onToggleFavoritesFilter) {
                            BadgedBox(
                                badge = {
                                    if (favoritesCount > 0) {
                                        Badge(containerColor = MaterialTheme.colorScheme.primary) {
                                            Text(favoritesCount.coerceAtMost(99).toString())
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Star,
                                    contentDescription = "Favorites filter",
                                    tint = if (showFavoritesOnly) MaterialTheme.colorScheme.primary
                                           else MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                        IconButton(onClick = { isSearchActive = !isSearchActive }) {
                            Icon(
                                imageVector = Icons.Rounded.Search,
                                contentDescription = "Search",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        IconButton(onClick = onAddTransaction) {
                            Icon(
                                imageVector = Icons.Rounded.Add,
                                contentDescription = "Add Transaction",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }

                // Search Bar
                AnimatedVisibility(
                    visible = isSearchActive,
                    enter = fadeIn() + slideInVertically(),
                    exit = fadeOut() + slideOutVertically()
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        placeholder = { Text("Search transactions...") },
                        leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { onSearchQueryChange("") }) {
                                    Icon(Icons.Rounded.Close, contentDescription = "Clear")
                                }
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                        )
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Greeting
                Text(
                    text = "Hey, $userName 👋",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Track your daily spending",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Balance Card
                BalanceCard(
                    balance = summary.balance,
                    income = summary.totalIncome,
                    expense = summary.totalExpense,
                    cardHolderName = userName
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Summary Cards — Income, Expense, Balance
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SummaryCard(
                        label = "Income",
                        amount = summary.totalIncome,
                        icon = Icons.Rounded.TrendingUp,
                        iconTint = IncomeGreen,
                        modifier = Modifier.weight(1f)
                    )
                    SummaryCard(
                        label = "Expense",
                        amount = summary.totalExpense,
                        icon = Icons.Rounded.TrendingDown,
                        iconTint = ExpenseRed,
                        modifier = Modifier.weight(1f)
                    )
                    SummaryCard(
                        label = "Balance",
                        amount = summary.balance,
                        icon = Icons.Rounded.AccountBalanceWallet,
                        iconTint = Teal80,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Recent Transactions header + filter
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = when {
                            searchQuery.isNotEmpty() -> "Search Results"
                            showFavoritesOnly -> "Favourites"
                            else -> "Recent Transactions"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (searchQuery.isEmpty() && !showFavoritesOnly) {
                    SegmentedToggle(
                        options = listOf("Weekly", "Monthly"),
                        selectedIndex = if (timeFilter == TimeFilter.WEEKLY) 0 else 1,
                        onSelectionChanged = { index ->
                            onTimeFilterChange(
                                if (index == 0) TimeFilter.WEEKLY else TimeFilter.MONTHLY
                            )
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }

        // Transaction list
        if (transactions.isEmpty()) {
            item {
                EmptyState(
                    title = when {
                        searchQuery.isNotEmpty() -> "No matches found"
                        showFavoritesOnly -> "No favourites yet"
                        else -> "No transactions yet"
                    },
                    subtitle = when {
                        searchQuery.isNotEmpty() -> "Try a different search term"
                        showFavoritesOnly -> "Tap the star on any transaction to save it here"
                        else -> "Tap the + button to add your first income or expense"
                    }
                )
            }
        } else {
            itemsIndexed(
                items = transactions,
                key = { _, transaction -> transaction.id }
            ) { index, transaction ->
                TransactionItem(
                    transaction = transaction,
                    onDelete = { onDeleteTransaction(transaction) },
                    onToggleFavorite = { onToggleFavorite(transaction) },
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
                    index = index
                )
            }
        }
    }
}

@Composable
private fun SummaryCard(
    label: String,
    amount: Double,
    icon: ImageVector,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .glassEffect()
            .padding(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(iconTint.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconTint,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "₹${String.format(java.util.Locale.US, "%,.0f", amount)}",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
