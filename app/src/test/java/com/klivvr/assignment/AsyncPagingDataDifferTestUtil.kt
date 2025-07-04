package com.klivvr.assignment

import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import kotlinx.coroutines.Dispatchers

/**
 * How to Use It in Tests
 * If you're testing PagingData<City>:
 *
 * ```
 * val differ = AsyncPagingDataDifferTestUtil(
 *     object : DiffUtil.ItemCallback<City>() {
 *         override fun areItemsTheSame(oldItem: City, newItem: City): Boolean {
 *             return oldItem.id == newItem.id // or appropriate identity comparison
 *         }
 *
 *         override fun areContentsTheSame(oldItem: City, newItem: City): Boolean {
 *             return oldItem == newItem
 *         }
 *     }
 * )
 * val items = differ.snapshot(pagingData)
 * ```
 */
class AsyncPagingDataDifferTestUtil<T : Any>(
    private val diffCallback: DiffUtil.ItemCallback<T>
) {
    suspend fun snapshot(pagingData: PagingData<T>): List<T> {
        val differ = AsyncPagingDataDiffer(
            diffCallback = diffCallback,
            updateCallback = NoopListCallback(),
            mainDispatcher = Dispatchers.Main,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(pagingData)
        return differ.snapshot().items
    }

    private class NoopListCallback : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }
}