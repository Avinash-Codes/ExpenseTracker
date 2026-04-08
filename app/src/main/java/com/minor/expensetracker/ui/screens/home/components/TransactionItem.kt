package com.minor.expensetracker.ui.screens.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.minor.expensetracker.data.model.Categories
import com.minor.expensetracker.data.model.Transaction
import com.minor.expensetracker.data.model.TransactionType
import com.minor.expensetracker.ui.components.glassEffect
import com.minor.expensetracker.ui.theme.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TransactionItem(
    transaction: Transaction,
    onDelete: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier,
    index: Int = 0
) {
    val category = Categories.getCategoryByName(transaction.category)
    val formatter = remember {
        NumberFormat.getNumberInstance(Locale.US).apply {
            minimumFractionDigits = 0
            maximumFractionDigits = 2
        }
    }
    val dateFormatter = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }
    val isDark = ThemeState.isDarkMode

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else false
        }
    )

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = fadeIn(tween(400, delayMillis = index * 60)) +
                slideInVertically(tween(400, delayMillis = index * 60)) { it / 2 }
    ) {
        SwipeToDismissBox(
            state = dismissState,
            backgroundContent = {
                val isIdle = dismissState.currentValue == SwipeToDismissBoxValue.Settled &&
                        dismissState.targetValue == SwipeToDismissBoxValue.Settled
                if (!isIdle) {
                    val dismissColor = if (transaction.type == TransactionType.INCOME) IncomeGreen else ExpenseRed
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .glassEffect()
                            .background(dismissColor.copy(alpha = 0.2f))
                            .padding(horizontal = 24.dp),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Text(
                            text = "Delete",
                            color = if (isDark) Color.White else Color(0xFF1A1A1A),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            },
            enableDismissFromStartToEnd = false
        ) {
            val accentColor = if (transaction.type == TransactionType.INCOME) IncomeGreen else ExpenseRed

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .glassEffect()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                accentColor.copy(alpha = if (isDark) 0.10f else 0.07f),
                                Color.Transparent
                            )
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category icon circle
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            if (isDark) Color.White
                            else category.color.copy(alpha = 0.15f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = category.icon,
                        contentDescription = category.name,
                        tint = if (isDark) Color(0xFF141414) else category.color,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = transaction.category.uppercase(),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (transaction.note.isNotEmpty())
                            transaction.note
                        else dateFormatter.format(Date(transaction.date)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Favourite star
                IconButton(
                    onClick = { onToggleFavorite() },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (transaction.isFavorited) Icons.Rounded.Star else Icons.Rounded.StarOutline,
                        contentDescription = "Favourite",
                        tint = if (transaction.isFavorited) Color(0xFFFFB300)
                               else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Amount chip
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(accentColor.copy(alpha = if (isDark) 0.18f else 0.12f))
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${if (transaction.type == TransactionType.INCOME) "+" else "-"}₹${formatter.format(transaction.amount)}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = accentColor
                    )
                }
            }
        }
    }
}
