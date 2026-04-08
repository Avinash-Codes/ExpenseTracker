package com.minor.expensetracker.ui.screens.stats.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.minor.expensetracker.ui.theme.*

data class BarData(
    val label: String,
    val value: Float
)

@Composable
fun MonthlyBarChart(
    data: List<BarData>,
    modifier: Modifier = Modifier,
    title: String = "Last 6 months expenses"
) {
    val maxValue = data.maxOfOrNull { it.value } ?: 1f

    val animatedProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(1200, easing = FastOutSlowInEasing),
        label = "barAnim"
    )

    val textColor = MaterialTheme.colorScheme.onSurfaceVariant
    val barColors = listOf(
        listOf(ChartBar1, ChartBar1.copy(alpha = 0.6f)),
        listOf(ChartBar2, ChartBar2.copy(alpha = 0.6f)),
        listOf(ChartBar3, ChartBar3.copy(alpha = 0.6f)),
        listOf(ChartBar4, ChartBar4.copy(alpha = 0.6f)),
        listOf(ChartBar1, ChartBar1.copy(alpha = 0.6f)),
        listOf(ChartBar2, ChartBar2.copy(alpha = 0.6f)),
    )

    Column(modifier = modifier.fillMaxWidth()) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            val barWidth = (size.width - (data.size + 1) * 20f) / data.size
            val maxBarHeight = size.height - 40f

            data.forEachIndexed { index, bar ->
                val barHeight = if (maxValue > 0)
                    (bar.value / maxValue) * maxBarHeight * animatedProgress
                else 0f

                val x = 20f + index * (barWidth + 20f)
                val y = size.height - 30f - barHeight

                val colorPair = barColors[index % barColors.size]

                // Bar
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = colorPair,
                        startY = y,
                        endY = y + barHeight
                    ),
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(8f, 8f)
                )
            }
        }

        // Labels
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            data.forEach { bar ->
                Text(
                    text = bar.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Title
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "₹${String.format(java.util.Locale.US, "%,.0f", data.sumOf { it.value.toDouble() })}",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = Teal80
            )
        }
    }
}
