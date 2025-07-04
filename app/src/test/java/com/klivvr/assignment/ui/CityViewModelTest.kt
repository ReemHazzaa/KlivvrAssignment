package com.klivvr.assignment.ui

import androidx.paging.PagingData
import app.cash.turbine.test
import com.klivvr.assignment.data.City
import com.klivvr.assignment.data.CityRepository
import com.klivvr.assignment.data.Coordinates
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import androidx.recyclerview.widget.DiffUtil
import com.klivvr.assignment.AsyncPagingDataDifferTestUtil
import io.mockk.clearAllMocks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import org.junit.After
import org.junit.Assert.assertTrue

@Suppress("UnusedFlow")
@OptIn(ExperimentalCoroutinesApi::class)
class CityViewModelTest {

    private lateinit var viewModel: CityViewModel
    private lateinit var cityRepository: CityRepository

    private val testDispatcher = StandardTestDispatcher()

    private val fakeCities = listOf(
        City("UK", "London", 1, Coordinates(0.0, 0.0)),
        City("USA", "Los Angeles", 2, Coordinates(-118.2, 34.0))
    )

    private val differUtil = AsyncPagingDataDifferTestUtil(
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
        cityRepository = mockk(relaxed = true)

        coEvery { cityRepository.loadCities() } returns Unit
        coEvery { cityRepository.getPaginatedCities("lon") } returns flowOf(
            PagingData.from(
                listOf(
                    fakeCities[0]
                )
            )
        )
        coEvery { cityRepository.getPaginatedCities("los") } returns flowOf(
            PagingData.from(
                listOf(
                    fakeCities[1]
                )
            )
        )

        viewModel = CityViewModel(cityRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `cityPagingFlow emits expected PagingData when search query changes`() = runTest {
        viewModel.onSearchQueryChange("lon")
        advanceTimeBy(300) // debounce

        viewModel.cityPagingFlow.test {
            val pagingData = awaitItem()
            val snapshot = differUtil.snapshot(pagingData)
            assertEquals(listOf(fakeCities[0]), snapshot)
            cancelAndIgnoreRemainingEvents()
        }

        coVerify(exactly = 1) { cityRepository.loadCities() }
        coVerify(exactly = 1) { cityRepository.getPaginatedCities("lon") }
    }

    @Test
    fun `cityPagingFlow cancels previous search and emits new results`() = runTest {
        viewModel.onSearchQueryChange("lo")
        advanceTimeBy(300)

        viewModel.onSearchQueryChange("los")
        advanceTimeBy(300)

        viewModel.cityPagingFlow.test {
            val pagingData = awaitItem()
            val snapshot = differUtil.snapshot(pagingData)
            assertEquals(listOf(fakeCities[1]), snapshot)
            cancelAndIgnoreRemainingEvents()
        }

        coVerify(exactly = 0) { cityRepository.getPaginatedCities("lo") }
        coVerify(exactly = 1) { cityRepository.getPaginatedCities("los") }
    }

    @Test
    fun `loadCities is only called once when ViewModel is created`() = runTest {
        // Already called in setup
        coVerify(exactly = 1) { cityRepository.loadCities() }

        // Change query — should not re-call loadCities
        viewModel.onSearchQueryChange("london")
        advanceTimeBy(300)

        coVerify(exactly = 1) { cityRepository.loadCities() }
    }

    @Test
    fun `cityPagingFlow emits empty when no results match prefix`() = runTest {
        coEvery { cityRepository.getPaginatedCities("zzz") } returns flowOf(PagingData.empty())

        viewModel.onSearchQueryChange("zzz")
        advanceTimeBy(300)

        viewModel.cityPagingFlow.test {
            val snapshot = differUtil.snapshot(awaitItem())
            assertTrue(snapshot.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }

        coVerify(exactly = 1) { cityRepository.getPaginatedCities("zzz") }
    }

    @Test
    fun `cityPagingFlow does not emit before debounce period`() = runTest {
        viewModel.onSearchQueryChange("los")

        // Do NOT advance time — we're still within the debounce window

        viewModel.cityPagingFlow.test {
            expectNoEvents() // should not emit anything yet
            cancelAndIgnoreRemainingEvents()
        }

        coVerify(exactly = 0) { cityRepository.getPaginatedCities(any()) }
    }

    @Test
    fun `cityPagingFlow only emits for last query when input changes rapidly`() = runTest {
        viewModel.onSearchQueryChange("l")
        advanceTimeBy(100)
        viewModel.onSearchQueryChange("lo")
        advanceTimeBy(100)
        viewModel.onSearchQueryChange("los")
        advanceTimeBy(300) // now only "los" is emitted

        viewModel.cityPagingFlow.test {
            val snapshot = differUtil.snapshot(awaitItem())
            assertEquals(listOf(fakeCities[1]), snapshot)
            cancelAndIgnoreRemainingEvents()
        }

        coVerify(exactly = 1) { cityRepository.getPaginatedCities("los") }
        coVerify(exactly = 0) { cityRepository.getPaginatedCities("l") }
        coVerify(exactly = 0) { cityRepository.getPaginatedCities("lo") }
    }

    @Test
    fun `searchQuery updates correctly on input`() = runTest {
        viewModel.onSearchQueryChange("Cairo")
        assertEquals("Cairo", viewModel.searchQuery.value)

        viewModel.onSearchQueryChange("")
        assertEquals("", viewModel.searchQuery.value)
    }

    @Test
    fun `multiple collectors receive same paging data`() = runTest {
        viewModel.onSearchQueryChange("los")
        advanceTimeBy(300)

        val resultsA = async { differUtil.snapshot(viewModel.cityPagingFlow.first()) }
        val resultsB = async { differUtil.snapshot(viewModel.cityPagingFlow.first()) }

        assertEquals(resultsA.await(), resultsB.await())
        coVerify(exactly = 1) { cityRepository.getPaginatedCities("los") }
    }

    @Test
    fun `debounce delay affects flow emission timing`() = runTest {
        viewModel.onSearchQueryChange("l")
        advanceTimeBy(200)
        viewModel.onSearchQueryChange("lo")
        advanceTimeBy(100)
        viewModel.onSearchQueryChange("lon")
        advanceTimeBy(250) // now only "lon" is emitted

        viewModel.cityPagingFlow.test {
            val snapshot = differUtil.snapshot(awaitItem())
            assertEquals(listOf(fakeCities[0]), snapshot)
            cancelAndIgnoreRemainingEvents()
        }

        coVerify(exactly = 1) { cityRepository.getPaginatedCities("lon") }
        coVerify(exactly = 0) { cityRepository.getPaginatedCities("l") }
        coVerify(exactly = 0) { cityRepository.getPaginatedCities("lo") }
    }


}