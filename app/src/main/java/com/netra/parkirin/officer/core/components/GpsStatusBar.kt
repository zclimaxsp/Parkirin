package com.netra.parkirin.officer.core.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.GpsNotFixed
import androidx.compose.material.icons.filled.GpsOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.netra.parkirin.officer.core.theme.GpsAcquiring
import com.netra.parkirin.officer.core.theme.GpsBlocked
import com.netra.parkirin.officer.core.theme.GpsFailed
import com.netra.parkirin.officer.core.theme.GpsReady

enum class GpsStatus { IDLE, ACQUIRING, READY, FAILED, BLOCKED }

@Composable
fun GpsStatusBar(
    status: GpsStatus,
    accuracyMeters: Float? = null,
    modifier: Modifier = Modifier,
) {
    val (bgColor, icon, label) = when (status) {
        GpsStatus.IDLE -> Triple(GpsBlocked.copy(alpha = 0.12f), Icons.Default.GpsOff, "GPS: Standby")
        GpsStatus.ACQUIRING -> Triple(GpsAcquiring.copy(alpha = 0.15f), Icons.Default.GpsNotFixed, "GPS: Acquiring...")
        GpsStatus.READY -> Triple(GpsReady.copy(alpha = 0.12f), Icons.Default.GpsFixed,
            if (accuracyMeters != null) "GPS Ready  ±${accuracyMeters.toInt()}m" else "GPS Ready")
        GpsStatus.FAILED -> Triple(GpsFailed.copy(alpha = 0.12f), Icons.Default.GpsOff, "GPS Failed — Move to open area")
        GpsStatus.BLOCKED -> Triple(GpsFailed.copy(alpha = 0.12f), Icons.Default.GpsOff, "GPS Required — Transaction blocked")
    }

    val iconColor by animateColorAsState(
        targetValue = when (status) {
            GpsStatus.READY -> GpsReady
            GpsStatus.ACQUIRING -> GpsAcquiring
            GpsStatus.FAILED, GpsStatus.BLOCKED -> GpsFailed
            else -> GpsBlocked
        }, label = "gpsColor"
    )

    // Pulsing animation for ACQUIRING state
    val infiniteTransition = rememberInfiniteTransition(label = "gpsAnim")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "pulse"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(bgColor)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier
                .size(18.dp)
                .alpha(if (status == GpsStatus.ACQUIRING) pulseAlpha else 1f),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = iconColor,
        )
    }
}
