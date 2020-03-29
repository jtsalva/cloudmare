package dev.jtsalva.cloudmare

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class PaginationListener(
    private val activity: CloudMareActivity,
    private val linearLayoutManager: LinearLayoutManager
) : RecyclerView.OnScrollListener() {

    companion object {

        fun lazy(
            activity: CloudMareActivity,
            recyclerViewId: Int,
            onFetchNextPage: PaginationListener.(pageNumber: Int) -> Unit
        ): Lazy<PaginationListener> =
            lazy {
                val layoutManager = activity.findViewById<RecyclerView>(recyclerViewId).layoutManager
                object : PaginationListener(activity, layoutManager as LinearLayoutManager) {
                    override fun fetchNextPage(pageNumber: Int) {
                        onFetchNextPage.invoke(this, pageNumber)
                    }
                }
            }
    }

    private var fetchingNextPage: Boolean = false
        set(value) {
            activity.showProgressBar = value
            field = value
        }

    private var reachedLastPage = false

    private inline val reachedBottom: Boolean get() =
        linearLayoutManager.run {
            (childCount + findFirstVisibleItemPosition()) >= itemCount
        }

    private var currentPage = 1

    private fun nextPage(): Int {
        currentPage += 1
        return currentPage
    }

    fun resetPage() {
        currentPage = 1
        reachedLastPage = false
    }

    fun reachedLastPage() {
        reachedLastPage = true
    }

    fun finishedFetchingPage() {
        fetchingNextPage = false
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        if (dy > 0 && !fetchingNextPage && reachedBottom && !reachedLastPage) {
            fetchingNextPage = true

            fetchNextPage(nextPage())
        }
    }

    abstract fun fetchNextPage(pageNumber: Int)
}
