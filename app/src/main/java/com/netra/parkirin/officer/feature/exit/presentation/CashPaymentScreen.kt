package com.netra.parkirin.officer.feature.exit.presentation

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MonetizationOn
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.netra.parkirin.officer.core.components.ParkiRinTopBar
import com.netra.parkirin.officer.core.theme.ParkiRinTheme
import com.netra.parkirin.officer.core.theme.Primary
import com.netra.parkirin.officer.core.theme.Secondary
import com.netra.parkirin.officer.core.theme.StatusSuccess

@Composable
fun CashPaymentScreen(
    invoiceNumber: String = "INV-20260330-042",
    plateNumber: String = "B 1234 XYZ",
    amount: Long = 5_000L,
    isLoading: Boolean = false,
    onBackClick: () -> Unit = {},
    onConfirmClick: (received: Long) -> Unit = {},
) {
    var receivedInput by remember { mutableStateOf("") }
    val received by remember { derivedStateOf { receivedInput.toLongOrNull() ?: 0L } }
    val change by remember { derivedStateOf { (received - amount).coerceAtLeast(0L) } }
    val isValid by remember { derivedStateOf { received >= amount } }

    // Quick-fill presets
    val presets = listOf(5_000L, 10_000L, 20_000L, 50_000L)

    Scaffold(
        topBar = {
            ParkiRinTopBar(
                title = "Cash Payment",
                showBackButton = true,
                onBackClick = onBackClick,
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        ) {

            // ── Invoice Summary ──────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Primary),
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
                                text = "Amount Due",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.75f),
                            )
                            Text(
                                text = "Rp ${"%,d".format(amount).replace(",", ".")}",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.MonetizationOn,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.5f),
                            modifier = Modifier.size(40.dp),
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "$plateNumber · $invoiceNumber",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.7f),
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Cash Received Input ──────────────────────────────
            Text(
                text = "Amount Received",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = receivedInput,
                onValueChange = { receivedInput = it.filter { c -> c.isDigit() } },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Cash Received (Rp)") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    focusedLabelColor = Primary,
                ),
                prefix = { Text("Rp") },
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ── Quick-fill presets ───────────────────────────────
            Text(
                text = "Quick Fill",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                presets.forEach { preset ->
                    FilledTonalButton(
                        onClick = { receivedInput = preset.toString() },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    ) {
                        Text(
                            text = "Rp ${"%,d".format(preset).replace(",", ".")}",
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Change Display ───────────────────────────────────
            if (received > 0) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isValid) StatusSuccess.copy(alpha = 0.1f)
                        else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
                    ),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = if (isValid) "Change" else "Insufficient",
                            style = MaterialTheme.typography.labelLarge,
                            color = if (isValid) StatusSuccess else MaterialTheme.colorScheme.error,
                        )
                        Text(
                            text = if (isValid) "Rp ${"%,d".format(change).replace(",", ".")}" else "—",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isValid) StatusSuccess else MaterialTheme.colorScheme.error,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { onConfirmClick(received) },
                enabled = isValid && !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.White, strokeWidth = 2.5.dp)
                } else {
                    Text("Confirm Payment", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = Color.White)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CashPaymentPreview() {
    ParkiRinTheme {
        CashPaymentScreen()
    }
}
