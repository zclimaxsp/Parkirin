package com.netra.parkirin.officer.feature.exit.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.netra.parkirin.officer.core.components.ParkiRinTopBar
import com.netra.parkirin.officer.core.theme.Primary
import com.netra.parkirin.officer.core.theme.StatusSuccess
// 🔍 1. IMPORT UTILS JAM SAKTI KITA MEKS:
import com.netra.parkirin.officer.core.utils.formatUtcToLocalTime
import com.netra.parkirin.officer.feature.exit.data.ExitViewModel
import com.netra.parkirin.officer.feature.exit.data.ExitUiState

@Composable
fun ExitResultScreen(
    onPrintClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onDoneClick: () -> Unit = {},
    viewModel: ExitViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val payment = uiState.paymentResult

    Scaffold(
        topBar = {
            ParkiRinTopBar(
                title = "Payment Success",
                showBackButton = true,
                onBackClick = onBackClick,
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = StatusSuccess,
                modifier = Modifier.size(72.dp),
            )

            Text(
                text = "Payment Confirmed!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )

            if (payment != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        ResultRow("Receipt", payment.receiptNumber)

                        // 🔍 2. TAMBAHKAN JAM KELUAR / BAYAR REAL-TIME DI SINI MEKS:
                        // Catatan: Sesuaikan nama properti jam dari model kamu (misal: paymentTime, exitTime, atau createdAt)
                        ResultRow(
                            label = "Exit Time",
                            value = formatUtcToLocalTime(utcString = payment.paidAt) // 🚀 Paket paidAt sesuai blueprint data class!
                        )

                        ResultRow(
                            "Amount",
                            "Rp ${"%,d".format(payment.amount).replace(",", ".")}"
                        )
                        ResultRow(
                            "Change",
                            "Rp ${"%,d".format(payment.change).replace(",", ".")}"
                        )
                        ResultRow("Lottery Code", payment.lotteryCode ?: "-")
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            FilledTonalButton(
                onClick = onPrintClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text(
                    "Print Receipt",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            Button(
                onClick = onDoneClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
            ) {
                Text(
                    "Done",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                )
            }
        }
    }
}

@Composable
private fun ResultRow(label: String, value: String) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
        )
    }
}