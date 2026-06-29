package com.netra.parkirin.officer.feature.history.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.netra.parkirin.officer.core.network.ParkingSessionResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class HistoryUiState(
    val isLoading: Boolean = false,
    val history: List<ParkingSessionResponse> = emptyList(),
    val errorMessage: String? = null
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: HistoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState

    // TODO: ganti dengan userId dari token login
    private val userId = "0ca9d1f2-6baa-46b6-9b1b-55a7b4f0a087"

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            repository.getMyProfile(userId)
                .onSuccess { officer ->
                    officer.assignedStreetId?.let { streetId ->
                        repository.getStreetById(streetId)
                            .onSuccess { street ->
                                street.idTariffZone?.let { zoneId ->
                                    loadHistory(zoneId)
                                } ?: run {
                                    _uiState.update { it.copy(isLoading = false, errorMessage = "Zone ID not found") }
                                }
                            }
                            .onFailure { error ->
                                _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                            }
                    } ?: run {
                        _uiState.update { it.copy(isLoading = false, errorMessage = "Street not assigned") }
                    }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
        }
    }

    private fun loadHistory(zoneId: String) {
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        viewModelScope.launch {
            repository.getTodayHistory(userId, zoneId, today)
                .onSuccess { allSessions ->
                    val completedOnly =
                        allSessions.filter {
                            it.status == "PAID"
                        }
                    _uiState.update { it.copy(isLoading = false, history = completedOnly) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
        }
    }
}
