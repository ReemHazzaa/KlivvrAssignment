package com.klivvr.assignment.data.repo

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.klivvr.assignment.data.models.City
import com.klivvr.assignment.data.pagingSource.CityPagingSource
import com.klivvr.assignment.data.search.Trie
import com.klivvr.assignment.domain.repo.CityRepo
import com.klivvr.assignment.domain.search.SearchAlgorithm
import com.klivvr.assignment.util.Constants.PAGE_SIZE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

/**
 * Repository responsible for loading and searching city data from a local JSON file
 * and serving it in a paginated form using Paging 3.
 *
 * This class internally builds a [Trie] for fast prefix-based searching.
 *
 * @param context The Android application context, used to access the assets folder.
 */
class CityRepoImpl @Inject constructor(
    private val context: Context,
    private val searchAlgorithm: SearchAlgorithm,
) : CityRepo {

    private val _isDataLoading = MutableStateFlow(true)
    override val isDataLoading: StateFlow<Boolean> = _isDataLoading.asStateFlow()

    // The full list of cities loaded from the JSON file
    private var allCities: List<City> = emptyList()

    /**
     * Loads city data from assets/cities.json once and caches it.
     * This function also populates the Trie for fast searching.
     *
     * This should be called before performing searches.
     *
     * It's safe to call this multiple times; it only loads once.
     */
    override suspend fun loadCities() {
        // Skip if already loaded
        if (allCities.isNotEmpty()) {
            _isDataLoading.value = false // If already loaded, we're not loading.
            return
        }

        withContext(Dispatchers.IO) {
            try {
                _isDataLoading.value = true // Set loading to TRUE before starting
                // Read JSON from assets as a string
                val jsonString = context.assets.open("cities.json")
                    .bufferedReader()
                    .use { it.readText() }

                // Parse the JSON string into a list of City objects using Gson
                val gson = Gson()
                val listType = object : TypeToken<List<City>>() {}.type
                val cities: List<City> = gson.fromJson(jsonString, listType)

                // Cache the cities and insert them into the Trie
                allCities = cities
                cities.forEach { searchAlgorithm.insert(it) }
            } catch (ioException: IOException) {
                // Log the error if file not found or unreadable
                ioException.printStackTrace()
            } finally {
                _isDataLoading.value = false // Set loading to FALSE when done
            }
        }
    }

    /**
     * Returns a paginated list of cities matching the given prefix.
     *
     * If the prefix is blank, returns an empty list.
     *
     * @param prefix The prefix string used for search (case-insensitive).
     * @return A [kotlinx.coroutines.flow.Flow] of [androidx.paging.PagingData] containing matched [City] items.
     */
    override fun getPaginatedCities(prefix: String): Flow<PagingData<City>> {
        // Get search results from the Trie or empty if prefix is blank
        val searchResults = if (prefix.isBlank()) {
            emptyList()
        } else {
            searchAlgorithm.search(prefix).sortedBy { it.name } // Sort alphabetically
        }

        // Return a Flow of paginated results using Paging 3
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE, // Number of items per page
                enablePlaceholders = false // No empty placeholders for unloaded data
            ),
            pagingSourceFactory = { CityPagingSource(searchResults) }
        ).flow
    }

    /**
     * Gets the total count of cities for a given search prefix.
     * This is more efficient than fetching the whole list if we only need the count.
     * @param prefix The prefix to search for.
     * @return The number of matching cities.
     */
    override suspend fun getCityCount(prefix: String): Int {
        return withContext(Dispatchers.Default) {
            // Return 0 for blank query, otherwise search the trie and get the size.
            if (prefix.isBlank()) 0 else searchAlgorithm.search(prefix).size
        }
    }

}