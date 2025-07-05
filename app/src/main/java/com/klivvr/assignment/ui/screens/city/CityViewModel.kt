package com.klivvr.assignment.ui.screens.city

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.klivvr.assignment.data.models.City
import com.klivvr.assignment.data.repo.CityRepoImpl
import com.klivvr.assignment.domain.repo.CityRepo
import com.klivvr.assignment.ui.screens.city.models.UiModel
import com.klivvr.assignment.util.Constants.SEARCH_DEBOUNCE_TIME
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(
    FlowPreview::class,
    ExperimentalCoroutinesApi::class
)
@HiltViewModel
class CityViewModel @Inject constructor(
    cityRepo: CityRepo,
) : ViewModel() {

    val isInitialLoading: StateFlow<Boolean> = cityRepo.isDataLoading

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _cityCount = MutableStateFlow(0)
    val cityCount: StateFlow<Int> = _cityCount.asStateFlow()

    // This flow now emits PagingData of our sealed UiModel
    val cityPagingFlow: Flow<PagingData<UiModel>>


    init {
        // Initial load of all city data into the Trie
        viewModelScope.launch {
            cityRepo.loadCities()
        }

        // This listens for query changes and updates the count separately from the list data.
        viewModelScope.launch {
            searchQuery
                .debounce(SEARCH_DEBOUNCE_TIME) // Debounce to avoid rapid updates
                .collect { query ->
                    _cityCount.value = cityRepo.getCityCount(query)
                }
        }

        // `flatMapLatest` is perfect here. It cancels the previous search
        // and starts a new one whenever the search query changes.
        cityPagingFlow = searchQuery
            .debounce(SEARCH_DEBOUNCE_TIME)
            .flatMapLatest { query ->
                // This function in the repository returns Flow<PagingData<City>>
                cityRepo.getPaginatedCities(query)
            }
            .map { pagingData: PagingData<City> ->
                // Transform each City object into a UiModel.CityItem
                pagingData.map { city ->
                    UiModel.CityItem(city)
                }
            }
            .map { pagingData ->
                // Insert separators (our letter headers)
                pagingData.insertSeparators { before: UiModel.CityItem?, after: UiModel.CityItem? ->
                    if (after == null) {
                        // End of the list
                        return@insertSeparators null
                    }
                    if (before == null) {
                        // Start of the list
                        return@insertSeparators UiModel.HeaderItem(
                            after.city.name.first().uppercaseChar()
                        )
                    }
                    if (before.city.name.first().uppercaseChar() != after.city.name.first()
                            .uppercaseChar()
                    ) {
                        // A new letter group starts
                        UiModel.HeaderItem(after.city.name.first().uppercaseChar())
                    } else {
                        // Same group, no separator needed
                        null
                    }
                }
            }
            .cachedIn(viewModelScope)
    }


    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }
}