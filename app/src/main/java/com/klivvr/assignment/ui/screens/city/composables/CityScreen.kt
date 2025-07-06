package com.klivvr.assignment.ui.screens.city.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.klivvr.assignment.ui.theme.KlivvrTheme
import com.klivvr.assignment.ui.theme.cityBackgroundColor

@Composable
fun CityScreen(viewModel: CityViewModel = hiltViewModel()) {

    val searchQuery by viewModel.searchQuery.collectAsState()
    val lazyPagingItems: LazyPagingItems<UiModel> =
        viewModel.cityPagingFlow.collectAsLazyPagingItems()
    val cityCount by viewModel.cityCount.collectAsState()

    val isInitialLoading by viewModel.isInitialLoading.collectAsState()

    var isSearchBarFocused by remember { mutableStateOf(false) }
    val searchBarVerticalPadding by animateDpAsState(
        targetValue = if (isSearchBarFocused) 0.dp else 20.dp,
        label = "searchBarVerticalPadding"
    )
    val searchBarHorizontalPadding by animateDpAsState(
        targetValue = if (isSearchBarFocused) 0.dp else 16.dp,
        label = "searchBarHorizontalPadding"
    )

    Scaffold(
        bottomBar = {
            Column(
                modifier = Modifier.background(MaterialTheme.colorScheme.surface)
            ) {
                AnimatedVisibility(visible = !isSearchBarFocused) {
                    HorizontalDivider(
                        thickness = 1.dp,
                        // Use the same color you chose for the unfocused search bar background.
                        color = KlivvrTheme.extendedColors.searchBarUnfocusedBackground
                    )
                }

                SearchBar(
                    query = searchQuery,
                    onQueryChange = viewModel::onSearchQueryChange,
                    isEnabled = !isInitialLoading,
                    isFocused = isSearchBarFocused,
                    onFocusChange = { isSearchBarFocused = it },
                    modifier = Modifier
                        .padding(horizontal = searchBarHorizontalPadding, vertical = searchBarVerticalPadding)
                        .imePadding()
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.cityBackgroundColor,
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
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
                            refresh is LoadState.Loading -> if (!isInitialLoading) LoadingState()
                            refresh is LoadState.NotLoading && lazyPagingItems.itemCount == 0 -> {
                                if (searchQuery.isBlank()) {
                                    if (!isInitialLoading)
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
            // This will show the progress bar on top of everything when loading
            if (isInitialLoading) {
                LoadingState()
            }
        }
    }
}
