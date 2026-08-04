package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ChatMessage
import com.example.data.KnownCities
import com.example.data.RouteDatabase
import com.example.data.RouteRepository
import com.example.data.SavedRoute
import com.example.ui.theme.AppSkin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class RouteBuddyViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: RouteRepository

    val activeSkin = MutableStateFlow(AppSkin.LIGHT)
    val startLocation = MutableStateFlow("New York")
    val endLocation = MutableStateFlow("London")
    val travelMode = MutableStateFlow("drive") // "drive", "boat", "flight"

    val searchQuery = MutableStateFlow("")

    val currentUser = MutableStateFlow<String?>("Traveler")
    val isLoggedIn = MutableStateFlow(false)

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                text = "Welcome! Enter starting & destination locations to navigate.",
                isSent = false
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    init {
        val dao = RouteDatabase.getDatabase(application).routeDao()
        repository = RouteRepository(dao)
    }

    val savedRoutes: StateFlow<List<SavedRoute>> = repository.allRoutes
        .combine(searchQuery) { routes, query ->
            if (query.isBlank()) {
                routes
            } else {
                routes.filter { route ->
                    route.startLocation.contains(query, ignoreCase = true) ||
                            route.endLocation.contains(query, ignoreCase = true) ||
                            route.travelMode.contains(query, ignoreCase = true)
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun setSkin(skin: AppSkin) {
        activeSkin.value = skin
    }

    fun setStartLocation(loc: String) {
        startLocation.value = loc
    }

    fun setEndLocation(loc: String) {
        endLocation.value = loc
    }

    fun setTravelMode(mode: String) {
        travelMode.value = mode
    }

    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun calculateAndSaveRoute() {
        val start = startLocation.value.trim()
        val end = endLocation.value.trim()
        var currentMode = travelMode.value.lowercase()

        if (start.isBlank() || end.isBlank()) {
            addChatMessage("Please enter both starting location and destination.", isSent = false)
            return
        }

        val startCity = KnownCities.findCity(start)
        val endCity = KnownCities.findCity(end)

        val isOversea = checkIfOversea(startCity?.name ?: start, endCity?.name ?: end)

        // Overseas check logic matching prototype specification
        if (isOversea && currentMode == "drive") {
            addChatMessage("Driving path not available across oceans! Automatically switching mode to 🚤 Boat / Ferry.", isSent = false)
            travelMode.value = "boat"
            currentMode = "boat"
        } else {
            val modeEmoji = getModeEmoji(currentMode)
            addChatMessage("Calculating $currentMode route from $start to $end...", isSent = true)
        }

        // Calculate distance and duration
        val distanceKm = calculateDistanceKm(
            startLat = startCity?.lat ?: 40.7128,
            startLng = startCity?.lng ?: -74.0060,
            endLat = endCity?.lat ?: 51.5074,
            endLng = endCity?.lng ?: -0.1278
        )

        val durationText = formatDuration(distanceKm, currentMode)

        val newRoute = SavedRoute(
            startLocation = startCity?.name ?: start,
            endLocation = endCity?.name ?: end,
            travelMode = currentMode,
            distanceKm = distanceKm,
            durationText = durationText,
            timestamp = System.currentTimeMillis(),
            username = currentUser.value ?: "Traveler"
        )

        viewModelScope.launch {
            repository.insertRoute(newRoute)
            val modeEmoji = getModeEmoji(currentMode)
            addChatMessage(
                text = "Route saved! ${startCity?.name ?: start} ➔ ${endCity?.name ?: end} via $modeEmoji (${String.format("%.1f", distanceKm)} km, ~$durationText)",
                isSent = false
            )
        }
    }

    private fun checkIfOversea(start: String, end: String): Boolean {
        val startLower = start.lowercase()
        val endLower = end.lowercase()

        val isStartUS = startLower.contains("new york") || startLower.contains("san francisco") || startLower.contains("los angeles") || startLower.contains("chicago") || startLower.contains("miami")
        val isEndOverseas = endLower.contains("london") || endLower.contains("tokyo") || endLower.contains("paris") || endLower.contains("sydney") || endLower.contains("berlin") || endLower.contains("rome")

        val isStartOverseas = startLower.contains("london") || startLower.contains("tokyo") || startLower.contains("paris") || startLower.contains("sydney") || startLower.contains("berlin") || startLower.contains("rome")
        val isEndUS = endLower.contains("new york") || endLower.contains("san francisco") || endLower.contains("los angeles") || endLower.contains("chicago") || endLower.contains("miami")

        return (isStartUS && isEndOverseas) || (isStartOverseas && isEndUS) || (startLower.contains("new york") && endLower.contains("london"))
    }

    private fun calculateDistanceKm(startLat: Double, startLng: Double, endLat: Double, endLng: Double): Double {
        val r = 6371.0 // Earth radius in kilometers
        val dLat = Math.toRadians(endLat - startLat)
        val dLng = Math.toRadians(endLng - startLng)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(startLat)) * cos(Math.toRadians(endLat)) *
                sin(dLng / 2) * sin(dLng / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }

    private fun formatDuration(distanceKm: Double, mode: String): String {
        val speedKmH = when (mode) {
            "boat" -> 35.0
            "flight" -> 850.0
            else -> 90.0 // drive
        }
        val hours = distanceKm / speedKmH
        val h = hours.toInt()
        val m = ((hours - h) * 60).toInt()
        return if (h > 0) "${h}h ${m}m" else "${m}m"
    }

    private fun getModeEmoji(mode: String): String {
        return when (mode) {
            "boat" -> "🚤"
            "flight" -> "✈️"
            else -> "🚗"
        }
    }

    private fun addChatMessage(text: String, isSent: Boolean) {
        val current = _chatMessages.value.toMutableList()
        current.add(ChatMessage(text = text, isSent = isSent))
        _chatMessages.value = current
    }

    fun deleteRoute(id: Int) {
        viewModelScope.launch {
            repository.deleteRoute(id)
            addChatMessage("Route deleted from saved routes.", isSent = false)
        }
    }

    fun clearAllRoutes() {
        viewModelScope.launch {
            repository.clearAll()
            addChatMessage("All saved routes cleared.", isSent = false)
        }
    }

    fun login(usernameInput: String) {
        val user = usernameInput.ifBlank { "Traveler" }
        currentUser.value = user
        isLoggedIn.value = true
        addChatMessage("Logged in as $user.", isSent = false)
    }

    fun logout() {
        currentUser.value = null
        isLoggedIn.value = false
        addChatMessage("Logged out.", isSent = false)
    }

    fun loadPresetRoute(start: String, end: String, mode: String) {
        startLocation.value = start
        endLocation.value = end
        travelMode.value = mode
        calculateAndSaveRoute()
    }
}
