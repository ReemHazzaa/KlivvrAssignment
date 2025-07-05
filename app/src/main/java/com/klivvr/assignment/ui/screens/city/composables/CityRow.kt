package com.klivvr.assignment.ui.screens.city.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.klivvr.assignment.data.models.City
import com.klivvr.assignment.ui.theme.flagBackgroundColor
import java.util.Locale

@Composable
fun CityRow(city: City, onCityClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onCityClick),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(80.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.flagBackgroundColor
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text(
                        countryCodeToEmojiFlag(city.country),
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "${city.name}, ${city.country}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    "Lat: ${city.coordinates.lat}, Lon: ${city.coordinates.lon}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Converts a 2-letter country code (e.g., "US") into its corresponding
 * flag emoji (e.g., "🇺🇸").
 */
fun countryCodeToEmojiFlag(countryCode: String): String {
    if (countryCode.length != 2 || !countryCode.all { it.isLetter() }) {
        return "🌍" // Fallback for invalid codes
    }

    return countryCode
        .uppercase(Locale.US)
        .map { char ->
            // This part is correct: converts a letter to a Unicode codepoint for the flag part
            Character.codePointAt("$char", 0) - 'A'.code + 0x1F1E6
        }
        .joinToString(separator = "") { codePoint ->
            // This is the fix: convert each integer codepoint directly to its character representation
            String(Character.toChars(codePoint))
        }
}