package com.netra.parkirin.officer.feature.entry.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.netra.parkirin.officer.core.components.ParkiRinTopBar
import com.netra.parkirin.officer.core.theme.ParkiRinTheme
import com.netra.parkirin.officer.core.theme.Primary
import com.netra.parkirin.officer.core.theme.StatusSuccess

data class EntryResultUi(
    val sessionNumber: String,
    val plateNumber: String,
    val vehicleType: String,
    val zoneName: String,
    val streetName: String?,
    val entryTime: String,
    val isSubscription: Boolean,
    val lotteryCode: String?,
)

@Composable
fun EntryResultScreen(
    result: EntryResultUi = EntryResultUi(
        sessionNumber = "TKT-20260330-001",
        plateNumber = "B 1234 XYZ",
        vehicleType = "MOTORCYCLE",
        zoneName = "Zone A – City Center",
        streetName = "Jl. Sudirman",
        entryTime = "08:42",
        isSubscription = false,
        lotteryCode = null,
    ),
    onPrintClick: () -> Unit = {},
    onDoneClick: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            ParkiRinTopBar(
                title = "Entry Recorded",
                showBackButton = false,
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            // ── Success Icon ─────────────────────────────────────
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(StatusSuccess.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = StatusSuccess,
                    modifier = Modifier.size(44.dp),
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (result.isSubscription) "Subscription Entry" else "Entry Recorded",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = StatusSuccess,
            )
            Text(
                text = result.sessionNumber,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ── Ticket Card ──────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    if (result.isSubscription) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Primary.copy(alpha = 0.1f))
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "SUBSCRIPTION — FREE",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = Primary,
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    TicketRow("Plate", result.plateNumber, bold = true)
                    TicketDivider()
                    TicketRow("Vehicle", result.vehicleType.lowercase().replaceFirstChar { it.uppercase() })
                    TicketDivider()
                    TicketRow("Zone", result.zoneName)
                    result.streetName?.let { TicketRow("Street", it) }
                    TicketDivider()
                    TicketRow("Entry Time", result.entryTime)

                    if (result.lotteryCode != null) {
                        TicketDivider()
                        TicketRow("Lottery Code", result.lotteryCode, highlight = true)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Actions ──────────────────────────────────────────
            FilledTonalButton(
                onClick = onPrintClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
            ) {
                Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Print Ticket", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onDoneClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
            ) {
                Text("Done", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = Color.White)
            }
        }
    }
}

@Composable
private fun TicketRow(
    label: String,
    value: String,
    bold: Boolean = false,
    highlight: Boolean = false,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = if (bold) MaterialTheme.typography.titleSmall else MaterialTheme.typography.bodyMedium,
            fontWeight = if (bold || highlight) FontWeight.Bold else FontWeight.Normal,
            color = if (highlight) Primary else MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun TicketDivider() {
    HorizontalDivider(
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.padding(vertical = 4.dp),
    )
}

@Preview(showBackground = true)
@Composable
private fun EntryResultPreview() {
    ParkiRinTheme {
        EntryResultScreen()
    }
}
