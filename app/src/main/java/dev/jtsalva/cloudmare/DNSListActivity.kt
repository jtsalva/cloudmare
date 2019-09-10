package dev.jtsalva.cloudmare

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.jtsalva.cloudmare.adapter.DNSListAdapter
import dev.jtsalva.cloudmare.api.dns.DNSRecord
import dev.jtsalva.cloudmare.api.dns.DNSRecordRequest
import kotlinx.android.synthetic.main.activity_dns_list.*
import timber.log.Timber

class DNSListActivity : CloudMareActivity(), SwipeRefreshable {

    private lateinit var domainId: String

    private lateinit var domainName: String

    private lateinit var records: MutableList<DNSRecord>

    private val initialized: Boolean get() = dns_list.adapter is DNSListAdapter

    private val pagination = object : RecyclerView.OnScrollListener() {

        private var fetchingNextPage: Boolean = false
            set(value) {
                showProgressBar = value
                field = value
            }

        private val reachedBottom: Boolean get() =
            (dns_list.layoutManager as LinearLayoutManager).run {
                (childCount + findFirstVisibleItemPosition()) >= itemCount
            }

        private var reachedLastPage = false

        private var currentPage = 1

        fun nextPage(): Int {
            currentPage += 1
            return currentPage
        }

        fun resetPage() {
            currentPage = 1
            reachedLastPage = false
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            if (dy > 0 && !fetchingNextPage && reachedBottom && !reachedLastPage)
                fetchNextPage()
        }

        private fun fetchNextPage() = launch {
            fetchingNextPage = true

            DNSRecordRequest(this@DNSListActivity).
                list(domainId, nextPage()).also {
                if (it.failure || it.result == null) {
                    Timber.e("can't list DNS Records")
                    dialog.error(message = it.firstErrorMessage)
                } else if (it.result.isNotEmpty()) it.result.also { nextPage ->
                    val positionStart = records.size
                    records.addAll(nextPage)
                    dns_list.adapter?.notifyItemRangeInserted(positionStart, records.size)
                } else reachedLastPage = true
            }

            fetchingNextPage = false
        }

    }

    companion object Request {
        const val EDIT_RECORD = 0
        const val CREATE_RECORD = 1
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data == null) {
            Timber.d("onActivityResult: null data")
            return
        }

        when (resultCode) {
            DNSRecordActivity.CHANGES_MADE -> launch {
                val position = data.getIntExtra("position", -1)
                val dnsRecordId = data.getStringExtra("dns_record_id") ?: records[position].id

                val response = DNSRecordRequest(this).get(domainId, dnsRecordId)
                if (response.failure || response.result == null) {
                    Timber.e("can't fetch DNS Record")
                    return@launch
                }

                records[position] = response.result
                dns_list.adapter?.notifyItemChanged(position) ?: Timber.e("Can't notify change")
            }

            DNSRecordActivity.CREATED -> launch {
                val dnsRecordId = data.getStringExtra("dns_record_id") ?: throw Exception("dns_record_id must be set")

                val response = DNSRecordRequest(this).get(domainId, dnsRecordId)
                if (response.failure || response.result == null) {
                    Timber.e("can't fetch DNS Record")
                    return@launch
                }

                records.add(0, response.result)
                dns_list.adapter?.notifyItemInserted(0) ?: Timber.e("Can't notify creation")
                dns_list.layoutManager?.scrollToPosition(0) ?: Timber.e("Can't scroll to top")
            }

            DNSRecordActivity.DELETED -> {
                val position = data.getIntExtra("position", -1)

                records.removeAt(position)
                dns_list.adapter?.let { adapter ->
                    adapter.notifyItemRemoved(position)
                    adapter.notifyItemRangeChanged(position, records.size)
                } ?: Timber.e("Can't notify delete")
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_add -> {
            Timber.d("add action clicked")
            startActivityWithExtrasForResult(DNSRecordActivity::class, CREATE_RECORD,
                "domain_id" to domainId,
                "domain_name" to domainName
            )
            true
        }

        else -> super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        with (intent) {
            domainId = getStringExtra("domain_id") ?: ""
            domainName = getStringExtra("domain_name") ?: ""
        }

        showAddMenuButton = true

        setLayout(R.layout.activity_dns_list)
        setToolbarTitle("$domainName | DNS Records")
    }

    override fun onStart() {
        super.onStart()

        render()
    }

    override fun onSwipeRefresh() {
        super.onSwipeRefresh()
        pagination.resetPage()
    }

    override fun render() = launch {
        val response = DNSRecordRequest(this).list(domainId)
        if (response.failure || response.result == null) {
            Timber.e("can't list DNS Records")
            dialog.error(message = response.firstErrorMessage, onAcknowledge = ::onStart)
        }

        else (response.result as MutableList<DNSRecord>).also { result ->
            if (initialized && result != records) {
                Timber.d("reloading dns list")

                records.apply {
                    clear()
                    addAll(0, result)
                }
                dns_list.adapter?.notifyDataSetChanged() ?: Timber.e("Can't reload dns list")
            } else {
                Timber.d("initializing dns list")

                records = result
                dns_list.adapter = DNSListAdapter(this, domainId, domainName, records)
                dns_list.layoutManager = LinearLayoutManager(this)
                dns_list.addOnScrollListener(pagination)
            }
        }

        showProgressBar = false
    }


}
