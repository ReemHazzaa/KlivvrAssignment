package com.klivvr.assignment.ui.screens.city.composables

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.klivvr.assignment.R
import com.klivvr.assignment.ui.theme.KlivvrTheme

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean
) {
    var isFocused by remember { mutableStateOf(false) }

    val backgroundColor by animateColorAsState(
        targetValue = if (isFocused) KlivvrTheme.extendedColors.searchBarFocusedBackground
        else KlivvrTheme.extendedColors.searchBarUnfocusedBackground,
        label = "background_color_animation"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isFocused) KlivvrTheme.extendedColors.searchBarFocusedContent
        else KlivvrTheme.extendedColors.searchBarUnfocusedContent,
        label = "content_color_animation"
    )
    val cornerRadius by animateDpAsState(
        targetValue = if (isFocused) 0.dp else 12.dp,
        label = "corner_radius_animation"
    )

    BasicTextField(
        value = query,
        onValueChange = onQueryChange,
        enabled = isEnabled,
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged { focusState ->
                isFocused = focusState.isFocused
            },
        textStyle = TextStyle(
            color = KlivvrTheme.extendedColors.searchBarFocusedContent,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal
        ),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        singleLine = true,
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = backgroundColor, // Animated background color
                        shape = RoundedCornerShape(cornerRadius) // Animated corner radius
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Icon",
                    tint = contentColor // Animated content color
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(modifier = Modifier.weight(1f)) {
                    if (query.isEmpty()) {
                        Text(
                            text = stringResource(R.string.search),
                            style = TextStyle(
                                color = contentColor, // Animated hint color
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal
                            )
                        )
                    }
                    innerTextField()
                }
                if (query.isNotEmpty()) {
                    IconButton(
                        onClick = { onQueryChange("") },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            "Clear search",
                            tint = contentColor // Animated icon color
                        )
                    }
                }
            }
        }
    )
}