package dev.jtsalva.cloudmare

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import dev.jtsalva.cloudmare.adapter.DNSListAdapter
import dev.jtsalva.cloudmare.api.Request
import dev.jtsalva.cloudmare.api.dns.DNSRecord
import dev.jtsalva.cloudmare.api.dns.DNSRecordRequest
import dev.jtsalva.cloudmare.api.zone.Zone
import kotlinx.android.synthetic.main.activity_dns_list.*
import timber.log.Timber

class DNSListActivity : CloudMareActivity(), SwipeRefreshable {

    private lateinit var domain: Zone

    private lateinit var records: MutableList<DNSRecord>

    private var sortBy = DNSRecord.SORT_BY_TYPE
        set(value) {
            if (value != field) {
                showProgressBar = true
                field = value
                pagination.resetPage()
                render()
            }
        }

    private var fromActivityResult = false
        get() {
            val current = field
            field = false
            return current
        }

    private val sortByTranslator by lazy {
        object {

            val idToReadable = mapOf(
                DNSRecord.SORT_BY_TYPE to getString(R.string.dns_list_sort_by_type),
                DNSRecord.SORT_BY_NAME to getString(R.string.dns_list_sort_by_name),
                DNSRecord.SORT_BY_CONTENT to getString(R.string.dns_list_sort_by_content),
                DNSRecord.SORT_BY_TTL to getString(R.string.dns_list_sort_by_ttl),
                DNSRecord.SORT_BY_PROXIED to getString(R.string.dns_list_sort_by_proxied)
            )

            fun indexOfValue(value: String): Int =
                idToReadable.run {
                    var index = 0
                    forEach {
                        if (it.key == value) return index
                        index += 1
                    }

                    return -1
                }

            fun getValue(readable: String): String =
                idToReadable.filterValues { it == readable }.keys.first()

        }
    }

    private val initialized: Boolean get() = dns_list.adapter is DNSListAdapter

    private val pagination by lazy {
        object : Pagination(this, dns_list) {

            override fun fetchNextPage(pageNumber: Int) =
                DNSRecordRequest(this@DNSListActivity).launch {
                    list(domain.id, pageNumber = pageNumber, order = sortBy).run {
                        if (failure || result == null)
                            dialog.error(message = firstErrorMessage)
                        else if (result.isNotEmpty()) result.let { nextPage ->
                            val positionStart = records.size
                            records.addAll(nextPage)
                            dns_list.adapter?.notifyItemRangeInserted(positionStart, records.size)
                        } else reachedLastPage = true
                    }

                    fetchingNextPage = false
                }

        }
    }

    companion object {
        const val EDIT_RECORD = 0
        const val CREATE_RECORD = 1
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        fromActivityResult = true

        if (data == null) {
            Timber.d("onActivityResult: null data")
            return
        }

        when (resultCode) {
            DNSRecordActivity.CHANGES_MADE -> {
                val dnsRecord = data.getParcelableExtra<DNSRecord>("dns_record")!!
                val position = records.indexOfFirst { it.id == dnsRecord.id }

                records[position] = dnsRecord
                dns_list.adapter!!.notifyItemChanged(position)
            }

            DNSRecordActivity.CREATED -> {
                val dnsRecord = data.getParcelableExtra<DNSRecord>("dns_record")!!

                records.add(0, dnsRecord)
                dns_list.adapter!!.notifyItemInserted(0)
                dns_list.layoutManager!!.scrollToPosition(0)

                showNonFoundMessage = false
            }

            DNSRecordActivity.DELETED -> {
                val dnsRecord = data.getParcelableExtra<DNSRecord>("dns_record")!!
                val position = records.indexOfFirst { it.id == dnsRecord.id }

                records.removeAt(position)
                dns_list.adapter!!.run {
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, records.size)
                }

                showNonFoundMessage = records.isEmpty()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_add -> {
            startActivityWithExtrasForResult(DNSRecordActivity::class, CREATE_RECORD,
                "domain" to domain
            )
            true
        }

        R.id.action_sort_by -> {
            val selectedItemIndex = sortByTranslator.indexOfValue(sortBy)
            dialog.multiChoice(
                title = "Sort By",
                resId = R.array.entries_dns_list_sort_by,
                initialSelection = selectedItemIndex) { _, _, text ->
                sortBy = sortByTranslator.getValue(text)
            }
            true
        }

        else -> super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        domain = intent.getParcelableExtra("domain")!!

        menuButtonInitializer.onInflateSetVisible(
            R.id.action_search,
            R.id.action_sort_by,
            R.id.action_add
        )

        setLayout(R.layout.activity_dns_list)
        setToolbarTitle("${domain.name} | DNS Records")
    }

    override fun onStart() {
        super.onStart()

        if (!fromActivityResult) render()
    }

    override fun onSwipeRefresh() {
        super.onSwipeRefresh()
        pagination.resetPage()
    }

    override fun render() = DNSRecordRequest(this).launch {
        val response = list(
            domain.id,
            order = sortBy,
            direction =
                if (sortBy == DNSRecord.SORT_BY_PROXIED) Request.DIRECTION_DESCENDING
                else Request.DIRECTION_ASCENDING
        )
        if (response.failure || response.result == null)
            dialog.error(message = response.firstErrorMessage, onAcknowledge = ::onStart)

        else (response.result as MutableList<DNSRecord>).also { result ->
            if (!initialized) dns_list.apply {
                records = result

                adapter = DNSListAdapter(this@DNSListActivity, domain, records)
                layoutManager = LinearLayoutManager(this@DNSListActivity)
                addOnScrollListener(pagination)
            } else if (result != records) records.apply {
                clear()
                addAll(0, result)

                dns_list.adapter!!.notifyDataSetChanged()
            }

            showNonFoundMessage = result.isEmpty()
        }

        showProgressBar = false

        swipeRefreshLayout.isRefreshing = false
    }


}
