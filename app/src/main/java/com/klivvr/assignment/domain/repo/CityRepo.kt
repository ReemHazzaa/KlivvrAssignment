package com.klivvr.assignment.domain.repo

import androidx.paging.PagingData
import com.klivvr.assignment.data.models.City
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * Defines the contract for the City Repository.
 * This interface abstracts the data source for city information, allowing for
 * different implementations (e.g., local JSON, remote API) to be used interchangeably.
 */
interface CityRepo {

    /**
     * A StateFlow representing the loading state of the data.
     * The repository uses it to tell the rest of the app when it's busy loading the JSON file.
     */
    val isDataLoading: StateFlow<Boolean>

    /**
     * Loads the initial data set of cities into memory.
     * This should be called before performing any search operations.
     */
    suspend fun loadCities()

    /**
     * Returns a paginated flow of cities that match a given search prefix.
     *
     * @param prefix The prefix string to filter cities by.
     * @return A Flow of PagingData containing the matching cities.
     */
    fun getPaginatedCities(prefix: String): Flow<PagingData<City>>

    /**
     * Returns the total count of cities that match a given search prefix.
     *
     * @param prefix The prefix string to filter cities by.
     * @return The integer count of matching cities.
     */
    suspend fun getCityCount(prefix: String): Int
}
