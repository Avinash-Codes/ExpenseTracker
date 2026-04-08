package com.minor.expensetracker.ui.screens.stats.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.minor.expensetracker.ui.theme.*

@Composable
fun CreditScoreGauge(
    score: Int,
    modifier: Modifier = Modifier,
    maxScore: Int = 900
) {
    val animatedScore by animateFloatAsState(
        targetValue = score.toFloat(),
        animationSpec = tween(1500, easing = FastOutSlowInEasing),
        label = "scoreAnim"
    )

    val animatedSweep by animateFloatAsState(
        targetValue = (score.toFloat() / maxScore) * 180f,
        animationSpec = tween(1500, easing = FastOutSlowInEasing),
        label = "sweepAnim"
    )

    val scoreLabel = when {
        score < 300 -> "Poor"
        score < 500 -> "Fair"
        score < 700 -> "Average"
        score < 800 -> "Good"
        else -> "Excellent"
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(220.dp)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 20f
                val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)
                val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)

                // Background arc
                drawArc(
                    color = Color.Gray.copy(alpha = 0.15f),
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )

                // Progress arc with gradient
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            Color(0xFFFF6B6B),
                            Color(0xFFFBBF24),
                            Color(0xFF4ADE80),
                            Color(0xFF2DD4BF)
                        )
                    ),
                    startAngle = 180f,
                    sweepAngle = animatedSweep,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )

                // Indicator dot
                val angle = Math.toRadians((180 + animatedSweep).toDouble())
                val radius = (arcSize.width / 2)
                val centerX = topLeft.x + arcSize.width / 2
                val centerY = topLeft.y + arcSize.height / 2
                val dotX = centerX + radius * Math.cos(angle).toFloat()
                val dotY = centerY + radius * Math.sin(angle).toFloat()

                drawCircle(
                    color = Color.White,
                    radius = 10f,
                    center = Offset(dotX, dotY)
                )
                drawCircle(
                    color = Teal80,
                    radius = 6f,
                    center = Offset(dotX, dotY)
                )
            }

            // Score text
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.offset(y = 10.dp)
            ) {
                Text(
                    text = animatedScore.toInt().toString(),
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        Text(
            text = "Your Credit Score is $scoreLabel",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Last Check on ${java.text.SimpleDateFormat("dd MMM", java.util.Locale.getDefault()).format(java.util.Date())}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
