package com.klivvr.assignment.data

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
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
class CityRepository @Inject constructor(
    private val context: Context,
    private val trie: Trie,
) {

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
    suspend fun loadCities() {
        // Skip if already loaded
        if (allCities.isNotEmpty()) return

        withContext(Dispatchers.IO) {
            try {
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
                cities.forEach { trie.insert(it) }
            } catch (ioException: IOException) {
                // Log the error if file not found or unreadable
                ioException.printStackTrace()
            }
        }
    }

    /**
     * Returns a paginated list of cities matching the given prefix.
     *
     * If the prefix is blank, returns an empty list.
     *
     * @param prefix The prefix string used for search (case-insensitive).
     * @return A [Flow] of [PagingData] containing matched [City] items.
     */
    fun getPaginatedCities(prefix: String): Flow<PagingData<City>> {
        // Get search results from the Trie or empty if prefix is blank
        val searchResults = if (prefix.isBlank()) {
            emptyList()
        } else {
            trie.search(prefix).sortedBy { it.name } // Sort alphabetically
        }

        // Return a Flow of paginated results using Paging 3
        return Pager(
            config = PagingConfig(
                pageSize = 20, // Number of items per page
                enablePlaceholders = false // No empty placeholders for unloaded data
            ),
            pagingSourceFactory = { CityPagingSource(searchResults) }
        ).flow
    }
}
