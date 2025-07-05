package com.klivvr.assignment.domain.search

import com.klivvr.assignment.data.models.City

/**
 * Defines the contract for a Trie data structure used for prefix-based searching.
 * This interface allows for different Trie implementations to be used interchangeably
 * throughout the application, promoting modularity and testability.
 */
interface SearchAlgorithm {
    /**
     * Inserts a city into the data structure.
     * @param city The City object to be added.
     */
    fun insert(city: City)

    /**
     * Searches for all cities that match the given prefix.
     * The search should be case-insensitive.
     * @param prefix The prefix string to search for.
     * @return A list of matching City objects.
     */
    fun search(prefix: String): List<City>

    /**
     * Clears all entries from the data structure, resetting it to an empty state.
     */
    fun clear()
}
