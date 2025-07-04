package com.klivvr.assignment.ui.composables.uiState

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun EmptyState(message: String) {
    // This outer Column uses weights to position its children.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // This Spacer takes up the top 1 part of the screen, pushing the content down.
        Spacer(modifier = Modifier.weight(1f))

        // This is the content, which will now appear in the top-middle third.
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // If you have a picture, it would go here.
            // Image( ... )
            // Spacer( ... )
            Text(
                text = message,
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // This Spacer takes up the remaining 2 parts of the screen below the content.
        Spacer(modifier = Modifier.weight(2f))
    }
}