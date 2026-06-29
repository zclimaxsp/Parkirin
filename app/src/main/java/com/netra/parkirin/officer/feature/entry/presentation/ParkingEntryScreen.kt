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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import android.util.Base64
import com.netra.parkirin.officer.core.components.GpsStatus
import com.netra.parkirin.officer.core.components.GpsStatusBar
import com.netra.parkirin.officer.core.components.ParkiRinTopBar
import com.netra.parkirin.officer.core.theme.Primary
import com.netra.parkirin.officer.core.theme.StatusError
import com.netra.parkirin.officer.core.theme.StatusSuccess
import com.netra.parkirin.officer.feature.entry.data.EntryViewModel
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardActions

data class ZoneInfoUi(
    val zoneId: String,
    val streetId: String,
    val zoneName: String,
    val streetName: String?,
    val tariffSummary: String,
)

@Composable
fun ParkingEntryScreen(
    onBackClick: () -> Unit = {},
    onScanClick: () -> Unit = {},
    onPhotoClick: () -> Unit = {},
    onSubmitSuccess: () -> Unit = {},
    viewModel: EntryViewModel = hiltViewModel(),
    // TODO: ambil dari DashboardViewModel nanti
    zoneInfo: ZoneInfoUi? = ZoneInfoUi(
        zoneId = "bc267baa-f306-4670-bccc-f3023cfa06ca",
        streetId = "c07214ea-85a4-45f3-8261-666c874c62db",
        zoneName = "Zone A",
        streetName = "Jalan Sudirman",
        tariffSummary = "Rp 2.000 / 2 jam"
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    var isManualEdit by remember { mutableStateOf(false) }

    // Kalau berhasil submit, navigate ke hasil
    // Buka ParkingEntryScreen.kt, ubah bagian ini:
    LaunchedEffect(uiState.result) {
        if (uiState.result != null) {
            onSubmitSuccess() // Cukup panggil navigasi saja, jangan di-clear dulu gess!
        }
    }

    // TODO: ganti dengan userId dari token login
    val userId = "0ca9d1f2-6baa-46b6-9b1b-55a7b4f0a087"

    val canSubmit = zoneInfo != null &&
            uiState.plateNumber.isNotBlank() &&
            !uiState.isLoading

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

            item {
                GpsStatusBar(status = GpsStatus.READY, accuracyMeters = 12f)
            }

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
                            zoneInfo.streetName?.let {
                                Text(
                                    text = it,
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
                        }
                    }
                }
            }

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

                        FilledTonalButton(
                            onClick = onScanClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                        ) {
                            Icon(Icons.Default.CameraAlt, null, Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = if (uiState.plateNumber.isNotBlank()) "Re-scan Plate" else "Scan Plate (OCR)",
                                style = MaterialTheme.typography.labelLarge,
                            )
                        }

                        if (uiState.plateNumber.isNotBlank() && !isManualEdit && uiState.photoBase64 != null) {                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(StatusSuccess.copy(alpha = 0.08f))
                                    .border(1.dp, StatusSuccess.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(Icons.Default.CheckCircle, null, tint = StatusSuccess, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = uiState.plateNumber,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f),
                                )
                                IconButton(
                                    onClick = { isManualEdit = true },
                                    modifier = Modifier.size(32.dp),
                                ) {
                                    Icon(Icons.Default.Edit, "Edit", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                                }
                            }
                        } else {
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(
                                value = uiState.plateNumber,
                                onValueChange = {
                                    viewModel.onPlateNumberChanged(it.uppercase())
                                },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Plate Number") },
                                placeholder = { Text("e.g. B 1234 XYZ") },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Characters,
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Done // 1. Munculin tombol centang/Done di keyboard HP
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        isManualEdit = false // 2. Kunci inputan saat tombol centang keyboard diklik
                                    }
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

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                ) {
                    Column {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(Icons.Default.PhotoCamera, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Vehicle Photo", style = MaterialTheme.typography.labelLarge)
                                Text(
                                    text = if (uiState.photoBase64 != null) "Photo captured ✓" else "Optional — tap to capture",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (uiState.photoBase64 != null) StatusSuccess else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            FilledTonalButton(onClick = onPhotoClick, shape = RoundedCornerShape(8.dp)) {
                                Text(
                                    text = if (uiState.photoBase64 != null) "Retake" else "Take Photo",
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }

                        if (uiState.photoBase64 != null) {
                            val imageBytes = remember(uiState.photoBase64) {
                                Base64.decode(uiState.photoBase64, Base64.DEFAULT)
                            }
                            AsyncImage(
                                model = imageBytes,
                                contentDescription = "Captured vehicle",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(160.dp)
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }

            if (uiState.errorMessage != null) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = StatusError.copy(alpha = 0.1f)),
                    ) {
                        Text(
                            text = uiState.errorMessage!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = StatusError,
                            modifier = Modifier.padding(16.dp),
                        )
                    }
                }
            }

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
                        onClick = {
                            zoneInfo?.let { zone ->
                                viewModel.recordEntry(
                                    userId = userId,
                                    zoneId = zone.zoneId,
                                    zoneName = zone.zoneName,
                                    streetId = zone.streetId,
                                    streetName = zone.streetName ?: "",
                                    lat = 0.0,
                                    lng = 0.0,
                                    accuracy = 0f
                                )
                            }
                        },
                        enabled = canSubmit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    ) {
                        if (uiState.isLoading) {
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