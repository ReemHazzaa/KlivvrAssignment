package com.klivvr.assignment.ui.screens.city.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp

@Composable
fun TimelineRow(
    modifier: Modifier = Modifier,
    // The composable for the timeline (the line, circles, etc.)
    timeline: @Composable () -> Unit,
    // The main content (Card)
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = {
            timeline()
            content()
        }
    ) { measurables, constraints ->
        // This logic ensures the timeline and the content are measured correctly.
        // It assumes the timeline has a fixed width of 40.dp.

        // First, measure the main content (Card).
        val contentPlaceable = measurables[1].measure(
            constraints.copy(maxWidth = constraints.maxWidth - 40.dp.roundToPx())
        )

        // Then, measure the timeline, forcing its height to match the card's height.
        val timelinePlaceable = measurables[0].measure(
            Constraints.fixed(
                width = 40.dp.roundToPx(),
                height = contentPlaceable.height
            )
        )

        // Finally, place the timeline and the card side-by-side.
        layout(width = constraints.maxWidth, height = contentPlaceable.height) {
            timelinePlaceable.placeRelative(x = 0, y = 0)
            contentPlaceable.placeRelative(x = 40.dp.roundToPx(), y = 0)
        }
    }
}