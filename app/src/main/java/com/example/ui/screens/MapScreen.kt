package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ChatMessage
import com.example.data.KnownCities
import com.example.ui.components.ChatLogView
import com.example.ui.components.InteractiveMapCanvas
import com.example.ui.theme.AppSkin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    startLocation: String,
    endLocation: String,
    travelMode: String,
    chatMessages: List<ChatMessage>,
    activeSkin: AppSkin,
    onStartLocationChange: (String) -> Unit,
    onEndLocationChange: (String) -> Unit,
    onTravelModeChange: (String) -> Unit,
    onCalculateClick: () -> Unit,
    onPresetClick: (String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var modeDropdownExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. Interactive Map Canvas
        InteractiveMapCanvas(
            startCityName = startLocation,
            endCityName = endLocation,
            travelMode = travelMode,
            activeSkin = activeSkin,
            onMapLocationTapped = { city ->
                // Quick tap on map city fills destination
                if (startLocation.isBlank()) {
                    onStartLocationChange(city.name)
                } else {
                    onEndLocationChange(city.name)
                }
            }
        )

        // 2. Preset Quick Cities Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Quick:",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            val presets = listOf(
                Triple("NYC", "London", "drive"), // Triggers overseas auto-switch logic!
                Triple("SF", "LA", "drive"),
                Triple("Tokyo", "Paris", "flight"),
                Triple("Miami", "Rome", "boat")
            )

            presets.forEach { (start, end, mode) ->
                SuggestionChip(
                    onClick = { onPresetClick(start, end, mode) },
                    label = {
                        Text(
                            text = "$start➔$end",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    },
                    modifier = Modifier.testTag("preset_chip_${start}_$end")
                )
            }
        }

        // 3. Inputs Group
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(16.dp)
                )
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Start Location
            OutlinedTextField(
                value = startLocation,
                onValueChange = onStartLocationChange,
                label = { Text("Start location (e.g. New York)") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("start_location_input")
            )

            // Destination Location
            OutlinedTextField(
                value = endLocation,
                onValueChange = onEndLocationChange,
                label = { Text("Destination (e.g. London)") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Navigation,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary
                    )
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("end_location_input")
            )

            // Travel Mode Selector Dropdown
            ExposedDropdownMenuBox(
                expanded = modeDropdownExpanded,
                onExpandedChange = { modeDropdownExpanded = !modeDropdownExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                val modeDisplay = when (travelMode.lowercase()) {
                    "boat" -> "🚤 Boat / Ferry"
                    "flight" -> "✈️ Flight"
                    else -> "🚗 Driving"
                }

                OutlinedTextField(
                    value = modeDisplay,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Travel Mode") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = modeDropdownExpanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .testTag("travel_mode_selector")
                )

                ExposedDropdownMenu(
                    expanded = modeDropdownExpanded,
                    onDismissRequest = { modeDropdownExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("🚗 Driving") },
                        onClick = {
                            onTravelModeChange("drive")
                            modeDropdownExpanded = false
                        },
                        modifier = Modifier.testTag("mode_option_drive")
                    )
                    DropdownMenuItem(
                        text = { Text("🚤 Boat / Ferry") },
                        onClick = {
                            onTravelModeChange("boat")
                            modeDropdownExpanded = false
                        },
                        modifier = Modifier.testTag("mode_option_boat")
                    )
                    DropdownMenuItem(
                        text = { Text("✈️ Flight") },
                        onClick = {
                            onTravelModeChange("flight")
                            modeDropdownExpanded = false
                        },
                        modifier = Modifier.testTag("mode_option_flight")
                    )
                }
            }

            // Calculate & Save Route Button
            Button(
                onClick = onCalculateClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("calculate_route_button"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Directions,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Calculate & Save Route",
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // 4. Assistant Log / Chat Window
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(bottom = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SmartToy,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Assistant Log",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                ChatLogView(
                    messages = chatMessages,
                    activeSkin = activeSkin,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
