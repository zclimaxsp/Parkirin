package com.netra.parkirin.officer.feature.exit.presentation

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.netra.parkirin.officer.core.components.GpsStatus
import com.netra.parkirin.officer.core.components.GpsStatusBar
import com.netra.parkirin.officer.core.components.ParkiRinTopBar
import com.netra.parkirin.officer.core.theme.ParkiRinTheme
import com.netra.parkirin.officer.core.theme.Primary
import com.netra.parkirin.officer.core.theme.Secondary
import com.netra.parkirin.officer.core.theme.StatusError

data class InvoicePreviewUi(
    val invoiceNumber: String,
    val plateNumber: String,
    val zoneName: String,
    val entryTime: String,
    val durationMinutes: Int,
    val amount: Long,
)

@Composable
fun ParkingExitScreen(
    gpsStatus: GpsStatus = GpsStatus.READY,
    gpsAccuracy: Float? = 15f,
    invoicePreview: InvoicePreviewUi? = InvoicePreviewUi(
        invoiceNumber = "INV-20260330-042",
        plateNumber = "B 1234 XYZ",
        zoneName = "Zone A – City Center",
        entryTime = "08:42",
        durationMinutes = 87,
        amount = 5_000L,
    ),
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onBackClick: () -> Unit = {},
    onScanClick: () -> Unit = {},
    onSearchClick: (plate: String) -> Unit = {},
    onCashPaymentClick: () -> Unit = {},
    onQrisPaymentClick: () -> Unit = {},
    onNotifyAbsentClick: () -> Unit = {},
) {
    var plate by remember { mutableStateOf("") }

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

            // ── GPS Status ───────────────────────────────────────
            item {
                GpsStatusBar(status = gpsStatus, accuracyMeters = gpsAccuracy)
            }

            // ── Plate Lookup ─────────────────────────────────────
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
                                value = plate,
                                onValueChange = { plate = it.uppercase() },
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
                            FilledTonalButton(
                                onClick = { onSearchClick(plate) },
                                modifier = Modifier.height(56.dp),
                                shape = RoundedCornerShape(12.dp),
                            ) {
                                Icon(Icons.Default.Search, null, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            }

            // ── Invoice Preview ──────────────────────────────────
            if (invoicePreview != null) {
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
                                Column {
                                    Text(
                                        text = invoicePreview.plateNumber,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Text(
                                        text = invoicePreview.invoiceNumber,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "Rp ${"%,d".format(invoicePreview.amount).replace(",", ".")}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Secondary,
                                    )
                                    Text(
                                        text = formatDuration(invoicePreview.durationMinutes),
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
                                LabeledValue("Zone", invoicePreview.zoneName)
                                LabeledValue("Entry", invoicePreview.entryTime, align = Alignment.End)
                            }
                        }
                    }
                }

                // ── Payment Actions ──────────────────────────────
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
                            onClick = onCashPaymentClick,
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
                            onClick = onQrisPaymentClick,
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
                            onClick = onNotifyAbsentClick,
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
        }
    }
}

@Composable
private fun LabeledValue(
    label: String,
    value: String,
    align: Alignment.Horizontal = Alignment.Start,
) {
    Column(horizontalAlignment = align) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
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

@Preview(showBackground = true)
@Composable
private fun ParkingExitPreview() {
    ParkiRinTheme {
        ParkingExitScreen()
    }
}
