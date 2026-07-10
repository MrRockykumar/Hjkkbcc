package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ShimmerBlock(
    modifier: Modifier = Modifier,
    height: androidx.compose.ui.unit.Dp,
    shape: RoundedCornerShape = RoundedCornerShape(16.dp)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmerAlpha"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(shape)
            .alpha(alpha)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0x33FFFFFF),
                        Color(0x11FFFFFF),
                        Color(0x33FFFFFF)
                    )
                )
            )
    )
}

@Composable
fun SkeletonLoaderList(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Featured card shimmer
        ShimmerBlock(height = 200.dp, shape = RoundedCornerShape(28.dp))
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Category row shimmer
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            repeat(4) {
                ShimmerBlock(
                    modifier = Modifier.width(100.dp),
                    height = 36.dp,
                    shape = RoundedCornerShape(18.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))

        // Grid cards shimmer
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                ShimmerBlock(height = 130.dp, shape = RoundedCornerShape(24.dp))
            }
            Box(modifier = Modifier.weight(1f)) {
                ShimmerBlock(height = 130.dp, shape = RoundedCornerShape(24.dp))
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                ShimmerBlock(height = 130.dp, shape = RoundedCornerShape(24.dp))
            }
            Box(modifier = Modifier.weight(1f)) {
                ShimmerBlock(height = 130.dp, shape = RoundedCornerShape(24.dp))
            }
        }
    }
}
