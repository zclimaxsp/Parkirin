package com.netra.parkirin.officer.feature.exit.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import com.netra.parkirin.officer.core.components.GpsStatus
import com.netra.parkirin.officer.core.components.GpsStatusBar
import com.netra.parkirin.officer.core.components.ParkiRinTopBar
import com.netra.parkirin.officer.core.theme.Primary
import com.netra.parkirin.officer.core.theme.Secondary
import com.netra.parkirin.officer.core.theme.StatusError
import com.netra.parkirin.officer.feature.exit.data.ExitViewModel

@Composable
fun ParkingExitScreen(
    onBackClick: () -> Unit = {},
    onScanClick: () -> Unit = {},
    onExitSuccess: (invoiceId: String) -> Unit = {},
    viewModel: ExitViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // TODO: ganti dengan userId dari token login
    val userId = "0ca9d1f2-6baa-46b6-9b1b-55a7b4f0a087"

    // Kalau exit berhasil, navigate ke cash payment
    LaunchedEffect(uiState.exitResult) {
        uiState.exitResult?.let { result ->
            // 🚀 PRIORITAS: Kirim invoiceId sebagai identifier utama! Kalau null baru sessionId.
            onExitSuccess(result.invoiceId ?: result.sessionId)
            // ⚠️ JANGAN CLEAR DI SINI! Soalnya butuh data exitResult di CashPaymentScreen gess!
        }
    }

    Scaffold(
        topBar = {
            ParkiRinTopBar(
                title = "Vehicle Exit",
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

            item {
                GpsStatusBar(status = GpsStatus.READY, accuracyMeters = 15f)
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Find Session",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        FilledTonalButton(
                            onClick = onScanClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                        ) {
                            Icon(Icons.Default.CameraAlt, null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Scan Plate (OCR)", style = MaterialTheme.typography.labelLarge)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            OutlinedTextField(
                                value = uiState.plateNumber,
                                onValueChange = { viewModel.onPlateNumberChanged(it.uppercase()) },
                                modifier = Modifier.weight(1f),
                                label = { Text("Plate Number") },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Characters,
                                ),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Primary,
                                    focusedLabelColor = Primary,
                                ),
                            )
                            // 🌟 GANTI JADI INI MEKS:
                            FilledTonalButton(
                                onClick = { viewModel.searchSession(userId, uiState.plateNumber) }, // 🚀 Oper plateNumber-nya ke sini!
                                modifier = Modifier.height(56.dp),
                                shape = RoundedCornerShape(12.dp),
                            ) {
                                if (uiState.isLoading) {
                                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                } else {
                                    Icon(Icons.Default.Search, null, modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }
                }
            }

            uiState.session?.let { session ->
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = session.platenumber ?: "-",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Text(
                                        text = session.id ?: "-",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "Rp ${"%,d".format(session.amount).replace(",", ".")}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Secondary,
                                    )
                                    Text(
                                        text = "${session.durationMinutes} min",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }

                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.padding(vertical = 12.dp),
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                LabeledValue("Zone", session.zoneId, modifier = Modifier.weight(1f))
                                LabeledValue("Entry", formatToLocalTime(session.entrytime), align = Alignment.End, modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Text(
                            text = "Payment Method",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )

                        Button(
                            onClick = { viewModel.recordExit(userId, "CASH") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Primary),
                        ) {
                            Text(
                                text = "Cash Payment",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White,
                            )
                        }

                        FilledTonalButton(
                            onClick = { viewModel.recordExit(userId, "QRIS") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                        ) {
                            Text("Show QRIS / QR Code", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                        }

                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.padding(vertical = 4.dp),
                        )

                        FilledTonalButton(
                            onClick = { viewModel.notifyAbsent(userId) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                        ) {
                            Text(
                                text = "Notify Owner (Vehicle Absent)",
                                style = MaterialTheme.typography.labelLarge,
                                color = StatusError,
                            )
                        }
                    }
                }
            }

            if (uiState.session == null && uiState.activeSessions.isNotEmpty()) {
                item {
                    Text(
                        text = "Active Sessions in Your Zone",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
                items(uiState.activeSessions) { session ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .clickable { viewModel.onSessionSelected(session) },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                val displayPlate = if (session.platenumber.isNullOrBlank()) {
                                    "Session (${session.id?.takeLast(4)?.uppercase() ?: "????"})"
                                } else {
                                    session.platenumber
                                }
                                Text(displayPlate, fontWeight = FontWeight.Bold)
                                Text("In: ${formatToLocalTime(session.entrytime)}", style = MaterialTheme.typography.bodySmall)
                            }
                            
                            Spacer(modifier = Modifier.weight(1f)) // 🚀 Dorong harga ke pojok kanan gess!

                            Column(horizontalAlignment = Alignment.End) {
                                Text("Rp ${"%,d".format(session.amount).replace(",", ".")}", color = Secondary, fontWeight = FontWeight.Bold)
                                Text("${session.durationMinutes}m", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }

            uiState.errorMessage?.let { error ->
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = StatusError.copy(alpha = 0.1f)),
                    ) {
                        Text(
                            text = error,
                            style = MaterialTheme.typography.bodySmall,
                            color = StatusError,
                            modifier = Modifier.padding(16.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LabeledValue(
    label: String,
    value: String?,
    align: Alignment.Horizontal = Alignment.Start,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = align
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value ?: "-",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
        )
    }
}

private fun formatDuration(minutes: Int): String {
    val h = minutes / 60
    val m = minutes % 60
    return if (h > 0) "${h}h ${m}m" else "${m}m"
}

private fun formatToLocalTime(entryTimeStr: String?): String {
    if (entryTimeStr.isNullOrBlank()) return "--:--"
    return try {
        val instant = Instant.parse(entryTimeStr)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            .withZone(ZoneId.systemDefault())
        formatter.format(instant)
    } catch (e: Exception) {
        entryTimeStr.take(16).replace("T", " ")
    }
}