package com.klivvr.assignment.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.klivvr.assignment.data.City
import com.klivvr.assignment.data.CityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(
    FlowPreview::class,
    ExperimentalCoroutinesApi::class
)
@HiltViewModel
class CityViewModel @Inject constructor(
    cityRepository: CityRepository,
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val cityPagingFlow: Flow<PagingData<City>>

    init {
        // Initial load of all city data into the Trie
        viewModelScope.launch {
            cityRepository.loadCities()
        }

        // `flatMapLatest` is perfect here. It cancels the previous search
        // and starts a new one whenever the search query changes.
        cityPagingFlow = searchQuery
            .debounce(300L)
            .flatMapLatest { query ->
                cityRepository.getPaginatedCities(query)
            }
            .cachedIn(viewModelScope) // Cache the results in the ViewModel scope
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }
}