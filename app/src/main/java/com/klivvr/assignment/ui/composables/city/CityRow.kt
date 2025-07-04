package com.klivvr.assignment.ui.composables.city

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.klivvr.assignment.data.City
import java.util.Locale

@Composable
fun CityRow(
    city: City,
    modifier: Modifier = Modifier,
    onCityClick: (City) -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCityClick(city) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Circular Flag Background
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = countryCodeToEmojiFlag(city.country),
                    fontSize = 24.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Title and Subtitle
            Column {
                Text(
                    text = "${city.name}, ${city.country}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${city.coordinates.lat}, ${city.coordinates.lon}", // Comma separated
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
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