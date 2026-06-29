package com.netra.parkirin.officer.feature.exit.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.netra.parkirin.officer.core.network.ParkingExitResponse
import com.netra.parkirin.officer.core.network.ParkingSessionResponse
import com.netra.parkirin.officer.core.network.CashPaymentResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ExitUiState(
    val isLoading: Boolean = false,
    val plateNumber: String = "",
    val zoneId: String? = null,
    val session: ParkingSessionResponse? = null,
    val activeSessions: List<ParkingSessionResponse> = emptyList(),
    val exitResult: ParkingExitResponse? = null,
    val paymentResult: CashPaymentResponse? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class ExitViewModel @Inject constructor(
    private val repository: ExitRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExitUiState())
    val uiState: StateFlow<ExitUiState> = _uiState

    // TODO: ganti dengan userId dari token login
    private val userId = "0ca9d1f2-6baa-46b6-9b1b-55a7b4f0a087"

    init {
        loadOfficerProfile(userId)
    }

    fun onPlateNumberChanged(plate: String) {
        _uiState.update { it.copy(plateNumber = plate) }
    }

    /**
     * FITUR OTOMATIS LIST SESI AKTIF
     * Mengambil daftar kendaraan yang sedang parkir saat ViewModel diinisialisasi.
     */
    fun loadActiveSessions(zoneId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            repository.getActiveSessions(userId, zoneId)
                .onSuccess { sessions ->
                    _uiState.update { it.copy(isLoading = false, activeSessions = sessions) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
        }
    }

    /**
     * HANDLE KLIK ITEM LIST (Otomatis binding session)
     */
    fun onSessionSelected(session: ParkingSessionResponse) {
        _uiState.update { 
            it.copy(
                session = session,
                plateNumber = session.platenumber ?: "", // Pastikan tidak null
                errorMessage = null
            )
        }
    }

    fun loadOfficerProfile(userId: String) {
        viewModelScope.launch {
            repository.getMyProfile(userId)
                .onSuccess { officer ->
                    officer.assignedStreetId?.let { streetId ->
                        repository.getStreetById(streetId)
                            .onSuccess { street ->
                                _uiState.update { it.copy(zoneId = street.idTariffZone) }
                                // 🚀 Load active sessions hanya jika zoneId tidak null
                                street.idTariffZone?.let { zoneId ->
                                    loadActiveSessions(zoneId)
                                }
                            }
                    }
                }
        }
    }

    fun searchSession(userId: String, inputPlate: String) {
        if (inputPlate.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Plate number cannot be empty") }
            return
        }

        val currentZoneId = _uiState.value.zoneId
        if (currentZoneId.isNullOrBlank()) {
            _uiState.update { it.copy(errorMessage = "Zone not loaded yet, please wait") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            repository.getSessionByPlate(userId, inputPlate, currentZoneId!!)
                .onSuccess { session ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            session = session,
                            plateNumber = inputPlate
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = error.message)
                    }
                }
        }
    }

    fun recordExit(userId: String, paymentMethod: String = "CASH") {
        val currentSession = _uiState.value.session
        val currentPlate = currentSession?.platenumber ?: _uiState.value.plateNumber
        val currentZoneId = _uiState.value.zoneId ?: ""

        if (currentPlate.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Plate number missing from session context") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            repository.recordExit(
                userId = userId,
                plateNumber = currentPlate,
                zoneId = currentZoneId,
                paymentMethod = paymentMethod
            )
                .onSuccess { result ->
                    _uiState.update { it.copy(isLoading = false, exitResult = result) }
                    // 🚀 Refresh list active sessions setelah sukses exit
                    _uiState.value.zoneId?.let { loadActiveSessions(it) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
        }
    }

    fun processCashPayment(userId: String, invoiceId: String, amountReceived: Long) {
        // 🚀 AMBIL ZONE ID AKTIF DARI UI STATE MEKS
        val currentZoneId = _uiState.value.exitResult?.zoneId ?: _uiState.value.zoneId

        if (currentZoneId.isNullOrBlank()) {
            _uiState.update { it.copy(errorMessage = "Zone ID missing. Please refresh or re-enter.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            repository.processCashPayment(
                userId = userId,
                invoiceId = invoiceId,
                zoneId = currentZoneId,
                amountReceived = amountReceived
            )
                .onSuccess { result ->
                    _uiState.update { it.copy(isLoading = false, paymentResult = result) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
        }
    }

    fun notifyAbsent(userId: String) {
        val invoiceId = _uiState.value.exitResult?.invoiceId ?: _uiState.value.exitResult?.sessionId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            repository.notifyAbsent(userId, invoiceId)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
        }
    }

    fun clearPaymentResult() {
        _uiState.update { it.copy(paymentResult = null) }
    }

    fun clearResult() {
        _uiState.update { it.copy(exitResult = null) }
    }
}