package com.klivvr.assignment.ui.screens.city.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.klivvr.assignment.R
import com.klivvr.assignment.ui.screens.city.CityViewModel
import com.klivvr.assignment.ui.screens.city.composables.uiState.EmptyState
import com.klivvr.assignment.ui.screens.city.composables.uiState.ErrorState
import com.klivvr.assignment.ui.screens.city.composables.uiState.LoadingState
import com.klivvr.assignment.ui.screens.city.models.UiModel

@Composable
fun CityScreen(viewModel: CityViewModel = hiltViewModel()) {

    val searchQuery by viewModel.searchQuery.collectAsState()
    val lazyPagingItems: LazyPagingItems<UiModel> =
        viewModel.cityPagingFlow.collectAsLazyPagingItems()
    val cityCount by viewModel.cityCount.collectAsState()

    Scaffold(
        bottomBar = {
            SearchBar(
                query = searchQuery,
                onQueryChange = viewModel::onSearchQueryChange,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 20.dp)
                    .imePadding()
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            CityScreenHeader(itemCount = cityCount)

            Box(modifier = Modifier.weight(1f)) {
                if (lazyPagingItems.itemCount > 0) {
                    CityList(lazyPagingItems = lazyPagingItems)
                }

                // Handle Paging 3 loading/empty states
                lazyPagingItems.loadState.apply {
                    when {
                        refresh is LoadState.Loading -> LoadingState()
                        refresh is LoadState.NotLoading && lazyPagingItems.itemCount == 0 -> {
                            if (searchQuery.isBlank()) {
                                EmptyState(message = stringResource(R.string.start_typing_to_search_for_a_city))
                            } else {
                                EmptyState(
                                    message = stringResource(
                                        R.string.no_cities_found_for,
                                        searchQuery
                                    )
                                )
                            }
                        }

                        refresh is LoadState.Error -> {
                            ErrorState(
                                (refresh as LoadState.Error).error.message
                                    ?: stringResource(R.string.an_unknown_error_occurred)
                            )
                        }
                    }
                }
            }
        }
    }
}
