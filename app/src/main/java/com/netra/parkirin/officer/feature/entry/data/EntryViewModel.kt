package com.netra.parkirin.officer.feature.entry.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.netra.parkirin.officer.core.network.ParkingEntryResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EntryUiState(
    val isLoading: Boolean = false,
    val plateNumber: String = "",
    val vehicleType: String = "MOTORCYCLE", // 🔍 1. Tambahkan state default kendaraan (Motor)
    val photoBase64: String? = null,
    val zoneName: String? = null,
    val streetName: String? = null,
    val result: ParkingEntryResponse? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class EntryViewModel @Inject constructor(
    private val repository: EntryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EntryUiState())
    val uiState: StateFlow<EntryUiState> = _uiState

    // 🔍 2. Tambahkan fungsi buat ngubah tipe kendaraan pas diklik di UI nanti
    fun onVehicleTypeChanged(type: String) {
        _uiState.update { it.copy(vehicleType = type) }
    }

    fun onPlateNumberChanged(plate: String) {
        _uiState.update { it.copy(plateNumber = plate) }
    }

    fun onPhotoCaptured(base64: String) {
        _uiState.update { it.copy(photoBase64 = base64) }
    }

    fun recordEntry(userId: String, zoneId: String, zoneName: String, streetId: String, streetName: String, lat: Double, lng: Double, accuracy: Float) {
        val plate = _uiState.value.plateNumber
        val photo = _uiState.value.photoBase64
        val vehicleType = _uiState.value.vehicleType // 🔍 3. Ambil nilainya dari state saat ini
        if (plate.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Plate number cannot be empty") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, zoneName = zoneName, streetName = streetName) }
            repository.recordEntry(
                userId = userId,
                plateNumber = plate,
                vehicleType = vehicleType, // 🔍 4. Oper ke repository (pastiin di Repository lu juga ditambahin parameternya ya meks!)
                zoneId = zoneId,
                streetId = streetId,
                streetName = streetName,
                lat = lat,
                lng = lng,
                accuracy = accuracy,
                photoBase64 = photo
            )
                .onSuccess { response ->
                    _uiState.update { it.copy(isLoading = false, result = response) }
                }
                .onFailure { error ->
                    // Tambahkan LOG ini buat ngintip di Logcat Android Studio
                    android.util.Log.e("PARKIRIN_VIEWMODEL", "Gagal Record Entry!", error)

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.localizedMessage ?: "Terjadi kesalahan tak terduga"
                        )
                    }
                }
        }
    }

    fun clearResult() {
        _uiState.update { it.copy(result = null) }
    }
}