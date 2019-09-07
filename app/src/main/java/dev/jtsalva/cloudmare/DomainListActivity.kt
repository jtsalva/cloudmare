package dev.jtsalva.cloudmare

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import dev.jtsalva.cloudmare.adapter.DomainListAdapter
import dev.jtsalva.cloudmare.api.zone.ZoneRequest
import kotlinx.android.synthetic.main.activity_domain_list.*
import timber.log.Timber

typealias DomainPair = Pair<String, String>

class DomainListActivity : CloudMareActivity(), SwipeRefreshable {

    // first: domain.domainId, second: domain.domainName
    // TODO: use data class instead of pairs
    private val domains = mutableListOf<DomainPair>()
    private val initialized get() = domain_list.adapter is DomainListAdapter

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

    override fun render() = launch {
        val response = ZoneRequest(this).list()
        if (response.failure || response.result == null) {
            Timber.e("response failure: ${response.firstErrorMessage}")
            dialog.error(message = response.firstErrorMessage, onAcknowledge = ::onStart)
        }

        else domain_list.apply {
            mutableListOf<DomainPair>().apply {
                response.result.forEach { zone ->
                    add(zone.id to zone.name)
                }
            }.let { result ->
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
            }
        }

        hideProgressBar()
    }
}
