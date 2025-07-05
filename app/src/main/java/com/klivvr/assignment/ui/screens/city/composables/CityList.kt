package com.klivvr.assignment.ui.screens.city.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.klivvr.assignment.data.openInMap
import com.klivvr.assignment.ui.screens.city.models.UiModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CityList(lazyPagingItems: LazyPagingItems<UiModel>) {
    val context = LocalContext.current
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(lazyPagingItems.itemCount, key = { index ->
            // Generate a stable key
            val item = lazyPagingItems.peek(index)
            when (item) {
                is UiModel.CityItem -> "city_${item.city.id}"
                is UiModel.HeaderItem -> "header_${item.letter}"
                else -> "placeholder_$index"
            }
        }) { index ->
            val item = lazyPagingItems[index]
            when (item) {
                is UiModel.CityItem -> {
                    CityRow(
                        city = item.city,
                        onCityClick = { item.city.openInMap(context) }
                    )
                }

                is UiModel.HeaderItem -> {
                    // This is our sticky header
                    Text(
                        text = item.letter.toString(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background)
                            .padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }

                null -> {
                    // Placeholder for when data is loading
                }
            }
        }
    }
}
