package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_routes")
data class SavedRoute(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val startLocation: String,
    val endLocation: String,
    val travelMode: String, // "drive", "boat", "flight"
    val distanceKm: Double = 0.0,
    val durationText: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val username: String = "Traveler"
)

data class ChatMessage(
    val id: Long = System.currentTimeMillis(),
    val text: String,
    val isSent: Boolean // true = sent by user, false = received from assistant
)

// Predefined city coordinates for realistic route map drawing
data class CityLocation(
    val name: String,
    val lat: Double,
    val lng: Double,
    val isOverseasFromUS: Boolean = false
)

object KnownCities {
    val CITIES = listOf(
        CityLocation("New York", 40.7128, -74.0060, false),
        CityLocation("London", 51.5074, -0.1278, true),
        CityLocation("Tokyo", 35.6762, 139.6503, true),
        CityLocation("Paris", 48.8566, 2.3522, true),
        CityLocation("San Francisco", 37.7749, -122.4194, false),
        CityLocation("Los Angeles", 34.0522, -118.2437, false),
        CityLocation("Sydney", -33.8688, 151.2093, true),
        CityLocation("Chicago", 41.8781, -87.6298, false),
        CityLocation("Miami", 25.7617, -80.1918, false),
        CityLocation("Berlin", 52.5200, 13.4050, true),
        CityLocation("Rome", 41.9028, 12.4964, true)
    )

    fun findCity(name: String): CityLocation? {
        val normalized = name.trim().lowercase()
        return CITIES.firstOrNull { it.name.lowercase().contains(normalized) || normalized.contains(it.name.lowercase()) }
    }
}
