package com.klivvr.assignment.data.repo

import android.content.Context
import android.content.res.AssetManager
import androidx.recyclerview.widget.DiffUtil
import com.google.gson.Gson
import com.klivvr.assignment.util.AsyncPagingDataDifferTestUtil
import com.klivvr.assignment.data.models.City
import com.klivvr.assignment.data.models.Coordinates
import com.klivvr.assignment.data.search.Trie
import com.klivvr.assignment.util.citySpringfieldCA
import com.klivvr.assignment.util.citySpringfieldUS
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayInputStream

@OptIn(ExperimentalCoroutinesApi::class)
class CityRepoImplTest {

    private lateinit var context: Context
    private lateinit var assetManager: AssetManager
    private lateinit var repository: CityRepoImpl

    private lateinit var trie: Trie

    private val testDispatcher = StandardTestDispatcher()

    private val testCities = listOf(
        City("USA", "New York", 1, Coordinates(-74.0060, 40.7128)),
        City("UK", "London", 2, Coordinates(-0.1276, 51.5074)),
        City("France", "Paris", 3, Coordinates(2.3522, 48.8566))
    )

    private val cityDiffer = AsyncPagingDataDifferTestUtil(
        object : DiffUtil.ItemCallback<City>() {
            override fun areItemsTheSame(oldItem: City, newItem: City): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: City, newItem: City): Boolean =
                oldItem == newItem
        }
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        context = mockk()
        assetManager = mockk()
        trie = mockk()
        every { context.assets } returns assetManager
        every { trie.search("") } returns emptyList()
        every { trie.search(any()) } returns testCities
        every { trie.insert(any()) } just Runs
        every { trie.clear() } just Runs

        // Mock assets/cities.json to return test data as JSON
        val jsonString = Gson().toJson(testCities)
        val inputStream = ByteArrayInputStream(jsonString.toByteArray())

        every { assetManager.open("cities.json") } returns inputStream

        repository = CityRepoImpl(context, trie)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `getPaginatedCities reads cities from assets and caches them for valid prefix`() = runTest {
        repository.loadCities()
        val job = launch {
            repository.getPaginatedCities("Lo").collectLatest { pagingData ->
                val snapshotItems = cityDiffer.snapshot(pagingData)
                Assert.assertEquals(listOf("London"), snapshotItems.map { it.name })
            }
        }
        job.cancel()
    }

    @Test
    fun `getPaginatedCities emits empty PagingData when blank prefix`() = runTest {
        repository.loadCities()
        val job = launch {
            repository.getPaginatedCities("").collectLatest { pagingData ->
                val snapshotItems = cityDiffer.snapshot(pagingData)
                Assert.assertTrue(snapshotItems.isEmpty())
            }
        }
        job.cancel()
    }

    @Test
    fun `getPaginatedCities() emits sorted PagingData by name`() = runTest {
        repository.loadCities()
        val job = launch {
            repository.getPaginatedCities("a").collectLatest { pagingData ->
                val snapshotItems = cityDiffer.snapshot(pagingData)
                val sorted = snapshotItems.sortedBy { it.name }
                Assert.assertEquals(sorted, snapshotItems)
            }
        }
        job.cancel()
    }

    @Test
    fun `getPaginatedCities() is case-insensitive when searching by prefix`() = runTest {
        repository.loadCities()
        val job = launch {
            // Mixed-case input; should match "London"
            repository.getPaginatedCities("lO").collectLatest { pagingData ->
                val snapshotItems = cityDiffer.snapshot(pagingData)
                Assert.assertEquals(1, snapshotItems.size)
                Assert.assertEquals("London", snapshotItems.first().name)
            }
        }
        job.cancel()
    }

    @Test
    fun `getCityCount returns 0 when prefix is blank`() = runTest {
        val result = repository.getCityCount("")
        Assert.assertEquals(0, result)
    }

    @Test
    fun `getCityCount returns correct count when prefix is valid`() = runTest {
        val prefix = "Spring"
        val expectedCities = listOf(citySpringfieldUS, citySpringfieldCA)
        every { trie.search(prefix) } returns expectedCities

        val result = repository.getCityCount(prefix)
        Assert.assertEquals(2, result)

        verify { trie.search(prefix) }
    }

}