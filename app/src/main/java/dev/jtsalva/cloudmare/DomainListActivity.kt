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
    private var initialized = false

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

        if (response.failure) {
            Timber.e("response failure: ${response.firstErrorMessage}")
            dialog.error(message = response.firstErrorMessage, onAcknowledge = ::checkAuthAndContinue)
            return@launch
        }

        if (response.result == null) return@launch

        Timber.d("List length: ${response.result.size}")

        domains.clear()
        response.result.forEach { zone ->
            Timber.d("Name: ${zone.name}")
            domains.add(zone.id to zone.name)
        }

        if (initialized) domain_list.swapAdapter(
            DomainListAdapter(this, domains), false
        ) else {
            domain_list.adapter = DomainListAdapter(this, domains)
            domain_list.layoutManager = LinearLayoutManager(this)

            initialized = true
        }
    }
}
