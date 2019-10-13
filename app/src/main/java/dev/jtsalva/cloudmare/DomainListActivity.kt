package dev.jtsalva.cloudmare

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import dev.jtsalva.cloudmare.MenuButtonInitializer.Companion.USER_ACTIVITY_ACTION
import dev.jtsalva.cloudmare.adapter.DomainListAdapter
import dev.jtsalva.cloudmare.api.zone.Zone
import dev.jtsalva.cloudmare.api.zone.ZoneRequest
import kotlinx.android.synthetic.main.activity_domain_list.*
import timber.log.Timber

class DomainListActivity : CloudMareActivity(), SwipeRefreshable {

    private val domains = mutableListOf<Zone>()

    private val initialized get() = domain_list.adapter is DomainListAdapter

    private val pagination by lazy {
        object : Pagination(this, domain_list) {

            override fun fetchNextPage(pageNumber: Int) =
                ZoneRequest(this@DomainListActivity).launch {
                    list(pageNumber).run {
                        if (failure || result == null)
                            dialog.error(message = firstErrorMessage)
                        else if (result.isNotEmpty()) result.let { nextPage ->
                            val positionStart = domains.size
                            domains.addAll(nextPage)
                            domain_list.adapter?.notifyItemRangeChanged(positionStart, domains.size)
                        } else reachedLastPage = true
                    }

                    fetchingNextPage = false
                }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())

        menuButtonInitializer.onInflateSetVisible(USER_ACTIVITY_ACTION)

        setLayout(R.layout.activity_domain_list)
        setToolbarTitle(R.string.title_domain_list_activity)
    }

    override fun onStart() {
        super.onStart()

        if (Auth.notSet) startActivity(UserActivity::class)
        else render()
    }

    override fun onSwipeRefresh() {
        super.onSwipeRefresh()
        pagination.resetPage()
    }

    override fun render() = ZoneRequest(this).launch {
        val response = list()
        if (response.failure || response.result == null)
            dialog.error(message = response.firstErrorMessage, onAcknowledge = ::onStart)

        else {
            if (response.result != domains) domain_list.apply {
                domains.clear()
                domains.addAll(0, response.result)

                if (initialized)
                    adapter!!.notifyDataSetChanged()
                else {
                    adapter = DomainListAdapter(this@DomainListActivity, domains)
                    layoutManager = LinearLayoutManager(this@DomainListActivity)
                    addOnScrollListener(pagination)
                }
            }

            showNonFoundMessage = response.result.isEmpty()
        }

        showProgressBar = false
    }
}
