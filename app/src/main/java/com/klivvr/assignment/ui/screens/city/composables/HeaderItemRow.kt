package com.klivvr.assignment.ui.screens.city.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.klivvr.assignment.ui.theme.stickyLetterStrokeColor
import com.klivvr.assignment.ui.theme.stickyLetterTextColor

@Composable
fun HeaderItemRow(letter: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxHeight()
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.stickyLetterStrokeColor,
                    shape = CircleShape
                )
                .background(
                    color = Color.White,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = letter,
                color = MaterialTheme.colorScheme.stickyLetterTextColor,
                fontWeight = FontWeight.Bold
            )
        }

        Box(
            modifier = Modifier
                .size(40.dp)
                .fillMaxHeight()
                .width(2.dp)
                .weight(1f)
                .background(MaterialTheme.colorScheme.primary)
        )
    }
}