package com.klivvr.assignment.ui.screens.city

import androidx.paging.PagingData
import androidx.recyclerview.widget.DiffUtil
import app.cash.turbine.test
import com.klivvr.assignment.util.AsyncPagingDataDifferTestUtil
import com.klivvr.assignment.data.models.City
import com.klivvr.assignment.data.repo.CityRepoImpl
import com.klivvr.assignment.data.models.Coordinates
import com.klivvr.assignment.ui.screens.city.models.UiModel
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@Suppress("UnusedFlow")
@OptIn(ExperimentalCoroutinesApi::class)
class CityViewModelTest {

    private lateinit var viewModel: CityViewModel
    private lateinit var cityRepoImpl: CityRepoImpl

    private val testDispatcher = StandardTestDispatcher()

    private val fakeCities = listOf(
        City("UK", "London", 1, Coordinates(0.0, 0.0)),
        City("USA", "Los Angeles", 2, Coordinates(-118.2, 34.0))
    )

    private val fakeUiModels = listOf(
        UiModel.HeaderItem('L'),
        UiModel.CityItem(fakeCities[0]),
        UiModel.HeaderItem('L'),
        UiModel.CityItem(fakeCities[1])
    )

    private val differUtil = AsyncPagingDataDifferTestUtil(
        object : DiffUtil.ItemCallback<UiModel>() {
            override fun areItemsTheSame(oldItem: UiModel, newItem: UiModel): Boolean =
                oldItem.hashCode() == newItem.hashCode()

            override fun areContentsTheSame(oldItem: UiModel, newItem: UiModel): Boolean =
                oldItem == newItem
        }
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        cityRepoImpl = mockk(relaxed = true)

        coEvery { cityRepoImpl.loadCities() } returns Unit
        coEvery { cityRepoImpl.getPaginatedCities("lon") } returns flowOf(
            PagingData.Companion.from(
                listOf(
                    fakeCities[0]
                )
            )
        )
        coEvery { cityRepoImpl.getPaginatedCities("los") } returns flowOf(
            PagingData.Companion.from(
                listOf(
                    fakeCities[1]
                )
            )
        )

        viewModel = CityViewModel(cityRepoImpl)
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
            Assert.assertEquals(listOf(fakeUiModels[0], fakeUiModels[1]), snapshot)
            cancelAndIgnoreRemainingEvents()
        }

        coVerify(exactly = 1) { cityRepoImpl.loadCities() }
        coVerify(exactly = 1) { cityRepoImpl.getPaginatedCities("lon") }
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
            Assert.assertEquals(listOf(fakeUiModels[2], fakeUiModels[3]), snapshot)
            cancelAndIgnoreRemainingEvents()
        }

        coVerify(exactly = 0) { cityRepoImpl.getPaginatedCities("lo") }
        coVerify(exactly = 1) { cityRepoImpl.getPaginatedCities("los") }
    }

    @Test
    fun `loadCities is only called once when ViewModel is created`() = runTest {
        // Already called in setup
        coVerify(exactly = 1) { cityRepoImpl.loadCities() }

        // Change query — should not re-call loadCities
        viewModel.onSearchQueryChange("london")
        advanceTimeBy(300)

        coVerify(exactly = 1) { cityRepoImpl.loadCities() }
    }

    @Test
    fun `cityPagingFlow emits empty when no results match prefix`() = runTest {
        coEvery { cityRepoImpl.getPaginatedCities("zzz") } returns flowOf(PagingData.Companion.empty())

        viewModel.onSearchQueryChange("zzz")
        advanceTimeBy(300)

        viewModel.cityPagingFlow.test {
            val snapshot = differUtil.snapshot(awaitItem())
            Assert.assertTrue(snapshot.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }

        coVerify(exactly = 1) { cityRepoImpl.getPaginatedCities("zzz") }
    }

    @Test
    fun `cityPagingFlow does not emit before debounce period`() = runTest {
        viewModel.onSearchQueryChange("los")

        // Do NOT advance time — we're still within the debounce window

        viewModel.cityPagingFlow.test {
            expectNoEvents() // should not emit anything yet
            cancelAndIgnoreRemainingEvents()
        }

        coVerify(exactly = 0) { cityRepoImpl.getPaginatedCities(any()) }
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
            Assert.assertEquals(listOf(fakeUiModels[2], fakeUiModels[3]), snapshot)
            cancelAndIgnoreRemainingEvents()
        }

        coVerify(exactly = 1) { cityRepoImpl.getPaginatedCities("los") }
        coVerify(exactly = 0) { cityRepoImpl.getPaginatedCities("l") }
        coVerify(exactly = 0) { cityRepoImpl.getPaginatedCities("lo") }
    }

    @Test
    fun `searchQuery updates correctly on input`() = runTest {
        viewModel.onSearchQueryChange("Cairo")
        Assert.assertEquals("Cairo", viewModel.searchQuery.value)

        viewModel.onSearchQueryChange("")
        Assert.assertEquals("", viewModel.searchQuery.value)
    }

    @Test
    fun `multiple collectors receive same paging data`() = runTest {
        viewModel.onSearchQueryChange("los")
        advanceTimeBy(300)

        val resultsA = async { differUtil.snapshot(viewModel.cityPagingFlow.first()) }
        val resultsB = async { differUtil.snapshot(viewModel.cityPagingFlow.first()) }

        Assert.assertEquals(resultsA.await(), resultsB.await())
        coVerify(exactly = 1) { cityRepoImpl.getPaginatedCities("los") }
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
            Assert.assertEquals(listOf(fakeUiModels[0], fakeUiModels[1]), snapshot)
            cancelAndIgnoreRemainingEvents()
        }

        coVerify(exactly = 1) { cityRepoImpl.getPaginatedCities("lon") }
        coVerify(exactly = 0) { cityRepoImpl.getPaginatedCities("l") }
        coVerify(exactly = 0) { cityRepoImpl.getPaginatedCities("lo") }
    }

}