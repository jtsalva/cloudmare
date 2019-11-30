package dev.jtsalva.cloudmare

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class PaginationListener(private val activity: CloudMareActivity,
                                  private val layoutManager: LinearLayoutManager) : RecyclerView.OnScrollListener() {

    protected var fetchingNextPage: Boolean = false
        set(value) {
            activity.showProgressBar = value
            field = value
        }

    protected var reachedLastPage = false

    private inline val reachedBottom: Boolean get() =
        layoutManager.run {
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

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        if (dy > 0 && !fetchingNextPage && reachedBottom && !reachedLastPage) {
            fetchingNextPage = true

            fetchNextPage(nextPage())
        }
    }

    abstract fun fetchNextPage(pageNumber: Int): Any

}