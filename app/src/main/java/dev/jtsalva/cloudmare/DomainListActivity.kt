package dev.jtsalva.cloudmare

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import dev.jtsalva.cloudmare.adapter.DomainListAdapter
import dev.jtsalva.cloudmare.api.zone.Zone
import dev.jtsalva.cloudmare.api.zone.ZoneRequest
import kotlinx.android.synthetic.main.activity_domain_list.*
import timber.log.Timber

typealias DomainPair = Pair<String, String>

class DomainListActivity : CloudMareActivity(), SwipeRefreshable {

    // first: domain.domainId, second: domain.domainName
    // TODO: use data class instead of pairs
    private val domains = mutableListOf<DomainPair>()

    private val initialized get() = domain_list.adapter is DomainListAdapter

    private val pagination by lazy {
        object : Pagination(this, domain_list) {

            override fun fetchNextPage(pageNumber: Int) = launch {
                ZoneRequest(this@DomainListActivity).list(pageNumber).run {
                    if (failure || result == null)
                        dialog.error(message = firstErrorMessage)
                    else if (result.isNotEmpty()) result.let { nextPage ->
                        val positionStart = domains.size
                        domains.addAll(nextPage.toDomainPairs())
                        domain_list.adapter?.notifyItemRangeChanged(positionStart, positionStart)
                    } else reachedLastPage = true
                }

                fetchingNextPage = false
            }

        }
    }

    private fun List<Zone>.toDomainPairs() = mutableListOf<DomainPair>().apply {
        this@toDomainPairs.forEach { zone ->
            add(zone.id to zone.name)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())

        showUserActivityMenuButton = true

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

    override fun render() = launch {
        val response = ZoneRequest(this).list()
        if (response.failure || response.result == null)
            dialog.error(message = response.firstErrorMessage, onAcknowledge = ::onStart)

        else domain_list.apply {
            response.result.toDomainPairs().let { result ->
                if (result != domains) {
                    domains.clear()
                    domains.addAll(0, result)
                }
            }

            if (initialized)
                adapter?.notifyDataSetChanged() ?: Timber.e("Can't reload domain list")
            else {
                adapter = DomainListAdapter(this@DomainListActivity, domains)
                layoutManager = LinearLayoutManager(this@DomainListActivity)
                addOnScrollListener(pagination)
            }
        }

        showProgressBar = false
    }
}
