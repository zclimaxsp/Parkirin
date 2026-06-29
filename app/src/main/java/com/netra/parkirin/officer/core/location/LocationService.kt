package com.netra.parkirin.officer.core.location

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

data class GpsLocation(
    val lat: Double,
    val lng: Double,
    val accuracy: Float,
    val timestamp: Long = System.currentTimeMillis()
)

enum class GpsState {
    IDLE, ACQUIRING, READY, FAILED, BLOCKED
}

@Singleton
class LocationService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val _gpsState = MutableStateFlow(GpsState.IDLE)
    val gpsState: StateFlow<GpsState> = _gpsState

    private var _lastLocation: GpsLocation? = null
    val lastLocation: GpsLocation? get() = _lastLocation

    @SuppressLint("MissingPermission")
    suspend fun requestLocation(timeoutMs: Long = 30000L): Result<GpsLocation> {
        _gpsState.value = GpsState.ACQUIRING

        return suspendCancellableCoroutine { continuation ->
            val cancellationTokenSource = CancellationTokenSource()

            continuation.invokeOnCancellation {
                cancellationTokenSource.cancel()
            }

            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            ).addOnSuccessListener { location ->
                if (location != null && location.accuracy <= 50f) {
                    val gpsLocation = GpsLocation(
                        lat = location.latitude,
                        lng = location.longitude,
                        accuracy = location.accuracy
                    )
                    _lastLocation = gpsLocation
                    _gpsState.value = GpsState.READY
                    continuation.resume(Result.success(gpsLocation))
                } else {
                    _gpsState.value = GpsState.FAILED
                    continuation.resume(Result.failure(Exception("GPS accuracy too low or unavailable")))
                }
            }.addOnFailureListener { e ->
                _gpsState.value = GpsState.FAILED
                continuation.resume(Result.failure(e))
            }
        }
    }

    fun stopLocationUpdates() {
        _gpsState.value = GpsState.IDLE
    }
}