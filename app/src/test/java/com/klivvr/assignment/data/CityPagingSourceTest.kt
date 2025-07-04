package com.klivvr.assignment.data

import androidx.paging.PagingConfig
import androidx.paging.PagingSource.LoadParams
import androidx.paging.PagingSource.LoadResult
import androidx.paging.PagingState
import com.klivvr.assignment.mockCitiesList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CityPagingSourceTest {

    private lateinit var cityList: List<City>
    private lateinit var pagingSource: CityPagingSource

    @Before
    fun setup() {
        cityList = mockCitiesList.dropLast(1)
        pagingSource = CityPagingSource(cityList)
    }

    @Test
    fun `load initial page with default key`() = runTest {
        val result = pagingSource.load(
            LoadParams.Refresh(
                key = null,
                loadSize = 2,
                placeholdersEnabled = false
            )
        )

        val expected = listOf(cityList[0], cityList[1])
        assertTrue(result is LoadResult.Page)
        val page = result as LoadResult.Page
        assertEquals(expected, page.data)
        assertNull(page.prevKey)
        assertEquals(1, page.nextKey)
    }

    @Test
    fun `load second page`() = runTest {
        val result = pagingSource.load(
            LoadParams.Refresh(
                key = 1,
                loadSize = 2,
                placeholdersEnabled = false
            )
        )

        val expected = listOf(cityList[2], cityList[3])
        assertTrue(result is LoadResult.Page)
        val page = result as LoadResult.Page
        assertEquals(expected, page.data)
        assertEquals(0, page.prevKey)
        assertEquals(2, page.nextKey)
    }

    @Test
    fun `load last page with partial data`() = runTest {
        val result = pagingSource.load(
            LoadParams.Refresh(
                key = 2,
                loadSize = 2,
                placeholdersEnabled = false
            )
        )

        val expected = listOf(cityList[4]) // Only one item left
        assertTrue(result is LoadResult.Page)
        val page = result as LoadResult.Page
        assertEquals(expected, page.data)
        assertEquals(1, page.prevKey)
        assertNull(page.nextKey) // No more pages
    }

    @Test
    fun `load with start index out of bounds returns empty page`() = runTest {
        val result = pagingSource.load(
            LoadParams.Refresh(
                key = 3, // 3 * 2 = 6, out of bounds
                loadSize = 2,
                placeholdersEnabled = false
            )
        )

        assertTrue(result is LoadResult.Page)
        val page = result as LoadResult.Page
        assertTrue(page.data.isEmpty())
        assertNull(page.nextKey)
        assertNull(page.prevKey)
    }

    @Test
    fun `getRefreshKey returns correct key near anchor`() {
        val state = PagingState(
            pages = listOf(
                LoadResult.Page(
                    data = listOf(cityList[0], cityList[1]),
                    prevKey = null,
                    nextKey = 1
                ),
                LoadResult.Page(data = listOf(cityList[2], cityList[3]), prevKey = 0, nextKey = 2)
            ),
            anchorPosition = 2,
            config = PagingConfig(pageSize = 2),
            leadingPlaceholderCount = 0
        )

        val refreshKey = pagingSource.getRefreshKey(state)
        assertEquals(1, refreshKey)
    }
}
