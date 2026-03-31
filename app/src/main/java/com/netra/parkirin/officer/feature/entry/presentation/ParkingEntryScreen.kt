package com.netra.parkirin.officer.feature.entry.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.netra.parkirin.officer.core.components.GpsStatus
import com.netra.parkirin.officer.core.components.GpsStatusBar
import com.netra.parkirin.officer.core.components.ParkiRinTopBar
import com.netra.parkirin.officer.core.theme.ParkiRinTheme
import com.netra.parkirin.officer.core.theme.Primary
import com.netra.parkirin.officer.core.theme.StatusError
import com.netra.parkirin.officer.core.theme.StatusSuccess

data class ZoneInfoUi(
    val zoneName: String,
    val streetName: String?,
    val tariffSummary: String,   // e.g. "Rp 2.000/2jam, +Rp 1.000/jam"
)

@Composable
fun ParkingEntryScreen(
    gpsStatus: GpsStatus = GpsStatus.READY,
    gpsAccuracy: Float? = 12f,
    zoneInfo: ZoneInfoUi? = ZoneInfoUi("Zone A – City Center", "Jl. Sudirman", "Rp 2.000 / 2 jam, +Rp 1.000/jam"),
    ocrResult: String? = "B 1234 XYZ",
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onBackClick: () -> Unit = {},
    onScanClick: () -> Unit = {},
    onPhotoClick: () -> Unit = {},
    onSubmitClick: (plate: String) -> Unit = {},
) {
    var plate by remember { mutableStateOf(ocrResult ?: "") }
    var isManualEdit by remember { mutableStateOf(false) }

    val canSubmit = gpsStatus == GpsStatus.READY && zoneInfo != null && plate.isNotBlank() && !isLoading

    Scaffold(
        topBar = {
            ParkiRinTopBar(
                title = "Vehicle Entry",
                showBackButton = true,
                onBackClick = onBackClick,
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 32.dp),
        ) {

            // ── GPS Status ───────────────────────────────────────
            item {
                GpsStatusBar(status = gpsStatus, accuracyMeters = gpsAccuracy)
            }

            // ── GPS blocked warning ──────────────────────────────
            if (gpsStatus == GpsStatus.FAILED || gpsStatus == GpsStatus.BLOCKED) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = StatusError.copy(alpha = 0.1f)),
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = StatusError,
                                modifier = Modifier.size(20.dp),
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "GPS unavailable. Move to an open area before recording entry.",
                                style = MaterialTheme.typography.bodySmall,
                                color = StatusError,
                            )
                        }
                    }
                }
            }

            // ── Zone Info ────────────────────────────────────────
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(18.dp),
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Detected Zone",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        if (zoneInfo != null) {
                            Text(
                                text = zoneInfo.zoneName,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                            )
                            zoneInfo.streetName?.let { street ->
                                Text(
                                    text = street,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Tariff: ${zoneInfo.tariffSummary}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        } else {
                            Text(
                                text = if (gpsStatus == GpsStatus.ACQUIRING) "Detecting zone..." else "—",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            if (gpsStatus == GpsStatus.ACQUIRING) {
                                Spacer(modifier = Modifier.height(4.dp))
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                )
                            }
                        }
                    }
                }
            }

            // ── Plate Scanner ────────────────────────────────────
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "License Plate",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Camera / OCR button
                        FilledTonalButton(
                            onClick = onScanClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (ocrResult != null) "Re-scan Plate" else "Scan Plate (OCR)",
                                style = MaterialTheme.typography.labelLarge,
                            )
                        }

                        // OCR result or manual input
                        if (ocrResult != null && !isManualEdit) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(StatusSuccess.copy(alpha = 0.08f))
                                    .border(1.dp, StatusSuccess.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = StatusSuccess,
                                    modifier = Modifier.size(18.dp),
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = ocrResult,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f),
                                )
                                IconButton(
                                    onClick = { isManualEdit = true },
                                    modifier = Modifier.size(32.dp),
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp),
                                    )
                                }
                            }
                        } else {
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(
                                value = plate,
                                onValueChange = { plate = it.uppercase() },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Plate Number") },
                                placeholder = { Text("e.g. B 1234 XYZ") },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Characters,
                                    keyboardType = KeyboardType.Text,
                                ),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Primary,
                                    focusedLabelColor = Primary,
                                ),
                            )
                        }
                    }
                }
            }

            // ── Photo (optional) ─────────────────────────────────
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp),
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Vehicle Photo",
                                style = MaterialTheme.typography.labelLarge,
                            )
                            Text(
                                text = "Optional — tap to capture",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        FilledTonalButton(
                            onClick = onPhotoClick,
                            shape = RoundedCornerShape(8.dp),
                        ) {
                            Text("Take Photo", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }

            // ── Error ────────────────────────────────────────────
            if (errorMessage != null) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = StatusError.copy(alpha = 0.1f)),
                    ) {
                        Text(
                            text = errorMessage,
                            style = MaterialTheme.typography.bodySmall,
                            color = StatusError,
                            modifier = Modifier.padding(16.dp),
                        )
                    }
                }
            }

            // ── Submit ───────────────────────────────────────────
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                ) {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.padding(bottom = 16.dp),
                    )
                    Button(
                        onClick = { onSubmitClick(plate) },
                        enabled = canSubmit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = Color.White,
                                strokeWidth = 2.5.dp,
                            )
                        } else {
                            Text(
                                text = "Record Entry",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ParkingEntryScreenPreview() {
    ParkiRinTheme {
        ParkingEntryScreen()
    }
}
