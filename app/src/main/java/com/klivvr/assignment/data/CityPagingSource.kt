package com.klivvr.assignment.data

import androidx.paging.PagingSource
import androidx.paging.PagingState

/**
 * A [PagingSource] implementation for paginating a static list of [City] objects.
 *
 * This class divides the given list of cities into pages of data, suitable for use with the
 * Android Paging 3 library.
 *
 * @param cities The complete list of cities to be paginated.
 */
class CityPagingSource(
    private val cities: List<City>
) : PagingSource<Int, City>() {

    /**
     * Loads a page of data.
     *
     * @param params The parameters for loading data, including the key (page index) and load size.
     * @return A [LoadResult.Page] with the requested list of cities and pagination keys.
     */
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, City> {
        // Use the passed key or start from 0 if it's the initial load
        val currentPageIndex = params.key ?: 0
        val pageSize = params.loadSize

        // Calculate the starting index based on page number and size
        val startIndex = currentPageIndex * pageSize

        // Ensure we don't go out of bounds
        // If the starting index is beyond the list size, return an empty page
        if (startIndex >= cities.size) {
            return LoadResult.Page(data = emptyList(), prevKey = null, nextKey = null)
        }

        // Calculate the end index safely to avoid IndexOutOfBounds
        val endIndex = (startIndex + pageSize).coerceAtMost(cities.size)

        // Slice the cities list to get the data for the current page
        val data = cities.subList(startIndex, endIndex)

        // Determine the next key (null if we reached the last page)
        val nextKey = if (endIndex >= cities.size) {
            null // We've reached the end
        } else {
            currentPageIndex + 1
        }

        // Return the loaded page, with previous and next page keys
        return LoadResult.Page(
            data = data,
            prevKey = if (currentPageIndex == 0) null else currentPageIndex - 1,
            nextKey = nextKey
        )
    }

    /**
     * Returns the key to use for reloading data after a refresh (e.g., swipe-to-refresh).
     * The refresh key is used for subsequent calls to PagingSource.load after the initial load.
     *
     * Paging uses this key to decide where to resume loading.
     * It typically anchors the refresh around the currently visible position.
     *
     * @param state The current paging state.
     * @return The refresh key, or null if it cannot be determined.
     */
    override fun getRefreshKey(state: PagingState<Int, City>): Int? {
        // Try to find the closest page to the current anchor (visible) position
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}