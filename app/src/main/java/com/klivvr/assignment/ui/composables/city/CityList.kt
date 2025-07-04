package com.klivvr.assignment.ui.composables.city

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.klivvr.assignment.data.City
import com.klivvr.assignment.data.openInMap
import com.klivvr.assignment.ui.composables.header.Header
import com.klivvr.assignment.ui.composables.header.StickyHeader

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CityList(lazyPagingItems: LazyPagingItems<City>) {
    val context = LocalContext.current

    Box { // Use a Box to draw the line behind everything
        // This is the main vertical timeline
        Box(
            modifier = Modifier
                .padding(start = 28.dp) // Position the line
                .width(2.dp)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Use a temporary variable to handle itemSnapshotList nullability
            val items = lazyPagingItems.itemSnapshotList.items
            if (items.isNotEmpty()) {
                val groupedItems = items.groupBy { it.name.first().uppercaseChar() }

                groupedItems.forEach { (letter, cities) ->
                    stickyHeader {
                        StickyHeader(letter = letter)
                    }

                    items(cities, key = { it.id }) { city ->
                        // Add padding to align the card with the timeline
                        Row {
                            Spacer(modifier = Modifier.width(64.dp))
                            CityRow(
                                city = city,
                                modifier = Modifier.padding(bottom = 12.dp),
                                onCityClick = { city.openInMap(context) }
                            )
                        }
                    }
                }
            }

            // Handle the append state (loading more items at the bottom)
            if (lazyPagingItems.loadState.append is LoadState.Loading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}
