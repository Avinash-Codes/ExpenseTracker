package com.minor.expensetracker.ui.screens.home.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.minor.expensetracker.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@Composable
fun BalanceCard(
    balance: Double,
    income: Double,
    expense: Double,
    cardHolderName: String,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "cardShimmer")
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = -300f,
        targetValue = 800f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerOffset"
    )

    val formatter = remember {
        NumberFormat.getNumberInstance(Locale.US).apply {
            minimumFractionDigits = 0
            maximumFractionDigits = 0
        }
    }
    val validThru = remember {
        val cal = java.util.Calendar.getInstance()
        val month = String.format(Locale.US, "%02d", cal.get(java.util.Calendar.MONTH) + 1)
        val year = (cal.get(java.util.Calendar.YEAR) + 3).toString().takeLast(2)
        "$month/$year"
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF0A3D2F),
                        Color(0xFF157A5E),
                        Color(0xFF3EBBA0),
                    ),
                    start = Offset.Zero,
                    end = Offset(900f, 500f)
                )
            )
            .drawBehind {
                // Shimmer
                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = 0.05f),
                            Color.Transparent
                        ),
                        start = Offset(shimmerOffset, 0f),
                        end = Offset(shimmerOffset + 250f, size.height)
                    )
                )
                // Decorative circles
                drawCircle(
                    color = Color.White.copy(alpha = 0.04f),
                    radius = 140f,
                    center = Offset(size.width - 60f, -20f)
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.03f),
                    radius = 100f,
                    center = Offset(40f, size.height + 30f)
                )
            }
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "Total Balance",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.75f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "₹${formatter.format(balance)}",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                // Simulated Chip
                Box(
                    modifier = Modifier
                        .size(width = 46.dp, height = 32.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFD4AF37).copy(alpha = 0.8f))
                ) {
                    Box(modifier = Modifier.fillMaxSize().padding(4.dp).background(Color.Transparent))
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Card Number
            Text(
                text = "****  ****  ****  0329",
                style = MaterialTheme.typography.titleLarge,
                letterSpacing = 2.sp,
                color = Color.White.copy(alpha = 0.9f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Footer of the card
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "CARDHOLDER",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                    Text(
                        text = cardHolderName.uppercase(Locale.US),
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "VALID THRU",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                    Text(
                        text = validThru,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
