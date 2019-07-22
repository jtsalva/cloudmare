package dev.jtsalva.cloudmare

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import dev.jtsalva.cloudmare.adapter.DomainListAdapter
import dev.jtsalva.cloudmare.api.zone.ZoneListResponse
import dev.jtsalva.cloudmare.api.zone.ZoneRequest
import kotlinx.android.synthetic.main.activity_domain_list.*

class DomainListActivity : CloudMareActivity() {

    override val TAG = "DomainListActivity"

    // first: domain.domainId, second: domain.domainName
    private val domains = mutableListOf<Pair<String, String>>()
    private var initialized = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        showSettingsMenuButton = true
    }

    override fun onResume() {
        super.onResume()

        Auth.load(this)
        checkAuthAndContinue()
    }

    private fun checkAuthAndContinue() {
        Log.d(TAG, "Checking auth - redirecting: ${Auth.notSet}")

        when {
            Auth.notSet -> startActivity(AppSettingsActivity::class.java)

            initialized -> ZoneRequest(this).list(::renderList)

            else -> {
                setLayout(R.layout.activity_domain_list)
                setToolbarTitle(R.string.title_domain_list)

                ZoneRequest(this).list(::renderList)
            }
        }
    }

    private fun renderList(response: ZoneListResponse) {
        if (response.failure) {
            Log.e(TAG, "response failure: ${response.firstErrorMessage}")
            longToast(response.firstErrorMessage)
            return
        }

        if (response.result == null) return

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
