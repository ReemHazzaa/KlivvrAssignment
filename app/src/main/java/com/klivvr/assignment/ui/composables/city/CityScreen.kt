package com.klivvr.assignment.ui.composables.city

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.klivvr.assignment.ui.composables.searchBar.AnimatedSearchBar
import com.klivvr.assignment.ui.CityViewModel
import com.klivvr.assignment.ui.composables.uiState.EmptyState
import com.klivvr.assignment.ui.composables.uiState.ErrorState
import com.klivvr.assignment.ui.composables.uiState.LoadingState

@Composable
fun CityScreen(viewModel: CityViewModel = hiltViewModel()) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val lazyPagingItems = viewModel.cityPagingFlow.collectAsLazyPagingItems()

    // Use a Column for the overall screen layout
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding() // Handle status bar overlap
    ) {
        // 1. Top Header
        ScreenHeader(itemCount = lazyPagingItems.itemCount)

        // 2. The List (takes up the remaining space)
        Box(modifier = Modifier.weight(1f)) {
            CityList(lazyPagingItems = lazyPagingItems)

            // Handle Paging 3 loading/empty states
            lazyPagingItems.loadState.apply {
                when {
                    refresh is LoadState.Loading -> LoadingState()
                    refresh is LoadState.NotLoading && lazyPagingItems.itemCount == 0 -> {
                        if (searchQuery.isBlank()) {
                            EmptyState(message = "Start typing to search...")
                        } else {
                            EmptyState(message = "No cities found for '$searchQuery'")
                        }
                    }
                    refresh is LoadState.Error -> {
                        ErrorState((refresh as LoadState.Error).error.message ?: "Unknown error")
                    }
                }
            }
        }

        // 3. Search Bar at the bottom
        AnimatedSearchBar(
            query = searchQuery,
            onQueryChange = viewModel::onSearchQueryChange
        )
    }
}

@Composable
fun ScreenHeader(itemCount: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Text(
            text = "City Search",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = if (itemCount > 0) "$itemCount cities" else "",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
    }
}
