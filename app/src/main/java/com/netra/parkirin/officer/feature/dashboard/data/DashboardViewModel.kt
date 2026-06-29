package com.netra.parkirin.officer.feature.dashboard.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.netra.parkirin.officer.core.network.DashboardSummaryResponse
import com.netra.parkirin.officer.core.network.OfficerResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val isLoading: Boolean = false,
    val isSummaryLoading: Boolean = false,
    val officer: OfficerResponse? = null,
    val streetName: String? = null,
    val zoneName: String? = null,
    val zoneId: String? = null,
    val summary: DashboardSummaryResponse? = null,
    val totalSlots: Int = 100, // Hardcoded for now, or fetch from zone if available
    val errorMessage: String? = null
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: DashboardRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState

    private val userId = "0ca9d1f2-6baa-46b6-9b1b-55a7b4f0a087" // TODO: Get from Auth session

    init {
        loadDashboardData(userId)
    }

    fun refresh() {
        loadDashboardData(userId)
    }

    fun loadDashboardData(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            loadMyProfile(userId)
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private suspend fun loadMyProfile(userId: String) {
        repository.getMyProfile(userId)
            .onSuccess { officer ->
                _uiState.update { it.copy(officer = officer) }

                officer.assignedStreetId?.let { streetId ->
                    repository.getStreetById(streetId)
                        .onSuccess { street ->
                            _uiState.update { it.copy(streetName = street.name) }

                            street.idTariffZone?.let { zoneId ->
                                repository.getZoneById(zoneId)
                                    .onSuccess { zone ->
                                        _uiState.update {
                                            it.copy(zoneName = zone.name, zoneId = zoneId)
                                        }
                                        // Panggil summary setelah dapat zoneId
                                        loadSummary(userId, zoneId)
                                    }
                            }
                        }
                }
            }
            .onFailure { error ->
                _uiState.update { it.copy(errorMessage = error.message) }
            }
    }

    private suspend fun loadSummary(userId: String, zoneId: String) {
        _uiState.update { it.copy(isSummaryLoading = true) }
        repository.getTodaySummary(userId, zoneId)
            .onSuccess { summary ->
                _uiState.update { it.copy(summary = summary, isSummaryLoading = false) }
            }
            .onFailure { error ->
                android.util.Log.e("DASHBOARD_VM", "Gagal load summary", error)
                _uiState.update { it.copy(isSummaryLoading = false) }
            }
    }
}