package dev.jtsalva.cloudmare

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import dev.jtsalva.cloudmare.adapter.DomainListAdapter
import dev.jtsalva.cloudmare.api.zone.ZoneRequest
import kotlinx.android.synthetic.main.activity_domain_list.*
import timber.log.Timber

class DomainListActivity : CloudMareActivity(), SwipeRefreshable {

    // first: domain.domainId, second: domain.domainName
    // TODO: use data class instead of pairs
    private val domains = mutableListOf<Pair<String, String>>()
    private val initialized get() = domain_list?.adapter is DomainListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        showUserActivityMenuButton = true

        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
    }

    override fun onResume() {
        super.onResume()

        checkAuthAndContinue()
    }

    override fun onSwipeRefresh() = checkAuthAndContinue()

    private fun checkAuthAndContinue() {
        Timber.d("Checking auth - redirecting: ${Auth.notSet}")

        when {
            Auth.notSet -> startActivity(UserActivity::class)

            initialized -> renderList()

            else -> {
                setLayout(R.layout.activity_domain_list)
                setToolbarTitle(R.string.title_domain_list_activity)

                renderList()
            }
        }
    }

    private fun renderList() = launch {
        val response = ZoneRequest(this).list()
        if (response.failure || response.result == null) {
            Timber.e("response failure: ${response.firstErrorMessage}")
            dialog.error(message = response.firstErrorMessage, onAcknowledge = ::checkAuthAndContinue)
        }

        else domain_list.apply {
            domains.clear()
            response.result.forEach { zone ->
                domains.add(zone.id to zone.name)
            }

            if (initialized)
                adapter?.notifyDataSetChanged() ?: Timber.e("Can't reload domain list")
            else {
                adapter = DomainListAdapter(this@DomainListActivity, domains)
                layoutManager = LinearLayoutManager(this@DomainListActivity)
            }
        }
    }
}
