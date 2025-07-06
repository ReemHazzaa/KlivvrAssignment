package com.klivvr.assignment.ui.screens.city.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.klivvr.assignment.data.models.openInMap
import com.klivvr.assignment.ui.screens.city.models.UiModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CityList(lazyPagingItems: LazyPagingItems<UiModel>) {
    val context = LocalContext.current
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        val itemCount = lazyPagingItems.itemCount
        for (index in 0 until itemCount) {
            val item = lazyPagingItems.peek(index)

            // Check the type of item to decide which function to call
            if (item is UiModel.HeaderItem) {
                // Use the special stickyHeader function for headers
                stickyHeader(key = "header_${item.letter}") {
                    HeaderItemRow(letter = item.letter.toString())
                }
            } else if (item is UiModel.CityItem) {
                // Use the regular item function for cities
                item(key = "city_${item.city.id}") {
                    // It's important to use lazyPagingItems[index] here to ensure
                    // the Paging library loads the next page when needed.
                    val cityItem = lazyPagingItems[index] as? UiModel.CityItem
                    if (cityItem != null) {
                        CityItemRow(
                            city = cityItem.city,
                            onCityClick = { cityItem.city.openInMap(context) }
                        )
                    }
                }
            }
        }
    }
}
