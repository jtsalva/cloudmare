package dev.jtsalva.cloudmare

import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import dev.jtsalva.cloudmare.adapter.DomainListAdapter
import dev.jtsalva.cloudmare.api.zone.ZoneRequest
import kotlinx.android.synthetic.main.activity_domain_list.*

class DomainListActivity : CloudMareActivity() {

    override val TAG = "DomainListActivity"

    // first: domain.domainId, second: domain.domainName
    private val domains = mutableListOf<Pair<String, String>>()
    private var initialized = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        showUserActivityMenuButton = true
    }

    override fun onResume() {
        super.onResume()

        Auth.load(this)
        checkAuthAndContinue()
    }

    private fun checkAuthAndContinue() {
        Log.d(TAG, "Checking auth - redirecting: ${Auth.notSet}")

        when {
            Auth.notSet -> startActivity(UserActivity::class.java)

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
            Log.e(TAG, "response failure: ${response.firstErrorMessage}")
            dialog.error(message = response.firstErrorMessage, onAcknowledge = ::checkAuthAndContinue)
            return@launch
        }

        if (response.result == null) return@launch

        Log.d(TAG, "List length: ${response.result.size}")

        domains.clear()
        response.result.forEach { zone ->
            Log.d(TAG, "Name: ${zone.name}")
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
