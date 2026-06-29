package com.netra.parkirin.officer.feature.entry.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.netra.parkirin.officer.core.components.ParkiRinTopBar
// 🔍 1. IMPORT UTILS JAM YANG SUDAH DIBUAT TADI MEKS:
import com.netra.parkirin.officer.core.utils.formatUtcToLocalTime
import com.netra.parkirin.officer.feature.entry.data.EntryViewModel

@Composable
fun EntryResultScreen(
    onPrintClick: () -> Unit = {},
    onDoneClick: () -> Unit = {},
    viewModel: EntryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val result = uiState.result

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
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Success Checkmark Circle (Adaptive background)
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(64.dp),
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Entry Recorded",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary,
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = result?.sessionNumber ?: "TKT-00000000-000",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Details Card (Adaptive surface color)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    TicketRow("Plate", result?.plateNumber ?: "-", isBoldValue = true, valueFontSize = 20.sp)
                    TicketDivider()
                    TicketRow("Vehicle", result?.vehicleType?.lowercase()?.replaceFirstChar { it.uppercase() } ?: "Motorcycle")
                    TicketDivider()
                    TicketRow("Zone", uiState.zoneName ?: "Zone A - City Center")
                    TicketDivider()
                    TicketRow("Street", uiState.streetName ?: "Jl. Sudirman")
                    TicketDivider()

                    // 🔍 2. GANTI STRATEGI SUBSTRING LAMA LU PAKE UTILS KITA MEKS:
                    TicketRow(
                        label = "Entry Time",
                        value = formatUtcToLocalTime(result?.entryTime)
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Print Ticket Button (Secondary style)
            Button(
                onClick = onPrintClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Icon(Icons.Default.Print, null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    "Print Ticket",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Done Button (Primary style)
            Button(
                onClick = {
                    viewModel.clearResult()
                    onDoneClick()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text(
                    "Done",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun TicketRow(
    label: String,
    value: String,
    isBoldValue: Boolean = false,
    valueFontSize: androidx.compose.ui.unit.TextUnit = 16.sp
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = valueFontSize,
                fontWeight = if (isBoldValue) FontWeight.ExtraBold else FontWeight.Medium
            ),
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun TicketDivider() {
    HorizontalDivider(
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
        thickness = 1.dp,
        modifier = Modifier.padding(vertical = 2.dp),
    )
}