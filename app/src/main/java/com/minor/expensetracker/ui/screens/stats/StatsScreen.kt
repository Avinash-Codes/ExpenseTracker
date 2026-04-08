package com.minor.expensetracker.ui.screens.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.TrendingDown
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.minor.expensetracker.data.local.CategorySum
import com.minor.expensetracker.ui.screens.stats.components.CategoryPieChart
import com.minor.expensetracker.ui.screens.stats.components.CreditScoreGauge
import com.minor.expensetracker.ui.screens.stats.components.BarData
import com.minor.expensetracker.ui.screens.stats.components.MonthlyBarChart
import com.minor.expensetracker.ui.theme.*
import com.minor.expensetracker.ui.viewmodel.MonthlySummary
import com.minor.expensetracker.ui.viewmodel.MonthBarEntry
import com.minor.expensetracker.ui.components.glassEffect

@Composable
fun StatsScreen(
    creditScore: Int,
    summary: MonthlySummary,
    categoryBreakdown: List<CategorySum>,
    monthlyBarData: List<MonthBarEntry> = emptyList(),
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(androidx.compose.ui.graphics.Color.Transparent)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 100.dp)
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
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

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Analytics",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Your spending insights & financial health",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Currency Selector
        var selectedCurrency by remember { mutableStateOf("INR") }
        val exchangeRates = mapOf("INR" to 1.0, "USD" to 0.012, "EUR" to 0.011)
        val currentRate = exchangeRates[selectedCurrency] ?: 1.0
        val currencySymbol = when(selectedCurrency) {
            "INR" -> "₹"
            "USD" -> "$"
            "EUR" -> "€"
            else -> "₹"
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("INR", "USD", "EUR").forEach { currency ->
                FilterChip(
                    selected = selectedCurrency == currency,
                    onClick = { selectedCurrency = currency },
                    label = { Text(currency) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Financial Health Score
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .glassEffect()
                .padding(20.dp)
        ) {
            Text(
                text = "Financial Health Score",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Based on your spending vs income ratio",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            CreditScoreGauge(score = creditScore)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Quick Stats cards
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Savings rate
            val savingsRate = if (summary.totalIncome > 0)
                ((summary.totalIncome - summary.totalExpense) / summary.totalIncome * 100).coerceIn(0.0, 100.0)
            else 0.0

            QuickStatCard(
                title = "Savings Rate",
                value = "${String.format(java.util.Locale.US, "%.0f", savingsRate)}%",
                subtitle = "This month",
                isPositive = savingsRate >= 20,
                modifier = Modifier.weight(1f)
            )

            // Avg daily spend
            val avgDailySpend = if (summary.totalExpense > 0) (summary.totalExpense / 30) * currentRate else 0.0

            QuickStatCard(
                title = "Daily Average",
                value = "$currencySymbol${String.format(java.util.Locale.US, "%.0f", avgDailySpend)}",
                subtitle = "Expense/day",
                isPositive = false,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Category Breakdown pie chart
        if (categoryBreakdown.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .glassEffect()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Category Breakdown",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Where your money goes",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Currently CategoryPieChart does its own internal calculation if needed, 
                // but we pass totalExpense correctly. Since pie is a ratio, currency doesn't strictly matter for the slice size.
                CategoryPieChart(
                    categoryData = categoryBreakdown,
                    totalExpense = summary.totalExpense
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Monthly Spending Trend
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .glassEffect()
                .padding(20.dp)
        ) {
            Text(
                text = "Monthly Trend ($currencySymbol)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Your spending over the months",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            MonthlyBarChart(
                data = if (monthlyBarData.isNotEmpty()) {
                    monthlyBarData.map { BarData(it.label, it.value * currentRate.toFloat()) }
                } else {
                    listOf(BarData("—", 0f))
                }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun QuickStatCard(
    title: String,
    value: String,
    subtitle: String,
    isPositive: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .glassEffect()
            .padding(16.dp)
    ) {
        Icon(
            imageVector = if (isPositive) Icons.Rounded.TrendingUp else Icons.Rounded.TrendingDown,
            contentDescription = null,
            tint = if (isPositive) IncomeGreen else ExpenseRed,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
